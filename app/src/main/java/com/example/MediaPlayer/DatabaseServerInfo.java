package com.example.MediaPlayer;

import android.provider.BaseColumns;



public final class DatabaseServerInfo {

    private DatabaseServerInfo() {

    }

    public static class Server implements BaseColumns {
        public static final String TABLE_NAME_SERVERS = "servers";
        public static final String TABLE_NAME_ADD_SERVER = "server";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IPADDRESS = "ipaddress";
        public static final String COLUMN_PROTOCOL = "protocol";
        public static final String COLUMN_MUSIC_FOLDER = "musicfolder";
        public static final String COLUMN_MUSIC_LOCKED = "musiclocked";
        public static final String COLUMN_PHOTO_FOLDER = "photofolder";
        public static final String COLUMN_PHOTO_LOCKED = "photolocked";
        public static final String COLUMN_VIDEO_FOLDER = "videofolder";
        public static final String COLUMN_VIDEO_LOCKED = "videolocked";
        public static final String COLUMN_ALWAYSUSE = "alwaysUse";

        public static final String CREATE_ADD_SERVER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_ADD_SERVER + " (" +
                COLUMN_NAME + " TEXT PRIMARY KEY, " +
                COLUMN_IPADDRESS + " TEXT, " +
                COLUMN_PROTOCOL + " TEXT, " +
                COLUMN_MUSIC_FOLDER + " TEXT," +
                COLUMN_MUSIC_LOCKED + " BOOLEAN," +
                COLUMN_PHOTO_FOLDER + " TEXT," +
                COLUMN_PHOTO_LOCKED + " BOOLEAN," +
                COLUMN_VIDEO_FOLDER + " TEXT," +
                COLUMN_VIDEO_LOCKED + " BOOLEAN," +
                COLUMN_ALWAYSUSE + " BOOLEAN)";

        public static final String CREATE_SERVERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_SERVERS + " (" +
                COLUMN_NAME + " TEXT PRIMARY KEY, " +
                COLUMN_ALWAYSUSE + " BOOLEAN)";

        public static final String TABLE_NAME_PLAYLIST = "playlist";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_AUDIO_CLASS = "audioclass";
        public static final String COLUMN_PLAYLIST_NAME = "playlistname";

        public static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_PLAYLIST + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AUDIO_CLASS + " TEXT, " +
                COLUMN_PLAYLIST_NAME + " TEXT)";

        public static final String TABLE_NAME_PLAYLIST_SONG = "playlistsong";
        public static final String COLUMN_PLAYLIST_SONG_NAME = "playlistsongname";
        public static final String COLUMN_PLAYLIST_SONG_LOCATION = "playlistsonglocation";

        public static final String CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_PLAYLIST_SONG + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYLIST_NAME + " TEXT, " +
                COLUMN_PLAYLIST_SONG_NAME + " TEXT, " +
                COLUMN_PLAYLIST_SONG_LOCATION + " TEXT, " +
                COLUMN_AUDIO_CLASS + " TEXT)";

        public static final String TABLE_NAME_USER = "users";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_PIN = "pin";

        public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_USER + " (" +
                COLUMN_USER + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PIN + " TEXT PRIMARY KEY)";
    }


}