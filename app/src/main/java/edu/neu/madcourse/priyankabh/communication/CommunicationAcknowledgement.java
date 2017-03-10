package edu.neu.madcourse.priyankabh.communication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.dictionary.Acknowledgement;
import edu.neu.madcourse.priyankabh.dictionary.TestDictionary;

/**
 * Created by priya on 3/10/2017.
 */

public class CommunicationAcknowledgement extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acknowledge_dict);
        this.setTitle("Acknowledgement");

        TextView textView = (TextView) findViewById(R.id.ack_view);
        textView.setText(R.string.communication_acknowledgement);

        Button button = (Button) findViewById(R.id.ok_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(CommunicationAcknowledgement.this, CommunicationActivity.class);
                CommunicationAcknowledgement.this.startActivity(intent);
            }
        });
    }
}
