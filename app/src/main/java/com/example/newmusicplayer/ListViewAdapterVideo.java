package com.example.newmusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterVideo extends ArrayAdapter<String> {
    private int groupid;
    private String type;
    private ArrayList<String> item_list;
    Context context;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listDataSubs = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();
    private _local_video local_video;
    private _wifi_video wifi_video;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;



    public ListViewAdapterVideo(Context context, int vg, int id, ArrayList<String> item_list, String type,
                                _local_video local_video, _wifi_video wifi_video){
        super(context,vg, id, item_list);
        this.context=context;
        this.type = type;
        this.groupid=vg;
        this.item_list=item_list;
        this.local_video = local_video;
        this.wifi_video = wifi_video;
    }
    static class ViewHolder {
        public TextView textview;
        public RelativeLayout relativeLayout;
        public Button btn_play_subs;
        public Button btn_play_no_subs;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        View rowView = convertView;
        if (rowView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.relativeLayout = rowView.findViewById(R.id.LL_PlaySub);
            viewHolder.textview = rowView.findViewById(R.id.lblListItemVideo);
            viewHolder.btn_play_no_subs = rowView.findViewById(R.id.BTN_PLAY_WITHOUT_SUBS);
            viewHolder.btn_play_subs = rowView.findViewById(R.id.BTN_PLAY_WITH_SUBS);

            rowView.setTag(viewHolder);


        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textview.setText(item_list.get(position));

        holder.btn_play_no_subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(local_video != null){
                    local_video.playVideo("");
                }else if(wifi_video != null){
                    wifi_video.playVideo("");
                }

            }
        });

        holder.btn_play_subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = null;
                alertDialogBuilder = new AlertDialog.Builder(context);
                if(type.equals("video")){
                    inflater = wifi_video.getLayoutInflater();
                }else{
                    inflater = local_video.getLayoutInflater();
                }

                alertDialogBuilder.setView(inflater.inflate(R.layout.subtitle_select, null));
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                final ListView listView = alertDialog.findViewById(R.id.listSubtitleSelect);
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listDataSubs);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setAdapter(adapter);
                if(type.equals("video")){
                    map = wifi_video.getMapSubs();
                    for(String str : map.keySet()){
                        adapter.add(str.substring(str.lastIndexOf("\\") + 1).replace("\\", ""));
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    new Filewalker(local_video.GetRootPath());
                }

                adapter.notifyDataSetChanged();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(type.equals("video")){
                            map = wifi_video.getMapSubs();
                            wifi_video.playVideo(map.get(listView.getItemAtPosition(position).toString()) + "/" + listView.getItemAtPosition(position).toString());
                        }else{
                            local_video.playVideo(map.get(listView.getItemAtPosition(position).toString()));
                        }
                    }
                });
            }
        });

        if (type.equals("video")){
            if (_wifi_video.GetIndex() == position && rowView.isActivated() && !type.equals("")) {
                rowView.setBackgroundResource(R.color.colorAccent);
                holder.relativeLayout.setVisibility(View.VISIBLE);

            } else {

                rowView.setBackgroundColor(Color.TRANSPARENT);
                holder.relativeLayout.setVisibility(View.INVISIBLE);
            }
        }else{
            if (_local_video.GetIndex() == position && rowView.isActivated() && !type.equals("")) {
                rowView.setBackgroundResource(R.color.colorAccent);
                holder.relativeLayout.setVisibility(View.VISIBLE);

            } else {

                rowView.setBackgroundColor(Color.TRANSPARENT);
                holder.relativeLayout.setVisibility(View.INVISIBLE);
            }
        }


        return rowView;
    }

    public class Filewalker {

        public Filewalker(String root) {
            listDataSubs.clear();
            File file = new File(root);
            File[] list = file.listFiles();

            for (File f : list) {
                if (f.isDirectory()) {
                    new Filewalker(f.getPath());
                }else if (f.isFile() && f.getName().endsWith(".srt")) {
                    map.put(f.getName(), f.getPath());
                    listDataSubs.add(f.getName());
                }
            }
        }
    }
}

