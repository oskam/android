package com.example.magdalena.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public class Arkanoid extends Activity {

    ArkanoidView arkanoidView;
    private SensorManager mSensorManager;
    private Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        arkanoidView = new ArkanoidView(this);
        setContentView(arkanoidView);
        mSensorManager.registerListener(arkanoidView, mSensor, SensorManager.SENSOR_DELAY_GAME);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler h = new Handler(Looper.getMainLooper());
                h.postDelayed(this, 5);
                h.post(new Runnable() {
                    public void run() {
                        arkanoidView.invalidate();
                    }
                });
            }
        });

        thread.start();
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(arkanoidView);
        super.onStop();
    }

    class ArkanoidView extends View implements SensorEventListener {

        // Game is paused at the start
        boolean paused = true;

        Canvas canvas;
        Paint paint;

        int screenSizeX;
        int screenSizeY;

        Paddle paddle;

        Ball ball;

        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        int score = 0;
        int lives = 3;
        int level = 1;
        int rowMax = 2;

        // constructor method
        public ArkanoidView(Context context) {
            super(context);

            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenSizeX = size.x;
            screenSizeY = size.y;

            paddle = new Paddle(screenSizeX, screenSizeY);

            ball = new Ball(screenSizeX, screenSizeY);

            createBricksAndRestart();
        }

        public void createBricksAndRestart() {
            int brickWidth = screenSizeX / 6;
            int brickHeight = screenSizeY / 15;

            if (lives == 0 || level == 4) {
                if (lives == 0) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.lost,
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.win,
                            Toast.LENGTH_LONG
                    ).show();
                    this.level = 1;
                }
                rowMax = 2;
                score = 0;
                lives = 3;
            } else if (level != 1) {
                Toast.makeText(
                        getApplicationContext(),
                        R.string.level_up,
                        Toast.LENGTH_LONG
                ).show();
                rowMax++;
            }


            // Build a wall of bricks
            numBricks = 0;
            for (int column = 0; column < 6; column++) {
                for (int row = 0; row < rowMax; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            ball.reset();
            paddle.reset();
        }


        public void update() {
            if (!paused) {
                paddle.update();
                ball.update();

                boolean levelEnd = true;

                // Check for ball colliding with a brick
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                            bricks[i].setInvisible();
                            ball.reverseYVelocity();
                            score = score + 1;
                        } else {
                            levelEnd = false;
                        }
                    }
                }

                if (levelEnd) {
                    paused = true;
                    level += 1;
                    createBricksAndRestart();
                }

                // Check for ball colliding with paddle
                // clearObstacleY method to make sure the ball doesn’t get stuck to the paddle
                if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                    ball.setRandomXVelocity();
                    ball.reverseYVelocity();
                    ball.clearObstacleY(paddle.getRect().top - 2);
                }

                // Bounce the ball back when it hits the bottom of screen
                if (ball.getRect().bottom > screenSizeY) {
                    ball.reverseYVelocity();
                    ball.clearObstacleY(screenSizeY - 2);

                    lives--;

                    if (lives == 0) {
                        level = 1;
                        createBricksAndRestart();
                    }
                }

                // Bounce the ball back when it hits the top of screen
                if (ball.getRect().top < 0) {
                    ball.reverseYVelocity();
                    ball.clearObstacleY(12);
                }

                // If the ball hits left wall bounce
                if (ball.getRect().left < 0) {
                    ball.reverseXVelocity();
                    ball.clearObstacleX(2);
                }

                // If the ball hits right wall bounce
                if (ball.getRect().right > screenSizeX) {
                    ball.reverseXVelocity();
                    ball.clearObstacleX(screenSizeX - 12);
                }
            }
        }

        // Draw the newly updated scene
        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            update();

            // background color
            canvas.drawARGB(255, 37, 43, 45);

            // Draw the paddle and ball
            paint.setColor(Color.argb(255, 188, 191, 196));
            canvas.drawRect(paddle.getRect(), paint);
            canvas.drawOval(ball.getRect(), paint);

            int green = Color.argb(255, 101, 219, 85);
            int violet = Color.argb(255, 216, 67, 206);
            int blue = Color.argb(255, 64, 93, 196);
            int yellow = Color.argb(255, 247, 243, 44);
            int[] colors = {yellow, blue, violet, green};


            // Draw the bricks if visible
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    paint.setColor(colors[bricks[i].row]);
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }

            // Draw the score and lives
            paint.setColor(Color.argb(255, 147, 154, 158));
            paint.setTextSize(30);
            canvas.drawText("Score: " + score, 10, 200, paint);
            canvas.drawText("Lives: " + lives, 10, 250, paint);
            canvas.drawText("Level: " + level, 10, 300, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    paused = false;

                    mSensorManager.unregisterListener(this);

                    if (motionEvent.getX() > screenSizeX / 2) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }
                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);

                    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
                    break;
            }
            return true;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float y = event.values[1];

                if (y > 2) {
                    paddle.setMovementState(paddle.RIGHT);
                } else if (y < -2) {
                    paddle.setMovementState(paddle.LEFT);
                } else {
                    paddle.setMovementState(paddle.STOPPED);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }
}

