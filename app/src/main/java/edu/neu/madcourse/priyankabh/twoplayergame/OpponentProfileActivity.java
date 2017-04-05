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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;

import static edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity.IS_NETWORK_AVAILABLE;

/**
 * Created by priya on 3/21/2017.
 */

public class OpponentProfileActivity extends Activity {
    private boolean isPlayer1=true;
    private String token;
    private DatabaseReference mDatabase;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.opponent_profile);

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
                        dialog = new Dialog(OpponentProfileActivity.this);
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

        TextView playername = (TextView) findViewById(R.id.player_name_val);
        TextView playerscore = (TextView) findViewById(R.id.player_score_val);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        token = FirebaseInstanceId.getInstance().getToken();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle  b = this.getIntent().getExtras();

        if (b != null) {
           isPlayer1 = b.getBoolean("isPlayer1");
            Map<String, Object> q = (Map<String, Object>) globalVariable.usersMap.get(token);
            String o = (String) q.get("opponent");
            Map<String, Object> opp = (Map<String, Object>) globalVariable.usersMap.get(o);
            playername.setText((String) opp.get("username"));
            playerscore.setText((String) opp.get("score"));
        } else {
            if (globalVariable.pairPlayers != null && globalVariable.pairPlayers.containsKey(token)) {
                Map<String, Object> p = (Map<String, Object>) globalVariable.usersMap.get(globalVariable.pairPlayers.get(token));
                playername.setText((String)p.get("username"));
                playerscore.setText((String)p.get("score"));

            }
        }

        Button ok = (Button) findViewById(R.id.ok_click);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpponentProfileActivity.this, TwoPlayerWordGameActivity.class);
                intent.putExtra("isPlayer1", isPlayer1);
                startActivity(intent);
            }
        });
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
