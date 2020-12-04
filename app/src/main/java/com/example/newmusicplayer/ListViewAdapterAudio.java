package com.example.newmusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class ListViewAdapterAudio extends ArrayAdapter<String> {
    private int groupid;
    private String type;
    private ArrayList<String> item_list, list_track_number;
    Context context;
    private _local_audio local_audio;
    private _wifi_audio wifi_audio;
    private _randomizer randomizer;
    LayoutInflater inflater;


    public ListViewAdapterAudio(Context context, int vg, int id, ArrayList<String> item_list, String type,
                                ArrayList<String> list_track_number,
                                _local_audio local_audio, _wifi_audio wifi_audio){
        super(context,vg, id, item_list);
        this.context = context;
        this.type = type;
        this.groupid = vg;
        this.item_list = item_list;
        this.list_track_number = list_track_number;
        this.local_audio = local_audio;
        this.wifi_audio = wifi_audio;
        randomizer = new _randomizer(local_audio, wifi_audio);
    }
    static class ViewHolder {
        public TextView txt_track_name, txt_track_number;
        public Button btnPlaylist;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.txt_track_name = rowView.findViewById(R.id.lblListItemAudio);
            viewHolder.txt_track_number = rowView.findViewById(R.id.txt_track_number);
            viewHolder.btnPlaylist = rowView.findViewById(R.id.BTN_ADD_PLAYLIST);
            rowView.setTag(viewHolder);


        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.txt_track_name.setText(item_list.get(position));

        if (type.equals("directory")){
            holder.txt_track_number.setVisibility(View.GONE);
            holder.btnPlaylist.setVisibility(View.GONE);
        }

        if (type.equals("localSong")) {
            if (list_track_number.size() > 0){ holder.txt_track_number.setText(list_track_number.get(position)); }

            if (!local_audio.GetShuffleState()) {
                if (local_audio.GetIndex() == position ) {
                    holder.btnPlaylist.setVisibility(View.VISIBLE);
                    rowView.setBackgroundResource(R.color.colorAccent);
                    rowView.setSelected(true);
                } else {
                    holder.btnPlaylist.setVisibility(View.INVISIBLE);
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                    rowView.setSelected(false);
                }
            } else {
                if (randomizer.GetNum() == position ) {
                    rowView.setSelected(true);
                    holder.btnPlaylist.setVisibility(View.VISIBLE);
                    rowView.setBackgroundResource(R.color.colorAccent);
                } else {
                    rowView.setSelected(false);
                    holder.btnPlaylist.setVisibility(View.INVISIBLE);
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }


        if (type.equals("wifiSong")) {
            if (!wifi_audio.GetShuffleState()) {
                if (wifi_audio.GetIndex() == position ) {
                    holder.btnPlaylist.setVisibility(View.VISIBLE);
                    rowView.setBackgroundResource(R.color.colorAccent);
                    rowView.setSelected(true);
                } else {
                    holder.btnPlaylist.setVisibility(View.INVISIBLE);
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                    rowView.setSelected(false);
                }
            } else {
                if (randomizer.GetNum() == position ) {
                    rowView.setSelected(true);
                    holder.btnPlaylist.setVisibility(View.VISIBLE);
                    rowView.setBackgroundResource(R.color.colorAccent);
                } else {
                    rowView.setSelected(false);
                    holder.btnPlaylist.setVisibility(View.INVISIBLE);
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }


        if(local_audio != null){
            if(local_audio.boolPlaylistLoaded){
                holder.btnPlaylist.setVisibility(View.INVISIBLE);
            }

            holder.btnPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    local_audio.AddToPlaylist();
                }
            });
        }

        if(wifi_audio != null){
            if(wifi_audio.boolPlaylistLoaded){
                holder.btnPlaylist.setVisibility(View.INVISIBLE);
            }

            holder.btnPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifi_audio.AddToPlaylist();
                }
            });
        }


        return rowView;
    }


}

