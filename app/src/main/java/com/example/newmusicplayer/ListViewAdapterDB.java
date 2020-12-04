package com.example.newmusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterDB extends ArrayAdapter<String> {
    private int groupid;
    private String type;
    private ArrayList<String> item_list;
    private ArrayList<Boolean> checkBoxes;
    Context context;
    private DatabaseManageServers databaseManageServers;


    public ListViewAdapterDB(Context context, int vg, int id, ArrayList<String> item_list, ArrayList<Boolean> checkBoxes, String type,
                             DatabaseManageServers databaseManageServers){
        super(context,vg, id, item_list);
        this.checkBoxes = checkBoxes;
        this.context=context;
        this.type = type;
        this.groupid=vg;
        this.item_list=item_list;
        this.databaseManageServers = databaseManageServers;
    }
    static class ViewHolder {
        public TextView textview;
        public CheckBox checkBox;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textview = rowView.findViewById(R.id.lblListItemDB);
            if (type.equals("dbAudio") || type.equals("dbVideo") || type.equals("dbPhoto")) {
                viewHolder.checkBox = new CheckBox(context);
                viewHolder.checkBox = rowView.findViewById((R.id.lblCheckItem));
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }
            rowView.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textview.setText(item_list.get(position));
        if (type.equals("dbAudio") || type.equals("dbVideo") || type.equals("dbPhoto")) {
            holder.checkBox.setChecked(checkBoxes.get(position));
            if (databaseManageServers.GetIndex() == position) {
                rowView.setBackgroundResource(R.color.colorAccent);
            } else {
                rowView.setBackgroundColor(Color.TRANSPARENT);
            }
        }






        return rowView;
    }


}

