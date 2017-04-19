package edu.neu.madcourse.priyankabh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.priyankabh.note2map.Note2MapMainActivity;
import edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity;
import edu.neu.madcourse.priyankabh.twoplayergame.RegisterActivity;
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;
import edu.neu.madcourse.priyankabh.dictionary.TestDictionary;
import edu.neu.madcourse.priyankabh.scroggle.WordGame;
import edu.neu.madcourse.priyankabh.tictactoe.TicTacToeMainActivity;

import static edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity.IS_NETWORK_AVAILABLE;

public class MainActivity extends Activity {

    public static boolean activityVisible; // Variable that will check the
    // current activity state

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ProgressDialog progressDialog;

    Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(DetectNetworkActivity.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                if(networkStatus.equals("connected")){
                 //   Log.d("MainActivity","networkStatus :" +networkStatus +" "+dialog.isShowing()+" "+dialog);
//                    text.setText("Internet connected");
                    if(dialog!=null && dialog.isShowing()){
                     //   Log.d("MainActivity", "onReceive: ...................");
                        dialog.cancel();
                        dialog.dismiss();
                        dialog.hide();
                    }
                } else {
                  //  Log.d("MainActivity","networkStatus :" +networkStatus);
                    if(dialog == null){
                    //    Log.d("MainActivity", "onReceive:d ");
                        dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.internet_connectivity);
                        dialog.setCancelable(false);
                        TextView text = (TextView) dialog.findViewById(R.id.internet_connection);
                        text.setText("Internet Disconnected");
                        dialog.show();
                    } else if(dialog != null && !dialog.isShowing()){
                      //  Log.d("MainActivity", "onReceive:d.. ");
                        dialog.show();
                    }
                }
            }
        }, intentFilter);

        this.setTitle("B H Priyanka");

        new fetchDataFromFireBase().execute();

        if(globalVariable.list.isEmpty()){
            new LoadWordList().execute();
        }

        //this button will show the dialog
        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.about_me);
                mDialog.setCancelable(true);

                // set up imei id-generate a 16 digit id
                TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String imei = manager.getDeviceId();

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.TextView01);
                text.setText(R.string.about_me);
                text.append(imei);

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        Button generateErrorButton = (Button) findViewById(R.id.generate_error_button);
        generateErrorButton.setOnClickListener(new View.OnClickListener() {
            public AlertDialog alertDialog;

            @Override
            public void onClick(View v) {
                //invoke an object without initializing to crash the app
                alertDialog.show();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button tictactoeButton = (Button) findViewById(R.id.tictactoe_button);
        tictactoeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TicTacToeMainActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Button dictionaryButton = (Button) findViewById(R.id.dictionary_button);
        dictionaryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestDictionary.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Button newGameButton = (Button) findViewById(R.id.scroggle_button);
        newGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, WordGame.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Button communicationButton = (Button) findViewById(R.id.communication_button);
        communicationButton.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, CommunicationActivity.class);
               MainActivity.this.startActivity(intent);
            }
        });

        Button twoPlayerButton = (Button) findViewById(R.id.twoplayer_button);
        twoPlayerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(MainActivity.this);
                //mDialog.setTitle("Enter your Full Name");
                mDialog.setContentView(R.layout.register_user);
                mDialog.setCancelable(true);

                //set up text
                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        String score = "";
                        EditText text = (EditText) mDialog.findViewById(R.id.user_name);
                        intent.putExtra("userName", text.getText().toString());
                        intent.putExtra("score", score);
                        String token = FirebaseInstanceId.getInstance().getToken();
                        intent.putExtra("token", token);
                        intent.putExtra("gameState", "");
                        startActivity(intent);
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });

                mDialog.show();
            }
        });


        Button trickiestPartButton = (Button) findViewById(R.id.trickiest_part_button);
        trickiestPartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, Note2MapMainActivity.class);
                startActivity(intent);
            }
        });

        Button quitButton = (Button) findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                globalVariable.list.clear();

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    private class LoadWordList extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            try {
                InputStream strF = getResources().getAssets().open("hashmap");
                ObjectInputStream ois=new ObjectInputStream(strF);

                globalVariable.list = (HashMap<String,ArrayList<String>>)ois.readObject();

                ois.close();

                //System.out.println("TestDictionary Loading done");

            } catch(IOException e) {
                System.err.print(e);
            }catch(ClassNotFoundException ce){
                System.err.print(ce);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
//            progressDialog.setProgress(params[0]);
        }

        protected void onPostExecute(Void v) {
          //  if (progressDialog != null && progressDialog.isShowing()) {
         //       progressDialog.dismiss();
         //   }
        }
    }

    public class fetchDataFromFireBase extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            fetchUsersFromDatabase();
            return null;
        }

        protected void onProgressUpdate(Integer... params) {

        }

        protected void onPostExecute(Void v) {
        }

    }

    private void collectUserNames() {
        //iterate through each user, ignoring their UID
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        for (Map.Entry<String, Object> entry : globalVariable.usersMap.entrySet()){
            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            if(!globalVariable.names.contains((String) singleUser.get("username"))){
                globalVariable.names.add((String) singleUser.get("username"));
            }
        }

    }

    public void fetchUsersFromDatabase(){
        //Get datasnapshot at your "users" root node
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("players");
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        dbref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        GenericTypeIndicator<Map<String,Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                        globalVariable.usersMap = dataSnapshot.getValue(genericTypeIndicator);

                     //   globalVariable.usersMap = (Map<String,Object>) dataSnapshot.getValue();
                        if(globalVariable.usersMap != null) {
                            collectUserNames();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    protected void onPause() {

        super.onPause();
        //GlobalClass.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
       // GlobalClass.activityResumed();// On Pause notify the Application
    }


}
