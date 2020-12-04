package com.example.MediaPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterPlaylist extends ArrayAdapter<String> {
    private int groupid;
    private String type;
    private ArrayList<String> item_list;
    Context context;
    private _local_audio local_audio;
    private _wifi_audio wifi_audio;
    LayoutInflater inflater;


    public ListViewAdapterPlaylist(Context context, int vg, int id, ArrayList<String> item_list, String type,
                                   _local_audio local_audio, _wifi_audio wifi_audio){
        super(context,vg, id, item_list);
        this.context=context;
        this.type = type;
        this.groupid=vg;
        this.item_list=item_list;
        this.local_audio = local_audio;
        this.wifi_audio = wifi_audio;
    }
    static class ViewHolder {
        public TextView textview;
        public Button btnPlaylist;
        public Button btnViewPlaylist;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textview = rowView.findViewById(R.id.lblListItemAudio);
            viewHolder.btnPlaylist = rowView.findViewById(R.id.BTN_ADD_PLAYLIST);
            viewHolder.btnViewPlaylist = rowView.findViewById(R.id.BTN_VIEW_PLAYLIST);
            rowView.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textview.setText(item_list.get(position));
        holder.btnPlaylist.setBackgroundResource(R.drawable.delete);
        holder.btnPlaylist.setVisibility(View.VISIBLE);
        holder.btnViewPlaylist.setVisibility(View.VISIBLE);

        if(!type.equals("playlist")){
            holder.btnViewPlaylist.setVisibility(View.INVISIBLE);
        }

        if(local_audio != null){
            holder.btnPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type.equals("playlist")){
                        local_audio.DeletePlaylist(item_list.get(position));
                    }else{
                        local_audio.DeletePlaylistSong(item_list.get(position));
                    }
                }
            });

            holder.btnViewPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    local_audio.ViewPlaylist(item_list.get(position));
                }
            });
        }

        if(wifi_audio != null){
            holder.btnPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type.equals("playlist")){
                        wifi_audio.DeletePlaylist(item_list.get(position));
                    }else{
                        wifi_audio.DeletePlaylistSong(item_list.get(position));
                    }
                }
            });

            holder.btnViewPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifi_audio.ViewPlaylist(item_list.get(position));
                }
            });
        }
        return rowView;
    }


}

