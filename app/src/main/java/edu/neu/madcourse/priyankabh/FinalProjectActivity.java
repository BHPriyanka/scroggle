package edu.neu.madcourse.priyankabh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
    }
}
