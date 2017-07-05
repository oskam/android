package com.example.magdalena.arkanoid;

import android.graphics.RectF;

import java.util.Random;

class Ball {
    private RectF rect;
    private float xVelocity;
    private float yVelocity;
    private float ballWidth = 10;
    private float ballHeight = 10;

    // X is the far left of the rectangle which forms our ball
    private float x;
    // Y is the top coordinate
    private float y;


    Ball(int screenX, int screenY){
        xVelocity = 3f;
//        xVelocity = 5f;
//        yVelocity = -10f;
        yVelocity = -6f;

        x = screenX / 2 - ballWidth/2;
        y = screenY - 20 - ballHeight;

        rect = new RectF(x, y, x + ballWidth, y + ballHeight);
    }

    RectF getRect(){
        return rect;
    }

    void update(){
        rect.offset(xVelocity, yVelocity);
    }

    void reverseYVelocity(){
        yVelocity = -yVelocity;
    }


    void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(6);

        switch (answer) {
            case 0:
                break;
            case 1:
                reverseXVelocity();
                break;
            case 2:
                reverseXVelocity();
            case 3:
                xVelocity = Math.signum(xVelocity) * (Math.abs(xVelocity) + 1 + generator.nextInt(2));
                break;
            case 4:
                reverseXVelocity();
            case 5:
                xVelocity = Math.signum(xVelocity) * (Math.abs(xVelocity) - 1 - generator.nextInt(2));
                break;
        }
        if (Math.abs(xVelocity) > 7f) { xVelocity = Math.signum(xVelocity) * 7f; }
        else if (Math.abs(xVelocity) < 3f) { xVelocity = Math.signum(xVelocity) * 3f; }
    }

    void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    void reset(){
        rect.left = x;
        rect.top = y;
        rect.right = x + ballWidth;
        rect.bottom = y + ballHeight;
    }


}
