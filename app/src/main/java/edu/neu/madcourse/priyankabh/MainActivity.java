package edu.neu.madcourse.priyankabh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.tictactoe.GameActivity;

import static edu.neu.madcourse.priyankabh.R.layout.activity_main;

public class MainActivity extends Activity {
    private AlertDialog mDialog;

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
                /*Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.setContentView(R.layout.home_fragment_main);
                mDialog.setCancelable(true);
                //there are a lot of settings, for dialog, check them all out!

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.TextView01);
                text.setText(R.string.about_me);

                //set up image view
                ImageView img = (ImageView) mDialog.findViewById(R.id.ImageView01);
                img.setImageResource(R.drawable.picture);*/

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());

                // set up text
                TextView text = (TextView) mDialog.findViewById(R.id.TextView01);
                text.setText(R.string.about_me);

                //set up image view
                ImageView img = (ImageView) mDialog.findViewById(R.id.ImageView01);
                img.setImageResource(R.drawable.picture);

                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        });
                mDialog = builder.show();

                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        Button generateErrorButton = (Button) findViewById(R.id.generate_error_button);
        generateErrorButton.setOnClickListener(new View.OnClickListener(){
            public AlertDialog alertDialog;
            @Override
            public void onClick(View v){
                //invoke an object without initializing to crash the app
                alertDialog.show();
            }
        });

        Button tictactoeButton = (Button) findViewById(R.id.tictactoe_button);
        tictactoeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setContentView(R.layout.activity_main_tictactoe);
            }
        });

        Button quitButton = (Button) findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

}
