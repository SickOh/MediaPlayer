package com.example.MediaPlayer;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LockScreen extends AppCompatActivity {

    Button BTN_MOVE;
    RelativeLayout rl_lockscreen;
    int[] location;
    private int _xDelta;
    private int _yDelta;
    float dX, dY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);




    }

    @SuppressLint("ClickableViewAccessibility")
    public LockScreen(){
        BTN_MOVE = findViewById(R.id.BTN_MOVE_NOTIFICATION);
        rl_lockscreen = findViewById(R.id.RL_Lockscreen);

        BTN_MOVE.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //final int X = (int) event.getRawX();
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        //dX = v.getX() - event.getRawX();
                        dY = v.getY() - rl_lockscreen.getHeight();
                        break;

                    case MotionEvent.ACTION_UP:

                        v.setY(v.getY());
                        break;

                    case MotionEvent.ACTION_MOVE:

                        rl_lockscreen.animate()
                                //.x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
                //return false;
            }
        });
    }
}
