package edu.neu.madcourse.priyankabh.dictionary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import junit.framework.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;


public class TestDictionary extends Activity{
    private static final String TAG = TestDictionary.class.getSimpleName();

    ProgressDialog progressDialog;
    MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_test);
        this.setTitle("Test Dictionary");

        Log.d("TestDictionary","Before calling asynctask" + globalVariable.list.isEmpty());
        if(globalVariable.list.isEmpty()) {
            Log.d("TestDictionary","After check.. calling asynctask" + globalVariable.list.isEmpty());

            progressDialog = new ProgressDialog(TestDictionary.this);
            progressDialog.setMax(100);
            progressDialog.setMessage("Please wait....");
            progressDialog.setTitle("Loading Dictionary");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

            new LoadWordList().execute();
        }

        EditText editText = (EditText) findViewById(R.id.word_entry);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean seen = false;
                String word = charSequence.toString();

                if(word.length() >=3){
                    seen = searchWordInMap(word);
                }

                if(seen){
                    Log.d("TAG", "The word is in the dictionary");
                    EditText editText = (EditText) findViewById(R.id.word_entry);
                    TextView textView = (TextView)findViewById(R.id.view_words);

                    if(!textView.getText().toString().contains(editText.getText().toString())) {
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep1);
                        mMediaPlayer.setVolume(0.5f, 0.5f);
                        mMediaPlayer.start();
                        textView.append(editText.getText() + "\n");
                    }
                }
                else{
                    Log.d("TAG", "The word does not exist in the dictionary");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.word_entry);
                TextView textView = (TextView)findViewById(R.id.view_words);
                editText.setText("");
                textView.setText("");
            }
        });
        Button ackButton = (Button) findViewById(R.id.ack_button);
        ackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestDictionary.this, Acknowledgement.class);
                TestDictionary.this.startActivity(intent);
            }
        });
        Button returnMenuButton = (Button) findViewById(R.id.menu_button);
        returnMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                Intent intent = new Intent(TestDictionary.this, MainActivity.class);
                TestDictionary.this.startActivity(intent);
                globalVariable.list.clear();
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.list.clear();
      //  System.gc();
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs

    }

    public Boolean searchWordInMap(String word) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        if(globalVariable.list.get(word.toLowerCase().substring(0,2)).contains(word.toLowerCase())){
            return true;
        }

        return false;

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
            progressDialog.setProgress(params[0]);
        }

        protected void onPostExecute(Void v) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }

}
