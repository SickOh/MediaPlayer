package com.example.MediaPlayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.exoplayer2.SimpleExoPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class _local_video extends AppCompatActivity {


    public ArrayList<String> xItems, folderItems;
    public static ArrayList<String> items;
    ArrayList<String> listDataDirectory = new ArrayList<>();
    static ArrayList<String> listDataVideo = new ArrayList<>();

    static ListViewAdapterVideo expVideoAdapter;
    static ListViewAdapterVideo expDirectoryAdapter;
    public ArrayAdapter<String> adapter;

    public static String rootPath, videoPath, fileOut, mainDirPath;
    public static int index, length;

    public ListView lv;
    TelephonyManager mgr;
    PhoneStateListener phoneStateListener;
    DrawerLayout mDrawerLayout;
    FrameLayout LL_Video;
    Toolbar tb;
    ListView expDirectoryListView;
    ListView expVideoView;
    static SimpleExoPlayer mPlayer;
    Snackbar snackbar;
    Snackbar snackbar2;

    private _get_files GetFiles;
    private static _randomizer Randomize;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.local_video);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        GetFiles = new _get_files();
        Randomize = new _randomizer(null, null);
        expDirectoryListView = new ListView(this);
        expVideoView = new ListView(this);
        items = new ArrayList<>();
        xItems = new ArrayList<>();
        folderItems = new ArrayList<>();
        tb = new Toolbar(this);

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
            dialogBox(e.toString());
        }

        try{


            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, folderItems);
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(adapter);
            adapter.add("Click here to select other folders");
            View headerView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            lv.addHeaderView(headerView);

            mainDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
            rootPath = mainDirPath;
            videoPath = rootPath;
            length = rootPath.length();

            expVideoAdapter = new ListViewAdapterVideo(this,R.layout.list_item_video,R.id.lblListItemVideo,listDataVideo,
                    "localVideo", this, null);
            expVideoView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setVideo(rootPath);
            expVideoView.setAdapter(expVideoAdapter);

            expDirectoryAdapter = new ListViewAdapterVideo(this,R.layout.list_item_video,R.id.lblListItemVideo,listDataDirectory,
                    "", this, null);
            expDirectoryListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            setExpDirectoryAdapter(rootPath, false);
            expDirectoryListView.setAdapter(expDirectoryAdapter);

        }catch (Exception e){
            dialogBox(e.toString());
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
                listDataDirectory.clear();
                expVideoAdapter.notifyDataSetChanged();
                listDataDirectory.add("[ BACK ]");

                try{


                    if (str.equals("[ BACK ]")) {
                        rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
                    } else {
                        rootPath += "/" + str;
                    }

                    if (rootPath.length() == length) {
                        listDataDirectory.remove(0);
                    }

                    if(rootPath.length() == length && expVideoView.getCount() == 0){
                        expDirectoryAdapter.notifyDataSetChanged();
                        expVideoView.setSelected(false);
                        expVideoAdapter.add("[ RELOAD ]");
                        setVideo(rootPath);
                    }

                    videoPath = rootPath;
                    setVideo(rootPath);
                    setExpDirectoryAdapter(rootPath, false);
                    expVideoView.setActivated(false);
                    expDirectoryAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_local_video.expDirectoryListView.setOnItemClickListener: " + e.toString());
                }
            }
        });


        expVideoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                if(expVideoView.getItemAtPosition(position).toString().equals("[ RELOAD ]")){
                    setVideo(rootPath);
                }

                try{
                    getActiveMediaPlayer().release();
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_video.expVideoView.setOnItemClickListener: " + e.toString());
                }


                Randomize.clearAlreadyPlayed();
                index = 0;

                index = position;

                expVideoView.setSelection(index);

                Randomize.setSelected(expVideoView.getItemAtPosition(index).toString());

                expVideoAdapter.notifyDataSetChanged();

                snackbar = Snackbar.make(LL_Video, "", Snackbar.LENGTH_LONG);
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                View snackView = getLayoutInflater().inflate(R.layout.snackbar_layout, null);

                TextView textViewTwo = (TextView) snackView.findViewById(R.id.second_text_view);
                textViewTwo.setText("CONVERT");
                textViewTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //convertVideo("normal");

                        snackbar.dismiss();
                        snackbar2 = Snackbar.make(LL_Video, "", Snackbar.LENGTH_LONG);
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
                        dialogBox("Confirm delete...");
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
        String selected = Randomize.getSelected().replace(" ", "'\\' ");
        if(selected.endsWith(".mp4")){
            fileOut = Randomize.getSelected().replace(" ", "'\\' ").substring(0, selected.lastIndexOf(".")) + ".mkv";
        }else{
            fileOut = Randomize.getSelected().replace(" ", "'\\' ").substring(0, selected.lastIndexOf(".")) + ".mp4";
        }

        if (speed.equals("normal")) {
            new media_demux(_local_video.this, videoPath + "/" + Randomize.getSelected().replace(" ", "%20"), videoPath + "/" + fileOut, "normal");
        }else{
            new media_demux(_local_video.this, videoPath + "/" + Randomize.getSelected().replace(" ", "%20"), videoPath + "/" + fileOut, "fast");
        }



    }

    public void playVideo(String subs){
        try{
            snackbar.dismiss();
        }catch(Exception ex){}



        _video_player.setSender("localVideo", subs);
        Intent homeIntent = new Intent(_local_video.this, _video_player.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent homeIntent = new Intent(this, MainActivityLocal.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public void setVideo(final String songPath){

        listDataVideo.clear();
        xItems.clear();

        xItems.addAll(GetFiles.getFiles(songPath));

        if(xItems.size() == 0){
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

            for (String tempFile : xItems){

                if(!tempFile.startsWith(".")){

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
        } catch (Exception e) { }
    }


    private void Destroy(){
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
                Log.d("EXCEPTION: ", "_local_video.Destroy: " + e.toString());
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Destroy();
    }

    public int setExpDirectoryAdapter(String path, Boolean boolTest){

        File maindir = new File(path);
        ArrayList<String> tempArrayList = new ArrayList<>();
        ArrayList<String> testArrayList = new ArrayList<>();

        if (maindir.listFiles().length != 0){
            File arr[] = maindir.listFiles();

            for (int i = 0; i < arr.length; i++) {
                if (arr[i].isDirectory()) {
                    if(!boolTest){
                        tempArrayList.add(arr[i].getName());
                    }else{
                        testArrayList.add(arr[i].getName());
                    }
                }
            }
            Collections.sort(tempArrayList);
            listDataDirectory.addAll(tempArrayList);
        }
        expDirectoryAdapter.notifyDataSetChanged();
        return testArrayList.size();
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

    public String GetRootPath() { return mainDirPath; }

    public static Integer GetIndex(){
        return index;
    }

    public void dialogBox(String s) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_local_video.this);
        alertDialogBuilder.setMessage(s);
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listDataVideo.remove(index);
                expVideoAdapter.notifyDataSetChanged();

                File file = new File(rootPath + "/" + Randomize.getSelected());
                if(file.exists()){file.delete();}

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
