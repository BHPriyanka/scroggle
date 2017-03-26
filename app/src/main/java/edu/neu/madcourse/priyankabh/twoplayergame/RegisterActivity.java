package edu.neu.madcourse.priyankabh.twoplayergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.twoplayergame.models.Player;

/**
 * Created by priya on 3/3/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String userName="";
    private String userScore="";
    private String userToken="";
    private String gameState="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();

        if (b != null) {
            userName = b.getString("userName");
            userScore = b.getString("score");
            userToken = b.getString("token");
            gameState = b.getString("gameState");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

            if(globalVariable.usersMap != null && globalVariable.usersMap.containsKey(userToken)){
                String str = "User already registered";
                Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this,ChoosePlayerActivity.class);
                startActivity(intent);
            } else {
                writeNewUser(userToken, userScore, userName, gameState);
                String msg = "Registered Successfully";
                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this,ChoosePlayerActivity.class);
                startActivity(intent);
                }

            }

    //}


    private void writeNewUser(String userId, String score, String name, String gameState) {
        Player user = new Player(name, score, userId, gameState);
        mDatabase.child("players").child(userId).setValue(user);

    }



}
