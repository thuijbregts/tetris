package com.example.thomas.tetris.tetriminos;

import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.thomas.tetris.GameActivity;
import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.R;
import com.example.thomas.tetris.matrix.Matrix;

public class I_Tetrimino extends Tetrimino {

    public static final float MAX_LENGTH = 4;

    public I_Tetrimino(GameController gameController) {
        super(gameController);
        Mino[] minos1 = new Mino[MINOS_PER_TETRIMINO];
        for(int i = 0; i < MINOS_PER_TETRIMINO; i++)
            minos1[i] = new Mino(1, Matrix.COLS / 2 - (int) Math.ceil(MAX_LENGTH / 2) + i);

        State state1 = new State(minos1);
        currentState = state1;

        Mino[] minos2 = new Mino[MINOS_PER_TETRIMINO];
        for(int i = 0; i < MINOS_PER_TETRIMINO; i++)
            minos2[i] = new Mino(i, Matrix.COLS/2);

        State state2 = new State(minos2);
        state2.setPreviousState(state1);
        state1.setNextState(state2);

        Mino[] minos3 = new Mino[MINOS_PER_TETRIMINO];
        for(int i = 0; i < MINOS_PER_TETRIMINO; i++)
            minos3[i] = new Mino(2, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + i);

        State state3 = new State(minos3);
        state3.setPreviousState(state2);
        state2.setNextState(state3);

        Mino[] minos4 = new Mino[MINOS_PER_TETRIMINO];
        for(int i = 0; i < MINOS_PER_TETRIMINO; i++)
            minos4[i] = new Mino(i, Matrix.COLS/2 - 1);

        State state4 = new State(minos4);
        state4.setPreviousState(state3);
        state3.setNextState(state4);
        state1.setPreviousState(state4);
        state4.setNextState(state1);

        color = BitmapFactory.decodeResource(GameActivity.gameActivity.getResources(), R.drawable.mino_i);
        ghostColor = GameActivity.gameActivity.getResources().getDrawable(R.drawable.ghost_i);
    }

    @Override
    public boolean rotateLeft() {
        return super.rotateLeft();
    }

    @Override
    public boolean rotateRight() {
       return super.rotateRight();
    }
}
