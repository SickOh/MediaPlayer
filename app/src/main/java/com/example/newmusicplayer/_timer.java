package com.example.newmusicplayer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class _timer extends AppCompatActivity {

    private int timerTick;
    public CountDownTimer thisTimer;
    public boolean timerstate = false;
    private _local_audio localAudio;
    private _wifi_audio wifiAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public _timer(_local_audio localAudio, _wifi_audio wifiAudio){
        this.localAudio = localAudio;
        this.wifiAudio = wifiAudio;
    }

    public void startTimer(final long timerStartFrom, final String caller) {

        thisTimer = new CountDownTimer(timerStartFrom, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerTick += 1000;

                switch (caller){
                    case "localAudio": localAudio.setProgressBar(timerTick); return;
                    case "wifiAudio": wifiAudio.setProgressBar(timerTick); return;
                }

                timerstate = true;
            }

            @Override
            public void onFinish() { }

        }.start();
    }

    public void cancelTimer(){
        thisTimer.cancel();
    }

    public Integer getTimerTick() { return timerTick; }

    public void setTimerTick(int i){
        timerTick = i;
    }

    public void resumeTimer() { thisTimer.start(); }

    public void pauseTimer() { thisTimer.cancel(); }
}
