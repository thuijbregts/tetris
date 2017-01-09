package com.example.thomas.tetris.sound;

import android.media.MediaPlayer;

import com.example.thomas.tetris.GameActivity;
import com.example.thomas.tetris.R;

import java.io.IOException;

public class Sounds {

    public static MediaPlayer TETRIS;
    public static MediaPlayer MOVE;
    public static MediaPlayer ROTATE;
    public static MediaPlayer LOCK;
    public static MediaPlayer LINE;
    public static MediaPlayer DROP;
    public static MediaPlayer GO;
    public static MediaPlayer COUNTDOWN_BIP;
    public static MediaPlayer LINES_TETRIS;
    public static MediaPlayer GAME_OVER;

    public static void loadSounds(){
        TETRIS = MediaPlayer.create(GameActivity.gameActivity, R.raw.tetris);
        TETRIS.setLooping(true);
        MOVE = MediaPlayer.create(GameActivity.gameActivity, R.raw.move);
        ROTATE = MediaPlayer.create(GameActivity.gameActivity, R.raw.rotate);
        LOCK = MediaPlayer.create(GameActivity.gameActivity, R.raw.lock);
        GO = MediaPlayer.create(GameActivity.gameActivity, R.raw.go);
        COUNTDOWN_BIP = MediaPlayer.create(GameActivity.gameActivity, R.raw.countdown_bip);
        /*DROP = MediaPlayer.create(GameActivity.gameActivity, R.raw.drop);
        DROP.setLooping(true);*/
        LINES_TETRIS = MediaPlayer.create(GameActivity.gameActivity, R.raw.lines_tetris);
        LINE = MediaPlayer.create(GameActivity.gameActivity, R.raw.line);
        GAME_OVER = MediaPlayer.create(GameActivity.gameActivity, R.raw.over);
    }

    public static void playSound(MediaPlayer sound){
        if(sound.isPlaying()){
            resetSound(sound);
        }
        sound.start();
    }

    public static void resetSound(MediaPlayer sound){
        sound.pause();
        sound.seekTo(0);
    }

    public static void pauseSound(MediaPlayer sound){
        sound.pause();
    }

    public static void stopSound(MediaPlayer sound){
        sound.stop();
    }
}
