package edu.neu.madcourse.priyankabh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.dictionary.TestDictionary;
import edu.neu.madcourse.priyankabh.tictactoe.TicTacToeMainActivity;

import static edu.neu.madcourse.priyankabh.R.layout.activity_main;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        this.setTitle("B H Priyanka");

        //this button will show the dialog
        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.about_me);
                mDialog.setCancelable(true);

                // set up imei id-generate a 16 digit id
                TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String imei = manager.getDeviceId();

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.TextView01);
                text.setText(R.string.about_me);
                text.append(imei);

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        if(mDialog != null)
                            mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        Button generateErrorButton = (Button) findViewById(R.id.generate_error_button);
        generateErrorButton.setOnClickListener(new View.OnClickListener() {
            public AlertDialog alertDialog;

            @Override
            public void onClick(View v) {
                //invoke an object without initializing to crash the app
                alertDialog.show();
            }
        });

        Button tictactoeButton = (Button) findViewById(R.id.tictactoe_button);
        tictactoeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TicTacToeMainActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Button dictionaryButton = (Button) findViewById(R.id.dictionary_button);
        dictionaryButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestDictionary.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Button quitButton = (Button) findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
