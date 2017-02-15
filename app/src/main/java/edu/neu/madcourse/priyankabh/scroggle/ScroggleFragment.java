package edu.neu.madcourse.priyankabh.scroggle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 2/9/2017.
 */

public class ScroggleFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};

    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private ScroggleTile mEntireBoard = new ScroggleTile(this);
    private ScroggleTile mLargeTiles[] = new ScroggleTile[9];
    private ScroggleTile mSmallTiles[][] = new ScroggleTile[9][9];
    private int mLastLarge;
    private int mLastSmall;
    private Set<ScroggleTile> mAvailable = new HashSet<ScroggleTile>();
    private String lettersSelected = "";
    static private List<List<Integer>> letterPostions = new ArrayList<List<Integer>>();
    static private List<String> listOfWords = new ArrayList<String>();
    private GameCountDownTimer countDownTimer;
    private final long startTime = 180000;
    private final long interval = 1000;
    private TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(globalVariable.nineLetterWords.isEmpty()){
            new LoadWords().execute();
        }
        try {
            listOfWords = new getWords().execute().get();
            placeLettersInGrids();
        }catch(InterruptedException ie){
            System.err.print(ie);
        }catch (ExecutionException ce){
            System.err.print(ce);
        }
        initGame();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.large_scroggle, container, false);
        initViews(rootView);
        startGame(rootView);
        text = (TextView) rootView.findViewById(R.id.timer);
        countDownTimer = new GameCountDownTimer(startTime, interval);
        text.setText(text.getText() + String.valueOf(startTime));
        countDownTimer.start();
        return rootView;

    }

    public void initGame() {
        mEntireBoard = new ScroggleTile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new ScroggleTile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new ScroggleTile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);
        // If the player moves first set which spots are available
        mLastLarge = -1;
        mLastSmall = -1;
        //setAvailableFromLastMove(mLastSmall);
    }


    private void startGame(View rootView) {
        mEntireBoard.setView(rootView);

        for(int large = 0; large< 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            List<Integer> pos = letterPostions.get(large);
            String s = listOfWords.get(large);
            for(int small = 0;small < 9; small++){
                int i = pos.get(small);
                Button inner = (Button) outer.findViewById(mSmallIds[i]);
                inner.setText(String.valueOf(s.charAt(small)));
                final ScroggleTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                smallTile.setmLetter(String.valueOf(s.charAt(small)));
            }
        }
    }


    private void initViews(View rootView) {
      mEntireBoard.setView(rootView);

        for(int large = 0;large < 9;large++){
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            for(int small = 0; small < 9; small++){
                final int fLarge = large;
                final int fSmall = small;
                final ScroggleTile smallTile = mSmallTiles[large][small];
                final Button inner = (Button) outer.findViewById(mSmallIds[small]);
                smallTile.setView(inner);

                inner.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //code to check if the letter is selected and then form a word
                        mLastLarge = fLarge;
                        mLastSmall = fSmall;
                    }
                });
            }
        }
    }

    private class LoadWords extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
            try {
                InputStream strF = getResources().getAssets().open("wordlist.txt");

                Scanner s = new Scanner(strF);
                while(s.hasNextLine()){
                    String word=s.nextLine();
                    if(word.length() == 9){
                        globalVariable.nineLetterWords.add(word);
                    }
                }

            } catch(IOException e) {
                System.err.print(e);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
        }

        protected void onPostExecute(Void v) {

        }
    }

    private class getWords extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> str = new ArrayList<String>();
            final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
             Collections.shuffle(globalVariable.nineLetterWords);
                for(int i=0;i<9;i++){
                    Random randomGenerator;
                    randomGenerator = new Random();
                    int index = randomGenerator.nextInt(globalVariable.nineLetterWords.size());
                    String item = globalVariable.nineLetterWords.get(index);
                    Log.d("TAG_ScroggleFragment","Words selected " + item + " our recommendation to you");
                    str.add(item);
                }
            return str;
        }

        protected void onProgressUpdate(Integer... params) {
        }

        protected void onPostExecute(Void v) {

        }
    }

    public void placeLettersInGrids(){
            List<List<Integer>> positions = new ArrayList<List<Integer>>();
            positions.addAll(Arrays.asList(
                    Arrays.asList(0,3,6,7,5,2,1,4,8),
                    Arrays.asList(0,1,4,6,3,7,8,5,2),
                    Arrays.asList(2,5,7,8,4,6,3,1,0),
                    Arrays.asList(2,5,1,0,3,7,6,4,8),
                    Arrays.asList(3,0,4,6,7,8,5,1,2),
                    Arrays.asList(5,8,4,6,7,3,0,1,2),
                    Arrays.asList(8,4,0,3,6,7,5,2,1),
                    Arrays.asList(1,5,8,7,6,3,0,4,2),
                    Arrays.asList(1,0,3,6,4,1,5,7,8),
                    Arrays.asList(6,7,8,5,1,0,3,4,2),
                    Arrays.asList(6,3,1,0,4,2,5,8,7),
                    Arrays.asList(3,6,4,8,7,5,2,1,0),
                    Arrays.asList(5,2,4,0,1,3,6,7,8),
                    Arrays.asList(4,1,2,5,8,7,6,3,0),
                    Arrays.asList(5,2,1,0,3,4,6,7,8),
                    Arrays.asList(8,7,6,3,4,5,2,1,0),
                    Arrays.asList(6,4,3,0,1,2,5,8,7),
                    Arrays.asList(4,0,1,3,6,7,8,5,2)
            ));

        for(int i=0;i<9;i++) {
            Random random = new Random();
            int index = random.nextInt(positions.size());
            letterPostions.add(positions.get(index));
        }
    }

    /** Create a string containing the state of the game. */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                //builder.append(mSmallTiles[large][small].getOwner().name());
                //builder.append(mSmallTiles[large][small].getLetter());
                builder.append(',');
            }
        }
        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    /*public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
            }
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }*/

    public boolean isAvailable(ScroggleTile tile) {
        return mAvailable.contains(tile);
    }

    private void clearAvailable() {
        mAvailable.clear();
    }


    private void addAvailable(ScroggleTile tile) {
        mAvailable.add(tile);
    }

    public class GameCountDownTimer extends CountDownTimer{

        public GameCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public  void onFinish(){
            text.setText("Time is up");
        }

        @Override
        public void onTick(long millisUnitFInished){
            String time = String.format("%d : %d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUnitFInished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUnitFInished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUnitFInished)));
            text.setText("Time Remaning: " + time);
        }

    }
}
