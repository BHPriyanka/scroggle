package edu.neu.madcourse.priyankabh.communication.realtimedatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Map;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;

/**
 * Created by priya on 3/11/2017.
 */

public class ViewScoreActivity extends AppCompatActivity {

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
    private String getTokenInstance;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstancetate){
        super.onCreate(savedInstancetate);
        setContentView(R.layout.activity_realtime_database);

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
                                Intent intent = new Intent(ViewScoreActivity.this, CommunicationActivity.class);
                                ViewScoreActivity.this.startActivity(intent);
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

        userName = (TextView) findViewById(R.id.username);
        score = (TextView) findViewById(R.id.score);
        userName2 = (TextView) findViewById(R.id.username2);
        score2 = (TextView) findViewById(R.id.score2);

        player1 = (RadioButton)findViewById(R.id.player1);
        word1 = (TextView) findViewById(R.id.word1);
        word2 = (TextView) findViewById(R.id.word2);

        //TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        add5 = (Button)findViewById(R.id.add5);
        add5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chosenUser1="",chosenUser2="";
                for (Map.Entry<String, Object> entry : users.entrySet()){
                    //Get user map
                    Map singleUser = (Map) entry.getValue();

                    if(entry.getKey().equalsIgnoreCase(getTokenInstance)){
                        chosenUser1 = entry.getKey();
                    } else{
                        chosenUser2 = entry.getKey();
                    }
                    Log.e(TAG, "onaddValueEvent: dataSnapshot = " + entry.getValue());

                }
                ViewScoreActivity.this.onAddScore(mDatabase, player1.isChecked() ? chosenUser1 : chosenUser2);
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
                            if(entry.getKey().equalsIgnoreCase(getTokenInstance)){
                                score.setText((String)singleUser.get("score"));
                                userName.setText((String)singleUser.get("username"));
                                word1.setText((String)singleUser.get("wordFormed"));
                            } else{
                                score2.setText((String.valueOf(singleUser.get("score"))));
                                userName2.setText((String)singleUser.get("username"));
                                word2.setText((String)singleUser.get("wordFormed"));
                            }
                            /*for(int i=0;i<users.size();i++){
                                TableRow row = new TableRow(getApplicationContext());
                                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);
                                row.setLayoutParams(lp);

                            }*/

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

        Button quit_button =(Button)findViewById(R.id.quit_button);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewScoreActivity.this, RealtimeDatabaseActivity.class);
                ViewScoreActivity.this.startActivity(intent);
            }
        });
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
                        if(u.score == ""){
                            u.score = String.valueOf(5);
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

}

