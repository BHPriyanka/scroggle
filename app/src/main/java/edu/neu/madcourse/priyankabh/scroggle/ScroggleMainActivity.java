package edu.neu.madcourse.priyankabh.scroggle;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/10/2017.
 */

public class ScroggleMainActivity extends Activity {
    MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scroggle);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.gat_loop);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }
}
