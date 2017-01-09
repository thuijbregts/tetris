package com.example.thomas.tetris.tetriminos;

import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.thomas.tetris.GameActivity;
import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.R;
import com.example.thomas.tetris.matrix.Matrix;

public class Z_Tetrimino extends Tetrimino {

    public static final float MAX_LENGTH = 3;

    public Z_Tetrimino(GameController gameController) {
        super(gameController);

        Mino[] minos1 = new Mino[MINOS_PER_TETRIMINO];
        minos1[0] = new Mino(0, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2));
        minos1[1] = new Mino(0, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos1[2] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos1[3] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 2);

        State state1 = new State(minos1);
        currentState = state1;

        Mino[] minos2 = new Mino[MINOS_PER_TETRIMINO];
        minos2[0] = new Mino(0, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 2);
        minos2[1] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos2[2] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 2);
        minos2[3] = new Mino(2, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);

        State state2 = new State(minos2);
        state1.setNextState(state2);
        state2.setPreviousState(state1);

        Mino[] minos3 = new Mino[MINOS_PER_TETRIMINO];
        minos3[0] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2));
        minos3[1] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos3[2] = new Mino(2, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos3[3] = new Mino(2, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 2);

        State state3 = new State(minos3);
        state2.setNextState(state3);
        state3.setPreviousState(state2);

        Mino[] minos4 = new Mino[MINOS_PER_TETRIMINO];
        minos4[0] = new Mino(0, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos4[1] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2));
        minos4[2] = new Mino(1, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2) + 1);
        minos4[3] = new Mino(2, Matrix.COLS/2 - (int) Math.ceil(MAX_LENGTH/2));

        State state4 = new State(minos4);
        state3.setNextState(state4);
        state4.setPreviousState(state3);
        state4.setNextState(state1);
        state1.setPreviousState(state4);
        color = BitmapFactory.decodeResource(GameActivity.gameActivity.getResources(), R.drawable.mino_z);
        ghostColor = GameActivity.gameActivity.getResources().getDrawable(R.drawable.ghost_z);
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
