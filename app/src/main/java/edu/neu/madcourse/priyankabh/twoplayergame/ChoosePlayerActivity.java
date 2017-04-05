package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;
import edu.neu.madcourse.priyankabh.twoplayergame.models.Player;

import static edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity.IS_NETWORK_AVAILABLE;

/**
 * Created by priya on 3/21/2017.
 */

public class ChoosePlayerActivity extends Activity {
    private String chosenUserKey="";
    String token;
    private DatabaseReference mDatabase;
    private Dialog dialog;
    private static final String SERVER_KEY = "key=AAAA3ITLrYc:APA91bEO4XNNsyoIbhH4T9y_NqaKMstR2BwSAgCG9I8-m9JzsKrzxi9XhNOArq2ShRPSM6mrOwvYj2-11o4JDVML2Oqca7HwAe13xcIssT7Z2dJX9Wg9G1ydLOOijzn47tUt7PG_Joq5";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(DetectNetworkActivity.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                if(networkStatus.equals("connected")){
                   // Log.d("MainActivity","networkStatus :" +networkStatus +" "+dialog.isShowing()+" "+dialog);
//                    text.setText("Internet connected");
                    if(dialog!=null && dialog.isShowing()){
                       // Log.d("MainActivity", "onReceive: ...................");
                        dialog.cancel();
                        dialog.dismiss();
                        dialog.hide();
                    }
                } else {
                    Log.d("MainActivity","networkStatus :" +networkStatus);
                    if(dialog == null){
                      ///  Log.d("MainActivity", "onReceive:d ");
                        dialog = new Dialog(ChoosePlayerActivity.this);
                        dialog.setContentView(R.layout.internet_connectivity);
                        dialog.setCancelable(false);
                        TextView text = (TextView) dialog.findViewById(R.id.internet_connection);
                        text.setText("Internet Disconnected");
                        dialog.show();
                    } else if(dialog != null && !dialog.isShowing()){
                       // Log.d("MainActivity", "onReceive:d.. ");
                        dialog.show();
                    }
                }
            }
        }, intentFilter);

        final Dialog mDialog = new Dialog(ChoosePlayerActivity.this);
        mDialog.setTitle("Players");
        mDialog.setContentView(R.layout.choose_user);
        mDialog.setCancelable(false);
        final ListView listView = (ListView) mDialog.findViewById(R.id.list_users);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        token = FirebaseInstanceId.getInstance().getToken();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        List<String> playersWithCurrentPlayer = new ArrayList<String>();
        if (globalVariable.usersMap != null && globalVariable.usersMap.containsKey(token)) {
            playersWithCurrentPlayer = globalVariable.names;
            Map<String, Object> p = (Map<String, Object>) globalVariable.usersMap.get(token);
            playersWithCurrentPlayer.remove(p.get("username"));

        }
        if ((playersWithCurrentPlayer.size() != 0)) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChoosePlayerActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, playersWithCurrentPlayer);

            // Assign adapter to ListView
            listView.setAdapter(adapter);

            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) listView.getItemAtPosition(position);

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "  Opponent Chosen : " + itemValue, Toast.LENGTH_LONG).show();

                    for (Map.Entry<String, Object> entry : globalVariable.usersMap.entrySet()) {
                        Map singleUser = (Map) entry.getValue();
                        if (itemValue.equals(singleUser.get("username"))) {
                            chosenUserKey = entry.getKey();
                            globalVariable.pairPlayers.put(token, chosenUserKey);
                            globalVariable.pairPlayers.put(chosenUserKey, token);
                        }
                    }

                    mDatabase.child("players").child(token).child("opponent").setValue(chosenUserKey);
                    mDatabase.child("players").child(chosenUserKey).child("opponent").setValue(token);

                    pushNotification();

                    if (mDialog != null) {
                        mDialog.dismiss();
                        Intent intent = new Intent(ChoosePlayerActivity.this, OpponentProfileActivity.class);
                        startActivity(intent);
                    }
                }

            });

            mDialog.show();
        } else {
            Intent intent = new Intent(ChoosePlayerActivity.this, TwoPlayerWordGameActivity.class);
            //intent.putExtra("isPlayer2", isPlayer2);
            startActivity(intent);
        }
    }


    public void pushNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushDNotification();
            }
        }).start();
    }

    private void pushDNotification() {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Scroggle");
            if(globalVariable.usersMap.containsKey(token)){
                Map<String,Object> p = (Map<String, Object>) globalVariable.usersMap.get(token);
                String user = (String) p.get("username");
                jNotification.put("body", user + " wants to play with you!!");

            } else {
                jNotification.put("body", "Do you want to Play?");
            }
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            //jNotification.put("click_action", OpponentProfileActivity.class);

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
                    //Log.e(TAG, "run: " + resp);
                    Toast.makeText(ChoosePlayerActivity.this,resp,Toast.LENGTH_LONG);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
