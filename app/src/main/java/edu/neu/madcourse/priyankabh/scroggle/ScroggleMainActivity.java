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
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/10/2017.
 */

public class ScroggleMainActivity extends Activity {
    MediaPlayer mMediaPlayer;
    private  ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scroggle);
        if(globalVariable.nineLetterWords.isEmpty()){

            progressDialog = new ProgressDialog(ScroggleMainActivity.this);
            progressDialog.setMax(100);
            progressDialog.setMessage("Please wait....");
            progressDialog.setTitle("Scroggle");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            new LoadWords().execute();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
       // mMediaPlayer = MediaPlayer.create(this, R.raw.gat_loop);
       // mMediaPlayer.setVolume(0.5f, 0.5f);
       // mMediaPlayer.setLooping(true);
       // mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mMediaPlayer.stop();
        //mMediaPlayer.reset();
       // mMediaPlayer.release();
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

                int count = 0;
                try {
                    while(count<100) {
                        Thread.sleep(200);
                        count +=1;
                        publishProgress(count);
                    }
                    Log.d("TAG","loading donw nineletter words");
                }catch(InterruptedException ie){
                    System.err.print(ie);
                }

            } catch(IOException e) {
                System.err.print(e);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
            progressDialog.setProgress(params[0]);
        }

        protected void onPostExecute(Void v) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
