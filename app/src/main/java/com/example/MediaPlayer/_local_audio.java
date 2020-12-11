package com.example.MediaPlayer;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.Mp3File;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class _local_audio extends AppCompatActivity {

    ArrayList<String> xItems, folderItems, items;

    ArrayList<String> listDataDirectory = new ArrayList<>();
    ArrayList<String> listDataSongs = new ArrayList<>();
    ArrayList<String> tempArrayList = new ArrayList<>();

    ArrayList<String> list_tracks = new ArrayList<>();
    ArrayList<String> list_track_number = new ArrayList<>();
    ArrayList<String> list_track_genre = new ArrayList<>();
    String str_artist, str_album;

    ArrayList<Bitmap> list_album_art = new ArrayList<>();
    ArrayList<String> list_title = new ArrayList<>();
    ArrayList<String> list_artist = new ArrayList<>();
    ArrayList<String> list_track_list_url = new ArrayList<>();
    ArrayList<String> list_loaded_songs = new ArrayList<>();
    ArrayList<String> list_album = new ArrayList<>();
    ArrayList<String> list_data_artist = new ArrayList<>();
    ArrayList<String> list_data_album = new ArrayList<>();
    ArrayList<String> list_data_song = new ArrayList<>();
    ArrayList<String> list_search = new ArrayList<>();
    HashMap<String, String> map_tracks = new HashMap<>();
    HashMap<String, String> playlistMap = new HashMap<>();
    HashMap<String, String> map_artist = new HashMap<>();
    HashMap<String, String> map_album = new HashMap<>();
    HashMap<String, String> map_song = new HashMap<>();

    ListViewAdapterAudio expSongAdapter;
    //ListViewAdapterAudio expDirectoryAdapter;
    ListViewAdapterVivo expVivoAdapter;
    ListViewAdapterVivoTracks expTrackList;
    ArrayAdapter<String> adapter, tempAdapter, viewPlaylistAdapter, expDirectoryAdapter;
    ListViewAdapterPlaylist playlistAdapter;
    private MediaSessionCompat mediaSession;

    int newSeekBarProgress, seekBarCurrentPosition, length, repeat, index, album_index;
    private final int REQUEST_SPEECH_RECOGNIZER = 3000;
    final int REPEAT_OFF = 0, REPEAT_ONE = 1, REPEAT_ALL = 2;
    String rootPath, songPath, playlistName, mainDirPath, str_search_result;
    boolean boolShuffle = false, boolPlaylistLoaded = false, isBackground = false;

    private _timer Timer;
    private _get_files GetFiles;
    private _randomizer Randomize;
    TelephonyManager mgr;
    PhoneStateListener phoneStateListener;

    Bitmap image;
    DrawerLayout mDrawerLayout;
    FrameLayout LL_Audio;
    Button btn_play, btn_pause, btn_previous, btn_next, btn_repeat, btn_shuffle, btn_playlist, btn_search;
    Toolbar tb;
    View transparentView, durationView;
    ListView expDirectoryListView, expSongView, list;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    Cursor cursor;
    TextInputEditText textInputEditText;
    MediaPlayer mPlayer;
    LinearLayout playButtonBackground, pauseButtonBackground;

    public SeekBar pb;
    public ListView lv;
    private TextView textDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.local_audio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        GetFiles = new _get_files();
        Randomize = new _randomizer(this, null);
        Timer = new _timer(this, null);

        items = new ArrayList<>();
        xItems = new ArrayList<>();
        folderItems = new ArrayList<>();
        transparentView = new View(this);
        durationView = new View(this);

        expDirectoryListView = findViewById(R.id.lvExpAudioDirectory);
        expSongView = findViewById(R.id.listview_song_list);
        playButtonBackground = findViewById(R.id.LL_play_btn);
        pauseButtonBackground = findViewById(R.id.LL_pause_btn);
        LL_Audio = findViewById(R.id.LL_Audio);
        tb = findViewById(R.id.Local_Audio_Toolbar);
        lv = findViewById(R.id.local_audio_listview);
        pb = findViewById(R.id.progressBar);
        btn_search = findViewById(R.id.BTN_SEARCH);
        btn_shuffle = findViewById(R.id.BTN_SHUFFLE);
        btn_playlist = findViewById(R.id.BTN_PLAYLIST);
        btn_play = findViewById(R.id.BTN_PLAY);
        btn_pause = findViewById(R.id.BTN_PAUSE);
        btn_previous = findViewById(R.id.BTN_PREVIOUS);
        btn_next = findViewById(R.id.BTN_NEXT);
        btn_repeat = findViewById(R.id.BTN_REPEAT);
        mDrawerLayout = findViewById(R.id.drawer_layout_main);

        mPlayer = new MediaPlayer();

        /*
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (mPlayer != null){
                        getActiveMediaPlayer().pause();
                        btn_play.setEnabled(true);
                        playButtonBackground.setAlpha(0f);
                        btn_pause.setEnabled(false);
                        pauseButtonBackground.setAlpha(0.2f);
                        Timer.setTimerTick(getActiveMediaPlayer().getCurrentPosition());
                        Timer.pauseTimer();
                    }

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    if (mPlayer != null){
                        btn_pause.setEnabled(true);
                        pauseButtonBackground.setAlpha(0f);
                        btn_play.setEnabled(false);
                        playButtonBackground.setAlpha(0.2f);
                        getActiveMediaPlayer().seekTo(Timer.getTimerTick());
                        Timer.resumeTimer();
                        getActiveMediaPlayer().start();
                    }
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
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
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
*/


        try{
            btn_play.setEnabled(false);
            btn_pause.setEnabled(false);
            pb.setProgress(0);
            setSupportActionBar(tb);

            LL_Audio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tb.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.OnCreate - Setup: " + e.toString());
        }

        try{


            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, folderItems);
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(adapter);
            adapter.add("Click here to select other folders");
            View headerView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            lv.addHeaderView(headerView);

            mainDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
            rootPath = mainDirPath;
            songPath = rootPath;
            length = rootPath.length();
            LoadListArtist(mainDirPath);

            expSongAdapter = new ListViewAdapterAudio(this,R.layout.list_item_audio,R.id.lblListItemAudio,listDataSongs,
                    "localSong",list_track_number,this, null);
            expSongView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setSongs(rootPath);
            expSongView.setAdapter(expSongAdapter);

            expDirectoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDataDirectory);
            expDirectoryListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            setExpDirectoryAdapter(rootPath, false);
            expDirectoryListView.setAdapter(expDirectoryAdapter);

        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.OnCreate - Set Adapters: " + e.toString());
        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognizer();
            }
        });

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
                        Log.d("EXCEPTION: ", "_local_audio.cbShuffle.setOnCheckedChangeListener.onCheckedChanged: " + e.toString());
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
                try {
                    if (mPlayer != null) {
                        pb.setProgress(progress);
                        Timer.setTimerTick(progress);
                        newSeekBarProgress = progress;

                        long hours = TimeUnit.MILLISECONDS.toHours(progress);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(progress);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(progress);

                        String str = String.format("%02d:%02d", minutes,
                                seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress)));

                        setDuration(str);
                    } else {
                        pb.setProgress(0);
                    }

                    getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayerListener();
                        }
                    });
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.pb.setOnSeekBarChangeListener: " + e.toString());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                try {
                    if (mPlayer == null) {
                        return;
                    }

                    LayoutInflater inflater = (LayoutInflater) _local_audio.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                    transparentView = inflater.inflate(R.layout.transparent_background, null);
                    LL_Audio.addView(transparentView);

                    durationView = inflater.inflate(R.layout.seekbar_seek_to_position, null);
                    LL_Audio.addView(durationView);

                    textDuration = new TextView(_local_audio.this);
                    textDuration = findViewById(R.id.textDuration);

                    seekBarCurrentPosition = seekBar.getMeasuredWidth() * seekBar.getProgress() / seekBar.getMax() - seekBar.getThumbOffset();
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.pb.setOnSeekBarChangeListener: " + e.toString());
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(mPlayer != null){
                    try{
                        LL_Audio.removeView(durationView);
                        LL_Audio.removeView(transparentView);
                    }catch (Exception e){
                        Log.d("EXCEPTION: ", "_local_audio.pb.setOnSeekBarChangeListener.onStopTrackingTouch: " + e.toString());
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
                try{
                    String str = parent.getItemAtPosition(position).toString();
                    boolPlaylistLoaded = false;
                    listDataDirectory.clear();
                    listDataDirectory.add("[ BACK ]");
                    btn_playlist.setBackgroundResource(R.drawable.view_playlist_grey);
                    ResetMPlayer();

                    if (str.equals("[ BACK ]")) {
                        rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
                    } else {
                        rootPath += "/" + str;
                    }

                    if (rootPath.length() == length) {
                        listDataDirectory.remove(0);
                    }

                    songPath = rootPath;
                    setSongs(rootPath);
                    setExpDirectoryAdapter(rootPath, false);
                    expDirectoryAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.expDirectoryListView.setOnItemClickListener: " + e.toString());
                }
            }
        });

        expSongView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                try {
                    Randomize.clearAlreadyPlayed();
                    index = 0;
                    boolShuffle = false;
                    btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle);

                    try {
                        getActiveMediaPlayer().stop();
                    } catch (Exception e) {
                        Log.d("EXCEPTION: ", "_local_audio.expSongView.setOnItemClickListener.onItemClick");
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
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.expSongView.setOnItemClickListener: " + e.toString());
                }
            }
        });

        expSongView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {


                ImageView iv_album_art = new ImageView(_local_audio.this);
                list_artist.clear();
                list_title.clear();
                list_album.clear();
                list_album_art.clear();
                list_track_list_url.clear();

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(songPath + "/" + expSongView.getItemAtPosition(position).toString());

                //String albumName = expSongView.getItemAtPosition(position).toString();
                //albumName = albumName.substring(0, albumName.lastIndexOf("."));
                //String titleName = expSongView.getItemAtPosition(position).toString();
                //titleName = titleName.substring(0, titleName.lastIndexOf("."));

                String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String titleName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                if (!titleName.equals("")){
                    GetMetaData(titleName, "track");
                    return true;
                }else if(!albumName.equals("")){
                    GetMetaData(albumName, "album");
                }else{
                    return false;
                }

                return true;
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.btn_previous.setOnClickListener: " + e.toString());
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
                try{
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
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.btn_play.setOnClickListener: " + e.toString());
                }
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (getActiveMediaPlayer() != null) {
                        getActiveMediaPlayer().pause();

                        btn_play.setEnabled(true);
                        playButtonBackground.setAlpha(0f);
                        btn_pause.setEnabled(false);
                        pauseButtonBackground.setAlpha(0.2f);
                        Timer.setTimerTick(getActiveMediaPlayer().getCurrentPosition());
                        Timer.pauseTimer();
                    }
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.btn_pause.setOnClickListener: " + e.toString());
                }
            }
        });

        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    if(btn_repeat.getBackground().getConstantState() == getResources().getDrawable(R.drawable.repeat).getConstantState()){
                        btn_repeat.setBackgroundResource(R.drawable.repeat_one_green);
                        repeat = REPEAT_ONE;
                    }else if(btn_repeat.getBackground().getConstantState() == getResources().getDrawable(R.drawable.repeat_one_green).getConstantState()){
                        btn_repeat.setBackgroundResource(R.drawable.repeat_all_green);
                        repeat = REPEAT_ALL;
                    }else if(btn_repeat.getBackground().getConstantState() == getResources().getDrawable(R.drawable.repeat_all_green).getConstantState()){
                        btn_repeat.setBackgroundResource(R.drawable.repeat);
                        repeat = REPEAT_OFF;
                    }

                    getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlayerListener();
                        }
                    });
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_audio.btn_repeat.setOnClickListener: " + e.toString());
                }
            }
        });

        btn_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPlaylist();
            }
        });
    }



    private void GetMetaData(final String titleName, final String type){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogBox("Loading albums, please wait...");
                    }
                });

                MetaDataSearch search = new MetaDataSearch(titleName, type);

                Log.i("JSON: ", search.GetOutput());
                try{
                    JSONObject jsonResponse = new JSONObject(search.GetOutput());

                    JSONArray data = jsonResponse.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject track = data.getJSONObject(i);

                            String str_title = track.getString("title_short");
                            String artist_temp = track.getString("artist");
                            String str_artist = artist_temp.substring(artist_temp.indexOf("\"name\":") + 7, artist_temp.indexOf(",\"link\"")).replace("\"", "");
                            String album_temp = track.getString("album");
                            String str_album = album_temp.substring(album_temp.indexOf("\"title\":") + 8, album_temp.indexOf(",\"cover\"")).replace("\"", "");
                            String str_album_art = album_temp.substring(album_temp.indexOf("\"cover\":") + 8, album_temp.indexOf(",\"cover_small\"")).replace("\"", "");
                            String str_track_list = album_temp.substring(album_temp.indexOf("\"tracklist\":") + 12, album_temp.indexOf(",\"type\"")).replace("\\", "").replace("\"", "");

                            URL url = new URL(str_album_art.replace("\\", ""));
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                            list_title.add("Title: " + str_title);
                            list_artist.add("Artist: " + str_artist);
                            list_album.add("Album: " + str_album);
                            list_album_art.add(image);
                            list_track_list_url.add(str_track_list);

                        }catch (Exception ex) { }
                    }
                }catch (Exception ex){ }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        dialogBoxVivo();
                        expVivoAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        thread.start();
    }

    private void GetTrackList(final String trackList){
        list_loaded_songs.clear();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogBox("Loading tracks, please wait...");
                    }
                });

                MetaDataTrackList search = new MetaDataTrackList(trackList);
                try{
                    JSONObject jsonResponse = new JSONObject(search.GetOutput());

                    JSONArray data = jsonResponse.getJSONArray("data");

                    list_loaded_songs.add("No Match");
                    list_loaded_songs.addAll(listDataSongs);

                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject track = data.getJSONObject(i);

                            String str_title = track.getString("title_short");
                            String str_track_position = track.getString("track_position");


                            Log.i("JSON: ", str_title);

                            list_tracks.add(str_title);
                            list_track_number.add(str_track_position);
                        }catch (Exception ex) { }
                    }


                }catch (Exception ex){ }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        alertDialog.dismiss();
                        dialogBoxTrackList();
                        expTrackList.notifyDataSetChanged();
                    }
                });

            }
        });
        thread.start();
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
                Timer.setTimerTick(0);
                Timer.cancelTimer();
                if(mPlayer.isPlaying()){
                    mPlayer.stop();
                    mPlayer.reset();
                }
                mPlayer.release();
                mPlayer = null;
            }catch (Exception e){
                Log.d("EXCEPTION: ", "_local_audio.onDestroy: " + e.toString());
            }
        }
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

    public void setSongs(final String songPath){

        listDataSongs.clear();
        list_track_number.clear();
        xItems.clear();
        xItems.addAll(GetFiles.getFiles(songPath));

        if(xItems.size() == 0){
            expSongAdapter.notifyDataSetChanged();
            return;
        }

        File file;
        String tempFile2 = "";
        ArrayList<String> tempList = new ArrayList<>();

        try {
            int track_position = 0;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            TreeMap<Integer, String> tempMap = new TreeMap<>();

            for (String tempFile : xItems){
                tempFile2 = tempFile;
                if(!tempFile.startsWith(".")){
                    file = new File(songPath + "/" + tempFile);

                    if (!file.isDirectory()) {

                        boolean tempBool = true;
                        String str[] = {".mp3", ".flac", ".m4a", ".wav", ".wma"};
                        for (int x = 0; x < str.length; x++) {

                            if (!file.getName().toLowerCase().endsWith(str[x])) {
                                tempBool = false;
                            } else {
                                tempBool = true;
                                break;
                            }
                        }
                        if (tempBool) {

                            try{
                                mmr.setDataSource(songPath + "/" + tempFile);
                                track_position = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));

                                if(tempMap.containsKey(track_position)){
                                    tempList.add(tempFile);
                                }else{
                                    tempMap.put(track_position, tempFile);
                                }
                            }catch (Exception ex){
                                tempList.add(tempFile);
                            }


                        }
                    }
                }
            }
            Collections.sort(tempList);
            if(tempMap.size() > 0){
                for(Integer key : tempMap.keySet()){
                    list_track_number.add(String.valueOf(key));
                    listDataSongs.add(tempMap.get(key));
                }
            }
            for(String str : tempList){
                list_track_number.add(String.valueOf(0));
                listDataSongs.add(str);
            }
            expSongAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.i("EXCEPTION: ", "_local_audio.setSongs: " + e.toString() + " : " + songPath + "/" + tempFile2);
        }
    }

    public void setExpDirectoryAdapter(String path, Boolean boolTest){
        try {
            File maindir = new File(path);
            ArrayList<String> tempArrayList = new ArrayList<>();
            ArrayList<String> testArrayList = new ArrayList<>();

            if (maindir.listFiles().length != 0) {
                File arr[] = maindir.listFiles();

                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].isDirectory() && !arr[i].getName().startsWith(".")) {
                        if (!boolTest) {
                            tempArrayList.add(arr[i].getName());
                        } else {
                            testArrayList.add(arr[i].getName());
                        }
                        Log.i("TITLE: ", arr[i].getName());
                    }
                }
                Collections.sort(tempArrayList);
                listDataDirectory.addAll(tempArrayList);
            }
            expDirectoryAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.setExpDirectoryAdapter: " + e.toString());
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

    public Integer GetAlbumIndex() { return album_index; }

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
            Log.d("EXCEPTION: ", "_local_audio.setDuration: " + e.toString());
        }
    }

    public void mPlayerListener(){

        if (boolShuffle) {
            if(repeat == REPEAT_ONE){
                expSongView.setSelection(index);
                expSongView.setActivated(true);
                startMPlayer();
                return;
            }

            if (index < listDataSongs.size()) {
                Randomize.randomize("localAudio");
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
                    Log.d("EXCEPTION: ", "_local_audio.mPlayerListener: " + e.toString());
                }
                ResetMPlayer();
                dialogBox("End of file list");
            }
        }else{
            if(repeat == REPEAT_ONE){
                expSongView.setSelection(index);
                expSongView.setActivated(true);
                startMPlayer();
                return;
            }

            index++;
            //expSongView.setSelection(index);

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
                    Log.d("EXCEPTION: ", "_local_audio.mPlayerListener: " + e.toString());
                }
                ResetMPlayer();
                dialogBox("End of file list");
            }
        }
    }

    private void startMPlayer(){
        turnOnScreen();

        try {
            Timer.cancelTimer();
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_local_audio.startMPlayer: " + e.toString());
        }
        try {
            getActiveMediaPlayer().reset();
            if(boolPlaylistLoaded){
                getActiveMediaPlayer().setDataSource(playlistMap.get(Randomize.getSelected()) + "/" + Randomize.getSelected());
                //dialogBox(playlistMap.get(Randomize.getSelected()));
            }else{
                getActiveMediaPlayer().setDataSource(songPath + "/" + Randomize.getSelected());
            }
            getActiveMediaPlayer().prepare();
            getActiveMediaPlayer().start();
            expSongView.setActivated(true);
        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_local_audio.startMPlayer: " + e.toString());
        }

        try {
            Timer.startTimer(getActiveMediaPlayer().getDuration(), "localAudio");
            Timer.setTimerTick(0);
            pb.setMax(getActiveMediaPlayer().getDuration());
            pb.setProgress(0);
            expSongAdapter.notifyDataSetChanged();
            if (boolShuffle) {
                expSongView.smoothScrollToPosition(Randomize.GetNum());
            } else {
                expSongView.smoothScrollToPosition(index);
            }
            btn_pause.setEnabled(true);
            btn_play.setEnabled(false);
            pauseButtonBackground.setAlpha(0.0f);
            playButtonBackground.setAlpha(0.2f);


        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.startMPlayer: " + e.toString());
        }
    }

    private void ResetMPlayer(){
        try{
            getActiveMediaPlayer().stop();
            getActiveMediaPlayer().reset();
            Timer.cancelTimer();
        }catch (Exception ex){
            Log.d("EXCEPTION: ", "_local_audio.ResetMPlayer");
        }
        pb.setProgress(0);
        btn_pause.setEnabled(false);
        btn_play.setEnabled(false);
        playButtonBackground.setAlpha(0);
        pauseButtonBackground.setAlpha(0);
    }

    private void LoadPlaylistDialog(){
        try {
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

                    if (tempArrayList.contains(textInputLayout.getEditText().getText().toString())) {
                        dialogBox("That playlist name already exists");
                    } else if (textInputLayout.getEditText().getText().toString().equals("")) {
                        dialogBox("Please enter a valid playlist name");
                    } else {
                        InsertIntoDB();
                        textInputEditText.getText().clear();
                        LoadPlaylistListView("playlist", "*", "audioclass", "_local_audio", "");
                        playlistAdapter.notifyDataSetChanged();
                    }
                }
            });
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.LoadPlaylistDialog: " + e.toString());
        }
    }

    private void LoadPlaylist(){
        try {
            LoadPlaylistDialog();

            final ListView playlistListView = alertDialog.findViewById(R.id.listview_playlist);
            playlistAdapter = new ListViewAdapterPlaylist(this, R.layout.list_item_audio, R.id.lblListItemAudio, tempArrayList,
                    "playlist", this, null);
            playlistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            playlistListView.setAdapter(playlistAdapter);

            LoadPlaylistListView("playlist", "*", "audioclass", "_local_audio", "");
            playlistAdapter.notifyDataSetChanged();
            playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    boolPlaylistLoaded = true;
                    try {
                        ResetMPlayer();
                        LoadPlaylistSongs(playlistListView.getItemAtPosition(position).toString());
                        Timer.cancelTimer();
                    } catch (Exception ex) {
                        Log.d("EXCEPTION: ", "_local_audio.LoadPlaylist");
                    }

                }
            });
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.LoadPlaylist: " + e.toString());
        }
    }

    private void LoadPlaylistSongs(String args){
        try {
            listDataSongs.clear();
            playlistMap.clear();
            btn_playlist.setBackgroundResource(R.drawable.view_playlist_green);
            LoadPlaylistListView("playlistsong", "*", "playlistname", args, "loadPlaylist");
            expSongAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.LoadPlaylistSongs: " + e.toString());
        }
    }

    public void AddToPlaylist(){

        try {
            LoadPlaylistDialog();

            final ListView playlistListView = alertDialog.findViewById(R.id.listview_playlist);
            playlistAdapter = new ListViewAdapterPlaylist(this, R.layout.list_item_audio, R.id.lblListItemAudio, tempArrayList,
                    "playlist", this, null);
            playlistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            playlistListView.setAdapter(playlistAdapter);

            LoadPlaylistListView("playlist", "*", "audioclass", "_local_audio", "");
            playlistAdapter.notifyDataSetChanged();

            playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    InsertIntoPlaylistDB(playlistListView.getItemAtPosition(position).toString(),
                            expSongView.getItemAtPosition(index).toString(),
                            songPath);
                }
            });
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.AddToPlaylist: " + e.toString());
        }
    }

    public void ViewPlaylist(final String args){
        try {
            playlistName = args;

            alertDialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.audio_view_playlist, null);
            alertDialogBuilder.setView(layout);
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

            ListView playlistListView = alertDialog.findViewById(R.id.listview_playlist);

            viewPlaylistAdapter = new ListViewAdapterPlaylist(this, R.layout.list_item_audio, R.id.lblListItemAudio, tempArrayList,
                    "playlistView", this, null);
            playlistListView.setAdapter(viewPlaylistAdapter);

            LoadPlaylistListView("playlistsong", "*", "playlistname", args, "");
            viewPlaylistAdapter.notifyDataSetChanged();

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LoadPlaylistListView("playlist", "*", "audioclass", "_local_audio", "");
                    playlistAdapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.ViewPlaylist: " + e.toString());
        }
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
        values.put(DatabaseServerInfo.Server.COLUMN_AUDIO_CLASS, "_local_audio");
        database.insert(DatabaseServerInfo.Server.TABLE_NAME_PLAYLIST_SONG, null, values);
        database.close();
    }

    private void InsertIntoDB(){
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseServerInfo.Server.COLUMN_AUDIO_CLASS, "_local_audio");
        values.put(DatabaseServerInfo.Server.COLUMN_PLAYLIST_NAME, textInputEditText.getText().toString());
        database.insert(DatabaseServerInfo.Server.TABLE_NAME_PLAYLIST, null, values);
        database.close();
    }

    private Cursor ReadFromDB(String table, String selectColumn, String column, String arg){
        String queryString = "SELECT " + selectColumn + " FROM " + table + " WHERE " + column + " = ? ";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        String[] args = new String[]{ arg };
        return database.rawQuery(queryString, args);
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
            cursor = ReadFromDBPlaylist(table, selectColumn, column, arg, "audioclass", "_local_audio");

            //dialogBox(String.valueOf(cursor.getCount()));
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
            Log.d("EXCEPTION: ", "_local_audio.LoadPlaylistListView: " + e.toString());
        }
    }

    public void DeletePlaylist(final String args){
        try {
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Delete Playlist? Cannot be undone");
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeleteFromDB("playlist", "playlistname", args);
                    DeleteFromDB("playlistsong", "playlistname", args);
                    LoadPlaylistListView("playlist", "*", "audioclass", "_local_audio", "");
                    viewPlaylistAdapter.notifyDataSetChanged();
                    playlistAdapter.notifyDataSetChanged();
                }
            });
            alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.DeletePlaylist: " + e.toString());
        }
    }

    public void DeletePlaylistSong(final String args){
        try {
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Delete Song? Cannot be undone");
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeleteFromDB("playlistsong", "playlistsongname", args.replace("'", "''"));
                    LoadPlaylistListView("playlistsong", "*", "playlistname", playlistName, "");
                    viewPlaylistAdapter.notifyDataSetChanged();
                }
            });
            alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.DeletePlaylistSong: " + e.toString());
        }
    }

    public void dialogBox(String s) {

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        alertDialogBuilder.setView(pb);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void dialogBoxVivo(){
        try {
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Select Album...");
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.vivo, null);
            alertDialogBuilder.setView(layout);

            list = layout.findViewById(R.id.list_vivo);
            Button btn_ok = layout.findViewById(R.id.BTN_VIVO_OK);
            Button btn_cancel = layout.findViewById(R.id.BTN_VIVO_CANCEL);

            expVivoAdapter = new ListViewAdapterVivo(_local_audio.this, R.layout.list_vivo, R.id.vivo_media_title,
                    list_title, list_album_art, list_album, list_artist, list_track_list_url, this);
            list.setAdapter(expVivoAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    album_index = position;
                    expVivoAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                    list_tracks.clear();
                    Log.i("TRACK: ",list_track_list_url.get(position));
                    GetTrackList(list_track_list_url.get(position));
                    //dialogBox(list_track_list.get(position));

                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBoxTrackList();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    expVivoAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.DeletePlaylistSong: " + e.toString());
        }
    }

    public void dialogBoxTrackList(){

        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.vivo, null);
        alertDialogBuilder.setView(layout);

        list = layout.findViewById(R.id.list_vivo);
        Button btn_ok = layout.findViewById(R.id.BTN_VIVO_OK);
        Button btn_cancel = layout.findViewById(R.id.BTN_VIVO_CANCEL);


        expTrackList = new ListViewAdapterVivoTracks(_local_audio.this, R.layout.list_vivo_tracks, R.id.txt_track,
                list_tracks, list_loaded_songs, this);
        list.setAdapter(expTrackList);


            /*
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View tempItem;
                    Spinner spinner_title;
                    String str, albumName, titleName;

                    for (int i = 0; i < list.getCount(); i++){
                        tempItem = list.getChildAt(i);
                        spinner_title = tempItem.findViewById(R.id.spinner_tracks);
                        str = spinner_title.getItemAtPosition(i).toString();
                        if (!str.equals("No Match")){

                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(songPath + "/" + str);

                            albumName = mmr.;
                            titleName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                        }
                    }
                }
            });


 */
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //list_track_list.clear();
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void LoadListArtist(String dirPath){
        try {
            File maindir = new File(dirPath);

            if (maindir.listFiles().length != 0) {
                File arr[] = maindir.listFiles();

                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].isDirectory()) {
                        list_data_artist.add(arr[i].getName());
                        map_artist.put(arr[i].getName(), dirPath);
                        LoadListAlbum(dirPath + "/" + arr[i].getName());
                    }else{
                        boolean tempBool = true;
                        String str[] = {".mp3", ".flac", ".m4a", ".wav", ".wma"};
                        for (int x = 0; x < str.length; x++) {

                            if (!arr[i].getName().toLowerCase().endsWith(str[x])) {
                                tempBool = false;
                            } else {
                                tempBool = true;
                                break;
                            }
                        }
                        if (tempBool) {
                            list_data_song.add(arr[i].getName());
                            map_song.put(arr[i].getName(), dirPath);
                        }
                        Collections.sort(list_data_song);
                    }
                }
                Collections.sort(list_data_album);
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.ListAlbumDirectory: " + e.toString());
        }
    }

    private void LoadListAlbum(String dirPath){
        try {
            File maindir = new File(dirPath);

            if (maindir.listFiles().length != 0) {
                File arr[] = maindir.listFiles();

                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].isDirectory()) {
                        list_data_album.add(arr[i].getName());
                        map_album.put(arr[i].getName(), dirPath);
                        LoadListAlbum(dirPath + "/" + arr[i].getName());
                    }else{
                        boolean tempBool = true;
                        String str[] = {".mp3", ".flac", ".m4a", ".wav", ".wma"};
                        for (int x = 0; x < str.length; x++) {

                            if (!arr[i].getName().toLowerCase().endsWith(str[x])) {
                                tempBool = false;
                            } else {
                                tempBool = true;
                                break;
                            }
                        }
                        if (tempBool) {
                            list_data_song.add(arr[i].getName());
                            map_song.put(arr[i].getName(), dirPath);
                        }
                        Collections.sort(list_data_song);
                    }
                }
                Collections.sort(list_data_album);
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_local_audio.ListAlbumDirectory: " + e.toString());
        }
    }

    private void startSpeechRecognizer() {
        Intent intent = new Intent
                (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNIZER) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra
                        (RecognizerIntent.EXTRA_RESULTS);
                //ListSearchResults(results.get(0));

            }
        }
    }

    private void ListSearchResults(String result){
        list_search.clear();
        //dialogBoxTrackList("search");
        tempAdapter.clear();
        tempAdapter.add("<--------->   ARTIST   <--------->");
        Collections.sort(list_data_artist);
        for(String item : list_data_artist){
            try{
                if(result.toLowerCase().equals(item.toLowerCase().substring(item.toLowerCase().indexOf(result.toLowerCase())))){
                    list_search.add(item);
                }
            }catch(Exception ex){ }

        }


        //list_search.addAll(list_data_artist);
        tempAdapter.add("<--------->   ALBUM   <--------->");
        Collections.sort(list_data_album);
        for(String item : list_data_album){
            try{
                if(result.toLowerCase().equals(item.toLowerCase().substring(item.toLowerCase().indexOf(result.toLowerCase())))){
                    list_search.add(item);
                }
            }catch(Exception ex){ }

        }

        tempAdapter.add("<--------->   SONGS   <--------->");
        Collections.sort(list_data_song);
        for(String item : list_data_song){
            try{
                if(result.toLowerCase().equals(item.toLowerCase().matches("(?i).*" + "barbie" + ".*"))){
                    list_search.add(item);
                }
            }catch(Exception ex){ }

        }

        //list_search.addAll(list_data_album);
        tempAdapter.notifyDataSetChanged();
    }

    public void turnOnScreen(){
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);
        setTurnScreenOn(true);
    }
}