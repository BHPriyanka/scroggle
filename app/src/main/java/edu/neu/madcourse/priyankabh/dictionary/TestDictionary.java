package edu.neu.madcourse.priyankabh.dictionary;

import android.app.Activity;
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
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 1/24/2017.
 */

public class TestDictionary extends Activity{
        MediaPlayer mMediaPlayer;

        // no of bits used to store each char (a-z)
        public static final int CHAR_SIZE = 5;

        // ascii code of 'a' is 97, offset for char's
        public static final int ASCII_OFFSET = 96;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_test);

        EditText editText = (EditText) findViewById(R.id.word_entry);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("WORD", charSequence.toString());
                boolean seen = false;
                String word = charSequence.toString();

                if(word.length() == 1){
                    new LoadWordList().execute(word);
                }

                if(word.length() >=3){
                seen = searchWordLessThan12Char(word.toLowerCase());
                }

                if(seen){
                    Log.d("TAG", "The word is in the dictionary");
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep1);
                    mMediaPlayer.setVolume(0.5f, 0.5f);
                    mMediaPlayer.start();
                    EditText editText = (EditText) findViewById(R.id.word_entry);
                    TextView textView = (TextView)findViewById(R.id.view_words);

                    if(!textView.getText().toString().contains(editText.getText().toString())) {
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
      /**
     * converts string to long, it can handle words of size equal or less than
     * 12 characters
     */
    public static long encodeWordInLong(String word) {
        long result = 0L;

        char letters[] = word.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            long val = letters[i] - ASCII_OFFSET;
            int shift = i * CHAR_SIZE;
            val = val << shift;
            result = result | val;
        }
        return result;
    }

    // Code to do search for words of length <= 12
    public boolean searchWordLessThan12Char(String keyword){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //search in strLong list;
        int r = Arrays.binarySearch(globalVariable.list.toArray(), keyword);
        //Boolean hasWord = globalVariable.strLong.contains(encodeWordInLong(keyword));
        //Boolean hasWord = globalVariable.strLong.containsKey(encodeWordInLong(keyword));
      //  Log.d("TAG","r=" + r);
        if(r>0){
            return true;
        }
        return false;
    }

    // also search for words of length >12 and<24
/*    public boolean searchLongerWords(String keyword){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        String left = keyword.substring(0, 12);
        String right = keyword.substring(12);
        int l = Arrays.binarySearch(globalVariable.leftLong.toArray(), left);
        //Boolean l = globalVariable.strLong.containsKey(encodeWordInLong(left));
        if(l>0){
            int r = Arrays.binarySearch(globalVariable.rightLong.toArray(), encodeWordInLong(right));
            //Boolean r = globalVariable.strLong.containsKey(encodeWordInLong(right));
            if(r>0){
                return true;
            }
        }
        return false;

    }*/



    private class LoadWordList extends AsyncTask<String, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(String... str) {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            try {
                InputStream strF = getResources().getAssets().open(str[0]+".txt");
                //InputStream leftF = getResources().getAssets().open("leftLongFile");
                //InputStream rightF = getResources().getAssets().open("rightLongFile");

                Scanner sc = new Scanner(strF);

                while(sc.hasNextLine()){
                    String word = sc.nextLine();
                    globalVariable.list.add(word);
                    Log.d("TAG", word);
                }

                Collections.sort(globalVariable.list);

                /*while(sc.hasNextLong()){
                    Long word = sc.nextLong();
                    lists[0].add(word);
                }

                Scanner scLeft = new Scanner(leftF);

                while(scLeft.hasNextLong()){
                    Long word1 = sc.nextLong();
                    lists[1].add(word1);
                }

                Scanner scRight = new Scanner(rightF);

                while(scRight.hasNextLong()){
                    Long word2 = sc.nextLong();
                    lists[2].add(word2);
                }*/
                return true;

            } catch (IOException e) {
                System.err.print(e);
            }
            return false;
        }

        /*
 * (non-Javadoc)
 *
 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
 */
        @Override
        protected void onPostExecute(Boolean bool) {
            // execution of result of Long time consuming operation
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }
    }

}
