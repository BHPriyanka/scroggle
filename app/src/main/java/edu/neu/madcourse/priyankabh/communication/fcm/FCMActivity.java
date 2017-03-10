package edu.neu.madcourse.priyankabh.communication.fcm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;

/**
 * Created by priya on 2/26/2017.
 */

public class FCMActivity extends AppCompatActivity {
    private static final String TAG = FCMActivity.class.getSimpleName();
    private String chosenUserKey="";
    private CoordinatorLayout mCoordinatorLayout;

    // Please add the server key from your firebase console in the follwoing format "key=<serverKey>"
    private static final String SERVER_KEY = "key=AAAA3ITLrYc:APA91bEO4XNNsyoIbhH4T9y_NqaKMstR2BwSAgCG9I8-m9JzsKrzxi9XhNOArq2ShRPSM6mrOwvYj2-11o4JDVML2Oqca7HwAe13xcIssT7Z2dJX9Wg9G1ydLOOijzn47tUt7PG_Joq5";

    // This is the client registration token
    private static final String CLIENT_REGISTRATION_TOKEN = "c-oERAVG_xs:APA91bHP1pvmhMVupa-_1Byypo0g2c6CSop6i14OKFmLP4hQwd5RSvYhthQhGPOwzRtXtBCkURQYWzgAG3VnrZzFa7GZ8DErJbgny74maYjbgHPkfZpAlvDVO5bYkni6TLcb2rjY9115";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        try{
            boolean isVisible = GlobalClass.isActivityVisible();
            //If its visible, trigger task, else do nothing
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

            // At activity startup we manually check the internet status and change
            // the text status
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new CommunicationActivity().changeTextStatus(true);
            } else {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                        "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Custom action
                                Intent intent = new Intent(FCMActivity.this, CommunicationActivity.class);
                                FCMActivity.this.startActivity(intent);
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View view = snackbar.getView();
                TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        Button registerButton = (Button) findViewById(R.id.registerUserToDatabaseButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(FCMActivity.this);
                //mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setTitle("Enter your Full Name");
                mDialog.setContentView(R.layout.register_user);
                mDialog.setCancelable(true);

                //set up text
                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FCMActivity.this, RegisterUserActivity.class);
                        String score = "";
                        EditText text = (EditText) mDialog.findViewById(R.id.user_name);
                        intent.putExtra("userName", text.getText().toString());
                        intent.putExtra("score", score);
                        String token = FirebaseInstanceId.getInstance().getToken();
                        intent.putExtra("token", token);
                        startActivity(intent);
                        if (mDialog != null)
                            mDialog.dismiss();

                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        Button choose = (Button) findViewById(R.id.ok_button);
        // get the list of users from the database

        new fetchDataFromFireBase().execute();

        choose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                final Dialog mDialog = new Dialog(FCMActivity.this);
                mDialog.setTitle("Players");
                mDialog.setContentView(R.layout.choose_user);
                mDialog.setCancelable(true);
                final ListView listView = (ListView) mDialog.findViewById(R.id.list_users);

                // Define a new Adapter
                // First parameter - Context
                // Second parameter - Layout for the row
                // Third parameter - ID of the TextView to which the data is written
                // Forth - the Array of data
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FCMActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, globalVariable.names);

                // Assign adapter to ListView
                listView.setAdapter(adapter);

                // ListView Item Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // ListView Clicked item index
                        int itemPosition = position;

                        // ListView Clicked item value
                        String  itemValue  = (String) listView.getItemAtPosition(position);

                        // Show Alert
                        Toast.makeText(getApplicationContext(),
                                "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();

                        for (Map.Entry<String, Object> entry : globalVariable.usersMap.entrySet()) {
                            Map singleUser = (Map) entry.getValue();
                            if (itemValue.equals(singleUser.get("username"))) {
                                chosenUserKey = entry.getKey();
                            }
                        }

                        pushNotification();

                        if (mDialog != null)
                            mDialog.dismiss();
                    }

                });


                mDialog.show();
            }
        });

     }

    /*public void pushNotification(View type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification();
            }
        }).start();
    }*/

    public void pushNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushDNotification();
            }
        }).start();
    }

    private void pushDNotification() {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Google I/O 2016");
            jNotification.put("body", "Do you want to Play?");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // If sending to a single client
            jPayload.put("to", chosenUserKey);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + resp);
                    Toast.makeText(FCMActivity.this,resp,Toast.LENGTH_LONG);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

/*
    private void pushNotification() {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Google I/O 2016");
            jNotification.put("body", "Firebase Cloud Messaging (App)");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // If sending to a single client
            jPayload.put("to", CLIENT_REGISTRATION_TOKEN);


            // If sending to multiple clients (must be more than 1 and less than 1000)
            //JSONArray ja = new JSONArray();
            //ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            //ja.put(FirebaseInstanceId.getInstance().getToken());
            //jPayload.put("registration_ids", ja);


            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + resp);
                    Toast.makeText(FCMActivity.this,resp,Toast.LENGTH_LONG);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }*/

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }



    public void fetchUsersFromDatabase(){
        //Get datasnapshot at your "users" root node
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("users");
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        dbref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        globalVariable.usersMap = (Map<String,Object>) dataSnapshot.getValue();
                        collectUserNames();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
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


    private class fetchDataFromFireBase extends AsyncTask<Void, Integer, Void> {

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

    @Override
    protected void onPause() {

        super.onPause();
        GlobalClass.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        GlobalClass.activityResumed();// On Resume notify the Application
    }

}
