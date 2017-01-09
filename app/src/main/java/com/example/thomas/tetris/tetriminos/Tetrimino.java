package com.example.thomas.tetris.tetriminos;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.matrix.Matrix;
import com.example.thomas.tetris.sound.Sounds;

public abstract class Tetrimino {

    public static final int I_BLOCK = 0;
    public static final int J_BLOCK = 1;
    public static final int L_BLOCK = 2;
    public static final int O_BLOCK = 3;
    public static final int S_BLOCK = 4;
    public static final int T_BLOCK = 5;
    public static final int Z_BLOCK = 6;

    public static final int TYPES_COUNT = 7;
    public static final int MINOS_PER_TETRIMINO = 4;

    private long timeLastDownMove;

    protected GameController gameController;
    protected Matrix matrix;

    protected State currentState;
    protected Mino[] ghostMinos;

    protected Bitmap color;
    protected Drawable ghostColor;

    protected int downMoves;

    public Tetrimino(GameController gameController){
        this.gameController = gameController;
        matrix = gameController.getMatrix();
        downMoves = 0;
        ghostMinos = new Mino[MINOS_PER_TETRIMINO];
        for(int i = 0; i < 4; i++)
            ghostMinos[i] = new Mino(0,0);
    }

    public void moveLeft(){
        if(canMoveLeft()){
            moveStatesLeft(1);
            updateGhostTetrimino();
            Sounds.playSound(Sounds.MOVE);
        }
    }

    private boolean canMoveLeft(){
        for(Mino mino: currentState.getMinos()){
            if(mino.getCol() == 0)
                return false;
            if(!matrix.emptySquareAt(mino.getRow(), mino.getCol()-1))
                return false;
        }
        return true;
    }

    public void moveRight(){
        if(canMoveRight()){
            moveStatesRight(1);
            updateGhostTetrimino();
            Sounds.playSound(Sounds.MOVE);
        }
    }

    private boolean canMoveRight(){
        for(Mino mino: currentState.getMinos()){
            if(mino.getCol() == Matrix.COLS - 1)
                return false;
            if(!matrix.emptySquareAt(mino.getRow(), mino.getCol()+1))
                return false;
        }
        return true;
    }

    public void moveDown(){
        if(downMoves == 0 && collidesWithSquare()) {
            gameController.lose();
        }
        else {
            if (canMoveDown()) {
                timeLastDownMove = System.currentTimeMillis();
                moveStatesDown(1);
                downMoves++;
            } else {
                if(!(downMoves > 0 && (System.currentTimeMillis() - timeLastDownMove < GameController.MAX_DELAY_BEFORE_LOCK) && gameController.hasMovedDuringDown())){
                    Log.d("test", "next");
                    Sounds.playSound(Sounds.LOCK);
                    matrix.lock(this);
                    gameController.nextTetrimino();
                }
            }
        }
    }

    private boolean collidesWithSquare(){
        for(Mino mino: currentState.getMinos()){
            if(!matrix.emptySquareAt(mino.getRow(), mino.getCol()))
                return true;
        }
        return false;
    }

    private boolean canMoveDown(){
        for(Mino mino: currentState.getMinos()){
            if(mino.getRow() == Matrix.ROWS - 1)
                return false;
            if(!matrix.emptySquareAt(mino.getRow()+1, mino.getCol()))
                return false;
        }
        return true;
    }

    protected void moveStatesRight(int shifting){
        gameController.setMovedDuringDown(true);
        State state = currentState;
        do{
            for(Mino mino: state.getMinos()){
                mino.setCol(mino.getCol() + shifting);
            }
            state = state.getNextState();
        }while(!state.equals(currentState));
    }

    protected void moveStatesLeft(int shifting){
        gameController.setMovedDuringDown(true);
        State state = currentState;
        do{
            for(Mino mino: state.getMinos()){
                mino.setCol(mino.getCol() - shifting);
            }
            state = state.getNextState();
        }while(!state.equals(currentState));
    }

    protected void moveStatesDown(int shifting){
        State state = currentState;
        do{
            for (Mino mino : state.getMinos()) {
                mino.setRow(mino.getRow() + shifting);
            }
            state = state.getNextState();
        }while(!state.equals(currentState));
    }

    protected void moveStatesUp(int shifting){
        State state = currentState;
        do{
            for (Mino mino : state.getMinos()) {
                mino.setRow(mino.getRow() + shifting);
            }
            state = state.getNextState();
        }while(!state.equals(currentState));
    }

    public boolean rotateLeft(){
        for(Mino mino:currentState.getPreviousState().getMinos()){
            if(mino.getRow() >= Matrix.ROWS || mino.getRow() < 0 || mino.getCol() >= Matrix.COLS || mino.getCol() < 0)
                return false;
            if(!matrix.emptySquareAt(mino.getRow(), mino.getCol()))
                return false;
        }
        currentState = currentState.getPreviousState();
        updateGhostTetrimino();
        return true;
    }

    public boolean rotateRight(){
        for(Mino mino:currentState.getNextState().getMinos()){
            if(mino.getRow() >= Matrix.ROWS || mino.getRow() < 0 || mino.getCol() >= Matrix.COLS || mino.getCol() < 0)
                return false;
            if(!matrix.emptySquareAt(mino.getRow(), mino.getCol()))
                return false;
        }
        currentState = currentState.getNextState();
        updateGhostTetrimino();
        return true;
    }

    public void updateGhostTetrimino(){
        boolean ok = true;
        for(int i = 0; i < MINOS_PER_TETRIMINO; i++) {
            ghostMinos[i].setRow(currentState.getMinos()[i].getRow()-1);
            ghostMinos[i].setCol(currentState.getMinos()[i].getCol());
        }
        while (ok){
            for(int i = 0; i < MINOS_PER_TETRIMINO; i++) {
                ghostMinos[i].setRow(ghostMinos[i].getRow() + 1);
                if(ghostMinos[i].getRow() == Matrix.ROWS - 1) {
                    ok = false;
                }
            }

            if(!ok)
                break;

            for(int i = 0; i < MINOS_PER_TETRIMINO; i++){
                if(!matrix.emptySquareAt(ghostMinos[i].getRow() + 1, ghostMinos[i].getCol())){
                    ok = false;
                    break;
                }
            }
        }
    }

    public Mino[] getMinos() {
        return currentState.getMinos();
    }

    public Mino[] getGhostMinos() {
        return ghostMinos;
    }

    public Drawable getGhostColor() {
        return ghostColor;
    }

    public Bitmap getColor() {
        return color;
    }

}
