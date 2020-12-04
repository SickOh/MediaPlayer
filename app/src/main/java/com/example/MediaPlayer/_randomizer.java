package com.example.MediaPlayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class _randomizer{
    private static int num;
    private static String selected;
    private static List<Integer> alreadyPlayed = new ArrayList<>();
    private _local_audio localAudio;
    private _wifi_audio wifiAudio;



    public _randomizer (_local_audio localAudio, _wifi_audio wifiAudio){
        this.localAudio = localAudio;
        this.wifiAudio = wifiAudio;
    }

    public void randomize(String type){

        int iTest;
        boolean playable = true;
        Random r = new Random();

        switch (type){
            case "localAudio":
                num = r.nextInt(localAudio.GetFileListSize());
                selected = localAudio.GetItem();
                break;
            case "wifiAudio":
                num = r.nextInt(wifiAudio.GetFileListSize());
                selected = wifiAudio.GetItem();
        }


        try{
            for (int i = 0; i < alreadyPlayed.size(); i++) {
                iTest = alreadyPlayed.get(i);
                if (iTest == num) {
                    playable = false;
                    break;
                } else {
                    playable = true;
                }
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_randomizer.randomize");
        }
        try{
            switch (type){
                case "localAudio":
                    if(localAudio.GetIndex() != 0){
                        if (playable){
                            alreadyPlayed.add(num);
                        }else{
                            randomize("localAudio");
                        }
                    }else{
                        playable = true;
                        alreadyPlayed.add(num);
                    }
                    break;
                case "wifiAudio":
                    if(wifiAudio.GetIndex() != 0){
                        if (playable){
                            alreadyPlayed.add(num);
                        }else{
                            randomize("wifiAudio");
                        }
                    }else{
                        playable = true;
                        alreadyPlayed.add(num);
                    }
            }
        }catch (Exception e) {
            Log.d("EXCEPTION: ", "_randomizer.randomize");
        }
    }

    public Integer GetNum(){
        return num;
    }

    public String getSelected(){
        return selected;
    }

    public void setSelected(String s){ selected = s; }

    public void clearAlreadyPlayed(){
        try{
            alreadyPlayed.clear();
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_randomizer.clearAlreadyPlayed");
        }
    }
}
