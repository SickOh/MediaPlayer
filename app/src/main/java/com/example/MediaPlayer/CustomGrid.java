package com.example.MediaPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CustomGrid extends BaseAdapter {
    private Context mContext;
    private final ArrayList<String> web;
    private final String str;
    private _local_photo_list local_photo_list = new _local_photo_list();
    private _local_dcim_photo local_dcim_photo = new _local_dcim_photo();
    private _wifi_photo wifi_photo = new _wifi_photo();

    public CustomGrid(Context c,ArrayList<String> web, String str ) {
        mContext = c;
        this.str = str;
        this.web = web;
    }

    @Override
    public int getCount() {
        return web.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount(){
        return getCount() +1;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid;
        ImageView imageView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            imageView = new ImageView(mContext);
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.gridview_layout, null);
            imageView = grid.findViewById(R.id.ivBackground);
        } else {
            grid = (View) convertView;
            imageView = new ImageView(mContext);
        }


        grid.setMinimumHeight(300);
        grid.setMinimumWidth(300);

        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .format(DecodeFormat.PREFER_RGB_565)
                .override(300, 300);

        if(str.equals("DCIM")){
            try{
                Glide.with(mContext)
                        .load(local_dcim_photo.getImagePath() + "/" + web.get(position))
                        .apply(myOptions)
                        .into(imageView);
            }catch (Exception e){
                Log.d("EXCEPTION: ", "CustomGrid.getView");
            }
        }else if (str.equals("WifiPhoto")) {
            try{
                Log.d("THIS IS THE PATH: ", wifi_photo.getImagePath());
                Glide.with(mContext)
                        .load(wifi_photo.getImagePath() + "/" + web.get(position))
                        .apply(myOptions)
                        .into(imageView);
            }catch (Exception e){
                Log.d("EXCEPTION: ", "CustomGrid.getView");
            }
        }else{
            try{
                Glide.with(mContext)
                        .load(local_photo_list.getImagePath() + "/" + web.get(position))
                        .apply(myOptions)
                        .into(imageView);
                //imageView.setImageBitmap(mBitmap);
            }catch (Exception e){
                Log.d("EXCEPTION: ", "CustomGrid.getView");
            }

        }
        return grid;
    }
}