/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
***/
package edu.neu.madcourse.priyankabh.tictactoe;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;

import edu.neu.madcourse.priyankabh.R;

public class TicTacToeMainActivity extends Activity {
   MediaPlayer mMediaPlayer;
   // ...

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //getActionBar().hide();
      setContentView(R.layout.activity_main_tictactoe);
   }

}
