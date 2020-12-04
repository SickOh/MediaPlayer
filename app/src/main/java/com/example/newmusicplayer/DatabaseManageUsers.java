package com.example.newmusicplayer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class DatabaseManageUsers extends AppCompatActivity {

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    Cursor cursor;
    ListView listView;
    ArrayList<String> userItems = new ArrayList<>();
    ArrayAdapter lstViewAdapter;
    Button addUser, editUser, removeUser;
    EditText userNameEditText, userPasswordEditText, userPinEditText, textInputEditText;
    String pinNumber = "", listViewSelection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_manage_users);

        listView = findViewById(R.id.listUsers);
        addUser = findViewById(R.id.BTN_ADD_USER);
        editUser = findViewById(R.id.BTN_EDIT_USER);
        removeUser = findViewById(R.id.BTN_REMOVE_USER);


        lstViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userItems);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(lstViewAdapter);

        getUserList();

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewUser(false);
            }
        });

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewUser(true);
            }
        });

        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = new DatabaseSQLiteHelper(DatabaseManageUsers.this).getWritableDatabase();
                database.execSQL("DELETE FROM users WHERE pin = '" + pinNumber + "'");
                RefreshList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewSelection = listView.getItemAtPosition(position).toString();
            }
        });
    }

    private Cursor readFromDB2(){
        String queryString = "SELECT * FROM users WHERE user = ?";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        String[] args = new String[]{ listViewSelection };
        return database.rawQuery(queryString, args);
    }

    private Cursor readFromDB(){
        String queryString = "SELECT * FROM users WHERE pin = ?";
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getReadableDatabase();
        String[] args = new String[]{ pinNumber };
        return database.rawQuery(queryString, args);
    }

    private void getUserList(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(inflater.inflate(R.layout.database_pin_number, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btn_ok = alertDialog.findViewById(R.id.BTN_PIN_OK);
        textInputEditText = alertDialog.findViewById(R.id.pinNumberEditText);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pinNumber = textInputEditText.getText().toString();
                alertDialog.dismiss();

                RefreshList();


            }
        });
    }

    private void RefreshList(){
        userItems.clear();

        try{
            cursor = readFromDB();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if(cursor.getString(2).equals(pinNumber)){
                        userItems.add(cursor.getString(0));
                    }
                    cursor.moveToNext();
                }
            }
        }catch (Exception e){
            Log.d("EXCEPTION: ", "_wifi_video.RefreshList: " + e.toString());
        }
        lstViewAdapter.notifyDataSetChanged();
    }

    private void AddNewUser(final boolean boolUpdate){
        if(listViewSelection.equals("") && boolUpdate){ return; }
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(inflater.inflate(R.layout.database_add_edit_user, null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        userNameEditText = alertDialog.findViewById(R.id.UserNameEditText);
        userPasswordEditText = alertDialog.findViewById(R.id.UserPasswordEditText);
        userPinEditText = alertDialog.findViewById(R.id.UserPinEditText);
        Button btn_save = alertDialog.findViewById(R.id.UserSaveButton);
        Button btn_cancel = alertDialog.findViewById(R.id.UserCancelButton);

        if(boolUpdate){
            btn_save.setText("UPDATE");

            try{
                cursor = readFromDB2();
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        if(cursor.getString(2).equals(pinNumber)){
                            userNameEditText.setText(cursor.getString(0));
                            userPasswordEditText.setText(cursor.getString(1));
                            userPinEditText.setText(cursor.getString(2));
                        }
                        cursor.moveToNext();
                    }
                }
            }catch (Exception e){
                Log.d("EXCEPTION: ", "_wifi_video.AddNewUser: " + e.toString());
            }
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userPinEditText.getText().toString().equals("")){ return; }

                if(boolUpdate){
                    UpdateDatabase();
                    getUserList();
                }else{
                    SaveToDatabase();
                    getUserList();
                }

                listViewSelection = "";
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                listViewSelection = "";
            }
        });
    }

    private void SaveToDatabase(){
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getWritableDatabase();
        ContentValues serverValues = new ContentValues();
        serverValues.put(DatabaseServerInfo.Server.COLUMN_USER, userNameEditText.getText().toString());
        serverValues.put(DatabaseServerInfo.Server.COLUMN_PASSWORD, userPasswordEditText.getText().toString());
        serverValues.put(DatabaseServerInfo.Server.COLUMN_PIN, userPinEditText.getText().toString());
        database.insert(DatabaseServerInfo.Server.TABLE_NAME_USER, null, serverValues);
        alertDialog.dismiss();
    }

    private void UpdateDatabase(){
        SQLiteDatabase database = new DatabaseSQLiteHelper(this).getWritableDatabase();
        ContentValues serverValues = new ContentValues();
        serverValues.put(DatabaseServerInfo.Server.COLUMN_USER, userNameEditText.getText().toString());
        serverValues.put(DatabaseServerInfo.Server.COLUMN_PASSWORD, userPasswordEditText.getText().toString());
        serverValues.put(DatabaseServerInfo.Server.COLUMN_PIN, userPinEditText.getText().toString());
        String[] mArgs = new String[]{ listViewSelection };
        database.update(DatabaseServerInfo.Server.TABLE_NAME_USER, serverValues, "user = ?", mArgs);
        alertDialog.dismiss();
    }
}
