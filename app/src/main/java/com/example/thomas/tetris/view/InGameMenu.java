package com.example.thomas.tetris.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thomas.tetris.GameActivity;
import com.example.thomas.tetris.GameController;
import com.example.thomas.tetris.MainActivity;
import com.example.thomas.tetris.R;
import com.example.thomas.tetris.matrix.Matrix;
import com.example.thomas.tetris.sound.Sounds;

public class InGameMenu extends RelativeLayout{

    public static int SIZE_SQUARE;
    public static int GAP;

    private GameController gameController;
    private Paint paint;
    private boolean draw = false;

    private TextView countDown;

    public InGameMenu(Context context) {
        super(context);
    }

    public InGameMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InGameMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int width, int height){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(lp);

        GAP = GameView.GAP;
        SIZE_SQUARE = GameView.SIZE_SQUARE;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(GameActivity.gameActivity.getResources().getColor(R.color.empty_square));
        paint.setStyle(Paint.Style.FILL);
        draw = true;

        countDown = (TextView) findViewById(R.id.countdown_text);
    }

    public void hideView(){
        setVisibility(GONE);
    }

    public void showCountDown(){
        setVisibility(VISIBLE);
        countDown.setText("3");
    }

    public void updateCountdown(int count){
        Animation scale = AnimationUtils.loadAnimation(GameActivity.gameActivity, R.anim.countdown);
        switch(count){
            case 0:
                countDown.setText("2");
                break;
            case 1:
                countDown.setText("1");
                break;
            case 2:
                countDown.setText("GO!");
                break;
        }
        countDown.startAnimation(scale);
    }

    public void showGameOver(){
        setVisibility(VISIBLE);
        countDown.setText("Tu as perdu ma pauvre petite bouboune :'(");
    }

    public void showPause(){
        setVisibility(VISIBLE);
        countDown.setText("PAUSED");
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
