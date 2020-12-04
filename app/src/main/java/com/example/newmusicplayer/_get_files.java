package com.example.newmusicplayer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class _get_files extends AppCompatActivity {
    ArrayList<String> lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                3);

    }

    public ArrayList<String> getFiles(String path){
        File iPath = new File(path);
        lines = new ArrayList<>();

        for (File file : iPath.listFiles()) {
            lines.add(file.getName());
        }
        return lines;
    }








        //try{

        //    if (dir.canRead() == true) {
        //        try {
        //            listFile = dir.listFiles();
        //            for (int i = 0; i < listFile.length; i++) {
        //                if (listFile[i].isDirectory()) {
        //                    fileList.add(listFile[i]);
        //                    file(listFile[i]);
        //                } else {
        //                    fileList.add(listFile[i]);
        //                }
        //            }
        //        } catch (Exception e) {
        //            //        ma.dialogBox(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString());
        //        }
        //    } else {
        //        //dialogBox("here");
        //    }
        //    alreadyPlayed = new ArrayList<>(fileList.size());

        //}catch (Exception e){
            //dialogBox(e.toString());
        //}
       //return listFile;
    //}

    public void dialogBox(String s) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}