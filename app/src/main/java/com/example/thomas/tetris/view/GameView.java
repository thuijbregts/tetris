package com.example.thomas.tetris.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.tetris.GameActivity;
import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.MainActivity;
import com.example.thomas.tetris.R;
import com.example.thomas.tetris.matrix.Matrix;
import com.example.thomas.tetris.tetriminos.I_Tetrimino;
import com.example.thomas.tetris.tetriminos.Mino;
import com.example.thomas.tetris.tetriminos.O_Tetrimino;

public class GameView extends LinearLayout {

    public static int SIZE_SQUARE;
    public static int GAP;

    private GameController gameController;
    private Paint paint;
    private boolean draw = false;

    private ImageView[][] matrix;
    private int[][] matrixColors;
    private ImageView[][] nextTetrimino;

    private int[] square_colors = new int[]{GameActivity.gameActivity.getResources().getColor(R.color.empty_square),
            GameActivity.gameActivity.getResources().getColor(R.color.empty_square_1),
            GameActivity.gameActivity.getResources().getColor(R.color.empty_square_2),
            GameActivity.gameActivity.getResources().getColor(R.color.empty_square_3),
            GameActivity.gameActivity.getResources().getColor(R.color.empty_square_4)};

    private LinearLayout next;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                GAP = MainActivity.UNIT;
                SIZE_SQUARE = ((ImageView) ((LinearLayout) getChildAt(0)).getChildAt(0)).getWidth() - GAP;

                next = (LinearLayout) GameActivity.gameActivity.findViewById(R.id.game_activity_next_tetrimino_panel);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (SIZE_SQUARE + GAP + (10 * MainActivity.UNIT)) * 2);
                next.setLayoutParams(lp);
                draw = true;

                paint = new Paint();
                paint.setStyle(Paint.Style.FILL);

                createMatrix();
                createNextTetrimino();
            }
        });
    }

    private void createMatrix(){
        matrix = new ImageView[Matrix.VISIBLE_ROWS][Matrix.COLS];
        matrixColors = new int[Matrix.VISIBLE_ROWS][Matrix.COLS];
        for(int i = 0; i < Matrix.VISIBLE_ROWS; i++){

            LinearLayout row = (LinearLayout) getChildAt(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SIZE_SQUARE+GAP);
            row.setLayoutParams(lp);

            for(int j = 0; j < Matrix.COLS; j++){
                matrix[i][j] = (ImageView) row.getChildAt(j);
                matrixColors[i][j] = square_colors[(int)Math.floor(Math.random()*square_colors.length)];
            }
        }
    }

    private void createNextTetrimino(){
        nextTetrimino = new ImageView[2][4];
        LinearLayout row = (LinearLayout) next.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        for(int j = 0; j < 4; j++) {
            nextTetrimino[0][j] = (ImageView) row.getChildAt(j);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(SIZE_SQUARE, SIZE_SQUARE);
            nextTetrimino[0][j].setLayoutParams(lps);
        }

        row = (LinearLayout) next.getChildAt(1);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        for(int j = 0; j < 3; j++) {
            nextTetrimino[1][j] = (ImageView) row.getChildAt(j);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(SIZE_SQUARE, SIZE_SQUARE);
            nextTetrimino[1][j].setLayoutParams(lps);
        }
    }

    public void updateInformation(){
        TextView score = (TextView) GameActivity.gameActivity.findViewById(R.id.score);
        TextView highScore = (TextView) GameActivity.gameActivity.findViewById(R.id.high_score);
        TextView level = (TextView) GameActivity.gameActivity.findViewById(R.id.level);
        TextView lines = (TextView) GameActivity.gameActivity.findViewById(R.id.lines);

        score.setText("" + gameController.getScore());
        highScore.setText("" + gameController.getHighScore());
        level.setText("" + gameController.getLevel());
        lines.setText("" + gameController.getTotalLinesCleared());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(draw){
            if(!gameController.isStarted()){
                gameController.start();
            }
            canvas.drawColor(GameActivity.gameActivity.getResources().getColor(R.color.square_border));
            for(int i = 0; i < Matrix.VISIBLE_ROWS; i++){
                for(int j = 0; j < Matrix.COLS; j++) {
                    paint.setColor(matrixColors[i][j]);
                    canvas.drawRect(GAP * (j + 1) + SIZE_SQUARE * j,
                            GAP * (i + 1) + SIZE_SQUARE * i,
                            GAP * (j + 1) + SIZE_SQUARE * j + SIZE_SQUARE,
                            GAP * (i + 1) + SIZE_SQUARE * i + SIZE_SQUARE,
                            paint);
                }
            }
        }
    }

    public void updateMatrix(){
        int rowIndex;
        for(int i = 0; i < Matrix.VISIBLE_ROWS; i++){
            for(int j = 0; j < Matrix.COLS; j++){
                rowIndex = i + 2;
                if(gameController.getMatrix().getSquareAt(rowIndex, j).isEmpty()) {
                    matrix[i][j].setImageDrawable(null);
                }
                else {
                    matrix[i][j].setImageBitmap(gameController.getMatrix().getSquareAt(rowIndex, j).getColor());
                }
            }
        }
        drawGhostTetrimino();
        drawCurrentTetrimino();
    }

    private void drawCurrentTetrimino(){
        for(Mino mino:gameController.getCurrentTetrimino().getMinos()){
            int i = mino.getRow() - 2;
            if(i < 0)
                continue;
            int j = mino.getCol();
            matrix[i][j].setImageBitmap(gameController.getCurrentTetrimino().getColor());
        }
    }

    private void drawGhostTetrimino(){
        for(Mino mino:gameController.getCurrentTetrimino().getGhostMinos()){
            int i = mino.getRow() - 2;
            if(i < 0)
                continue;
            int j = mino.getCol();
            matrix[i][j].setImageDrawable(gameController.getCurrentTetrimino().getGhostColor());
        }
    }

    public void drawNextTetrimino(){
        if(gameController.getNextTetrimino() instanceof I_Tetrimino){
            next.getChildAt(1).setVisibility(View.GONE);
            ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(3)).setVisibility(View.VISIBLE);
            ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(2)).setVisibility(View.VISIBLE);
            for(int i = 0; i < 4; i++){
                nextTetrimino[0][i].setImageBitmap(gameController.getNextTetrimino().getColor());
            }
        }
        else {
            next.getChildAt(1).setVisibility(View.VISIBLE);
            ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(3)).setVisibility(View.GONE);
            if(gameController.getNextTetrimino() instanceof O_Tetrimino){
                ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(2)).setVisibility(View.GONE);
                ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(2)).setVisibility(View.GONE);
                ((ImageView)((LinearLayout) next.getChildAt(1)).getChildAt(2)).setVisibility(View.GONE);
                ((ImageView)((LinearLayout) next.getChildAt(1)).getChildAt(2)).setVisibility(View.GONE);
                for(int i = 0; i < 2; i++){
                    for(int j = 0; j < 2; j++){
                        nextTetrimino[i][j].setImageBitmap(gameController.getNextTetrimino().getColor());
                    }
                }
            }
            else{
                ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(2)).setVisibility(View.VISIBLE);
                ((ImageView)((LinearLayout) next.getChildAt(0)).getChildAt(2)).setVisibility(View.VISIBLE);
                ((ImageView)((LinearLayout) next.getChildAt(1)).getChildAt(2)).setVisibility(View.VISIBLE);
                ((ImageView)((LinearLayout) next.getChildAt(1)).getChildAt(2)).setVisibility(View.VISIBLE);
                boolean found;
                for(int i = 0; i < 2; i++){
                    for(int j = 0; j < 3; j++){
                        found = false;
                        for(Mino mino:gameController.getNextTetrimino().getMinos()) {
                            if (mino.getRow() == i && mino.getCol() == j + 3) {
                                nextTetrimino[i][j].setImageBitmap(gameController.getNextTetrimino().getColor());
                                found = true;
                                break;
                            }
                        }
                        if(!found)
                            nextTetrimino[i][j].setImageBitmap(null);
                    }
                }
            }
        }
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
