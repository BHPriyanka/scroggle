package edu.neu.madcourse.priyankabh.dictionary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;


public class TestDictionary extends Activity{
    private static final String TAG = TestDictionary.class.getSimpleName();

    MediaPlayer mMediaPlayer;
    private ProgressBar progressBar;
    Integer count=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_test);
        this.setTitle("Test Dictionary");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        new LoadWordList().execute();

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
                Intent intent = new Intent(TestDictionary.this, MainActivity.class);
                TestDictionary.this.startActivity(intent);
            }
        });

    }

    private class LoadWordList extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            try {
                InputStream strF = getResources().getAssets().open("wordlist.txt");
                Scanner sc = new Scanner(strF);

                while (sc.hasNextLine()) {
                    String word = sc.nextLine();
                    Boolean present = globalVariable.list.containsKey(word.substring(0, 2));
                    if (present) {
                        ArrayList<String> ll = globalVariable.list.get(word.substring(0, 2));
                            ll.add(word);
                    }
                    else{
                        ArrayList<String> ll = new ArrayList<String>();
                        ll.add(word);
                        globalVariable.list.put(word.substring(0,2), ll);
                    }
                }

                publishProgress(count);

                try {////this should let "Please wait for sometime" appears for 2 secs
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("TestDictionary", "Loading done");
            } catch (IOException e) {
                System.err.print(e);
            }            return null;
        }

        protected void onProgressUpdate(Integer... params) {
            progressBar.setProgress(params[0]);
        }

        protected void onPostExecute(Void v) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public Boolean searchWordInMap(String word) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        if(globalVariable.list.get(word.substring(0,2)).contains(word)){
            return true;
        }

        return false;

    }

}
