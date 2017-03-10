package edu.neu.madcourse.priyankabh.communication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.fcm.FCMActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.RealtimeDatabaseActivity;
import edu.neu.madcourse.priyankabh.dictionary.TestDictionary;

/**
 * Created by priya on 2/26/2017.
 */

public class CommunicationActivity extends AppCompatActivity {
    private static TextView internetStatus;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
       //setContentView(R.layout.connectivity_network);
        //internetStatus = (TextView) findViewById(R.id.internet_status);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // At activity startup we manually check the internet status and change
        // the text status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        Button ackButton = (Button) findViewById(R.id.ack_button);
        ackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunicationActivity.this, CommunicationAcknowledgement.class);
                CommunicationActivity.this.startActivity(intent);
            }
        });
        Button returnMenuButton = (Button) findViewById(R.id.main_menu__button);
        returnMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                Intent intent = new Intent(CommunicationActivity.this, MainActivity.class);
                CommunicationActivity.this.startActivity(intent);
                globalVariable.list.clear();
            }
        });
    }

    public void openFCMActivity(View view) {
        startActivity(new Intent(CommunicationActivity.this, FCMActivity.class));
    }

    public void openDBActivity(View view) {
        startActivity(new Intent(CommunicationActivity.this, RealtimeDatabaseActivity.class));
    }

    public void changeTextStatus(boolean isConnected) {
        if(!isConnected) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                    "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Custom action
                            Intent intent = new Intent(CommunicationActivity.this, CommunicationActivity.class);
                            CommunicationActivity.this.startActivity(intent);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View view = snackbar.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        GlobalClass.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        GlobalClass.activityResumed();// On Resume notify the Application
    }
}
