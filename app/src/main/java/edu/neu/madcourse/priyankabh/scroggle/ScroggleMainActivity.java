package edu.neu.madcourse.priyankabh.scroggle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/10/2017.
 */

public class ScroggleMainActivity extends Activity {
    MediaPlayer mMediaPlayer;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scroggle);

        if(globalVariable.list.isEmpty()) {
            Log.d("TestDictionary","After check.. calling asynctask" + globalVariable.list.isEmpty());
            new LoadWordList().execute();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.gat_loop);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
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
}
