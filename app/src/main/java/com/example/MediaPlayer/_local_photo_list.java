package com.example.MediaPlayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.util.ArrayList;

public class _local_photo_list extends AppCompatActivity {

    static ArrayList<String> xItems = new ArrayList<>();
    static ArrayList<Object> items;
    ArrayList<Bitmap> bitmapItems, directoryIcons;
    ArrayList<String> directoryMenu, directoryItems, web;
    ArrayList<String> lv_files = new ArrayList<>();
    ArrayList<String> listDataHeader = new ArrayList<>();
    static ArrayList<Integer> checkId = new ArrayList<>();

    String maindirpath;
    static String rootPath;
    Boolean firstRun = true, boolPrevious = false, boolNext = false, boolChangeImage, boolCloseImage;
    int ImageCounter, index, mPtrCount, mViewScaledTouchSlop ;

    ListViewAdapterPhoto expAdapter;
    ArrayAdapter<String> lv_adapter;
    CustomGrid adapter;

    ListView expListView, lv;
    FrameLayout LL_Photo;
    RelativeLayout LL_Image;
    ImageView mImage2, mImage, mImage3;
    GridView gridview;
    Toolbar tb;
    Button btn_rotate;
    View layout;
    DrawerLayout mDrawerLayout;
    LayoutInflater inflater;
    LayoutParams lp;
    RequestOptions myOptions;
    DisplayMetrics displayMetrics;
    ScaleGestureDetector mScaleDetector;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog dialogProgress;
    TextView progressDialog;

    _get_files GetFiles = new _get_files();
    LongOperation longOperation;

    float mPrimStartTouchEventX = -1;
    float mPrimStartTouchEventY = -1;
    float mSecStartTouchEventX = -1;
    float mSecStartTouchEventY = -1;
    float mPrimSecStartTouchDistance = 0;

    float dX = 0, dY;
    float deltaX, deltaY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.local_photo_list);

            progressDialog = new TextView(_local_photo_list.this);
            alertDialogBuilder = new AlertDialog.Builder(_local_photo_list.this);
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

            rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
            maindirpath = rootPath;
            setImages(maindirpath);
            firstRun = false;
            expAdapter = new ListViewAdapterPhoto(this,R.layout.list_item_photo,R.id.lblListItemPhoto,listDataHeader,"");
            setExpAdapter(maindirpath);
            expListView.setAdapter(expAdapter);

            lv_adapter = new ArrayAdapter<>(_local_photo_list.this,
                    android.R.layout.simple_list_item_1,
                    lv_files);
            lv_files.add("Click here to select other folders");
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(lv_adapter);

            View headerView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            lv.addHeaderView(headerView);

        } catch (Exception e) {
            dialogBox(e.toString());
        }

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
                inflater = (LayoutInflater) _local_photo_list.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                layout = inflater.inflate(R.layout.popup_image,null);
                layout.findViewById(R.id.layout_root);
                LL_Photo.addView(layout);
                btn_rotate = layout.findViewById(R.id.BTN_ROTATE);
                Toast.makeText(_local_photo_list.this, "Slide Down to Close", Toast.LENGTH_LONG).show();
                RefreshGridView();
            }
        });


        expListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = parent.getItemAtPosition(position).toString();

                listDataHeader.clear();

                listDataHeader.add("[ BACK ]");

                try{

                    if (str.equals("[ BACK ]")){
                        rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
                    }else {
                        rootPath += "/" + str;
                    }
                    if (rootPath.length() == maindirpath.length()){
                        listDataHeader.remove(0);
                        File file = new File(rootPath);
                        File[] tempfile = file.listFiles();
                        if (tempfile.length != xItems.size()) { checkId.clear(); }

                    }
                    setExpAdapter(rootPath);
                    setImages(rootPath);
                    adapter.notifyDataSetChanged();
                    expAdapter.notifyDataSetChanged();

                }catch (Exception e){
                    dialogBox("Error 1: " + e.toString());
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
                    Log.d("EXCEPTION: ", "_local_photo_list.dialogBox: " + e.toString());
                }

                if (dialogProgress.isShowing()){
                    dialogProgress.dismiss();
                }
            }
        });

        dialogProgress = alertDialogBuilder.show();
    }

    public void setExpAdapter(String path){

        File maindir = new File(path);

        if (maindir.listFiles().length != 0){
            File arr[] = maindir.listFiles();

            for (int i = 0; i < arr.length; i++) {
                //
                if (arr[i].isDirectory()) {
                        listDataHeader.add(arr[i].getName());
                }
            }
            expAdapter.notifyDataSetChanged();
        }
    }

    public void setImages(final String imagePath){

        items = new ArrayList<>();
        lv_files = new ArrayList<>();
        directoryItems = new ArrayList<>();
        directoryIcons = new ArrayList<>();
        directoryMenu = new ArrayList<>();
        bitmapItems = new ArrayList<>();
        web = new ArrayList<>();

        adapter = new CustomGrid(_local_photo_list.this, web, "LocalPhoto");

        if(!adapter.isEmpty()){
            gridview.setAdapter(adapter);
        }

        web.clear();
        bitmapItems.clear();
        xItems.clear();
        ImageCounter = 0;

        xItems.addAll(GetFiles.getFiles(rootPath));

        if(xItems.size() == 0){
            gridview.setAdapter(adapter);
            return;
        }

        longOperation = new LongOperation();
        longOperation.execute(imagePath);
    }


    public class LongOperation extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(final String... params) {

            File file;

            try {

                for (String tempFile : xItems){

                    if (isCancelled()) { return "Cancelled"; }

                    if(!tempFile.startsWith(".")){

                        file = new File(params[0] + "/" + tempFile);

                        if (file.isDirectory()) {

                            directoryMenu.add(tempFile);
                        } else {
                            boolean tempBool = true;
                            String str[] = {".png", ".jpg", ".jpeg", ".gif", ".bmp", ".tiff", ".exif"};
                            for (int x = 0; x < str.length; x++) {

                                if (!file.getName().toLowerCase().endsWith(str[x])) {
                                    tempBool = false;
                                } else {
                                    tempBool = true;
                                    break;
                                }

                            }
                            if (tempBool) {

                                web.add(tempFile);

                            }
                        }
                    }

                }
            } catch (Exception e) {
                return e.toString();
            }

            return "Finished";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(final String result) {
            gridview.setAdapter(adapter);
        }
    }

    private boolean setImages(int mIndex){


        myOptions = new RequestOptions()
            .fitCenter()
            .override(displayMetrics.widthPixels, displayMetrics.heightPixels);

        if(mIndex > 0){
            Glide.with(_local_photo_list.this)
                    .load(rootPath + "/" + web.get(mIndex - 1))
                    .apply(myOptions)
                    .into(mImage);
        }

        if(mIndex < gridview.getCount() - 1){
            Glide.with(_local_photo_list.this)
                    .load(rootPath + "/" + web.get(mIndex + 1))
                    .apply(myOptions)
                    .into(mImage3);
        }
    return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void RefreshGridView(){

        try {

            LL_Image = layout.findViewById(R.id.LL_Image);
            mImage = new ImageView(_local_photo_list.this);
            mImage2 = new ImageView(_local_photo_list.this);
            mImage3 = new ImageView(_local_photo_list.this);

            BitmapFactory.Options options = new BitmapFactory.Options();
            displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            myOptions = new RequestOptions()
                .fitCenter()
                .override(displayMetrics.widthPixels, displayMetrics.heightPixels);
            options.inSampleSize = 2;

            Glide.with(_local_photo_list.this)
                    .load(rootPath + "/" + web.get(index))
                    .apply(myOptions)
                    .into(mImage2);

            setImages(index);

            btn_rotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                try {
                    ExifInterface ei = new ExifInterface(rootPath + "/" + web.get(index));
                    ei.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_ROTATE_90);
                    ei.saveAttributes();

                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    BitmapDrawable drawable = (BitmapDrawable) mImage2.getDrawable();
                    Bitmap rotatedBitmap = null;

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:

                            rotatedBitmap = rotateImage(drawable.getBitmap(), 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(drawable.getBitmap(), 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(drawable.getBitmap(), 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = drawable.getBitmap();
                    }

                    Glide.with(_local_photo_list.this)
                            .load(rotatedBitmap)
                            .apply(myOptions)
                            .into(mImage2);

                }catch (Exception e){
                    Log.d("EXCEPTION: ", "_local_photo_list.RefreshGridView: " + e.toString());
                }
                }
            });

            LL_Image.addView(mImage2);

            lp = (LayoutParams) mImage2.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mImage2.setLayoutParams(lp);

            mScaleDetector = new ScaleGestureDetector(this, new MyPinchListener());

            mImage2.setOnTouchListener(new View.OnTouchListener() {

                GestureDetector gestureDetector = new GestureDetector(_local_photo_list.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d("TEST", "onDoubleTap");
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    if(gestureDetector.onTouchEvent(event)) {
                        Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                        double height = mImage2.getLayoutParams().height * .25;
                        double width = mImage2.getLayoutParams().width * .25;
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                        LL_Image.setLayoutParams(layoutParams);
                        return true;
                    }

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
                                Log.d("EXCEPTION: ", "_local_photo_list.mImage2.setOnTouchListener");
                            }

                            break;
                    }
                    return true;
                }
            });



        } catch (Exception e) {
            Log.d("EXCEPTION: ", "_local_photo_list.RefreshGridView: " + e.toString());
        }
    }

    public void preventClicks(View view) {}

    private float downX, downY, upX, upY;

    public String getImagePath(){
        return rootPath;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public float distance(MotionEvent event, int first, int second) {
        if (event.getPointerCount() >= 2) {
            final float x = event.getX(first) - event.getX(second);
            final float y = event.getY(first) - event.getY(second);

            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    static class MyPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("THIS IS A PINCH!", "");
            return true;
        }
    }
}
