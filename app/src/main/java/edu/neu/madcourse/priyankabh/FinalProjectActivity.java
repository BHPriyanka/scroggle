package edu.neu.madcourse.priyankabh;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.note2map.Note2MapMainActivity;

/**
 * Created by priya on 4/28/2017.
 */

public class FinalProjectActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_project);

        Button launchApp = (Button) findViewById(R.id.n2m_launch_app_button);
        launchApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinalProjectActivity.this, Note2MapMainActivity.class);
                startActivity(intent);
            }
        });

        Button ackFinalProjectButton = (Button) findViewById(R.id.n2m_acknowledgement_button);
        ackFinalProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(FinalProjectActivity.this);
                mDialog.setTitle("Acknowledgement");
                mDialog.setContentView(R.layout.n2m_final_project_ack);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ack_project);
                text.setText(R.string.project_ack);

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });

        Button appDescButton = (Button) findViewById(R.id.n2m_app_desc_button);
        appDescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(FinalProjectActivity.this);
                mDialog.setTitle("Note2Map");
                mDialog.setContentView(R.layout.n2m_final_project_ack);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ack_project);
                text.setText(R.string.note2map_app_description);

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });
    }
}
