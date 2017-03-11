package edu.neu.madcourse.priyankabh.communication.realtimedatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;
import edu.neu.madcourse.priyankabh.communication.fcm.FCMActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;


public class RealtimeDatabaseActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private CoordinatorLayout mCoordinatorLayout;
    private String getTokenInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_realtime_database);
        setContentView(R.layout.realtime_database_menu);
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
                                Intent intent = new Intent(RealtimeDatabaseActivity.this, CommunicationActivity.class);
                                RealtimeDatabaseActivity.this.startActivity(intent);
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
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        if (globalVariable.list.isEmpty()) {
            progressDialog = new ProgressDialog(RealtimeDatabaseActivity.this);
            progressDialog.setMax(100);
            progressDialog.setMessage("Please wait....");
            progressDialog.setTitle("Loading Dictionary");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

            new LoadWordList().execute();
        }

        Button enterWordButton = (Button) findViewById(R.id.enter_word_button);
        enterWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RealtimeDatabaseActivity.this, EnterWordToDatabaseActivity.class);
                startActivity(intent);
            }
        });


        Button viewScoreButton = (Button) findViewById(R.id.view_score_button);
        viewScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RealtimeDatabaseActivity.this, ViewScoreActivity.class);
                startActivity(intent);
            }
        });

        Button quit_button =(Button)findViewById(R.id.quit_button);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RealtimeDatabaseActivity.this, CommunicationActivity.class);
                RealtimeDatabaseActivity.this.startActivity(intent);
            }
        });
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



}