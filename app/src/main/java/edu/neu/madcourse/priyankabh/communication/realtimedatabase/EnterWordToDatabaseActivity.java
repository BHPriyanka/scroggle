package edu.neu.madcourse.priyankabh.communication.realtimedatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;
import edu.neu.madcourse.priyankabh.communication.fcm.FCMActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;

/**
 * Created by priya on 3/11/2017.
 */

public class EnterWordToDatabaseActivity extends Activity {
    private String wordTyped;
    private MediaPlayer mMediaPlayer;
    private Map<String,Object> users;
    private String getTokenInstance;
    private DatabaseReference mDatabase;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_word_to_database);

        try {
            //If its visible, trigger task, else do nothing
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

            // At activity startup we manually check the internet status and change
            // the text status
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new CommunicationActivity().changeTextStatus(true);
            } else {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                        "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Custom action
                                Intent intent = new Intent(EnterWordToDatabaseActivity.this, CommunicationActivity.class);
                                EnterWordToDatabaseActivity.this.startActivity(intent);
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View view = snackbar.getView();
                TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        getTokenInstance = FirebaseInstanceId.getInstance().getToken();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
                    EditText editText = (EditText) findViewById(R.id.word_entry);
                    wordTyped = editText.getText().toString();
                    TextView textView = (TextView)findViewById(R.id.view_words);

                    if(!textView.getText().toString().contains(editText.getText().toString())) {
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep1);
                        mMediaPlayer.setVolume(0.5f, 0.5f);
                        mMediaPlayer.start();
                        textView.append(editText.getText() + "\n");
                    }
                }
                // else{
                // Log.d("TAG", "The word does not exist in the dictionary");
                // }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_button);
        mDatabase.child("users").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        users = (Map<String, Object>) dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                     }
            });

        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.word_entry);
                TextView textView = (TextView)findViewById(R.id.view_words);
                editText.setText("");
                textView.setText("");
                String chosenUser1="",chosenUser2="";
                for (Map.Entry<String, Object> entry : users.entrySet()){
                    //Get user map
                    if(entry.getKey().equalsIgnoreCase(getTokenInstance)){
                        chosenUser1 = entry.getKey();
                    } else{
                        chosenUser2 = entry.getKey();
                    }

                }
                EnterWordToDatabaseActivity.this.onAddWord(mDatabase, getTokenInstance.equals(chosenUser1) ? chosenUser1 : chosenUser2);

            }
        });

        Button quit_button =(Button)findViewById(R.id.quit_button);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterWordToDatabaseActivity.this, RealtimeDatabaseActivity.class);
                EnterWordToDatabaseActivity.this.startActivity(intent);
            }
        });

    }


    public Boolean searchWordInMap(String word) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        if(globalVariable.list.get(word.toLowerCase().substring(0,2)).contains(word.toLowerCase())){
            return true;
        }

        return false;

    }

    /**
     * Called on word add
     * @param postRef
     * @param user
     */
    private void onAddWord(DatabaseReference postRef, String user) {
        postRef
                .child("users")
                .child(user)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        User u = mutableData.getValue(User.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }

                        u.score = String.valueOf(computeScore());
                        u.wordFormed = wordTyped;

                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        //Log.d("", "postTransaction:onComplete:" + databaseError);
                    }
                });
    }

    public int getLetterScore(char letter) {
        int point = 0;
        if ((letter == 'e') || (letter == 'a') || (letter == 'i') || (letter == 'o')
                || (letter == 'r') || (letter == 't') || (letter == 'l')
                || (letter == 'n') || (letter == 's') || (letter == 'u')) {
            point = 1;
        } else if (letter == ('d') || letter == ('g')) {
            point = 2;
        } else if (letter == ('b') || letter == ('c') || letter == ('m') || letter == ('p')) {
            point = 3;
        } else if (letter == ('f') || letter == ('h') || letter == ('v') || letter == ('w') || letter == ('y')) {
            point = 4;
        } else if (letter == ('k')) {
            point = 5;
        } else if (letter == ('j') || letter == ('x')) {
            point = 8;
        } else if (letter == ('q') || letter == ('z')) {
            point = 10;
        }
        return point;
    }

    public int computeScore(){
        int total=0;
        for(int i=0;i<wordTyped.length();i++){
            total+=getLetterScore(wordTyped.toCharArray()[i]);
        }
        return total;
    }
}
