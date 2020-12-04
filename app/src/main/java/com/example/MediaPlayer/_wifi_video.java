package com.example.MediaPlayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.exoplayer2.SimpleExoPlayer;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.*;

public class _wifi_video extends AppCompatActivity {
    public ArrayList<String> xItems, folderItems;
    public static ArrayList<String> items;
    ArrayList<String> serverItems = new ArrayList<>();
    HashMap<String, String> mapSubs = new HashMap<>();
    ArrayList<String> listDataDirectory = new ArrayList<>();
    static ArrayList<String> listDataVideo = new ArrayList<>();

    ListViewAdapterVideo expVideoAdapter;
    ListViewAdapterVideo expDirectoryAdapter;
    public ArrayAdapter<String> adapter;

    public static String rootPath, videoPath, fileOut, mainDirPath, httpPath = "", username, password;
    public static int index, length;
    Boolean boolAlwaysUse, boolLocked;

    private _get_files GetFiles;
    private static _randomizer Randomize;
    TelephonyManager mgr;
    PhoneStateListener phoneStateListener;

    LinearLayout selectDatabaseBackground;
    DrawerLayout mDrawerLayout;
    FrameLayout LL_Video;
    Toolbar tb;
    TextView videoConnectStatus, videoRetryConnect;
    ListView expDirectoryListView, listView, serverListView, expVideoView, lv;
    static SimpleExoPlayer mPlayer;
    Snackbar snackbar;
    Snackbar snackbar2;
    Cursor cursor;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    EditText textInputEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.local_video);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        serverListView = new ListView(this);
        GetFiles = new _get_files();
        Randomize = new _randomizer(null, null);
        expDirectoryListView = new ListView(this);
        expVideoView = new ListView(this);
        items = new ArrayList<>();
        xItems = new ArrayList<>();
        folderItems = new ArrayList<>();
        tb = new Toolbar(this);

        videoConnectStatus = findViewById(R.id.videoConnectStatus);
        selectDatabaseBackground = findViewById(R.id.select_database_background);
        expDirectoryListView = findViewById(R.id.lvExpVideoDirectory);
        expVideoView = findViewById(R.id.listview_video_list);
        LL_Video = findViewById(R.id.LL_Video);
        lv = findViewById(R.id.local_video_listview);
        mDrawerLayout = findViewById(R.id.drawer_video_layout_main);
        tb = findViewById(R.id.local_Video_Toolbar);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (mPlayer != null){
                        //getActiveMediaPlayer().setPlayWhenReady(false);
                    }

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    if (mPlayer != null){
                        //getActiveMediaPlayer().setPlayWhenReady(true);
                    }
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    if (mPlayer != null){
                        //getActiveMediaPlayer().setPlayWhenReady(false);
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        try{

            LL_Video.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            setSupportActionBar(tb);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }catch (Exception e){
            Log.d("ERROR 2: ", e.toString());
        }

        try{


            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, folderItems);
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(adapter);
            adapter.add("Click here to select other folders");
            View headerView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            lv.addHeaderView(headerView);

            expVideoAdapter = new ListViewAdapterVideo(this,R.layout.list_item_video,R.id.lblListItemVideo,listDataVideo,
                    "video", null, this);
            expVideoView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            expVideoView.setAdapter(expVideoAdapter);

            expDirectoryAdapter = new ListViewAdapterVideo(this,R.layout.list_item_video,R.id.lblListItemVideo,listDataDirectory,
                    "", null, this);
            expDirectoryListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            expDirectoryListView.setAdapter(expDirectoryAdapter);

            getServer();


        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_video.onCreate: " + e.toString());
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        expDirectoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();

                listDataVideo.clear();
                listDataDirectory.clear();
                listDataDirectory.add("[ BACK ]");

                try{
                    if (str.equals("[ RELOAD ]") ) {
                        listDataDirectory.clear();
                        MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                        new setExpDirectoryAdapter().execute(params);
                        if(httpPath.equals("") && parent.getItemAtPosition(0).toString().equals("[ BACK ]")){
                            listDataDirectory.remove(0);
                            expDirectoryAdapter.notifyDataSetChanged();
                        }
                        return;
                    }

                    if (str.equals("[ BACK ]")) {
                        try{
                            httpPath = httpPath.substring(0, httpPath.lastIndexOf("/"));
                        }catch (Exception ex){
                            httpPath = "";
                        }

                        if(httpPath.equals("") && parent.getItemAtPosition(0).toString().equals("[ BACK ]")){
                            listDataDirectory.remove(0);
                            expDirectoryAdapter.notifyDataSetChanged();
                        }

                    } else {
                        if(httpPath.equals("")){
                            httpPath = str;
                        }else{
                            httpPath += "/" + str;
                        }
                    }

                    videoPath = rootPath + "/" + httpPath;
                    MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                    new setExpDirectoryAdapter().execute(params);
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_video.expDirectoryListView.setOnItemClickListener: " + e.toString());
                }
            }
        });

        expVideoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                Randomize.clearAlreadyPlayed();
                index = position;
                expVideoView.setSelection(index);
                Randomize.setSelected(expVideoView.getItemAtPosition(index).toString());

                expVideoAdapter.notifyDataSetChanged();

                snackbar = Snackbar.make(LL_Video, "", Snackbar.LENGTH_INDEFINITE);
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                View snackView = getLayoutInflater().inflate(R.layout.snackbar_layout, null);

                TextView textViewTwo = (TextView) snackView.findViewById(R.id.second_text_view);
                textViewTwo.setText("CONVERT");
                textViewTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        snackbar.dismiss();
                        snackbar2 = Snackbar.make(LL_Video, "", Snackbar.LENGTH_INDEFINITE);
                        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar2.getView();
                        View snackView = getLayoutInflater().inflate(R.layout.snackbar_layout_speed, null);

                        TextView textViewOne = (TextView) snackView.findViewById(R.id.normal);
                        textViewOne.setText("NORMAL");
                        textViewOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                convertVideo("normal");
                            }
                        });

                        TextView textViewTwo = (TextView) snackView.findViewById(R.id.fast);
                        textViewTwo.setText("FAST (lower quality)");
                        textViewTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                convertVideo("fast");
                            }
                        });
                        layout.addView(snackView);
                        snackbar2.show();

                    }
                });

                TextView textViewThree = (TextView) snackView.findViewById(R.id.third_text_view);
                textViewThree.setText("DELETE");
                textViewThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBox("Error: Unable to delete files from web servers.");
                        snackbar.dismiss();
                    }
                });

                layout.addView(snackView);
                snackbar.show();
                expDirectoryAdapter.notifyDataSetChanged();
            }
        });

    }

    private void convertVideo(String speed){
        snackbar.dismiss();

        Randomize.setSelected(expVideoView.getItemAtPosition(index).toString());
        String selected = Randomize.getSelected();
        String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
        if(selected.endsWith(".mp4")){
            fileOut = selected.substring(0, selected.lastIndexOf(".")) + ".mkv";
        }else{
            fileOut = selected.substring(0, selected.lastIndexOf(".")) + ".mp4";
        }

        Log.d("THIS IS THE PATH :", videoPath + "/" + Randomize.getSelected() );
        if (speed.equals("normal")) {
            new media_demux(_wifi_video.this, videoPath + "/" + Randomize.getSelected().replace(" ", "%20"), savePath + "/" + fileOut, "normal");
        }else{
            new media_demux(_wifi_video.this, videoPath + "/" + Randomize.getSelected().replace(" ", "%20"), savePath + "/" + fileOut, "fast");
        }



    }

    public void playVideo(String subs){
        try{
            snackbar.dismiss();
        }catch(Exception ex){
            Log.d("EXCEPTION: ", "_wifi_video.playVideo: " + ex.toString());
        }

        new _video_player().setSender("wifiVideo", subs.replace("\\", "/"));
        Intent homeIntent = new Intent(_wifi_video.this, _video_player.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
                Intent homeIntent = new Intent(this, MainActivityLocal.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if(mgr != null) {
                    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
                    phoneStateListener = null;
                }

                if(getActiveMediaPlayer() != null){
                    try{
                        index = 0;
                        expVideoAdapter.notifyDataSetChanged();
                        getActiveMediaPlayer().release();
                        mPlayer = null;
                    }catch (Exception e){
                        Log.d("EXCEPTION: ", "_wifi_video.onOptionsItemSelected: " + e.toString());
                    }
                }

                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public void destroy(){
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            phoneStateListener = null;
        }

        try {
            videoConnectStatus.setText("Not connected");
            videoRetryConnect.setVisibility(View.VISIBLE);
            listDataDirectory.clear();
            listDataVideo.clear();
            expDirectoryAdapter.notifyDataSetChanged();
            expVideoAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.d("ERROR 9: ", e.toString());
        }

    }

    public SimpleExoPlayer getActiveMediaPlayer() {
        if (mPlayer != null)
            return _video_player.player;
        else
            return null;
    }

    public String GetItem(){
        return videoPath + "/" + Randomize.getSelected();
    }

    public HashMap<String, String> getMapSubs(){
        return mapSubs;
    }

    public static Integer GetIndex(){
        return index;
    }

    public void dialogBox(String s) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_wifi_video.this);
        alertDialogBuilder.setMessage(s);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class CheckConnected extends Thread {
        String path;
        String caller;

        private CheckConnected(String path, String caller){
            this.path = path;
            this.caller = caller;
            this.start();
        }

        @Override
        public void run(){
            super.run();

            HttpURLConnection urlConnection;
            URL url;

            try {
                url = new URL(rootPath);
                urlConnection = (HttpURLConnection) url.openConnection();
                String ipAddress = rootPath.replace("http://", "");
                ipAddress = ipAddress.substring(0, ipAddress.indexOf("/"));
                final String status = "Connected to: " + ipAddress;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoConnectStatus.setText(status);
                    }
                });


                urlConnection.disconnect();
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogBox(e.toString());
                    }
                });
            }
        }
    }

    private static class MyTaskParams {
        String mRootPath;
        String mHttpPath;

        MyTaskParams(String mRootPath, String mHttpPath) {
            this.mRootPath = mRootPath;
            this.mHttpPath = mHttpPath;
        }
    }

    private class setExpDirectoryAdapter extends AsyncTask<MyTaskParams, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(final MyTaskParams... arg0) {

            try{
                final URL url = new URL(arg0[0].mRootPath + "/video.php?folder=" + arg0[0].mHttpPath.replace(" ", "%20"));
                mainDirPath = arg0[0].mRootPath;
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String str = in.readLine();


                while (!str.equals("")) {
                    String link = str.substring(0, str.indexOf("<>"));
                    str = str.substring(str.indexOf("<>") + 2);

                    boolean tempBool = true;
                    String strings[] = {".mkv", ".m4a", ".mp4", ".fmp4", ".webm", ".mts", ".m4v", ".3gp", ".3g2", ".flac", ".flv",
                            ".vob", ".gif", ".avi", ".mov", ".qt", ".wmv", ".yuv", ".rm", ".mpg", ".mpeg", ".ogg", ".srt"};

                    for (int x = 0; x < strings.length; x++) {
                        if (!link.toLowerCase().endsWith(strings[x])) {
                            tempBool = false;
                        } else {
                            tempBool = true;
                            break;
                        }
                    }

                    if (tempBool) {
                        final String tempStr = link.substring(link.lastIndexOf("/") + 1);
                        if(tempStr.endsWith(".srt")){

                            try{
                                mapSubs.put(tempStr.substring(tempStr.lastIndexOf("\\") + 1),
                                        arg0[0].mRootPath + tempStr.substring(tempStr.indexOf("\\"),
                                        tempStr.lastIndexOf("\\")).replace("\\", "/"));
                            }catch (Exception ex){
                                Log.d("EXCEPTION: ", "_wifi_video.setExpDirectoryAdapter: " + ex.toString());
                            }
                        }else{
                            listDataVideo.add(tempStr);
                        }

                    }else{
                        listDataDirectory.add(link);
                    }
                }

                in.close();
                urlConnection.disconnect();

            }catch (Exception e){
                Log.d("EXCEPTION: ", "_wifi_video.setExpDirectoryAdapter: " + e.toString());
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            expDirectoryAdapter.notifyDataSetChanged();
            expVideoAdapter.notifyDataSetChanged();

            if (expDirectoryListView.getCount() == 0){
                listDataDirectory.add("[ RELOAD ]");
            }

            expDirectoryAdapter.notifyDataSetChanged();
        }

    }

    private Cursor readFromDB(String sender){
        String queryString;
        if(!sender.equals("GET")){
            queryString = "SELECT * FROM server";
        }else {
            queryString = "SELECT * FROM server WHERE name = name";
        }
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        return database.rawQuery(queryString,null);
    }

    private Cursor readFromDB2(){
        String queryString = "SELECT * FROM servers";
        //String queryString = "SELECT * FROM server WHERE name = name";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        return database.rawQuery(queryString,null);
    }

    private Cursor readFromDB3() {
        String queryString = "SELECT * FROM users";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        return database.rawQuery(queryString, null);
    }

    private void setServerList () {
        alertDialogBuilder = new AlertDialog.Builder(_wifi_video.this);
        //alertDialogBuilder.setMessage("Delete Playlist? Cannot be undone");
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.database_select_server, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        listView = alertDialog.findViewById(R.id.listview_select_server);
        ArrayAdapter lstViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serverItems);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(lstViewAdapter);

        serverItems.clear();

        try {
            cursor = readFromDB2();
            boolAlwaysUse = false;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    serverItems.add(cursor.getString(0));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_wifi_video.setServerList: " + e.toString());
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    cursor = readFromDB("GET");
                    Log.d("CURSOR COUNT: ", String.valueOf(cursor.getCount()));
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            if(cursor.getString(0).equals(listView.getItemAtPosition(position).toString()))
                            {
                                rootPath = cursor.getString(2) + cursor.getString(1) + cursor.getString(7);
                                if(cursor.getInt(4) > 0){ boolLocked = true; }
                                //dialogBox(String.valueOf(boolLocked));
                            }
                            cursor.moveToNext();
                        }
                    }
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_wifi_video.setServerList.listView.setOnItemClickListener: " + e.toString());
                }

                alertDialog.dismiss();
                MyTaskParams params = new MyTaskParams(rootPath, null);
                if(boolLocked){
                    getPinNumber();
                    new setExpDirectoryAdapter().execute(params);
                }else{
                    new setExpDirectoryAdapter().execute(params);
                }
            }
        });

        lstViewAdapter.notifyDataSetChanged();
    }


    private Boolean getServer () {

        try {
            cursor = readFromDB("");
            boolAlwaysUse = false;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (cursor.getInt(9) > 0) {
                        boolAlwaysUse = true;
                        if (cursor.getInt(4) > 0) {
                            boolLocked = true;
                        }
                        break;
                    } else {
                        boolAlwaysUse = false;
                    }
                    cursor.moveToNext();
                }
                if (boolAlwaysUse) {
                    if (boolLocked) {
                        getPinNumber();
                    }
                    rootPath = cursor.getString(2) + cursor.getString(1) + cursor.getString(7);
                    length = rootPath.length();
                    videoPath = rootPath;
                    MyTaskParams params = new MyTaskParams(rootPath, null);
                    new CheckConnected(rootPath, "");
                    new setExpDirectoryAdapter().execute(params);


                } else {
                    setServerList();
                }
            }
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_wifi_video.getServer: " + e.toString());
        }

        return true;
    }

    public void getPinNumber () {
        alertDialogBuilder = new AlertDialog.Builder(_wifi_video.this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(inflater.inflate(R.layout.database_pin_number, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        textInputEditText = alertDialog.findViewById(R.id.pinNumberEditText);
        Button btn_ok = alertDialog.findViewById(R.id.BTN_PIN_OK);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    cursor = readFromDB3();
                    boolAlwaysUse = false;
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            if (cursor.getString(2).equals(textInputEditText.getText().toString())) {
                                username = cursor.getString(0);
                                password = cursor.getString(1);
                            }
                            cursor.moveToNext();
                        }
                    }

                    if (textInputEditText.getText().toString().equals("")) {
                        username = "";
                        password = "";
                    }

                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password.toCharArray());
                        }
                    });
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_video.GetPinNumber.btn_ok.setOnClickListener: " + e.toString());
                }
                alertDialog.dismiss();
            }
        });

    }

    public void setVideo ( final String songPath){

        listDataVideo.clear();
        xItems.clear();

        xItems.addAll(GetFiles.getFiles(songPath));

        if (xItems.size() == 0) {
            expVideoAdapter.notifyDataSetChanged();
            return;
        }

        File file;

        try {
            Collections.sort(xItems, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            for (String tempFile : xItems) {

                if (!tempFile.startsWith(".")) {

                    file = new File(songPath + "/" + tempFile);

                    if (!file.isDirectory()) {

                        boolean tempBool = true;
                        String str[] = {".mkv", ".m4a", ".mp4", ".fmp4", ".webm", ".mts", ".m4v", ".3gp", ".3g2", ".flac", ".flv",
                                ".vob", ".gif", ".avi", ".mov", ".qt", ".wmv", ".yuv", ".rm", ".mpg", ".mpeg", ".ogg"};
                        for (int x = 0; x < str.length; x++) {

                            if (!file.getName().toLowerCase().endsWith(str[x])) {
                                tempBool = false;
                            } else {
                                tempBool = true;
                                break;
                            }
                        }
                        if (tempBool) {
                            listDataVideo.add(tempFile);
                        }
                    }
                }
            }

            expVideoView.setAdapter(expVideoAdapter);
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_wifi_video.setVideo: " + e.toString());
        }
    }
}

