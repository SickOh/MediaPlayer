package com.example.newmusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.*;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.ads.AdView;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class _wifi_audio extends AppCompatActivity {
    public ArrayList<String> xItems, folderItems;
    ArrayList<String> serverItems = new ArrayList<>();
    public static ArrayList<String> items;
    public ArrayList<String> listDataDirectory = new ArrayList<>();
    ArrayList<String> listDataSongs = new ArrayList<>();
    ArrayList<String> tempArrayList = new ArrayList<>();
    HashMap<String, String> playlistMap = new HashMap<>();
    boolean boolLocked = false;

    ListViewAdapterAudio expSongAdapter;
    public ListViewAdapterAudio expDirectoryAdapter;
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> viewPlaylistAdapter;
    ListViewAdapterPlaylist playlistAdapter;

    int newSeekBarProgress, seekBarCurrentPosition, length, repeat, i;
    final int REPEAT_OFF = 0, REPEAT_ONE = 1, REPEAT_ALL = 2;
    String songPath, rootPath, playlistName, mainDirPath, httpPath = "", username, password;
    public int index;
    public Boolean boolShuffle = false, boolPlaylistLoaded = false;

    private _timer Timer;
    private _randomizer Randomize;
    TelephonyManager mgr;
    PhoneStateListener phoneStateListener;

    DrawerLayout mDrawerLayout;
    FrameLayout LL_Audio;
    RelativeLayout rl_lockscreen;
    Button btn_play, btn_pause, btn_previous, btn_next, btn_repeat, btn_shuffle, btn_playlist;
    Toolbar tb;
    View transparentView, durationView;
    ListView expDirectoryListView;
    ListView expSongView;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    Cursor cursor;
    TextInputEditText textInputEditText;
    private AdView mAdView;
    MediaPlayer mPlayer;
    LinearLayout playButtonBackground, pauseButtonBackground;
    TextView connectStatus;
    Boolean boolAlwaysUse;
    AlertDialog dialogProgress;
    ScreenOnOffReceiver screenOnOffReceiver;
    File file;
    ListView listView;
    FileOutputStream os;
    SeekBar pb;
    public ListView lv;
    float dX, dY = 0;
    private TextView textDuration;
    HttpURLConnection urlConnection;

    Thread thread;




    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.local_audio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        /*
        try{
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView = findViewById(R.id.adView);

            mAdView.loadAd(adRequest);
        }catch(Exception e){
            dialogBox(e.toString());
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        */

        Randomize = new _randomizer(null, this);
        Timer = new _timer(null, this);
        mPlayer = new MediaPlayer();
        items = new ArrayList<>();
        xItems = new ArrayList<>();
        folderItems = new ArrayList<>();
        transparentView = new View(this);
        durationView = new View(this);
        connectStatus = new TextView(this);
        mPlayer = new MediaPlayer();

        expDirectoryListView = findViewById(R.id.lvExpAudioDirectory);
        expSongView = findViewById(R.id.listview_song_list);
        playButtonBackground = findViewById(R.id.LL_play_btn);
        pauseButtonBackground = findViewById(R.id.LL_pause_btn);
        LL_Audio = findViewById(R.id.LL_Audio);
        tb = findViewById(R.id.Local_Audio_Toolbar);
        lv = findViewById(R.id.local_audio_listview);
        pb = findViewById(R.id.progressBar);
        btn_shuffle = findViewById(R.id.BTN_SHUFFLE);
        btn_playlist = findViewById(R.id.BTN_PLAYLIST);
        btn_play = findViewById(R.id.BTN_PLAY);
        btn_pause = findViewById(R.id.BTN_PAUSE);
        btn_previous = findViewById(R.id.BTN_PREVIOUS);
        btn_next = findViewById(R.id.BTN_NEXT);
        btn_repeat = findViewById(R.id.BTN_REPEAT);
        mDrawerLayout = findViewById(R.id.drawer_layout_main);
        connectStatus = findViewById(R.id.connectStatus);

        screenOnOffReceiver = new ScreenOnOffReceiver();
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnOffReceiver, screenStateFilter);

        /*
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (mPlayer != null) {
                        getActiveMediaPlayer().pause();
                        btn_play.setEnabled(true);
                        playButtonBackground.setAlpha(0f);
                        btn_pause.setEnabled(false);
                        pauseButtonBackground.setAlpha(0.2f);
                        Timer.setTimerTick(getActiveMediaPlayer().getCurrentPosition());
                        Timer.pauseTimer();
                    }

                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    if (mPlayer != null) {
                        btn_pause.setEnabled(true);
                        pauseButtonBackground.setAlpha(0f);
                        btn_play.setEnabled(false);
                        playButtonBackground.setAlpha(0.2f);
                        getActiveMediaPlayer().seekTo(Timer.getTimerTick());
                        Timer.resumeTimer();
                        getActiveMediaPlayer().start();
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    if (mPlayer != null) {
                        getActiveMediaPlayer().pause();
                        btn_play.setEnabled(true);
                        playButtonBackground.setAlpha(0f);
                        btn_pause.setEnabled(false);
                        pauseButtonBackground.setAlpha(0.2f);
                        Timer.setTimerTick(getActiveMediaPlayer().getCurrentPosition());
                        Timer.pauseTimer();
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

         */

        try{
            btn_play.setEnabled(false);
            btn_pause.setEnabled(false);
            pb.setProgress(0);
            setSupportActionBar(tb);

            LL_Audio.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            tb.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_audio.onCreate: " + e.toString());
        }

        try{
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, folderItems);
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(adapter);
            adapter.add("Click here to select other folders");
            View headerView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            lv.addHeaderView(headerView);

            expSongAdapter = new ListViewAdapterAudio(this,R.layout.list_item_audio,R.id.lblListItemAudio,listDataSongs,
                    "wifiSong", null,null, this);
            expSongView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            //new setSongs().execute(rootPath);
            expSongView.setAdapter(expSongAdapter);

            expDirectoryAdapter = new ListViewAdapterAudio(this,R.layout.list_item_audio,R.id.lblListItemAudio,listDataDirectory,
                    "", null, null, this);
            expDirectoryListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            //new setExpDirectoryAdapter().execute(rootPath);
            expDirectoryListView.setAdapter(expDirectoryAdapter);

            getServer();

        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_audio.onCreate: " + e.toString());
        }

        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(boolShuffle){
                    boolShuffle = false;
                    index = Randomize.GetNum();
                    btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle);
                }else{
                    try{
                        repeat = REPEAT_OFF;
                        boolShuffle = true;
                        Randomize.clearAlreadyPlayed();
                        index = 0;
                        btn_repeat.setBackgroundResource(R.drawable.repeat);
                        btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle_green);
                    }catch (Exception e){
                        Log.d("EXCEPTION: ", "_wifi_audio.cbShuffle.setOnCheckedChangeListener.onCheckedChanged");
                    }
                    mPlayerListener();
                }

                getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayerListener();
                    }
                });
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        pb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null){
                    pb.setProgress(progress);
                    Timer.setTimerTick(progress);
                    newSeekBarProgress = progress;

                    long hours = TimeUnit.MILLISECONDS.toHours(progress);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(progress);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(progress);

                    String str = String.format("%02d:%02d", minutes,
                            seconds  - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress)));

                    setDuration(str);
                }else{
                    pb.setProgress(0);
                }

                getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayerListener();
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(mPlayer == null){ return; }

                LayoutInflater inflater = (LayoutInflater) _wifi_audio.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                transparentView = inflater.inflate(R.layout.transparent_background, null);
                LL_Audio.addView(transparentView);

                durationView = inflater.inflate(R.layout.seekbar_seek_to_position, null);
                LL_Audio.addView(durationView);

                textDuration = new TextView(_wifi_audio.this);
                textDuration = findViewById(R.id.textDuration);

                seekBarCurrentPosition = seekBar.getMeasuredWidth() * seekBar.getProgress() / seekBar.getMax() - seekBar.getThumbOffset();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(mPlayer != null){
                    try{
                        LL_Audio.removeView(durationView);
                        LL_Audio.removeView(transparentView);
                    }catch (Exception e){
                        Log.d("EXCEPTION: ", "_wifi_audio.pb.setOnSeekBarChangeListener.onStopTrackingTouch");
                    }

                    getActiveMediaPlayer().seekTo(newSeekBarProgress);

                    getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayerListener();
                        }
                    });
                }else{
                    pb.setProgress(0);
                }
            }
        });


        expDirectoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = parent.getItemAtPosition(position).toString();
                boolPlaylistLoaded = false;
                listDataSongs.clear();
                listDataDirectory.clear();
                listDataDirectory.add("[ BACK ]");
                btn_playlist.setBackgroundResource(R.drawable.view_playlist_grey);
                ResetMPlayer();

                try{
                    if (str.equals("[ RELOAD ]") ) {
                        listDataDirectory.clear();
                        //dialogBox(rootPath + " : " + httpPath);
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

                    songPath = rootPath + "/" + httpPath;
                    MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                    new setExpDirectoryAdapter().execute(params);
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_audio.expDirectoryListView.setOnItemClickListener: " + e.toString());
                }
            }
        });

        expSongView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                Randomize.clearAlreadyPlayed();
                index = 0;
                boolShuffle = false;
                btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle);

                try {
                    getActiveMediaPlayer().stop();
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_audio.expSongView.setOnItemClickListener.onItemClick");
                }

                index = position;
                expSongView.setSelection(index);

                Randomize.setSelected(expSongView.getItemAtPosition(index).toString());
                startMPlayer();

                getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayerListener();
                    }
                });
            }
        });



        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (index != 0) {
                    boolShuffle = false;
                    btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle);
                    btn_pause.setEnabled(true);
                    btn_play.setEnabled(false);
                    index--;
                    Randomize.setSelected(expSongView.getItemAtPosition(index).toString());
                    startMPlayer();

                    getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayerListener();
                        }
                    });
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPlayer == null){ return; }

                mPlayerListener();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_pause.setEnabled(true);
                pauseButtonBackground.setAlpha(0f);
                btn_play.setEnabled(false);
                playButtonBackground.setAlpha(0.2f);

                getActiveMediaPlayer().seekTo(Timer.getTimerTick());
                Timer.resumeTimer();
                getActiveMediaPlayer().start();

                getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayerListener();
                    }
                });
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getActiveMediaPlayer() != null) {
                    getActiveMediaPlayer().pause();

                    btn_play.setEnabled(true);
                    playButtonBackground.setAlpha(0f);
                    btn_pause.setEnabled(false);
                    pauseButtonBackground.setAlpha(0.2f);
                    Timer.setTimerTick(getActiveMediaPlayer().getCurrentPosition());
                    Timer.pauseTimer();
                }
            }
        });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_repeat.getBackground().getConstantState() == getResources().getDrawable(R.drawable.repeat, null).getConstantState()){
                    btn_repeat.setBackgroundResource(R.drawable.repeat_one_green);
                    repeat = REPEAT_ONE;
                }else if(btn_repeat.getBackground().getConstantState() == getResources().getDrawable(R.drawable.repeat_one_green, null).getConstantState()){
                    btn_repeat.setBackgroundResource(R.drawable.repeat_all_green);
                    repeat = REPEAT_ALL;
                }else if(btn_repeat.getBackground().getConstantState() == getResources().getDrawable(R.drawable.repeat_all_green, null).getConstantState()){
                    btn_repeat.setBackgroundResource(R.drawable.repeat);
                    repeat = REPEAT_OFF;
                }

                getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayerListener();
                    }
                });
            }
        });

        btn_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPlaylist();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent homeIntent = new Intent(this, MainActivityServer.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            phoneStateListener = null;
        }

        if(getActiveMediaPlayer() != null){
            try{

                //connectTimer.cancel();
                listDataDirectory.clear();
                listDataSongs.clear();
                expDirectoryAdapter.notifyDataSetChanged();
                expSongAdapter.notifyDataSetChanged();
                index = 0;
                btn_next.setEnabled(false);
                btn_pause.setEnabled(false);
                btn_play.setEnabled(false);
                btn_previous.setEnabled(false);
                pauseButtonBackground.setAlpha(0f);
                playButtonBackground.setAlpha(0f);
                pb.setProgress(0);

                unregisterReceiver(screenOnOffReceiver);
            }catch (Exception e){
                Log.d("EXCEPTION: ", "_wifi_audio.onDestroy: " + e.toString());
            }
            if(mPlayer.isPlaying()){
                mPlayer.stop();
                mPlayer.reset();
                Timer.setTimerTick(0);
                Timer.cancelTimer();
                mPlayer.release();
                mPlayer = null;
            }
        }
    }

    public MediaPlayer getActiveMediaPlayer() {
        if (mPlayer != null)
            return this.mPlayer;
        else
            return null;
    }

    public String GetItem(){
        return expSongView.getItemAtPosition(Randomize.GetNum()).toString();
    }

    public Integer GetFileListSize(){
        return listDataSongs.size();
    }

    public Integer GetIndex(){
        return index;
    }

    public Boolean GetShuffleState(){
        return boolShuffle;
    }

    public void setProgressBar(int i){
        pb.setProgress(i);
    }

    public void setDuration(String s){
        try{
            textDuration.setText(s);
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_audio.setDuration");
        }
    }

    public void mPlayerListener(){

        if (boolShuffle) {
            if(repeat == REPEAT_ONE){
                expSongView.setSelection(index);
                startMPlayer();
                return;
            }

            if (index < listDataSongs.size()) {
                Randomize.randomize("wifiAudio");
                index++;
                startMPlayer();
            } else {
                if(repeat == REPEAT_ALL){
                    index = 0;
                    Randomize.clearAlreadyPlayed();
                    mPlayerListener();
                    return;
                }

                try {
                    Timer.cancelTimer();
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_audio.mPlayerListener");
                }
                ResetMPlayer();
                dialogBox("End of file list");
            }
        }else{
            if(repeat == REPEAT_ONE){
                expSongView.setSelection(index);
                startMPlayer();
                return;
            }

            index++;

            if (index < listDataSongs.size()) {
                Randomize.setSelected(expSongView.getItemAtPosition(index).toString());
                startMPlayer();

            } else {
                if(repeat == REPEAT_ALL){
                    index = -1;
                    mPlayerListener();
                    return;
                }
                try {
                    Timer.cancelTimer();
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_audio.mPlayerListener: " + e.toString());
                }
                ResetMPlayer();
                dialogBox("End of file list");
            }
        }
    }



    private void startMPlayer(){
        try {
            Timer.cancelTimer();
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_wifi_audio.startMPlayer");
        }

        try {
            getActiveMediaPlayer().stop();
            getActiveMediaPlayer().reset();

            if(boolPlaylistLoaded){
                getActiveMediaPlayer().setDataSource(playlistMap.get(Randomize.getSelected()) + "/" + Randomize.getSelected());
            }else{
                getActiveMediaPlayer().setDataSource(songPath + "/" + Randomize.getSelected());
            }

            turnOnScreen();

            getActiveMediaPlayer().prepare();
            getActiveMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    getActiveMediaPlayer().start();
                }
            });

            expSongView.setActivated(true);
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_wifi_audio.startMPlayer: " + e.toString());
        }

        Timer.startTimer(getActiveMediaPlayer().getDuration(), "wifiAudio");
        Timer.setTimerTick(0);
        pb.setMax(getActiveMediaPlayer().getDuration());
        pb.setProgress(0);
        expSongAdapter.notifyDataSetChanged();
        if(boolShuffle){
            expSongView.smoothScrollToPosition(Randomize.GetNum());
        }else{
            expSongView.smoothScrollToPosition(index);
        }
        btn_pause.setEnabled(true);
        btn_play.setEnabled(false);
        pauseButtonBackground.setAlpha(0.0f);
        playButtonBackground.setAlpha(0.2f);
    }

    private void ResetMPlayer(){
        try{
            getActiveMediaPlayer().stop();
            getActiveMediaPlayer().reset();
            pb.setProgress(0);
            btn_pause.setEnabled(false);
            btn_play.setEnabled(false);
            playButtonBackground.setAlpha(0);
            pauseButtonBackground.setAlpha(0);
            Timer.cancelTimer();
        }catch (Exception ex){
            Log.d("EXCEPTION: ", "_wifi_audio.ResetMPlayer: " + ex.toString());
        }
    }

    private void LoadPlaylistDialog(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.audio_playlist, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        final TextInputLayout textInputLayout = alertDialog.findViewById(R.id.PlaylistNameInputLayout);
        textInputEditText = alertDialog.findViewById(R.id.PlaylistNameEditText);
        Button btn_add_playlist = alertDialog.findViewById(R.id.BTN_ADD_PLAYLIST);

        btn_add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tempArrayList.contains(textInputLayout.getEditText().getText().toString())){
                    dialogBox("That playlist name already exists");
                }else if(textInputLayout.getEditText().getText().toString().equals("")) {
                    dialogBox("Please enter a valid playlist name");
                }else{
                    InsertIntoDB();
                    textInputEditText.getText().clear();
                    LoadPlaylistListView("playlist", "*","audioclass", "_wifi_audio", "");
                    playlistAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void LoadPlaylist(){
        LoadPlaylistDialog();

        final ListView playlistListView = alertDialog.findViewById(R.id.listview_playlist);
        playlistAdapter = new ListViewAdapterPlaylist(this,R.layout.list_item_audio,R.id.lblListItemAudio,tempArrayList,
                "playlist",null, this);
        playlistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        playlistListView.setAdapter(playlistAdapter);

        LoadPlaylistListView("playlist", "*","audioclass", "_wifi_audio", "");
        playlistAdapter.notifyDataSetChanged();
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolPlaylistLoaded = true;
                try{
                    ResetMPlayer();
                    LoadPlaylistSongs(playlistListView.getItemAtPosition(position).toString());
                    Timer.cancelTimer();
                }catch(Exception ex){
                    Log.d("EXCEPTION: ", "_wifi_audio.LoadPlaylist: " + ex.toString());
                }

            }
        });
    }

    private void LoadPlaylistSongs(String args){
        listDataSongs.clear();
        playlistMap.clear();
        btn_playlist.setBackgroundResource(R.drawable.view_playlist_green);
        LoadPlaylistListView("playlistsong", "*","playlistname", args, "loadPlaylist");
        expSongAdapter.notifyDataSetChanged();
        alertDialog.dismiss();
    }

    public void AddToPlaylist(){

        LoadPlaylistDialog();

        final ListView playlistListView = alertDialog.findViewById(R.id.listview_playlist);
        playlistAdapter = new ListViewAdapterPlaylist(this,R.layout.list_item_audio,R.id.lblListItemAudio,tempArrayList,
                "playlist",null, this);
        playlistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        playlistListView.setAdapter(playlistAdapter);

        LoadPlaylistListView("playlist", "*","audioclass", "_wifi_audio", "");
        playlistAdapter.notifyDataSetChanged();

        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InsertIntoPlaylistDB(playlistListView.getItemAtPosition(position).toString(),
                        expSongView.getItemAtPosition(index).toString(),
                        songPath);
            }
        });
    }

    public void ViewPlaylist(final String args){
        playlistName = args;

        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.audio_view_playlist, null);
        alertDialogBuilder.setView(layout);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        ListView playlistListView = alertDialog.findViewById(R.id.listview_playlist);

        viewPlaylistAdapter = new ListViewAdapterPlaylist(this,R.layout.list_item_audio,R.id.lblListItemAudio,tempArrayList,
                "playlistView",null, this);
        playlistListView.setAdapter(viewPlaylistAdapter);

        LoadPlaylistListView("playlistsong", "*","playlistname", args, "");
        viewPlaylistAdapter.notifyDataSetChanged();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LoadPlaylistListView("playlist", "*","audioclass", "_wifi_audio", "");
                playlistAdapter.notifyDataSetChanged();
            }
        });
    }

    private void DeleteFromDB(String table, String column, String args){
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getWritableDatabase();
        database.execSQL("DELETE FROM " + table + " WHERE " + column + " = '" + args + "'");
        database.close();
    }

    private void InsertIntoPlaylistDB(String playlistName, String songName, String songLocation){
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseServerInfo.Server.COLUMN_PLAYLIST_NAME, playlistName);
        values.put(DatabaseServerInfo.Server.COLUMN_PLAYLIST_SONG_NAME, songName);
        values.put(DatabaseServerInfo.Server.COLUMN_PLAYLIST_SONG_LOCATION, songLocation);
        values.put(DatabaseServerInfo.Server.COLUMN_AUDIO_CLASS, "_wifi_audio");
        database.insert(DatabaseServerInfo.Server.TABLE_NAME_PLAYLIST_SONG, null, values);
        database.close();
    }

    private void InsertIntoDB(){
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseServerInfo.Server.COLUMN_AUDIO_CLASS, "_wifi_audio");
        values.put(DatabaseServerInfo.Server.COLUMN_PLAYLIST_NAME, textInputEditText.getText().toString());
        database.insert(DatabaseServerInfo.Server.TABLE_NAME_PLAYLIST, null, values);
        database.close();
    }

    private Cursor ReadFromDBPlaylist(String table, String selectColumn, String column, String arg, String column2, String arg2){
        String queryString = "SELECT " + selectColumn + " FROM " + table + " WHERE " + column + " = ? AND " + column2 + " = ?";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        String[] args = new String[]{ arg, arg2 };
        return database.rawQuery(queryString, args);
    }

    private void LoadPlaylistListView(String table, String selectColumn, String column, String arg, String type){

        tempArrayList.clear();

        try{
            cursor = ReadFromDBPlaylist(table, selectColumn, column, arg, "audioclass", "_wifi_audio");

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    if(type.equals("loadPlaylist") && boolPlaylistLoaded){
                        playlistMap.put(cursor.getString(2), cursor.getString(3));
                        listDataSongs.add(cursor.getString(2));
                    }

                    tempArrayList.add(cursor.getString(2));

                    cursor.moveToNext();
                }
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_audio.LoadPlaylistListView: " + e.toString());
        }
    }

    public void DeletePlaylist(final String args){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete Playlist? Cannot be undone");

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteFromDB("playlist", "playlistname", args);
                DeleteFromDB("playlistsong", "playlistname", args);
                LoadPlaylistListView("playlist", "*","audioclass", "_wifi_audio", "");
                viewPlaylistAdapter.notifyDataSetChanged();
                playlistAdapter.notifyDataSetChanged();
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void DeletePlaylistSong(final String args){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete Song? Cannot be undone");

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteFromDB("playlistsong", "playlistsongname", args.replace("'", "''"));
                LoadPlaylistListView("playlistsong", "*","playlistname", playlistName,"");
                viewPlaylistAdapter.notifyDataSetChanged();
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        alertDialog = alertDialogBuilder.create();
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

            URL url;

            try {
                url = new URL(rootPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String ipAddress = rootPath.replace("http://", "");
                ipAddress = ipAddress.substring(0, ipAddress.indexOf("/"));
                final String status = "Connected to: " + ipAddress;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectStatus.setText(status);
                    }
                });

                urlConnection.disconnect();
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogBox("Error4" + e.toString());
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
                final URL url = new URL(arg0[0].mRootPath + "/music.php?folder=" + arg0[0].mHttpPath.replace(" ", "%20"));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String str = in.readLine();

                while (!str.equals("")) {
                    String link = str.substring(0, str.indexOf("<>"));
                    str = str.substring(str.indexOf("<>") + 2);

                    boolean tempBool = true;
                    String strings[] = {".mp3", ".flac", ".m4a", ".wav", ".wma"};

                    for (int x = 0; x < strings.length; x++) {
                        if (!link.toLowerCase().endsWith(strings[x])) {
                            tempBool = false;
                        } else {
                            tempBool = true;
                            break;
                        }
                    }

                    if (tempBool) {
                        listDataSongs.add(link.substring(link.lastIndexOf("/") + 1));
                    }else{
                        listDataDirectory.add(link);
                    }
                }

                in.close();
                urlConnection.disconnect();

            }catch (Exception e){
                Log.d("ERROR HERE: ", e.toString());
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
            expSongAdapter.notifyDataSetChanged();

            if (expDirectoryListView.getCount() == 0){
                listDataDirectory.add("[ RELOAD ]");
            }

            expDirectoryAdapter.notifyDataSetChanged();
        }

    }

    public void dialogBox(String s) {
        alertDialogBuilder = new AlertDialog.Builder(_wifi_audio.this);
        alertDialogBuilder.setMessage(s);
        dialogProgress = alertDialogBuilder.show();
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

    private Cursor readFromDB3(){
        String queryString = "SELECT * FROM users";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        return database.rawQuery(queryString,null);
    }

    private void setServerList() {
        alertDialogBuilder = new AlertDialog.Builder(_wifi_audio.this);
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

        try{
            cursor = readFromDB2();
            boolAlwaysUse = false;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    serverItems.add(cursor.getString(0));
                    cursor.moveToNext();
                }
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_audio.setServerList: " + e.toString());
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
                                rootPath = cursor.getString(2) + cursor.getString(1) + cursor.getString(3);
                                if(cursor.getInt(4) > 0){ boolLocked = true; }
                            }
                            cursor.moveToNext();
                        }
                    }
                }catch (Exception e){

                }

                alertDialog.dismiss();
                new CheckConnected(rootPath, "");
                MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                if(boolLocked){
                    getPinNumber();
                    new setExpDirectoryAdapter().execute(params);
                }else{
                    new setExpDirectoryAdapter().execute(params);
                }


                //alertDialog.dismiss();
            }
        });

        lstViewAdapter.notifyDataSetChanged();
    }

    private Boolean getServer(){

        boolean boolPin = false;

        try{
            cursor = readFromDB("");
            boolAlwaysUse = false;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if(cursor.getInt(9) > 0) {
                        boolAlwaysUse = true;
                        if(cursor.getInt(4) > 0){ boolLocked = true; }
                        rootPath = cursor.getString(2) + cursor.getString(1) + cursor.getString(3);
                        break;
                    }else{
                        boolAlwaysUse = false;
                    }
                    cursor.moveToNext();
                }
                if(boolAlwaysUse){
                    if(boolLocked){
                        boolPin = getPinNumber();
                    }

                    if(boolPin) {
                        length = rootPath.length();
                        songPath = rootPath;
                        //new setSongs().execute(rootPath);
                        //new MyTaskParams(rootPath, null);
                        MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                        new CheckConnected(rootPath, "");
                        new setExpDirectoryAdapter().execute(params);
                    }

                }else{
                    setServerList();
                }
            }
        }catch (Exception e){

        }

        return true;
    }

    public boolean getPinNumber(){


        alertDialogBuilder = new AlertDialog.Builder(_wifi_audio.this);
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

                try{
                    cursor = readFromDB3();
                    boolAlwaysUse = false;
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            if(cursor.getString(2).equals(textInputEditText.getText().toString())){
                                username = cursor.getString(0);
                                password = cursor.getString(1);
                            }
                            cursor.moveToNext();
                        }
                    }

                    if(textInputEditText.getText().toString().equals("")){
                        username = "";
                        password = "";
                    }

                    Authenticator.setDefault (new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication (username, password.toCharArray());
                        }
                    });


                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_wifi_audio.getPinNumber: " + e.toString());
                }
                alertDialog.dismiss();
            }
        });

        return true;

    }

    public class ScreenOnOffReceiver extends BroadcastReceiver
    {



        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {

                //LL_Audio.removeView(rl_lockscreen);
                //setShowWhenLocked(false);
                //timer.cancel();
                //TempWakeLock.release();
                //setwifilock();
                //thread.interrupt();
                //dialogBox("SCREEN ON");
                //connectTimer.cancel();
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {

                rl_lockscreen = new RelativeLayout(_wifi_audio.this);
                rl_lockscreen = findViewById(R.id.RL_Lockscreen);
                LayoutInflater inflater = getLayoutInflater();
                final View rowView = inflater.inflate(R.layout.lock_screen, null);
                LL_Audio.addView(rowView);
                setShowWhenLocked(true);
                final RelativeLayout rl_lockscreen_info = rowView.findViewById(R.id.rl_lockscreen_info);
                Button BTN_MOVE = rowView.findViewById(R.id.BTN_MOVE_NOTIFICATION);
                BTN_MOVE.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        //final int X = (int) event.getRawX();
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                //dY = v.getY();
                                //dX = v.getX() - event.getRawX();
                                dY = v.getY() - rl_lockscreen_info.getHeight();
                                break;

                            case MotionEvent.ACTION_UP:

                                v.setY(v.getY());
                                break;

                            case MotionEvent.ACTION_MOVE:

                                rl_lockscreen_info.animate()
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
    }

    public void turnOnScreen(){
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);
        setTurnScreenOn(true);
    }
}

