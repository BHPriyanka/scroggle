package edu.neu.madcourse.priyankabh.scroggle;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.dictionary.TestDictionary;

/**
 * Created by priya on 2/9/2017.
 */

public class ScroggleGameActivity extends FragmentActivity {
    public static String KEY_RESTORE = "key_restore";
    public static String PREF_RESTORE = "pref_restore";

    private ScroggleFragment sFragment;
    private ProgressDialog progressDialog;
    private Boolean isEnd;
    public TextView scoreView;
    private int phaseOnePoints = 0;
    private int phaseTwoPoints = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_scroggle);
        sFragment = (ScroggleFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_scroggle);

        scoreView = (TextView) findViewById(R.id.score);

        if(globalVariable.list.isEmpty()) {
            Log.d("TestDictionary","After check.. calling asynctask" + globalVariable.list.isEmpty());
            new LoadWordList().execute();
        }
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);

        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                //sFragment.putState(gameData);
            }

        }

    }

    @Override
    public void onPause(){
        super.onPause();
        String gameData = sFragment.getState();
        getPreferences(MODE_PRIVATE).edit().putString(PREF_RESTORE, gameData).commit();
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    public void onScrogglePause(){

    }

    public void onScroggleResume(){

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

                int count = 0;
                try {
                    while(count<100) {
                        Thread.sleep(200);
                        count +=2;
                        publishProgress(count);
                    }
                }catch(InterruptedException ie){
                    System.err.print(ie);
                }

                System.out.println("TestDictionary Loading done");

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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }

    public void setGameEnd(Boolean val){
        this.isEnd = val;
    }

    public void stopThinking() {
        scoreView.setText(String.valueOf(phaseOnePoints));
    }
}
