package com.example.MediaPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterPhoto extends ArrayAdapter<String> {
    private int groupid;
    private String type;
    private ArrayList<String> item_list;
    Context context;


    public ListViewAdapterPhoto(Context context, int vg, int id, ArrayList<String> item_list, String type){
        super(context,vg, id, item_list);
        this.context=context;
        this.type = type;
        this.groupid=vg;
        this.item_list=item_list;
    }
    static class ViewHolder {
        public TextView textview;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textview = rowView.findViewById(R.id.lblListItemPhoto);
            rowView.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textview.setText(item_list.get(position));

        return rowView;
    }


}

