package edu.neu.madcourse.priyankabh.scroggle;

import android.app.Activity;
import android.os.Bundle;

import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/9/2017.
 */

public class ScroggleGameActivity extends Activity {
    private ScroggleFragment sFragment;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_scroggle);
        sFragment = (ScroggleFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_scroggle);

    }
}
