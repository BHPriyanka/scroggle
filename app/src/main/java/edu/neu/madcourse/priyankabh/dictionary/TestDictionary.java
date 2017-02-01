package edu.neu.madcourse.priyankabh.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 1/24/2017.
 */

public class TestDictionary extends Activity{
        // no of bits used to store each char (a-z)
        public static final int CHAR_SIZE = 5;

        // ascii code of 'a' is 97, offset for char's
        public static final int ASCII_OFFSET = 96;

        public static List<Long> leftLong;
        public static List<Long> rightLong;
        public static List<Long> strLong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_test);

        try {
            InputStream strF = getResources().getAssets().open("strLongFile");
            InputStream leftF = getResources().getAssets().open("leftLongFile");
            InputStream rightF = getResources().getAssets().open("rightLongFile");

            ObjectInputStream ois = new ObjectInputStream(strF);
            strLong = (ArrayList) ois.readObject();

            ObjectInputStream ois1 = new ObjectInputStream(leftF);
            leftLong = (ArrayList) ois1.readObject();

            ObjectInputStream ois2 = new ObjectInputStream(rightF);
            rightLong = (ArrayList) ois2.readObject();

        }catch (IOException e){
            System.err.print(e);
        }catch(ClassNotFoundException c){
            System.err.print(c);
        }

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

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.word_entry);
                String word = editText.getText().toString();
                boolean seen = false;

                if(word.length()> 12 && word.length() < 24){
                    seen = searchLongerWords(word);
                }
                else if(word.length() <= 12){
                    seen = searchWordLessThan12Char(word);
                }
                if(seen){
                    Log.d("TAG", "The word is in the dictionary");
                }
                else{
                    Log.d("TAG", "The word does not exist in the dictionary");
                }
            }
        });

        Button viewText = (Button) findViewById(R.id.view_button);
        viewText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText editText = (EditText) findViewById(R.id.word_entry);
                TextView textView = (TextView)findViewById(R.id.view_words);
                textView.setText(editText.getText());
            }
        });
    }
    /**
     * Convert word encoded as long to String
     *
     * @param value
     * @return
     */
    public static String decodeWordfromLong(long value) {
        StringBuffer word = new StringBuffer();

        while (value != 0) {
            // take out bits needed to decode each char
            int tmp = (int) value & 0x1F;
            // shift processed bits right
            value = value >> CHAR_SIZE;
            // add offset to form char
            tmp = tmp + ASCII_OFFSET;
            word.append((char)tmp);
        }
        return word.toString();
    }


    /**
     * converts string to long, it can handle words of size equal or less than
     * 12 characters
     *
     * @param word
     * @return
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

    // Code to do binarysearch for words of length <= 12
    public static boolean searchWordLessThan12Char(String keyword){
        //search in strLong list;
        int r = Arrays.binarySearch(strLong.toArray(), encodeWordInLong(keyword));
        Log.d("TAG","r=" + r);
        if(r>0){
            return true;
        }
        return false;
    }

    // also binarysearch for words of length >12 and<24
    public static boolean searchLongerWords(String keyword){
        String left = keyword.substring(0, 12);
        String right = keyword.substring(12);
        int l = Arrays.binarySearch(leftLong.toArray(), encodeWordInLong(left));
        if(l > 0){
            int r = Arrays.binarySearch(rightLong.toArray(), encodeWordInLong(right));
            if(r > 0){
                return true;
            }
        }
        return false;

    }


}
