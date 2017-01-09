package com.example.thomas.tetris;

import android.app.Activity;
import android.os.Bundle;

import com.example.thomas.tetris.sound.Sounds;
import com.example.thomas.tetris.view.InGameMenu;
import com.example.thomas.tetris.view.GameView;

public class GameActivity extends Activity {

    private GameController gameController;
    private GameView gameView;
    private InGameMenu inGameMenu;

    public static GameActivity gameActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        gameView = (GameView) findViewById(R.id.game_matrix);
        inGameMenu = (InGameMenu) findViewById(R.id.countdown_view);
        gameController = new GameController();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameController.reload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameController.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameController.pause();
    }

    public GameController getGameController() {
        return gameController;
    }

    public GameView getGameView() {
        return gameView;
    }

    public InGameMenu getInGameMenu() {
        return inGameMenu;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gameController.stopThreads();
        Sounds.stopSound(Sounds.TETRIS);
    }
}
