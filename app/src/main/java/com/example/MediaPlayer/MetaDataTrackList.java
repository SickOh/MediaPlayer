package com.example.MediaPlayer;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MetaDataTrackList extends Thread{

    String args, line;
    URL url;

    public MetaDataTrackList(String args){
        this.args = args;
        this.start();
    }

    @Override
    public void run(){
        super.run();
        try {
            //args = args.replace(".", " ").replace(" ", "%20");
            url = new URL(args);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //Log.i("TITLE: ", title);

            /*

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("timestamp", 1488873360);
            jsonParam.put("uname", message.getUser());
            jsonParam.put("message", message.getMessage());
            jsonParam.put("latitude", 0D);
            jsonParam.put("longitude", 0D);
*/
            //Log.i("JSON", jsonParam.toString());


            //DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            line = bufferedReader.readLine();
/*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogBox(line);
                }
            });

 */

            //os.writeBytes(jsonParam.toString());

            //os.flush();
            //os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());
            Log.i("OUTPUT: ", args);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String GetOutput(){
        try{
            this.join();
        }catch(Exception ex) { }

        return line;
    }
}
