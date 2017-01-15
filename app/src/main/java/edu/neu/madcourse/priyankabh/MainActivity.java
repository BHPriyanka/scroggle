package edu.neu.madcourse.priyankabh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.tictactoe.GameActivity;

import static edu.neu.madcourse.priyankabh.R.layout.activity_main;

public class MainActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        //this button will show the dialog
        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.setContentView(R.layout.home_fragment_main);
                mDialog.setCancelable(true);
                //there are a lot of settings, for dialog, check them all out!


                // set up imei id-generate a 16 digit id
                Random random = new Random();
                char[] digits = new char[16];
                digits[0] = (char) (random.nextInt(9) + '1');
                for (int i = 1; i < 16; i++) {
                    digits[i] = (char) (random.nextInt(10) + '0');
                }

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.TextView01);
                text.setText(R.string.about_me);
                text.append(new String(digits));

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
                setContentView(R.layout.activity_main_tictactoe);
            }
        });

        Button quitButton = (Button) findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
