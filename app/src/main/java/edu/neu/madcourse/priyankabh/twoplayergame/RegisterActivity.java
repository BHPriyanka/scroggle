package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.scroggle.WordGame;
import edu.neu.madcourse.priyankabh.twoplayergame.models.Player;

import static edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity.IS_NETWORK_AVAILABLE;

/**
 * Created by priya on 3/3/2017.
 */

public class RegisterActivity extends Activity {
    private DatabaseReference mDatabase;
    private String userName="";
    private String userScore="";
    private String userToken="";
    private String gameState="";
    private String token;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter(DetectNetworkActivity.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                if(networkStatus.equals("connected")){
                    Log.d("MainActivity","networkStatus :" +networkStatus +" "+dialog.isShowing()+" "+dialog);
//                    text.setText("Internet connected");
                    if(dialog!=null && dialog.isShowing()){
                        Log.d("MainActivity", "onReceive: ...................");
                        dialog.cancel();
                        dialog.dismiss();
                        dialog.hide();
                    }
                } else {
                    Log.d("MainActivity","networkStatus :" +networkStatus);
                    if(dialog == null){
                        Log.d("MainActivity", "onReceive:d ");
                        dialog = new Dialog(RegisterActivity.this);
                        dialog.setContentView(R.layout.internet_connectivity);
                        dialog.setCancelable(false);
                        TextView text = (TextView) dialog.findViewById(R.id.internet_connection);
                        text.setText("Internet Disconnected");
                        dialog.show();
                    } else if(dialog != null && !dialog.isShowing()){
                        Log.d("MainActivity", "onReceive:d.. ");
                        dialog.show();
                    }
                }
            }
        }, intentFilter);

        Bundle b = this.getIntent().getExtras();

        if (b != null) {
            userName = b.getString("userName");
            userScore = b.getString("score");
            userToken = b.getString("token");
            gameState = b.getString("gameState");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

            if(globalVariable.usersMap != null && globalVariable.usersMap.containsKey(userToken)){
                String str = "User already registered";
                Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
                Intent intent;
                if(globalVariable.usersMap.size() == 0 || (globalVariable.usersMap.size() == 1 &&
                globalVariable.usersMap.containsKey(token))){
                    intent = new Intent(RegisterActivity.this,WordGame.class);
                }else {
                    intent = new Intent(RegisterActivity.this,ChoosePlayerActivity.class);
                }
                startActivity(intent);
            } else {
                writeNewUser(userToken, userScore, userName, gameState);
                String msg = "Registered Successfully";
                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                Intent intent;
                if(globalVariable.usersMap.size() == 0 || (globalVariable.usersMap.size() == 1 &&
                        globalVariable.usersMap.containsKey(token))){
                    intent = new Intent(RegisterActivity.this,WordGame.class);
                }else {
                    intent = new Intent(RegisterActivity.this,ChoosePlayerActivity.class);
                }
                startActivity(intent);
                }

            }

    private void writeNewUser(String userId, String score, String name, String gameState) {
        Player user = new Player(name, score, userId, gameState);
        mDatabase.child("players").child(userId).setValue(user);

    }



}
