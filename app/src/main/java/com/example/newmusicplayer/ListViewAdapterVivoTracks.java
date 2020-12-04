package com.example.newmusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterVivoTracks extends ArrayAdapter<String> {
    private int groupid;
    private ArrayList<String> list_track, list_track_position, list_loaded_album_tracks;
    Context context;
    private _local_audio local_audio;


    public ListViewAdapterVivoTracks(Context context, int vg, int id,
                                     ArrayList<String> list_track,
                                     ArrayList<String> list_loaded_album_tracks,
                                     _local_audio local_audio){
        super(context,vg, id, list_track);
        this.context = context;
        this.groupid = vg;
        this.list_track = list_track;
        this.list_loaded_album_tracks = list_loaded_album_tracks;
        this.local_audio = local_audio;
    }
    static class ViewHolder {
        public Spinner spinner_loaded_album;
        public TextView textview_title;
        public Button btn_cancel, btn_ok;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);

            viewHolder.textview_title = rowView.findViewById(R.id.txt_track);
            viewHolder.spinner_loaded_album = rowView.findViewById(R.id.spinner_tracks);


            rowView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.textview_title.setText(list_track.get(position));
        viewHolder.spinner_loaded_album.setAdapter(new ArrayAdapter<>(context, R.layout.layout_spinner_item, list_loaded_album_tracks));

        for(int i = 0; i < list_loaded_album_tracks.size(); i++){
            String str1 = list_loaded_album_tracks.get(i).toLowerCase().replaceAll("\\\\W+", "");
            String str2 = list_track.get(position).toLowerCase().replaceAll("\\\\W+", "");

            if (!str1.contains(str2)){
                viewHolder.spinner_loaded_album.setSelection(0);
                rowView.setBackgroundColor(context.getColor(R.color.LightBlue));
            }else{
                viewHolder.spinner_loaded_album.setSelection(i);
                rowView.setBackgroundColor(context.getColor(R.color.White));
                break;
            }
        }


        return rowView;
    }


}

