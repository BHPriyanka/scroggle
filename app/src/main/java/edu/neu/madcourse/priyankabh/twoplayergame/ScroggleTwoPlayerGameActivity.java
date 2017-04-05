package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleFragment;

import static edu.neu.madcourse.priyankabh.twoplayergame.DetectNetworkActivity.IS_NETWORK_AVAILABLE;

/**
 * Created by priya on 3/12/2017.
 */

public class ScroggleTwoPlayerGameActivity extends FragmentActivity {
    public static String KEY_RESTORE = "key_restore";
    public static String PREF_RESTORE = "pref_restore";

    public TwoPlayerScroggleFragment sFragment;
    private MediaPlayer mediaPlayer;
    private int playerOnePoints = 0;
   // private int phaseTwoPoints = 0;
    private String gameData = "";
    private int player = 1;
    public static Boolean isResume = false;
   // private String phaseTwoWord;
    private TextView scoreView;
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private boolean isGameDataPresent = false;
    private String getTokenInstance;
    private boolean isPlayer2 = true;
    private static TextView internetStatus;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_twoplayer_scroggle);


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
                   //     Log.d("MainActivity", "onReceive: ...................");
                        dialog.cancel();
                        dialog.dismiss();
                        dialog.hide();
                    }
                } else {
                    Log.d("MainActivity","networkStatus :" +networkStatus);
                    if(dialog == null){
                       // Log.d("MainActivity", "onReceive:d ");
                        dialog = new Dialog(ScroggleTwoPlayerGameActivity.this);
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

        getTokenInstance = FirebaseInstanceId.getInstance().getToken();

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            isPlayer2 = b.getBoolean("isPlayer2");

            if (b.getInt("player") == 1) {
                player = 2;
            } else if (b.getInt("player") == 2) {
                player = 1;
            }

        }


        if (globalVariable.usersMap.containsKey(getTokenInstance)) {
            Map entry = (Map) globalVariable.usersMap.get(getTokenInstance);
            if (((String) entry.get("gameState")).equals("")) {
                if(b!=null){
                    gameData = b.getString("gameData");
                }
            }  else{
                gameData = (String) entry.get("gameState");
            }

        }

        sFragment = (TwoPlayerScroggleFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_scroggle_twoplayer);
        scoreView = (TextView) findViewById(R.id.large_score);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);

        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                sFragment.putState(gameData);
            }

        }
      //  if (phase == 1) {
        scoreView.setText("Total Points = " + String.valueOf(this.playerOnePoints));
      //  } else {
      //      scoreView.setText("Total Points = " + String.valueOf(this.phaseOnePoints + this.phaseTwoPoints));
      //  }

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                shuffleBoard();
            }
        });


    }

    public void setFragmentInvisible() {
        sFragment.getView().setVisibility(View.INVISIBLE);
    }

    public void shuffleBoard(){
        System.out.println("Shake detected");
    }

    @Override
    public void onPause() {
        super.onPause();
        sFragment.mHandler.removeCallbacks(null);
        mSensorManager.unregisterListener(mShakeDetector);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        sFragment.countDownTimer.cancel();
        if (isResume) {
            isResume = false;
        }
        String gameData = sFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        GlobalClass.activityPaused();// On Pause notify the Application
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        sFragment.mHandler.removeCallbacks(null);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        sFragment.countDownTimer.cancel();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isResume) {
            isResume = true;
            if (sFragment.timeRemaning > 0) {
                sFragment.countDownTimer.start();
            } else {
                sFragment.countDownTimer.start();
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.happy_music);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        // mediaPlayer.start();
       // if (phase == 1) {
          scoreView.setText("Total Points = " + String.valueOf(this.playerOnePoints));
       // } else {
      //      scoreView.setText("Total Points = " + String.valueOf(this.phaseOnePoints + this.phaseTwoPoints));
     //   }

        mSensorManager.registerListener(mShakeDetector, mAccelerometer,SensorManager.SENSOR_DELAY_UI);
        GlobalClass.activityResumed();// On Resume notify the Application
    }


    public void stopThinking() {
      //  if (phase == 1) {
            scoreView.setText("Total Score: " + String.valueOf(this.playerOnePoints));
      //  } else {
      //      scoreView.setText("Total Score: " + String.valueOf(this.phaseTwoPoints + this.phaseOnePoints));
      //  }
    }

    public void setPhaseOnePoints(int points) {
        this.playerOnePoints = points;
    }

    // Method to change the text status
    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            internetStatus.setText("Internet Connected.");
            internetStatus.setTextColor(Color.parseColor("#00ff00"));
        } else {
            internetStatus.setText("Internet Disconnected.");
            internetStatus.setTextColor(Color.parseColor("#ff0000"));
        }
    }

}