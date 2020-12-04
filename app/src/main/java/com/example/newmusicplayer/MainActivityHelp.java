package com.example.newmusicplayer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivityHelp extends AppCompatActivity {

    Button faq, abyss, back, generate;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    LayoutInflater inflater;
    View layout;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);

        faq = findViewById(R.id.BTN_FAQ);
        back = findViewById(R.id.BTN_HELP_BACK);
        abyss = findViewById(R.id.BTN_ABYSS);
        generate = findViewById(R.id.BTN_GENERATE);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxFAQ();
            }
        });

        abyss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxAbyss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityHelp.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GenerateFiles();
                File fileMusic = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/music.php");
                File fileVideo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/video.php");
                File filePhoto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/photo.php");
                if(fileMusic.exists() && fileVideo.exists() && filePhoto.exists()) {
                    dialogBox("The php files needed to copy to your root /music, /video, and /photo folders have been generated and are " +
                            "located in your download folder on your phone.");
                }else{
                    dialogBox("An error has occurred with your request. Please make sure proper app permissions have been granted " +
                            "and try again");
                }
            }
        });
    }

    private void dialogBoxFAQ (){
        alertDialogBuilder = new AlertDialog.Builder(MainActivityHelp.this);
        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.help_dialog, null);
        alertDialogBuilder.setView(layout);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        expandableListView = new ExpandableListView(MainActivityHelp.this);
        expandableListView = (ExpandableListView) alertDialog.findViewById(R.id.help_listview);
        ExpandableListDataPumpFAQ pumpFAQ = new ExpandableListDataPumpFAQ();
        expandableListDetail = pumpFAQ.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ListViewAdapterHelp(MainActivityHelp.this, expandableListTitle, expandableListDetail);
        Log.d("HERE: ", "MADE IT HERE");
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition == 2 && childPosition == 0){
                    new GenerateFiles();
                }
                return false;
            }
        });
    }

    public class ExpandableListDataPumpFAQ {
        public LinkedHashMap<String, List<String>> getData() {
            LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

            List<String> available = new ArrayList<String>();
            available.add("As more questions or bugs are submitted, more FAQs will be added");

            List<String> photos = new ArrayList<String>();
            available.add("Photos from older cameras may not have EXIF data and will appear sideways in the picture grid and when you open them");

            List<String> bluetooth = new ArrayList<String>();
            bluetooth.add("You can sync Bluetooth to your car and hear the audio, however, there is not yet any support for using controls");

            List<String> abyss = new ArrayList<String>();
            abyss.add("You do not need to use Abyss. Php files that can be downloaded to your phone by clicking this answer can be " +
                            "dropped into your root media folders to be used by any server that has php support.");

            expandableListDetail.put("Common questions...", available);
            expandableListDetail.put("Why are my photos sideways?", photos);
            expandableListDetail.put("Can I use the Bluetooth in my car?", bluetooth);
            expandableListDetail.put("Do I need to use Abyss Web Server?", abyss);
            return expandableListDetail;
        }
    }

    private void dialogBoxAbyss (){
        alertDialogBuilder = new AlertDialog.Builder(MainActivityHelp.this);
        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.help_dialog, null);
        alertDialogBuilder.setView(layout);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        expandableListView = new ExpandableListView(MainActivityHelp.this);
        expandableListView = (ExpandableListView) alertDialog.findViewById(R.id.help_listview);
        ExpandableListDataPumpAbyss pumpAbyss = new ExpandableListDataPumpAbyss();
        expandableListDetail = pumpAbyss.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ListViewAdapterHelp(MainActivityHelp.this, expandableListTitle, expandableListDetail);
        Log.d("HERE: ", "MADE IT HERE");
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition == 7 && childPosition == 0){
                    new GenerateFiles();
                }
                return false;
            }
        });
    }

    public class ExpandableListDataPumpAbyss {
        public LinkedHashMap<String, List<String>> getData() {
            LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

            List<String> available = new ArrayList<String>();
            available.add("Google 'abyss web server download' > Choose your flavor > Install");

            List<String> abyssConfig = new ArrayList<String>();
            abyssConfig.add("Navigate to 127.0.0.1:9999 > Console Configuration > Fill out the form and save");
            abyssConfig.add("Next, click on Configure next to your server > Directory Listing > Disable");

            List<String> phpInstall = new ArrayList<String>();
            phpInstall.add("Navigate to php.net/downloads.php > In the top most stable build, click 'Windows Downloads' download the thread safe .zip file");
            phpInstall.add("Unzip the downloaded files then copy and paste it to the Abyss Web Server install directory in its own folder. eg... php");

            List<String> phpConfig = new ArrayList<String>();
            phpConfig.add("Navigate to 127.0.0.1:9999 > Configure > Scripting Parameters");
            phpConfig.add("in the Interpreters box > Add");
            phpConfig.add("Interface = FastCGI (Local - Pipes)");
            phpConfig.add("For Interpreter, click 'Browse', navigate to the php folder and select php-cgi.exe");
            phpConfig.add("Leave Arguments blank");
            phpConfig.add("Type = Standard");
            phpConfig.add("Add php to Associated Extensions then save");

            List<String> addUsers = new ArrayList<String>();
            addUsers.add("Navigate to 127.0.0.1:9999 > Configure > Users and Groups > Add a user");

            List<String> addAlias = new ArrayList<String>();
            addAlias.add("Navigate to 127.0.0.1:9999 > Configure > Aliases > Add alias");
            addAlias.add("In Alias box eg... /whatever then navigate to the 'Real Path' you want to share");

            List<String> lockAlias = new ArrayList<String>();
            lockAlias.add("Navigate to 127.0.0.1:9999 > Configure > Access Control");
            lockAlias.add("Click 'Add' and select the alias (virtual path) you want to lock");
            lockAlias.add("Put anything you want in 'Realm'");
            lockAlias.add("Leave 'Order' as-is and select the user(s) you want to have access or denied access");

            List<String> genFiles = new ArrayList<String>();
            genFiles.add("CLICK THIS ANSWER to generate the php files needed to copy to your root /music, /video, and /photo folders. " +
                    "Once they are generated they will be stored in your downloads folder on your phone");

            expandableListDetail.put("Step 1: Installing Abyss Web Server", available);
            expandableListDetail.put("Step 2: Configuring Abyss Settings", abyssConfig);
            expandableListDetail.put("Step 3: Installing php Support", phpInstall);
            expandableListDetail.put("Step 4: Configuring php Settings", phpConfig);
            expandableListDetail.put("Step 5: Adding Users", addUsers);
            expandableListDetail.put("Step 6: Adding Aliases", addAlias);
            expandableListDetail.put("Step 7: Locking Alias Directories", lockAlias);
            expandableListDetail.put("Step 8: Generate php Files", genFiles);


            return expandableListDetail;
        }
    }

    public void dialogBox(String s) {

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(s);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
