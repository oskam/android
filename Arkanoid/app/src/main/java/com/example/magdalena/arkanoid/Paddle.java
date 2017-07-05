package com.example.magdalena.arkanoid;

import android.graphics.RectF;

class Paddle {

    private RectF rect;
    private float paddleLength;
    private float paddleHeight;

    private int screenX;
    private int screenY;

    // X is the far left of the rectangle which forms our paddle
    private float x;
    // Y is the top coordinate
    private float y;

    // Ways can the paddle move
    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    // Direction of paddle move
    private int paddleMoving = STOPPED;

    Paddle(int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;

//        paddleLength = screenX;
        paddleLength = 140;
        paddleHeight = 20;

        x = screenX / 2 - paddleLength/2;
        y = screenY - paddleHeight;

        rect = new RectF(x, y, x + paddleLength, y + paddleHeight);
    }

    RectF getRect(){
        return rect;
    }

    void setMovementState(int state){
        paddleMoving = state;
    }

    void update(){
        if(paddleMoving == LEFT){
            if (rect.left-15 >= 0) {
                rect.left = rect.left - 15;
            } else {
                rect.left = 0;
            }
        }

        if(paddleMoving == RIGHT){
            if (rect.left+15 <= screenX-paddleLength) {
                rect.left = rect.left + 15;
            } else {
                rect.left = screenX-paddleLength;
            }
        }

        rect.right = rect.left + paddleLength;
    }

    void reset(){
        rect.left = x;
        rect.top = y;
        rect.right = x + paddleLength;
        rect.bottom = y + paddleHeight;
    }
}

