package edu.neu.madcourse.priyankabh.communication.realtimedatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;


public class RealtimeDatabaseActivity extends Activity {

    private static final String TAG = RealtimeDatabaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private TextView userName;
    private TextView score;
    private TextView userName2;
    private TextView word1,word2;
    private TextView score2;
    private RadioButton player1;
    private Map<String,Object> users;
    private Button add5;
    ProgressDialog progressDialog;
    MediaPlayer mMediaPlayer;
    private String wordTyped;
    private String getTokenInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_database);

        getTokenInstance = FirebaseInstanceId.getInstance().getToken();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        if(globalVariable.list.isEmpty())
        {
            progressDialog = new ProgressDialog(RealtimeDatabaseActivity.this);
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
                    Map singleUser = (Map) entry.getValue();

                    if(entry.getKey().equalsIgnoreCase(getTokenInstance)){
                        chosenUser1 = entry.getKey();
                    } else{
                        chosenUser2 = entry.getKey();
                    }

                }
                RealtimeDatabaseActivity.this.onAddWord(mDatabase, player1.isChecked() ? chosenUser1 : chosenUser2);

            }
        });

        userName = (TextView) findViewById(R.id.username);
        score = (TextView) findViewById(R.id.score);
        userName2 = (TextView) findViewById(R.id.username2);
        score2 = (TextView) findViewById(R.id.score2);

        player1 = (RadioButton)findViewById(R.id.player1);
        word1 = (TextView) findViewById(R.id.word1);
        word2 = (TextView) findViewById(R.id.word2);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        add5 = (Button)findViewById(R.id.add5);
        add5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chosenUser1="",chosenUser2="";
                for (Map.Entry<String, Object> entry : users.entrySet()){
                    //Get user map
                    Map singleUser = (Map) entry.getValue();

                    //if(((String)singleUser.get("username")).equalsIgnoreCase("Player 1")){
                    if(entry.getKey().equalsIgnoreCase(getTokenInstance)){
                        chosenUser1 = entry.getKey();
                    } else{
                           chosenUser2 = entry.getKey();
                    }
                    Log.e(TAG, "onaddValueEvent: dataSnapshot = " + entry.getValue());

                }
                RealtimeDatabaseActivity.this.onAddScore(mDatabase, player1.isChecked() ? chosenUser1 : chosenUser2);
            }
        });



        mDatabase.child("users").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        users = (Map<String,Object>) dataSnapshot.getValue();
                        for (Map.Entry<String, Object> entry : users.entrySet()){
                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //if(((String)singleUser.get("username")).equalsIgnoreCase("Player 1")){
                            if(entry.getKey().equalsIgnoreCase(getTokenInstance)){
                               score.setText((String)singleUser.get("score"));
                                userName.setText((String)singleUser.get("username"));
                                word1.setText((String)singleUser.get("wordFormed"));
                            } else{
                                score2.setText((String.valueOf(singleUser.get("score"))));
                                userName2.setText((String)singleUser.get("username"));
                                word2.setText((String)singleUser.get("wordFormed"));
                            }
                            Log.e(TAG, "onaddValueEvent: dataSnapshot = " + entry.getValue());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);

                        if (dataSnapshot.getKey().equalsIgnoreCase(getTokenInstance)) {
                            score.setText(user.score);
                            userName.setText(user.username);
                            word1.setText(user.wordFormed);
                        } else {
                            score2.setText(String.valueOf(user.score));
                            userName2.setText(user.username);
                            word2.setText(user.wordFormed);
                        }
                        Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);

                        if (dataSnapshot.getKey().equalsIgnoreCase(getTokenInstance)) {
                            score.setText(user.score);
                            userName.setText(user.username);
                            word1.setText(user.wordFormed);
                        } else {
                            score2.setText(String.valueOf(user.score));
                            userName2.setText(user.username);
                            word2.setText(user.wordFormed);
                        }
                        Log.v(TAG, "onChildChanged: "+dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled:" + databaseError);
                    }
                }
        );


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


    /**
     * Called on score add
     * @param postRef
     * @param user
     */
    private void onAddScore(DatabaseReference postRef, String user) {
        postRef
                .child("users")
                .child(user)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        //Map user = (Map) mutableData.getValue();
                        User u = mutableData.getValue(User.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }

                        u.score = String.valueOf(Integer.valueOf(u.score) + 5);

                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    }
                });
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
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
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