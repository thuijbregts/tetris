package com.example.thomas.tetris.matrix;

import android.util.Log;

import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.tetriminos.Mino;
import com.example.thomas.tetris.tetriminos.Tetrimino;

public class Matrix {

    public static final int ROWS = 22;
    public static final int VISIBLE_ROWS = 20;
    public static final int COLS = 10;

    private GameController gameController;
    private Square[][] matrix;

    public Matrix(GameController gameController){
        this.gameController = gameController;
        matrix = new Square[ROWS][COLS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                matrix[i][j] = new Square(i, j);
            }
        }
    }

    public void reset(){
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                matrix[i][j].setEmpty(true);
            }
        }
    }

    public boolean emptySquareAt(int row, int col){
        return matrix[row][col].isEmpty();
    }

    public void lock(Tetrimino tetrimino){
        int minRow = ROWS - 1;
        int maxRow = 0;
        for(Mino mino:tetrimino.getMinos()){
            if(mino.getRow() < minRow)
                minRow = mino.getRow();
            if(mino.getRow() > maxRow)
                maxRow = mino.getRow();
            matrix[mino.getRow()][mino.getCol()].setEmpty(false);
            matrix[mino.getRow()][mino.getCol()].setColor(tetrimino.getColor());
        }

        findLinesToClear(minRow, maxRow);
    }

    private void findLinesToClear(int minRow, int maxRow){
        int[] linesToClear = new int[4];
        byte linesCleared = 0;
        boolean fullLine;
        for(int i = minRow; i <= maxRow; i++){
            fullLine = true;
            for(int j = 0; j < COLS; j++){
                if(matrix[i][j].isEmpty()){
                    fullLine = false;
                    break;
                }
            }
            if(fullLine){
                linesToClear[linesCleared] = i;
                linesCleared++;
            }
        }

        if(linesCleared > 0){
            for(int i = 0; i < linesCleared; i++)
                clearLine(linesToClear[i]);
            gameController.updateScore(linesCleared);
        }
    }

    private void clearLine(int row){
        for(int i = row-1; i >= 0; i--){
            for(int j = 0; j < COLS; j++){
                matrix[i+1][j].setEmpty(matrix[i][j].isEmpty());
                matrix[i+1][j].setColor(matrix[i][j].getColor());
                matrix[i][j].setEmpty(true);
            }
        }
        for(int j = 0; j < COLS; j++){
            matrix[0][j].setEmpty(true);
        }
    }

    public Square getSquareAt(int row, int col){
        return matrix[row][col];
    }
}
