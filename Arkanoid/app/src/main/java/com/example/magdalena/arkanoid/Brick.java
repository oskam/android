package com.example.magdalena.arkanoid;

import android.graphics.RectF;

class Brick {

    private RectF rect;

    private boolean isVisible;

    public int row;
    public int column;

    Brick(int row, int column, int width, int height){

        isVisible = true;
        this.row = row;
        this.column = column;

        int padding = 1;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    RectF getRect(){
        return this.rect;
    }

    void setInvisible(){
        isVisible = false;
    }

    boolean getVisibility(){
        return isVisible;
    }
}
