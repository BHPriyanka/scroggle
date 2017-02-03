package edu.neu.madcourse.priyankabh.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/1/2017.
 */

public class Acknowledgement extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acknowledge_dict);

        TextView textView = (TextView) findViewById(R.id.ack_view);
        textView.setText(R.string.acknowledgement);

        Button button = (Button) findViewById(R.id.ok_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(Acknowledgement.this, TestDictionary.class);
                Acknowledgement.this.startActivity(intent);
            }
        });
    }

}
