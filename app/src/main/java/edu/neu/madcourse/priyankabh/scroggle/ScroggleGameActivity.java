package edu.neu.madcourse.priyankabh.scroggle;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/9/2017.
 */

public class ScroggleGameActivity extends FragmentActivity {
    public static String KEY_RESTORE = "key_restore";
    public static String PREF_RESTORE = "pref_restore";

    public ScroggleFragment sFragment;
    private MediaPlayer mediaPlayer;
    private int phaseOnePoints = 0;
    private int phaseTwoPoints = 0;
    private SoundPool mSoundPool;
    private String gameData = "";
    private int gameScore =0;
    private int phase=1;
    private Boolean restore=false;
    public static Boolean isResume = false;
    private String phaseTwoWord;
    private TextView scoreView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) this.getApplicationContext();
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
            scoreView.setText("Total Points = " + String.valueOf(this.phaseOnePoints));
        } else {
            scoreView.setText("Total Points = " + String.valueOf(this.phaseOnePoints + this.phaseTwoPoints));
        }

    }

    public void setFragmentInvisible(){
        sFragment.getView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause(){
        super.onPause();
        sFragment.mHandler.removeCallbacks(null);
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
        mediaPlayer = MediaPlayer.create(this, R.raw.happy_music);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        if (phase == 1) {
            scoreView.setText("Total Points = " + String.valueOf(this.phaseOnePoints));
        } else {
            scoreView.setText("Total Points = " + String.valueOf(this.phaseOnePoints + this.phaseTwoPoints));
        }

    }



    public void stopThinking() {
        if(phase == 1) {
            scoreView.setText("Total Score: " + String.valueOf(this.phaseOnePoints));
        } else {
            scoreView.setText("Total Score: " + String.valueOf(this.phaseTwoPoints+this.phaseOnePoints));
        }
    }

    public void startThinking() {
        View thinkView = findViewById(R.id.thinking);
        thinkView.setVisibility(View.VISIBLE);
    }

    public int getPhaseTwoPoints(){
        return this.phaseTwoPoints;
    }

    public void setPhaseOnePoints(int points){
        this.phaseOnePoints = points;
    }

    public void setPhaseTwoPoints(int points){
        this.phaseTwoPoints = points;
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

    private class LoadWords extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            try {
                InputStream strF = getResources().getAssets().open("wordlist.txt");

                Scanner s = new Scanner(strF);
                while(s.hasNextLine()){
                    String word=s.nextLine();
                    if(word.length() == 9){
                        globalVariable.nineLetterWords.add(word);
                    }
                }

            } catch(IOException e) {
                System.err.print(e);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
        }

        protected void onPostExecute(Void v) {
        }
    }
}
