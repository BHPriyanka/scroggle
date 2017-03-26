package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;
import java.util.Random;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.twoplayergame.models.Player;

/**
 * Created by priya on 3/11/2017.
 */

public class TwoPlayerWordGameActivity extends Activity {
    private String getTokenInstance;
    private int n;
    //private boolean isPlayer2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_word_player_two);

        getTokenInstance = FirebaseInstanceId.getInstance().getToken();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Random rand = new Random();
        n = rand.nextInt(50) + 1;

        TwoPlayerWordGameActivity.this.onAddGameIdForPlayer(mDatabase, getTokenInstance, n);
        if(globalVariable.pairPlayers.containsKey(getTokenInstance)) {
            TwoPlayerWordGameActivity.this.onAddGameIdForPlayer(mDatabase, globalVariable.pairPlayers.get(getTokenInstance), n);
        }
        fetchUsersFromDatabase();
    }

    private void onAddGameIdForPlayer(DatabaseReference postRef, String player, int n) {
       // final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        Log.d("TAG", "onAddGameIdForPlayer: "+player);
        final int val =n;
        postRef
                .child("players")
                .child(player)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Player u = mutableData.getValue(Player.class);
                        if (u == null) {
                            return Transaction.success(mutableData);
                        }
                        if(u.gameID == 0) {
                            u.gameID = val;
                        }
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                    }
                });

    }


    public void fetchUsersFromDatabase(){
        //Get datasnapshot at your "users" root node
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("players");
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        dbref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        globalVariable.usersMap = (Map<String,Object>) dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


}
