package com.example.thomas.tetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.thomas.tetris.matrix.Matrix;
import com.example.thomas.tetris.sound.Sounds;
import com.example.thomas.tetris.tetriminos.I_Tetrimino;
import com.example.thomas.tetris.tetriminos.J_Tetrimino;
import com.example.thomas.tetris.tetriminos.L_Tetrimino;
import com.example.thomas.tetris.tetriminos.O_Tetrimino;
import com.example.thomas.tetris.tetriminos.S_Tetrimino;
import com.example.thomas.tetris.tetriminos.T_Tetrimino;
import com.example.thomas.tetris.tetriminos.Tetrimino;
import com.example.thomas.tetris.tetriminos.Z_Tetrimino;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameController{

    public final static int INITIAL_REGULAR_DROP_DELAY = 500;
    public final static int SOFT_DROP_DELAY = 50;
    public final static int SIDE_MOVES_DELAY = 200;
    public static int MAX_DELAY_BEFORE_LOCK = 3000;

    public final static int POINTS_SINGLE_LINE = 40;
    public final static int POINTS_DOUBLE_LINE = 100;
    public final static int POINTS_TRIPLE_LINE = 300;
    public final static int POINTS_TETRIS = 1200;

    public final static int MAX_LEVEL = 9;
    public final static int LINES_TO_NEXT_LEVEL = 10;

    private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GameActivity.gameActivity);
    private SharedPreferences.Editor editor = preferences.edit();
    private Vibrator v = (Vibrator) GameActivity.gameActivity.getSystemService(Context.VIBRATOR_SERVICE);
    private Matrix matrix;

    private TetriminoBag currentTetriminoBag;
    private Tetrimino currentTetrimino;
    private Tetrimino nextTetrimino;

    private boolean gameOver;
    private boolean paused;
    private boolean started;

    private int level;
    private int score;
    private int highScore;

    private int delayBetweenDownMoves;

    private ScheduledExecutorService drop = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService regularDrop = Executors.newScheduledThreadPool(2);
    private ScheduledExecutorService leftRightHandler = Executors.newScheduledThreadPool(3);
    private ScheduledFuture scheduledFutureForDrop;
    private ScheduledFuture scheduledFutureForMoves;

    private boolean movingLeft;
    private boolean movingRight;
    private boolean movedDuringDown;
    private boolean softDrop;

    private int totalLinesCleared;

    private RelativeLayout moveLeft;
    private RelativeLayout moveRight;
    private RelativeLayout dropDown;
    private RelativeLayout rotateLeft;
    private RelativeLayout rotateRight;
    private RelativeLayout pauseButton;

    private byte count;
    private Timer timer;

    public GameController(){
        GameActivity.gameActivity.getGameView().setGameController(this);
        GameActivity.gameActivity.getInGameMenu().setGameController(this);
        setControlButtons();
        started = false;
    }

    public void start() {
        started = true;
        Sounds.loadSounds();
        pause();
        initializeGame();
        reset();
        reload();
    }

    public void reload(){
        GameActivity.gameActivity.getInGameMenu().init(GameActivity.gameActivity.getGameView().getWidth(),
                GameActivity.gameActivity.getGameView().getHeight());

        startCountDown();
    }

    public void pause(){
        count = 0;
        if(Sounds.TETRIS.isPlaying())
            Sounds.pauseSound(Sounds.TETRIS);
        stopThreads();
        pauseControls();

        GameActivity.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity.gameActivity.getInGameMenu().showPause();
            }
        });
        paused = true;
    }

    public void resume(){
        Sounds.playSound(Sounds.TETRIS);
        startRegularDrop();
        resumeControls();
    }

    public void startCountDown(){
        GameActivity.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity.gameActivity.getInGameMenu().showCountDown();
            }
        });
        paused = false;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (count < 3) {
                    GameActivity.gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GameActivity.gameActivity.getInGameMenu().updateCountdown(count);
                            count++;
                        }
                    });
                    if(count < 2)
                        Sounds.playSound(Sounds.COUNTDOWN_BIP);
                    else
                        Sounds.playSound(Sounds.GO);
                } else {
                    GameActivity.gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GameActivity.gameActivity.getInGameMenu().hideView();
                            GameActivity.gameActivity.getGameView().setVisibility(View.VISIBLE);
                        }
                    });
                    resume();
                    timer.cancel();
                }
            }
        }, 800, 1000);
    }

    private void cancelSoftDrop(){
        /*if(Sounds.DROP.isPlaying()){
            Sounds.pauseSound(Sounds.DROP);
        }*/
        scheduledFutureForDrop.cancel(true);
    }

    public boolean hasMovedDuringDown(){
        return movedDuringDown;
    }

    public void setMovedDuringDown(boolean movedDuringDown){
        this.movedDuringDown = movedDuringDown;
    }

    private void startRegularDrop(){
        scheduledFutureForDrop = regularDrop.scheduleAtFixedRate(new Runnable() {
            public void run() {
                currentTetrimino.moveDown();
                invalidate();
                movedDuringDown = false;
            }
        }, 0, delayBetweenDownMoves, TimeUnit.MILLISECONDS);
    }

    private void startSoftDrop(){
        //Sounds.playSound(Sounds.DROP);
        scheduledFutureForDrop = drop.scheduleAtFixedRate(new Runnable() {
            public void run() {
                currentTetrimino.moveDown();

                score++;
                if (score > highScore) {
                    updateHighScore();
                }

                updateInformation();

                invalidate();
                movedDuringDown = false;
            }
        }, 0, SOFT_DROP_DELAY, TimeUnit.MILLISECONDS);
    }

    private void startLeftRightHandler(){
        scheduledFutureForMoves = leftRightHandler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (movingLeft && !movingRight) {
                    currentTetrimino.moveLeft();
                    invalidate();
                } else if (movingRight && !movingLeft) {
                    currentTetrimino.moveRight();
                    invalidate();
                }
            }
        }, 0, SIDE_MOVES_DELAY, TimeUnit.MILLISECONDS);
    }

    private void initializeGame(){
        matrix = new Matrix(this);

        highScore = preferences.getInt("highScore", 0);
    }

    private void reset(){
        matrix.reset();

        gameOver = false;

        level = 1;
        setNewDelay();
        score = 0;

        currentTetriminoBag = new TetriminoBag();
        nextTetrimino();

        updateInformation();
        invalidate();
    }

    public void updateScore(int linesCleared){
        int pointsToAdd;
        switch (linesCleared){
            case 1:
                pointsToAdd = POINTS_SINGLE_LINE;
                Sounds.playSound(Sounds.LINE);
                break;
            case 2:
                pointsToAdd = POINTS_DOUBLE_LINE;
                Sounds.playSound(Sounds.LINE);
                break;
            case 3:
                pointsToAdd = POINTS_TRIPLE_LINE;
                Sounds.playSound(Sounds.LINE);
                break;
            default:
                pointsToAdd = POINTS_TETRIS;
                Sounds.playSound(Sounds.LINES_TETRIS);
        }
        score += pointsToAdd * (level + 1);

        if(score > highScore) {
            updateHighScore();
        }

        updateLinesCleared(linesCleared);

        updateInformation();
    }

    private void updateHighScore(){
        highScore = score;
        editor.putInt("highScore", highScore);
        editor.apply();
    }

    public void updateLinesCleared(int linesCleared){
        totalLinesCleared += linesCleared;
        if(level < MAX_LEVEL && totalLinesCleared >= level * LINES_TO_NEXT_LEVEL)
            nextLevel();
    }

    public void nextLevel(){
        level++;
        setNewDelay();
        if(!softDrop){
            cancelSoftDrop();
            startRegularDrop();
        }
    }

    public void nextTetrimino(){
        currentTetriminoBag.getNextTetrimino();
        currentTetrimino.updateGhostTetrimino();

        GameActivity.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity.gameActivity.getGameView().drawNextTetrimino();
            }
        });
    }

    public void invalidate(){
        GameActivity.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity.gameActivity.getGameView().updateMatrix();
            }
        });
    }

    private void updateInformation(){
        GameActivity.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity.gameActivity.getGameView().updateInformation();
            }
        });
    }

    public void setNewDelay(){
        delayBetweenDownMoves = INITIAL_REGULAR_DROP_DELAY - (40 * (level-1));
        MAX_DELAY_BEFORE_LOCK -= (187 * (level-1));
    }

    public void lose(){
        gameOver = true;
        Sounds.resetSound(Sounds.TETRIS);
        Sounds.playSound(Sounds.GAME_OVER);
        pauseControls();
        stopThreads();

        v.vibrate(500);

        GameActivity.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity.gameActivity.getInGameMenu().showGameOver();
            }
        });
    }

    private void setControlButtons(){
        moveLeft = (RelativeLayout) GameActivity.gameActivity.findViewById(R.id.game_view_left);
        moveRight = (RelativeLayout) GameActivity.gameActivity.findViewById(R.id.game_view_right);
        dropDown = (RelativeLayout) GameActivity.gameActivity.findViewById(R.id.game_view_down);
        rotateLeft = (RelativeLayout) GameActivity.gameActivity.findViewById(R.id.game_view_rotate_left);
        rotateRight = (RelativeLayout) GameActivity.gameActivity.findViewById(R.id.game_view_rotate_right);
        pauseButton = (RelativeLayout) GameActivity.gameActivity.findViewById(R.id.game_button_pause);

        resumeControls();
    }

    public void pauseControls(){
        moveLeft.setOnTouchListener(null);
        moveRight.setOnTouchListener(null);
        dropDown.setOnTouchListener(null);
        rotateLeft.setOnClickListener(null);
        rotateRight.setOnClickListener(null);

        if(gameOver)
            pauseButton.setOnClickListener(null);
    }

    public void resumeControls(){
        moveLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    movingLeft = true;
                    if (!movingRight)
                        startLeftRightHandler();
                    else if(scheduledFutureForMoves != null)
                        scheduledFutureForMoves.cancel(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    movingLeft = false;
                    if (!movingRight && scheduledFutureForMoves != null)
                        scheduledFutureForMoves.cancel(true);
                    else
                        startLeftRightHandler();
                }
                return false;
            }
        });
        moveRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    movingRight = true;
                    if (!movingLeft)
                        startLeftRightHandler();
                    else if(scheduledFutureForMoves != null)
                        scheduledFutureForMoves.cancel(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    movingRight = false;
                    if (!movingLeft && scheduledFutureForMoves != null)
                        scheduledFutureForMoves.cancel(true);
                    else
                        startLeftRightHandler();
                }
                return false;
            }
        });
        dropDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (scheduledFutureForDrop != null)
                        cancelSoftDrop();
                    startSoftDrop();
                    softDrop = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (scheduledFutureForDrop != null)
                        cancelSoftDrop();
                    if (!gameOver)
                        startRegularDrop();
                    softDrop = false;
                }
                return false;
            }
        });
        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTetrimino.rotateLeft()) {
                    //TODO
                    Sounds.playSound(Sounds.ROTATE);
                }
                invalidate();
            }
        });
        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTetrimino.rotateRight()) {
                    //TODO
                    Sounds.playSound(Sounds.ROTATE);
                }
                invalidate();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPaused()){
                    if(timer != null)
                        timer.cancel();
                    startCountDown();
                    pauseButton.getChildAt(0).setBackgroundResource(R.drawable.pause_icon);
                }
                else{
                    pause();
                    pauseButton.getChildAt(0).setBackgroundResource(R.drawable.play_icon);
                }
            }
        });
    }

    public void stopThreads(){
        if(scheduledFutureForDrop != null)
            cancelSoftDrop();
        if(scheduledFutureForMoves != null)
            scheduledFutureForMoves.cancel(true);
        if(timer != null)
            timer.cancel();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Tetrimino getCurrentTetrimino() {
        return currentTetrimino;
    }

    public Tetrimino getNextTetrimino() {
        return nextTetrimino;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }


    public int getLevel() {
        return level;
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStarted() {
        return started;
    }


    private class TetriminoBag {

        public static final int BAG_MAX_CONTENT = 7;
        private Tetrimino[] tetriminos = new Tetrimino[BAG_MAX_CONTENT];
        private byte currentIndex = 0;

        public TetriminoBag(){
            byte indexesAvailable = Tetrimino.TYPES_COUNT;
            byte[] indexes = new byte[indexesAvailable];
            for(byte i = 0; i < indexesAvailable; i++)
                indexes[i] = i;

            int random;
            for(byte i = 0; i < BAG_MAX_CONTENT; i++){
                random = (int) Math.floor(Math.random()*indexesAvailable);
                switch (indexes[random]){
                    case Tetrimino.I_BLOCK:
                        tetriminos[i] = new I_Tetrimino(GameController.this);
                        break;
                    case Tetrimino.J_BLOCK:
                        tetriminos[i] = new J_Tetrimino(GameController.this);
                        break;
                    case Tetrimino.L_BLOCK:
                        tetriminos[i] = new L_Tetrimino(GameController.this);
                        break;
                    case Tetrimino.O_BLOCK:
                        tetriminos[i] = new O_Tetrimino(GameController.this);
                        break;
                    case Tetrimino.S_BLOCK:
                        tetriminos[i] = new S_Tetrimino(GameController.this);
                        break;
                    case Tetrimino.T_BLOCK:
                        tetriminos[i] = new T_Tetrimino(GameController.this);
                        break;
                    case Tetrimino.Z_BLOCK:
                        tetriminos[i] = new Z_Tetrimino(GameController.this);
                        break;
                }
                indexes[random] = indexes[--indexesAvailable];
            }

            for(int i = 0; i < tetriminos.length; i++)
                Log.d("bag", tetriminos[i].getClass().getSimpleName());
        }

        public void getNextTetrimino(){
            if(currentIndex < 6){
                currentTetrimino = tetriminos[currentIndex];
                nextTetrimino = tetriminos[++currentIndex];
            }
            else{
                currentTetrimino = tetriminos[currentIndex];
                currentTetriminoBag = new TetriminoBag();
                nextTetrimino = currentTetriminoBag.getTetriminos()[currentTetriminoBag.getCurrentIndex()];
            }
        }

        public Tetrimino[] getTetriminos() {
            return tetriminos;
        }

        public byte getCurrentIndex() {
            return currentIndex;
        }
    }
}
