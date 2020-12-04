package com.example.newmusicplayer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

public class DatabaseManageServers extends AppCompatActivity {

    private int index = 0;
    static String selectedServer = "", sender;

    private ListView listServer;
    Button btnAddServer, btnEditServer, btnRemoveServer, btn_music_locked, btn_video_locked, btn_photo_locked, btnCancel, btnSave;
    private CheckBox alwaysUse;
    private static ArrayList<String> listData;
    private static ArrayList<Boolean> listChecked;
    boolean bool_music_locked, bool_video_locked, bool_photo_locked;
    RelativeLayout databaseMain, activityFirst;
    View addServer;
    Toolbar toolbar;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    static ListViewAdapterDB lstViewAdapter;
    EditText nameEditText, ipAddressEditText, folderMusicEditText, folderVideoEditText, folderPhotoEditText;

    private EditText editTextServerName;
    private EditText editTextProtocol;
    private EditText editTextIPAddress;
    private EditText editTextMusic;
    private EditText editTextVideo;
    private EditText editTextPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_manage_server);

        listServer = new ListView(this);
        btnAddServer = new Button(this);
        btnEditServer = new Button(this);
        btnRemoveServer = new Button(this);
        databaseMain = new RelativeLayout(this);
        activityFirst = new RelativeLayout(this);
        addServer = new View(this);
        toolbar = new Toolbar(this);
        listData = new ArrayList<>();
        listChecked = new ArrayList<>();
        btnCancel = new Button(this);
        btnSave = new Button(this);
        alwaysUse = new CheckBox(this);
        nameEditText = new EditText(this);
        ipAddressEditText = new EditText(this);
        folderMusicEditText = new EditText(this);
        folderVideoEditText = new EditText(this);
        folderPhotoEditText = new EditText(this);

        btnSave = findViewById(R.id.saveButton);
        btnCancel = findViewById(R.id.cancelButton);
        alwaysUse = findViewById(R.id.alwaysUseButton);
        nameEditText = findViewById(R.id.ServerNameEditText);
        ipAddressEditText = findViewById(R.id.ipAddressEditText);
        folderMusicEditText = findViewById(R.id.folderMusicEditText);
        folderVideoEditText = findViewById(R.id.folderVideoEditText);
        folderPhotoEditText = findViewById(R.id.folderPhotoEditText);
        btnAddServer = findViewById(R.id.BTN_ADD_SERVER);
        listServer = findViewById(R.id.listServer);
        btnRemoveServer = findViewById(R.id.BTN_REMOVE_SERVER);
        btnEditServer = findViewById(R.id.BTN_EDIT_SERVER);
        databaseMain = findViewById(R.id.database_main);
        toolbar = findViewById(R.id.Manage_Server_Toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listServer.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new DatabaseSQLiteHelper(this);

        lstViewAdapter = new ListViewAdapterDB(this, R.layout.list_item_database, R.id.lblListItemDB, listData, listChecked,
                "dbAudio", this);
        listServer.setAdapter(lstViewAdapter);

        readServerList();


        listServer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedServer = listServer.getItemAtPosition(position).toString();
                index = position;
                lstViewAdapter.notifyDataSetChanged();
            }
        });

        btnAddServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sender = "add";

                AddServer("SAVE");
                readDB(sender);


            }
        });

        btnRemoveServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeServer(selectedServer);
                readServerList();

            }
        });

        btnEditServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedServer.equals("")){ return; }
                AddServer("UPDATE");
            }
        });


    }

    private void readServerList(){
        try{
            Cursor cursor = readFromDB();
            listData.clear();
            listChecked.clear();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(0);
                    Boolean mAlwaysUse = cursor.getInt(1) > 0;
                    listData.add(name);
                    listChecked.add(mAlwaysUse);
                    cursor.moveToNext();
                }
            }
            lstViewAdapter.notifyDataSetChanged();

        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_video.readServerList: " + e.toString());
        }
    }

    public void AddServer(final String args){



        alertDialogBuilder = new AlertDialog.Builder(DatabaseManageServers.this);
        //alertDialogBuilder.setMessage("Delete Playlist? Cannot be undone");
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.database_add_edit_server, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        editTextServerName = alertDialog.findViewById(R.id.ServerNameEditText);
        editTextProtocol = alertDialog.findViewById(R.id.ProtocolEditText);
        editTextIPAddress = alertDialog.findViewById(R.id.ipAddressEditText);
        editTextMusic = alertDialog.findViewById(R.id.folderMusicEditText);
        editTextVideo = alertDialog.findViewById(R.id.folderVideoEditText);
        editTextPhoto = alertDialog.findViewById(R.id.folderPhotoEditText);

        alwaysUse = alertDialog.findViewById(R.id.alwaysUseButton);

        btn_music_locked = alertDialog.findViewById(R.id.BTN_MUSIC_LOCKED);
        btn_video_locked = alertDialog.findViewById(R.id.BTN_VIDEO_LOCKED);
        btn_photo_locked = alertDialog.findViewById(R.id.BTN_PHOTO_LOCKED);
        Button btn_save = alertDialog.findViewById(R.id.saveButton);
        Button btn_cancel = alertDialog.findViewById(R.id.cancelButton);



        if(args.equals("UPDATE")){
            btn_save.setText("UPDATE");
            readDB("UPDATE");
        }



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextMusic.getText().toString().equals("") && !editTextMusic.getText().toString().startsWith("/")){ editTextMusic.setText("/" + editTextMusic.getText().toString()); }
                if(!editTextVideo.getText().toString().equals("") && !editTextVideo.getText().toString().startsWith("/")){ editTextVideo.setText("/" + editTextVideo.getText().toString()); }
                if(!editTextPhoto.getText().toString().equals("") && !editTextPhoto.getText().toString().startsWith("/")){ editTextPhoto.setText("/" + editTextPhoto.getText().toString()); }

                if(args.equals("SAVE")){
                    Cursor cursor = readFromDB();
                    boolean exists = false;
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            if (cursor.getString(0).equals(editTextServerName.getText().toString())) {
                                exists = true;
                                break;
                            } else {
                                exists = false;
                            }
                            cursor.moveToNext();
                        }
                    }

                    if (!exists) {

                        saveToDB();
                        alertDialog.dismiss();
                    } else {
                        editTextServerName.setText("");
                        editTextServerName.requestFocus();
                    }
                }else{
                    updateRecord(selectedServer);
                    alertDialog.dismiss();
                }

                selectedServer = "";
                index = 0;
                readServerList();
            }
        });

        btn_music_locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_music_locked.getBackground().getConstantState() == getResources().getDrawable(R.drawable.locked, null).getConstantState()){
                    btn_music_locked.setBackgroundResource(R.drawable.locked_green);
                    bool_music_locked = true;
                }else{
                    btn_music_locked.setBackgroundResource(R.drawable.locked);
                    bool_music_locked = false;
                }
            }
        });

        btn_video_locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_video_locked.getBackground().getConstantState() == getResources().getDrawable(R.drawable.locked, null).getConstantState()){
                    btn_video_locked.setBackgroundResource(R.drawable.locked_green);
                    bool_video_locked = true;
                }else{
                    btn_video_locked.setBackgroundResource(R.drawable.locked);
                    bool_video_locked = false;
                }
            }
        });

        btn_photo_locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_photo_locked.getBackground().getConstantState() == getResources().getDrawable(R.drawable.locked, null).getConstantState()){
                    btn_photo_locked.setBackgroundResource(R.drawable.locked_green);
                    bool_photo_locked = true;
                }else{
                    btn_photo_locked.setBackgroundResource(R.drawable.locked);
                    bool_photo_locked = false;
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        lstViewAdapter.notifyDataSetChanged();
    }

    private void saveToDB() {
        SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageServers.this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseServerInfo.Server.COLUMN_NAME, editTextServerName.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_PROTOCOL, editTextProtocol.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_IPADDRESS, editTextIPAddress.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_MUSIC_FOLDER, editTextMusic.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_VIDEO_FOLDER, editTextVideo.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_PHOTO_FOLDER, editTextPhoto.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_ALWAYSUSE, alwaysUse.isChecked());

        if(bool_music_locked){
            values.put(DatabaseServerInfo.Server.COLUMN_MUSIC_LOCKED, true);
        }else{
            values.put(DatabaseServerInfo.Server.COLUMN_MUSIC_LOCKED, false);
        }
        if(bool_video_locked){
            values.put(DatabaseServerInfo.Server.COLUMN_VIDEO_LOCKED, true);
        }else{
            values.put(DatabaseServerInfo.Server.COLUMN_VIDEO_LOCKED, false);
        }
        if(bool_photo_locked){
            values.put(DatabaseServerInfo.Server.COLUMN_PHOTO_LOCKED, true);
        }else{
            values.put(DatabaseServerInfo.Server.COLUMN_PHOTO_LOCKED, false);
        }

        database.insert(DatabaseServerInfo.Server.TABLE_NAME_ADD_SERVER, null, values);

        ContentValues serverValues = new ContentValues();
        serverValues.put(DatabaseServerInfo.Server.COLUMN_NAME, editTextServerName.getText().toString());
        serverValues.put(DatabaseServerInfo.Server.COLUMN_ALWAYSUSE, alwaysUse.isChecked());

        database.insert(DatabaseServerInfo.Server.TABLE_NAME_SERVERS, null, serverValues);

        updateAlwaysUse(editTextServerName.getText().toString());
    }

    private void updateAlwaysUse(String server){
        SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageServers.this).getWritableDatabase();
        try{
            Cursor cursor = readFromDB3();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    if(!cursor.getString(0).equals(server)){
                        String[] mArgs = new String[]{ cursor.getString(0) };
                        ContentValues values = new ContentValues();
                        values.put(DatabaseServerInfo.Server.COLUMN_ALWAYSUSE, false);

                        database.update(DatabaseServerInfo.Server.TABLE_NAME_ADD_SERVER, values, "name = ?", mArgs);
                        database.update(DatabaseServerInfo.Server.TABLE_NAME_SERVERS, values, "name = ?", mArgs);
                    }

                    cursor.moveToNext();
                }
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_video.updateAlwaysUse: " + e.toString());
        }
    }

    private Cursor readFromDB3(){
        String queryString = "SELECT * FROM server WHERE name = name";
        SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageServers.this).getReadableDatabase();
        return database.rawQuery(queryString,null);
    }

    private Cursor readFromDB(){
        String queryString = "SELECT * FROM servers WHERE name = name";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        return database.rawQuery(queryString,null);
    }

    private Cursor readFromDB2(){
        String queryString = "SELECT * FROM server WHERE name LIKE ? ";
        SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageServers.this).getReadableDatabase();
        String[] args = new String[]{ selectedServer };
        return database.rawQuery(queryString,args);
    }



    private Boolean updateRecord (String args){
        SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageServers.this).getWritableDatabase();

        String[] mArgs = new String[]{ args };

        ContentValues values = new ContentValues();
        values.put(DatabaseServerInfo.Server.COLUMN_NAME,editTextServerName.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_PROTOCOL,editTextProtocol.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_IPADDRESS,editTextIPAddress.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_MUSIC_FOLDER,editTextMusic.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_VIDEO_FOLDER,editTextVideo.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_PHOTO_FOLDER,editTextPhoto.getText().toString());
        values.put(DatabaseServerInfo.Server.COLUMN_ALWAYSUSE,alwaysUse.isChecked());

        if(bool_music_locked){
            values.put(DatabaseServerInfo.Server.COLUMN_MUSIC_LOCKED, true);
        }else{
            values.put(DatabaseServerInfo.Server.COLUMN_MUSIC_LOCKED, false);
        }
        if(bool_video_locked){
            values.put(DatabaseServerInfo.Server.COLUMN_VIDEO_LOCKED, true);
        }else{
            values.put(DatabaseServerInfo.Server.COLUMN_VIDEO_LOCKED, false);
        }
        if(bool_photo_locked){
            values.put(DatabaseServerInfo.Server.COLUMN_PHOTO_LOCKED, true);
        }else{
            values.put(DatabaseServerInfo.Server.COLUMN_PHOTO_LOCKED, false);
        }

        ContentValues serverValues = new ContentValues();
        serverValues.put(DatabaseServerInfo.Server.COLUMN_NAME, editTextServerName.getText().toString());
        serverValues.put(DatabaseServerInfo.Server.COLUMN_ALWAYSUSE, alwaysUse.isChecked());

        database.update(DatabaseServerInfo.Server.TABLE_NAME_ADD_SERVER, values, "name = ?", mArgs);
        database.update(DatabaseServerInfo.Server.TABLE_NAME_SERVERS, serverValues, "name = ?", mArgs);

        updateAlwaysUse(selectedServer);

        return alwaysUse.isChecked();
    }





    private void removeServer (String args){

        SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageServers.this).getWritableDatabase();

        database.execSQL("DELETE FROM server WHERE name = '" + args + "'");
        database.execSQL("DELETE FROM servers WHERE name = '" + args + "'");
    }



    private void readDB(String sender){
        if(sender.equals("UPDATE")){
            Cursor cursor = readFromDB2();
            try{
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String name = cursor.getString(0);
                        String ipAddress = cursor.getString(1);
                        String protocol = cursor.getString(2);
                        String folderMusic = cursor.getString(3);
                        String folderPhoto = cursor.getString(5);
                        String folderVideo = cursor.getString(7);
                        Boolean mAlwaysUse = cursor.getInt(9) > 0;

                        editTextServerName.setText(name);
                        editTextIPAddress.setText(ipAddress);
                        editTextProtocol.setText(protocol);
                        editTextMusic.setText(folderMusic);
                        editTextPhoto.setText(folderPhoto);
                        editTextVideo.setText(folderVideo);
                        alwaysUse.setChecked(mAlwaysUse);

                        if(cursor.getInt(4) > 0){
                            btn_music_locked.setBackgroundResource(R.drawable.locked_green);
                            bool_music_locked = true;
                        }else{
                            btn_music_locked.setBackgroundResource(R.drawable.locked);
                            bool_music_locked = false;
                        }
                        if(cursor.getInt(6) > 0){
                            btn_photo_locked.setBackgroundResource(R.drawable.locked_green);
                            bool_photo_locked = true;
                        }else{
                            btn_photo_locked.setBackgroundResource(R.drawable.locked);
                            bool_photo_locked = false;
                        }
                        if(cursor.getInt(8) > 0){
                            btn_video_locked.setBackgroundResource(R.drawable.locked_green);
                            bool_video_locked = true;
                        }else{
                            btn_video_locked.setBackgroundResource(R.drawable.locked);
                            bool_video_locked = false;
                        }

                        //dialogBox(String.valueOf(mAlwaysUse));

                        cursor.moveToNext();
                    }
                }

            }catch (Exception e){
                Log.d("EXCEPTION: ", "_wifi_video.readDB: " + e.toString());
            }
        }else{
//            nameEditText.setText("");
//            ipAddressEditText.setText("");
//            folderMusicEditText.setText("");
//            folderVideoEditText.setText("");
//            folderPhotoEditText.setText("");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivityServer.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public void dialogBox(String s) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public int GetIndex(){ return index; }
}