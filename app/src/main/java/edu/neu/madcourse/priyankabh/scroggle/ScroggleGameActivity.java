package edu.neu.madcourse.priyankabh.scroggle;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/9/2017.
 */

public class ScroggleGameActivity extends FragmentActivity {
    public static String KEY_RESTORE = "key_restore";
    public static String PREF_RESTORE = "pref_restore";

    public ScroggleFragment sFragment;
    private ProgressDialog progressDialog;
    private Boolean isEnd;
    private int phaseOnePoints = 0;
    private int phaseTwoPoints = 0;
    private String gameData = "";
    private int gameScore =0;
    private int phase=1;
    private Boolean restore=false;
    public static Boolean isResume = false;
    private String phaseTwoWord;
    private TextView scoreView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            phaseTwoWord="";
            phase = 2;
            gameData = b.getString("gameData");
        }

        setContentView(R.layout.activity_game_scroggle);
        sFragment = (ScroggleFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_scroggle);
        scoreView = (TextView) findViewById(R.id.score);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);

        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                sFragment.putState(gameData);
            }

        }
        if (phase == 1) {
            scoreView.setText("Total Points = " + String.valueOf(phaseOnePoints));
        } else {
            scoreView.setText("Total Points = " + String.valueOf(phaseOnePoints + phaseTwoPoints));
        }

    }

    public void setFragmentInvisible(){
        sFragment.getView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause(){
        super.onPause();
        sFragment.mHandler.removeCallbacks(null);
        sFragment.mediaPlayer.stop();
        sFragment.mediaPlayer.reset();
        sFragment.mediaPlayer.release();
        sFragment.countDownTimer.cancel();
        if (isResume) {
            isResume = false;
        }
        String gameData = sFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
    }




    @Override
    public void onResume(){
        super.onResume();
        if(!isResume){
            isResume = true;
            if(sFragment.timeRemaning > 0){
                sFragment.countDownTimer.start();
            } else {
                sFragment.countDownTimer.start();
            }
        }
        sFragment.mediaPlayer = MediaPlayer.create(this, R.raw.joanne_rewind);
        sFragment.mediaPlayer.start();
        if (phase == 1) {
            scoreView.setText("Total Points = " + String.valueOf(phaseOnePoints));
        } else {
            scoreView.setText("Total Points = " + String.valueOf(phaseOnePoints + phaseTwoPoints));
        }

    }


    public void restartGame(){
    //    sFragment.restartGame();
    }

    public void setGameEnd(Boolean val){
        this.isEnd = val;
    }

    public void setGameScore(int score){
        this.phaseOnePoints = score;
    }


    public void stopThinking() {
        if(phase == 1) {
            scoreView.setText("Total Score: " + String.valueOf(phaseOnePoints));
        } else {
            scoreView.setText("Total Score: " + String.valueOf(phaseTwoPoints+phaseOnePoints));
        }
    }

    public void startThinking() {
        View thinkView = findViewById(R.id.thinking);
        thinkView.setVisibility(View.VISIBLE);
    }


    public int getPhaseOnePoints(){
        return this.phaseOnePoints;
    }

    public int getPhaseTwoPoints(){
        return this.phaseTwoPoints;
    }

    public void setPhaseOnePoints(int points){
        this.phaseOnePoints += points;
    }

    public void setPhaseTwoPoints(int points){
        this.phaseTwoPoints += points;
    }

    public int getPhase(){
        return this.phase;
    }

    public void setPhase(int phase){
        this.phase = phase;
    }

    public Boolean getRestore(){
        return this.restore;
    }

    public void setRestore(Boolean res){
        this.restore = res;
    }

    public boolean isRestore(){
        return this.restore;
    }
}
