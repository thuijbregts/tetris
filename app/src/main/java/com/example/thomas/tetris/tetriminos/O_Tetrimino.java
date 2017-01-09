package com.example.thomas.tetris.tetriminos;

import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.thomas.tetris.GameActivity;
import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.R;
import com.example.thomas.tetris.matrix.Matrix;

public class O_Tetrimino extends Tetrimino {

    public static final float MAX_LENGTH = 2;

    public O_Tetrimino(GameController gameController) {
        super(gameController);
        Mino[] minos = new Mino[MINOS_PER_TETRIMINO];
        minos[0] = new Mino(0, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2));
        minos[1] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2));
        minos[2] = new Mino(0, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos[3] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);

        currentState = new State(minos);
        currentState.setPreviousState(currentState);
        currentState.setNextState(currentState);

        color = BitmapFactory.decodeResource(GameActivity.gameActivity.getResources(), R.drawable.mino_o);
        ghostColor = GameActivity.gameActivity.getResources().getDrawable(R.drawable.ghost_o);
    }

    @Override
    public boolean rotateLeft() {
        return true;
    }

    @Override
    public boolean rotateRight() {
        return true;
    }
}
