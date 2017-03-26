package edu.neu.madcourse.priyankabh.communication.fcm;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;

/**
 * Created by priya on 3/3/2017.
 */

public class RegisterUserActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private CoordinatorLayout coordinatorLayout;
    private String chosenUserKey="";

    private String userName="";
    private static final String TAG = FCMActivity.class.getSimpleName();
    private String userScore="";
    private String userToken="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.after_registration);
        Bundle b = this.getIntent().getExtras();

        if (b != null) {
            userName = b.getString("userName");
            userScore = b.getString("score");
            userToken = b.getString("token");
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            if(globalVariable.usersMap.containsKey(userToken)){
                String str = "User already registered";
                Toast.makeText(RegisterUserActivity.this, str, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterUserActivity.this,FCMActivity.class);
                startActivity(intent);
            } else {
                writeNewUser(userToken, userScore, userName);
                String msg = "Registered Successfully";
                Toast.makeText(RegisterUserActivity.this, msg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterUserActivity.this,FCMActivity.class);
                startActivity(intent);
            }
        } else{
             Log.d(TAG,"Internet not available");
             new CommunicationActivity().changeTextStatus(false);

        }
    }


    private void writeNewUser(String userId, String score, String name) {
        User user = new User(name, score, userId);
        mDatabase.child("users").child(userId).setValue(user);
    }

}
