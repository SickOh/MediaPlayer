package com.example.newmusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class media_demux extends AppCompatActivity{
    TextView textView;
    AlertDialog alertDialog;
    FFmpeg ffmpeg;
    public media_demux(final Context context, String fileIn, final String fileOut, String speed){

        final _local_video local_video = new _local_video();



        ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }



        try {
            //FileOutputStream fileOutputStream = new FileOutputStream(fileOut);

            String[] cmd;

            if (speed.equals("normal")) {
                cmd = new String[]{"-y", "-i", fileIn, "-c:a", "aac", fileOut };
            }else{
                cmd = new String[]{"-y", "-i", fileIn, "-preset", "ultrafast", "-c:a", "aac", fileOut};
            }


            dialogBox(context, "Status...");

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {


                }

                @Override
                public void onProgress(String message) {

                    textView.setText(message);
                    Log.d("INFO: ", message);
                }

                @Override
                public void onFailure(String message) {}

                @Override
                public void onSuccess(String message) {}

                @Override
                public void onFinish() {
                    alertDialog.dismiss(); //local_video.notifyAdapter();

                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            textView.setText("File conversion in progress, please wait until the previous task is finished.");
        }




    }

    public void dialogBox(Context context, String s) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(s);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = new View(context);

        layout = inflater.inflate(R.layout.popup_text,null);
        layout.findViewById(R.id.layout_text_root);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setCancelable(false);
        textView = new TextView(context);
        textView = layout.findViewById(R.id.fulltext);
        alertDialogBuilder.setNegativeButton("STOP CONVERTING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ffmpeg.killRunningProcesses();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
