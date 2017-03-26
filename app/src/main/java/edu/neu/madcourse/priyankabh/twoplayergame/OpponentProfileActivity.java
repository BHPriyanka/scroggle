package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 3/21/2017.
 */

public class OpponentProfileActivity extends Activity {
    private boolean isPlayer2 = false;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.opponent_profile);

        Bundle  b = this.getIntent().getExtras();

        if (b != null) {
           isPlayer2 = b.getBoolean("isPlayer2");
        }

        Button ok = (Button) findViewById(R.id.ok_click);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpponentProfileActivity.this, TwoPlayerWordGameActivity.class);
                intent.putExtra("isPlayer2", isPlayer2);
                startActivity(intent);
            }
        });
    }

}
