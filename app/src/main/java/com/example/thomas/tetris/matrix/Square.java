package com.example.thomas.tetris.matrix;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Square {

    private Bitmap color;
    private boolean empty;

    private int row;
    private int col;

    public Square(int row, int col){
        this.row = row;
        this.col = col;

        empty = true;
        color = null;
    }

    public Bitmap getColor() {
        return color;
    }

    public void setColor(Bitmap color) {
        this.color = color;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
        if(empty)
            color = null;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
