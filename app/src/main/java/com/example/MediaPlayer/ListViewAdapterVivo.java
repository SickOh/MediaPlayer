package com.example.MediaPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterVivo extends ArrayAdapter<String> {
    private int groupid;
    private String type;
    private ArrayList<String> item_list;
    private ArrayList<Boolean> checkBoxes;
    private ArrayList<Bitmap> list_album_art;
    private ArrayList<String> list_title;
    private ArrayList<String> list_artist;
    private ArrayList<String> list_album;
    private ArrayList<String> list_track_list;
    Context context;
    private DatabaseManageServers databaseManageServers;
    private _local_audio local_audio;


    public ListViewAdapterVivo(Context context, int vg, int id, ArrayList<String> list_title,
                               ArrayList<Bitmap> list_album_art,
                               ArrayList<String> list_album,
                               ArrayList<String> list_artist,
                               ArrayList<String> list_track_list,
                               _local_audio local_audio){
        super(context,vg, id, list_title);
        this.context=context;
        this.list_album_art = list_album_art;
        this.list_title = list_title;
        this.list_album = list_album;
        this.list_artist = list_artist;
        this.list_track_list = list_track_list;
        this.type = type;
        this.groupid=vg;
        this.item_list=item_list;
        this.local_audio = local_audio;
    }
    static class ViewHolder {
        public ImageView albumArt;
        public TextView textview_title;
        public TextView textview_artist;
        public TextView textview_album;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);

            viewHolder.albumArt = rowView.findViewById(R.id.iv_album_art);
            viewHolder.textview_title = rowView.findViewById(R.id.vivo_media_title);
            viewHolder.textview_artist = rowView.findViewById(R.id.vivo_media_artist);
            viewHolder.textview_album = rowView.findViewById(R.id.vivo_media_album);

            rowView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.textview_title.setText(list_title.get(position));
        viewHolder.textview_artist.setText(list_artist.get(position));
        viewHolder.textview_album.setText(list_album.get(position));
        viewHolder.albumArt.setImageBitmap(list_album_art.get(position));

        if (local_audio.GetAlbumIndex() == position) {
            rowView.setBackgroundResource(R.color.colorAccent);
        } else {
            rowView.setBackgroundColor(Color.TRANSPARENT);
        }

        /*
        //holder.textview.setText(item_list.get(position));
        if (type.equals("dbAudio") || type.equals("dbVideo") || type.equals("dbPhoto")) {
            //holder.checkBox.setChecked(checkBoxes.get(position));

        }

         */

        return rowView;
    }


}

