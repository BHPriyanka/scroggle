package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;
import java.util.Random;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.twoplayergame.models.Player;

import static edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity.IS_NETWORK_AVAILABLE;

/**
 * Created by priya on 3/11/2017.
 */

public class TwoPlayerWordGameActivity extends Activity {
    private String getTokenInstance;
    private int n;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_word_player_two);

        IntentFilter intentFilter = new IntentFilter(DetectNetworkActivity.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                if(networkStatus.equals("connected")){
                  //  Log.d("MainActivity","networkStatus :" +networkStatus +" "+dialog.isShowing()+" "+dialog);
//                    text.setText("Internet connected");
                    if(dialog!=null && dialog.isShowing()){
                      //  Log.d("MainActivity", "onReceive: ...................");
                        dialog.cancel();
                        dialog.dismiss();
                        dialog.hide();
                    }
                } else {
                    Log.d("MainActivity","networkStatus :" +networkStatus);
                    if(dialog == null){
                     //   Log.d("MainActivity", "onReceive:d ");
                        dialog = new Dialog(TwoPlayerWordGameActivity.this);
                        dialog.setContentView(R.layout.internet_connectivity);
                        dialog.setCancelable(false);
                        TextView text = (TextView) dialog.findViewById(R.id.internet_connection);
                        text.setText("Internet Disconnected");
                        dialog.show();
                    } else if(dialog != null && !dialog.isShowing()){
                     //   Log.d("MainActivity", "onReceive:d.. ");
                        dialog.show();
                    }
                }
            }
        }, intentFilter);

        getTokenInstance = FirebaseInstanceId.getInstance().getToken();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Random rand = new Random();
        n = rand.nextInt(50) + 1;

        TwoPlayerWordGameActivity.this.onAddGameIdForPlayer(mDatabase, getTokenInstance, n);
        if(globalVariable.pairPlayers.containsKey(getTokenInstance)) {
            TwoPlayerWordGameActivity.this.onAddGameIdForPlayer(mDatabase, globalVariable.pairPlayers.get(getTokenInstance), n);
        }
        fetchUsersFromDatabase();
    }

    private void onAddGameIdForPlayer(DatabaseReference postRef, String player, int n) {
        Log.d("TAG", "onAddGameIdForPlayer: "+player);
        final int val =n;
        postRef
                .child("players")
                .child(player)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Player u = mutableData.getValue(Player.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        if(u.gameID == 0) {
                            u.gameID = val;
                        }
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                    }
                });

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
                        globalVariable.usersMap = (Map<String,Object>) dataSnapshot.getValue();
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
     //   GlobalClass.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
       // GlobalClass.activityResumed();// On Resume notify the Application
    }

}
