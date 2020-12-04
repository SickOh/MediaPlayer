package com.example.newmusicplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;

public class _wifi_photo extends AppCompatActivity {
    static ArrayList<String> xItems = new ArrayList<>();
    ArrayList<Bitmap> bitmapItems;
    ArrayList<String> web = new ArrayList<>();
    ArrayList<String> serverItems = new ArrayList<>();
    ArrayList<String> lv_files = new ArrayList<>();
    ArrayList<String> listDataDirectory = new ArrayList<>();
    ArrayList<String> listDataHeader = new ArrayList<>();

    String mainDirPath, photoPath, username, password, httpPath = "", str = "";
    static String rootPath;
    Boolean firstRun = true, boolPrevious = false, boolNext = false, boolChangeImage, boolCloseImage, boolAlwaysUse, boolLocked;
    float downX, downY, upX, upY;
    float dX = 0, dY;
    float deltaX, deltaY;
    int index, length;

    ListViewAdapterPhoto expAdapter;
    ArrayAdapter<String> lv_adapter;
    CustomGrid adapter;

    ListView expListView, lv, listView;
    FrameLayout LL_Photo;
    RelativeLayout LL_Image;
    ImageView mImage2, mImage, mImage3;
    GridView gridview;
    Toolbar tb;
    Cursor cursor;
    Button btn_rotate;
    EditText textInputEditText;
    View layout;
    DrawerLayout mDrawerLayout;
    LayoutInflater inflater;
    RelativeLayout.LayoutParams lp;
    RequestOptions myOptions;
    DisplayMetrics displayMetrics;

    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog dialogProgress;
    TextView progressDialog;

    _get_files GetFiles = new _get_files();
    LongOperation longOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //try {

            setContentView(R.layout.local_photo_list);

            progressDialog = new TextView(_wifi_photo.this);
            alertDialogBuilder = new AlertDialog.Builder(_wifi_photo.this);
            GetFiles = new _get_files();
            layout = new View(this);
            expListView = new ListView(this);

            expListView = findViewById(R.id.lvExp);
            gridview = findViewById(R.id.local_photo_gridview);
            lv = findViewById(R.id.local_photo_listview);
            LL_Photo = findViewById(R.id.LL_Photo_List);
            tb = findViewById(R.id.local_photo_list_toolbar);
            mDrawerLayout = findViewById(R.id.drawer_layout_main);
            layout.findViewById(R.id.layout_root);

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    3);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            setSupportActionBar(tb);
            LL_Photo.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            tb.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));


            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            firstRun = false;
            expAdapter = new ListViewAdapterPhoto(this,R.layout.list_item_photo,R.id.lblListItemPhoto,listDataHeader,"");
            expListView.setAdapter(expAdapter);

            adapter = new CustomGrid(_wifi_photo.this, web, "WifiPhoto");
            gridview.setAdapter(adapter);

            lv_adapter = new ArrayAdapter<>(_wifi_photo.this,
                    android.R.layout.simple_list_item_1,
                    lv_files);
            lv_files.add("Click here to select other folders");
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(lv_adapter);

            View headerView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            lv.addHeaderView(headerView);

            getServer();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View v,
                                    final int position, long id) {

                index = position;
                inflater = (LayoutInflater) _wifi_photo.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                layout = inflater.inflate(R.layout.popup_image,null);
                layout.findViewById(R.id.layout_root);
                LL_Photo.addView(layout);
                btn_rotate = layout.findViewById(R.id.BTN_ROTATE);
                Toast.makeText(_wifi_photo.this, "Slide Down to Close", Toast.LENGTH_LONG).show();
                RefreshGridView();
            }
        });


        expListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = parent.getItemAtPosition(position).toString();
                listDataHeader.clear();
                web.clear();
                listDataHeader.add("[ BACK ]");
                web.clear();

                try{
                    if (str.equals("[ RELOAD ]") ) {
                        MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                        new LongOperation().execute(params);
                        if(httpPath.equals("") && parent.getItemAtPosition(0).toString().equals("[ BACK ]")){
                            listDataDirectory.remove(0);
                            expAdapter.notifyDataSetChanged();
                        }
                        return;
                    }

                    if (str.equals("[ BACK ]")) {
                        try{
                            httpPath = httpPath.substring(0, httpPath.lastIndexOf("/"));
                        }catch (Exception ex){
                            httpPath = "";
                            rootPath = mainDirPath;
                        }

                        if(httpPath.equals("") && parent.getItemAtPosition(0).toString().equals("[ BACK ]")) {
                            listDataHeader.remove(0);
                            expAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if(httpPath.equals("")){
                            httpPath = str;
                        }else{
                            httpPath += "/" + str;
                        }
                    }
                    Log.d("HTTPPATH: ", httpPath);
                    photoPath = rootPath + "/" + httpPath;
                    MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                    new LongOperation().execute(params);
                    adapter.notifyDataSetChanged();
                    expAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "_wifi_photo.expListView.setOnItemClickListener: " + e.toString());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
                Intent homeIntent = new Intent(this, MainActivityLocal.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);

                listDataHeader.clear();
                web.clear();
                bitmapItems.clear();
                xItems.clear();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public void dialogBox(String s) {
        alertDialogBuilder.setMessage(s);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("STOP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    longOperation.cancel(true);
                    gridview.setAdapter(adapter);
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_wifi_photo.dialogBox: " + e.toString());
                }

                if (dialogProgress.isShowing()){
                    dialogProgress.dismiss();
                }
            }
        });

        dialogProgress = alertDialogBuilder.show();
    }

    private static class MyTaskParams {
        String mRootPath;
        String mHttpPath;

        MyTaskParams(String mRootPath, String mHttpPath) {
            this.mRootPath = mRootPath;
            this.mHttpPath = mHttpPath;
        }
    }

    public class LongOperation extends AsyncTask<MyTaskParams, String, Boolean> {

        @Override
        protected Boolean doInBackground(final MyTaskParams... arg0) {


            try {

                final URL url = new URL(arg0[0].mRootPath + "/photo.php?folder=" + arg0[0].mHttpPath.replace(" ", "%20"));
                setRootPath(arg0[0].mRootPath + "/" + arg0[0].mHttpPath.replace(" ", "%20"));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                str = in.readLine();

                while (!str.equals("")) {
                    String link = str.substring(0, str.indexOf("<>"));
                    str = str.substring(str.indexOf("<>") + 2);

                    boolean tempBool = true;
                    String strings[] = {".bmp", ".jpg", ".jpeg", ".ico", ".tiff", ".gif", ".png"};

                    for (int x = 0; x < strings.length; x++) {
                        if (!link.toLowerCase().endsWith(strings[x])) {
                            tempBool = false;
                        } else {
                            tempBool = true;
                            break;
                        }
                    }

                    if (tempBool) {
                        web.add(link.substring(link.lastIndexOf("/") + 1));
                    }else{
                        listDataHeader.add(link);
                    }
                }
            } catch (Exception e) {
                Log.d("ERROR RIGHT HERE", e.toString());
                return false;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(final Boolean result) {

            if(listDataHeader.size() == 0){
                listDataHeader.add("[ RELOAD ]");
            }
            adapter.notifyDataSetChanged();
            expAdapter.notifyDataSetChanged();
        }
    }

    private boolean setImages(int mIndex){


        myOptions = new RequestOptions()
                .fitCenter()
                .override(displayMetrics.widthPixels, displayMetrics.heightPixels);

        if(mIndex > 0){
            Glide.with(_wifi_photo.this)
                    .load(rootPath + "/" + web.get(mIndex - 1))
                    .apply(myOptions)
                    .into(mImage);
        }

        if(mIndex < gridview.getCount() - 1){
            Glide.with(_wifi_photo.this)
                    .load(rootPath + "/" + web.get(mIndex + 1))
                    .apply(myOptions)
                    .into(mImage3);
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void RefreshGridView(){

        //try {

        LL_Image = layout.findViewById(R.id.LL_Image);
        mImage = new ImageView(_wifi_photo.this);
        mImage2 = new ImageView(_wifi_photo.this);
        mImage3 = new ImageView(_wifi_photo.this);


        BitmapFactory.Options options = new BitmapFactory.Options();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        myOptions = new RequestOptions()
                .fitCenter()
                .override(displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inSampleSize = 2;

        Glide.with(_wifi_photo.this)
                .load(rootPath + "/" + web.get(index))
                .apply(myOptions)
                .into(mImage2);

        setImages(index);

        btn_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(_wifi_photo.this, "Cannot save EXIF data to photos over the internet", Toast.LENGTH_LONG).show();
            }
        });

        LL_Image.addView(mImage2);

        lp = (RelativeLayout.LayoutParams) mImage2.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImage2.setLayoutParams(lp);

        mImage2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {



                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //dX = v.getX() - event.getRawX();

                        downX = event.getX();
                        downY = event.getY();

                        //dY = v.getY() - rl_lockscreen.getHeight();
                        break;

                    case MotionEvent.ACTION_UP:
                        upX = event.getX();
                        upY = event.getY();
                        deltaX = downX - upX;
                        deltaY = downY - upY;

                        if(Math.abs(deltaX) > 200 && deltaX < 0) {
                            //right
                            boolChangeImage = true;
                            dX = mImage2.getWidth() * 2;
                            if(index <= 0){  return false; }
                            index--;
                            boolPrevious = true;
                        }

                        if(Math.abs(deltaX) > 200 && deltaX > 0) {
                            //left
                            boolChangeImage = true;
                            dX = 0 - mImage2.getWidth();
                            if(index >= (gridview.getCount() - 1)){  return false; }
                            index++;
                            boolNext = true;
                        }

                        if(Math.abs(deltaY) > 200 && deltaY < 0) {
                            //up
                            //boolCloseImage = true;
                            //dY = 0 - mImage.getHeight();
                            //LL_Photo.removeView(layout);
                            //if(index <= 0){  return false; }
                            //return true;
                        }

                        if(Math.abs(deltaY) > 200 && deltaY < 0) {
                            //down
                            boolCloseImage = true;
                            dY = mImage2.getHeight() * 2;
                            mImage2.animate()
                                    .y(dY)
                                    .setDuration(250)
                                    .start();

                            mImage2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LL_Photo.removeView(layout);
                                }
                            }, 250);
                            if(index <= 0){  return false; }
                            return true;
                        }
                        //v.setY(0);
                        //v.setY(v.getY());
                        try {
                            if (Math.abs(deltaX) > 200 && boolChangeImage) {

                                mImage2.animate()
                                        .x(dX)
                                        .setDuration(400)
                                        .start();

                                mImage2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (deltaX < 0) {
                                            mImage2.setX(0 - displayMetrics.widthPixels);
                                        }

                                        if (deltaX > 0) {
                                            mImage2.setX(displayMetrics.widthPixels);
                                        }

                                        if (boolPrevious) {
                                            mImage2.setImageBitmap(null);
                                            mImage2.setImageDrawable(mImage.getDrawable());
                                            boolPrevious = false;
                                        } else if (boolNext) {
                                            mImage2.setImageBitmap(null);
                                            mImage2.setImageDrawable(mImage3.getDrawable());
                                            boolNext = false;
                                        }

                                        if(setImages(index)){
                                            mImage2.animate()
                                                    .x(0)
                                                    .setDuration(400)
                                                    .start();
                                        }


                                    }
                                }, 400);
                            }
                        }catch (Exception ex){
                            Log.d("EXCEPTION: ", "_wifi_photo.mImage2.setOnTouchListener");
                        }

                        break;
                }
                return true;
            }
        });

        //} catch (Exception e) {
        //    dialogBox(e.toString());
        //}
    }

    public void preventClicks(View view) {}



    public String getImagePath(){
        return rootPath;
    }

    public void setRootPath(String path){
        rootPath = path;
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
        alertDialogBuilder = new AlertDialog.Builder(_wifi_photo.this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.database_select_server, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        listView = alertDialog.findViewById(R.id.listview_select_server);
        ArrayAdapter lstViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serverItems);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(lstViewAdapter);

        serverItems.clear();
        boolLocked = false;

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
            Log.d("EXCEPTION: ", "_wifi_photo.setServerList: " + e.toString());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    cursor = readFromDB("GET");
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            if(cursor.getString(0).equals(listView.getItemAtPosition(position).toString()))
                            {
                                mainDirPath = cursor.getString(2) + cursor.getString(1) + cursor.getString(5);
                                rootPath = mainDirPath;
                                if(cursor.getInt(6) > 0){ boolLocked = true; }
                            }
                            cursor.moveToNext();
                        }
                    }
                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_wifi_photo.listView.setOnItemClickListener: " + e.toString());
                }

                alertDialog.dismiss();
                if(boolLocked){
                    getPinNumber();
                }else{
                    MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                    new LongOperation().execute(params);
                }
            }
        });

        lstViewAdapter.notifyDataSetChanged();
    }

    private Boolean getServer(){

        try{
            cursor = readFromDB("");
            boolAlwaysUse = false;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    if(cursor.getInt(9) > 0) {
                        boolAlwaysUse = true;
                        if(cursor.getInt(6) > 0){ boolLocked = true; }else{ boolLocked = false; }

                        break;
                    }else{
                        boolAlwaysUse = false;
                    }
                    cursor.moveToNext();
                }
                if(boolAlwaysUse){

                    if(boolLocked){
                        getPinNumber();
                    }
                    mainDirPath = cursor.getString(2) + cursor.getString(1) + cursor.getString(5);
                    rootPath = mainDirPath;
                    length = rootPath.length();
                    photoPath = rootPath;
                    MyTaskParams params = new MyTaskParams(rootPath, httpPath);
                    new LongOperation().execute(params);

                }else{
                    setServerList();
                }
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_photo.getServer: " + e.toString());
        }
        return true;
    }

    public void getPinNumber(){
        alertDialogBuilder = new AlertDialog.Builder(_wifi_photo.this);
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
                    Log.d("EXCEPTION: ", "_wifi_photo.btn_ok.setOnClickListener: " + e.toString());
                }
                alertDialog.dismiss();
            }
        });
    }
}
