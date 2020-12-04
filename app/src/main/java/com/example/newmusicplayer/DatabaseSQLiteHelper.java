package com.example.newmusicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "server_database.db";

    public DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DatabaseServerInfo.Server.CREATE_SERVERS_TABLE);
        sqLiteDatabase.execSQL(DatabaseServerInfo.Server.CREATE_ADD_SERVER_TABLE);
        sqLiteDatabase.execSQL(DatabaseServerInfo.Server.CREATE_PLAYLIST_TABLE);
        sqLiteDatabase.execSQL(DatabaseServerInfo.Server.CREATE_PLAYLIST_SONG_TABLE);
        sqLiteDatabase.execSQL(DatabaseServerInfo.Server.CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseServerInfo.Server.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseServerInfo.Server.TABLE_NAME_SERVERS);
        onCreate(sqLiteDatabase);
    }

    public String getDBName(){
        return getDatabaseName();
    }
}