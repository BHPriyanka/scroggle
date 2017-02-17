package edu.neu.madcourse.priyankabh.scroggle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.dictionary.TestDictionary;
import edu.neu.madcourse.priyankabh.tictactoe.Tile;

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
    private ScroggleFragment sFragment;
    private ScroggleControlFragment cFragment;
    private ScroggleTile mLargeTiles[] = new ScroggleTile[9];
    private ScroggleTile mSmallTiles[][] = new ScroggleTile[9][9];
    private int mLastLarge;
    private int mLastSmall;
    private Set<ScroggleTile> mAvailable = new HashSet<ScroggleTile>();
    static private List<List<Integer>> letterPostions = new ArrayList<List<Integer>>();
    static private List<String> listOfWords = new ArrayList<String>();
    private GameCountDownTimer countDownTimer;
    private final long startTime = 180000;
    private Handler mHandler = new Handler();
    private final long interval = 1000;
    private long timeRemaning=0;
    private TextView text;
    public String word="";
    private int phase = 1;
    public Vibrator vibrator;
    private Map<Integer,String> listOfWordsFormed = new HashMap<Integer,String>();
    public int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private String letterArray = "";
    private Set<ScroggleTile> nextMoves = new HashSet<ScroggleTile>();
    private String gameData="";
    private char[][] letterState = new char[9][9];
    private boolean[] isAWord = new boolean[9];
    private View rView;
    private Map<Integer,ArrayList<Integer>> smallIdsWhichFomWord = new HashMap<Integer,ArrayList<Integer>>();
    private int mPhaseOnePoints=0;
    private int mPhaseTwoPoints=0;
    private int pointsForNumberOfWordsFound = 0;
    private int pointsForLetters = 0;
    private String phaseTwoWord = "";
    private int pointsForNineLetterWords = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(globalVariable.nineLetterWords.isEmpty()){
            new LoadWords().execute();
        }
        try {
            if(!((ScroggleGameActivity)this.getActivity()).isRestore()) {
                listOfWords = new getWords().execute().get();
                placeLettersInGrids();
            }
        }catch(InterruptedException ie){
            System.err.print(ie);
        }catch (ExecutionException ce){
            System.err.print(ce);
        }
        initGame();

        Bundle b = getActivity().getIntent().getExtras();
        if (b != null) {
            phase = 2;
            gameData = b.getString("gameData");
        }

        vibrator = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.sergenious_moveo, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
        mSoundRewind = mSoundPool.load(getActivity(), R.raw.oldedgar_winner, 1);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.large_scroggle, container, false);
        rView = rootView;
        initViews(rootView);
        if (gameData != "" && gameData != null) {
            putState(gameData);
        } else {
            startGame(rootView);
        }
        updateAllTiles();
        if(phase == 2) {
            if (timeRemaning < 1000) {
                countDownTimer = new GameCountDownTimer(90000, interval);
            } else {
                countDownTimer = new GameCountDownTimer(timeRemaning, interval);
            }
        } else {
            if(timeRemaning > 0){
                countDownTimer = new GameCountDownTimer(timeRemaning, interval);
            } else {
                countDownTimer = new GameCountDownTimer(90000, interval);
            }
        }
        text = (TextView) rootView.findViewById(R.id.timer);
        /*countDownTimer = new GameCountDownTimer(startTime, interval);
        text.setText(text.getText() + String.valueOf(startTime));*/
        countDownTimer.start();
        return rootView;

    }

    private void startGame(View rootView) {
        mEntireBoard.setView(rootView);

        for(int large = 0; large< 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            List<Integer> pos = letterPostions.get(large);
            String s = listOfWords.get(large);
            for(int small = 0;small < 9; small++) {
                int i = pos.get(small);
                Button inner = (Button) outer.findViewById(mSmallIds[i]);
                inner.setText(String.valueOf(s.charAt(small)));
                final ScroggleTile smallTile = mSmallTiles[large][i];
                letterState[large][i] = s.charAt(small);
                smallTile.setLetter(String.valueOf(s.charAt(small)));
            }
        }
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
        setAvailableFromLastMove(mLastSmall);
        setValidNextMove(mLastLarge,mLastSmall);
    }


    private void initViews(View rootView) {
      mEntireBoard.setView(rootView);

        for(int large = 0; large < 9; large++){
            final View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            //List<Integer> pos = letterPostions.get(large);
            //String s = listOfWords.get(large);
            for(int small = 0; small < 9; small++){
              //  int i = pos.get(small);
                final int fLarge = large;
                final int fSmall = small;
                final ScroggleTile smallTile = mSmallTiles[large][small];
                final Button inner = (Button) outer.findViewById(mSmallIds[small]);
                //inner.setText(String.valueOf(s.charAt(small)));
                smallTile.setView(inner);
                //smallTile.setLetter(String.valueOf(s.charAt(small)));

                inner.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        smallTile.animate();
                        if (phase == 1) {
                            if (isValidMove(smallTile)) {
                                if (!isChosen(smallTile)) {
                                    smallTile.setChosen(true);
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                    //GradientDrawable gd = (GradientDrawable)inner.getBackground().getCurrent();
                                    //gd.setColor(getResources().getColor(R.color.green_color));
                                    formWord(String.valueOf(smallTile.getLetter()), fLarge, fSmall);
                                } else {
                                    smallTile.setChosen(false);
                                    word = delLastChar(listOfWordsFormed.get(fLarge));
                                    listOfWordsFormed.put(fLarge, word);
                                    //GradientDrawable gd = (GradientDrawable)inner.getBackground().getCurrent();
                                    //gd.setColor(getResources().getColor(R.color.gray_color));
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_avail));
                                }
                                mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                                vibrator.vibrate(15);
                                think();
                            } else {
                                mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                            }
                            //code to check if the letter is selected and then form a word
                            mLastLarge = fLarge;
                            mLastSmall = fSmall;
                        } else {
                            if (mLastLarge != fLarge) {
                                if (!smallTile.getIsEmpty()) {
                                    smallTile.setIsEmpty(true);
                                    phaseTwoWord = appendLetter(smallTile.getLetter());
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                    mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                                    vibrator.vibrate(15);
                                    think();
                                } else {
                                    mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                                    ((ScroggleGameActivity) getActivity()).stopThinking();
                                }
                                mLastLarge = fLarge;
                                mLastSmall = fSmall;
                            }
                        }
                    }
                });

                inner.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v) {
                        //onclick recognise its a word, clearoff all other boxes in this tile
                        //call the search method and check if the word exists in the dictionary
                        if (phase == 1) {
                            if (isValidMove(smallTile)) {
                                formWord(String.valueOf(smallTile.getLetter()), fLarge, fSmall);
                                if (word.length() >= 3) {
                                    Boolean isWordPresent = searchWordInMap(word);
                                    if (isWordPresent) {
                                        Log.d("WORD PRESENT:", word);
                                        listOfWordsFormed.put(fLarge, "");
                                        word = "";
                                        //isAWord[fLarge] = false;
                                        setAllNextMoves();
                                        for (int small = 0; small < 9; small++) {
                                            final ScroggleTile otherTile = mSmallTiles[fLarge][small];
                                            final Button innerButton = (Button) outer.findViewById(mSmallIds[small]);
                                            otherTile.setView(innerButton);
                                            if (smallIdsWhichFomWord.get(fLarge).contains(small)) {
                                                innerButton.setEnabled(false);
                                                innerButton.setTextColor(getResources().getColor(R.color.black_color));
                                                innerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                            } else {
                                                //innerButton.setVisibility(View.GONE);
                                                innerButton.setEnabled(false);
                                                innerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_gray));
                                            }
                                            otherTile.animate();
                                            otherTile.setChosen(false);
                                        }
                                        smallIdsWhichFomWord.get(fLarge).clear();
                                        isAWord[fLarge] = false;
                                        vibrator.vibrate(30);
                                    } else {
                                        setAllNextMoves();
                                        for (int small = 0; small < 9; small++) {
                                            final ScroggleTile otherTile = mSmallTiles[fLarge][small];
                                            final Button innerButton = (Button) outer.findViewById(mSmallIds[small]);
                                            otherTile.setView(innerButton);

                                            if (smallIdsWhichFomWord.get(fLarge).contains(small)) {
                                                if (otherTile.getLetter().equals(word.substring(word.length() - 1)) && (small == fSmall)) {
                                                    //innerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_gray));
                                                    word = delLastChar(listOfWordsFormed.get(fLarge));
                                                    listOfWordsFormed.put(fLarge, word);
                                                    ArrayList<Integer> list = smallIdsWhichFomWord.get(fLarge);
                                                    for (Integer i : list) {
                                                        if (i == small) {
                                                            list.remove(i);
                                                        }
                                                    }
                                                    otherTile.animate();
                                                    otherTile.setChosen(false);
                                                } else {
                                                    innerButton.setTextColor(getResources().getColor(R.color.black_color));
                                                    // innerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                                }

                                            }

                                        }
                                        setValidNextMove(mLastLarge, mLastSmall);
                                        isAWord[fLarge] = false;
                                        vibrator.vibrate(30);

                                    }
                                } else {
                                    setValidNextMove(mLastLarge, mLastSmall);
                                }
                            }
                        } else {
                            final View rootView = mEntireBoard.getView();
                            for(int l =0; l< 9 ; l++) {
                                final View outer = rootView.findViewById(mLargeIds[l]);
                                mLargeTiles[l].setView(outer);
                                for(int s= 0; s< 9; s++){
                                    Button button = (Button) outer.findViewById(mSmallIds[s]);
                                    ScroggleTile tinyTile = (ScroggleTile) mSmallTiles[l][s];
                                    if (tinyTile.getIsChosen()) {
                                        if (isAWord[l]) {
                                            tinyTile.setIsEmpty(false);
                                            letterArray="";
                                            mLastLarge = -1;
                                            button.setText(String.valueOf(tinyTile.getLetter()));
                                            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_gray));
                                        } else {
                                                button.setText("");
                                                button.setEnabled(false);
                                                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_avail));
                                        }
                                    }
                                }
                            }
                            vibrator.vibrate(45);
                        }
                        return true;
                    }
                });
            }
        }
    }


    private String delLastChar(String str) {
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void formWord(String letter, int large, int small) {
        if(large == mLastLarge){
            word = word.concat(letter);
            if(listOfWordsFormed != null) {
                if (word.length() == 1) {
                    word = listOfWordsFormed.get(large).concat(word);
                }
                listOfWordsFormed.put(large, word);
            } else {
                    listOfWordsFormed.put(large, word);
                }
            if(smallIdsWhichFomWord.get(large) == null) {
                smallIdsWhichFomWord.put(large, new ArrayList<Integer>(Arrays.asList(small)));
            } else {
                ArrayList<Integer> smalls = smallIdsWhichFomWord.get(large);
                smalls.add(small);
            }
            } else {
                word = "".concat(letter);
            if(listOfWordsFormed != null && listOfWordsFormed.get(large) != null) {
                    word = listOfWordsFormed.get(large).concat(word);
                    listOfWordsFormed.put(large, word);
                } else {
                   listOfWordsFormed.put(large, word);
                }
            if(smallIdsWhichFomWord.get(large) == null) {
                smallIdsWhichFomWord.put(large, new ArrayList<Integer>(Arrays.asList(small)));
            } else {
                ArrayList<Integer> smalls = smallIdsWhichFomWord.get(large);
                smalls.add(small);
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

    public String appendLetter(String letter){
        letterArray = letterArray.concat(letter);
        return letterArray;
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
                    Arrays.asList(1,0,3,6,4,2,5,7,8),
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

    public void display(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) getView().findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        //display the word formed at top
        text.setText("Word: " + word);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void think(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (phase == 2) {
                    if(getActivity() == null) return;
                    if(phaseTwoWord.length() >= 3){
                        Boolean isWordPresent = searchWordInMap(phaseTwoWord);
                        if(isWordPresent){
                            Log.d("WORD PRESENT: ",phaseTwoWord);
                            display();
                            // re display the board
                            final View rootView = mEntireBoard.getView();
                            for(int l =0; l< 9 ; l++) {
                                final View outer = rootView.findViewById(mLargeIds[l]);
                                mLargeTiles[l].setView(outer);
                                for(int s= 0; s< 9; s++){
                                    Button button = (Button) outer.findViewById(mSmallIds[s]);
                                    ScroggleTile tinyTile = (ScroggleTile) mSmallTiles[l][s];
                                    if (tinyTile.getIsChosen()) {
                                        if (isAWord[l]) {
                                            tinyTile.setIsEmpty(false);
                                            letterArray="";
                                            mLastLarge = -1;
                                            button.setText(String.valueOf(tinyTile.getLetter()));
                                            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_gray));
                                        } else {
                                            button.setText("");
                                            button.setEnabled(false);
                                            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_avail));
                                        }
                                    }
                                }
                            }
                            vibrator.vibrate(45);
                        }
                    }
                    ((ScroggleGameActivity) getActivity()).stopThinking();
                } else {
                    if (getActivity() == null) return;
                    setValidNextMove(mLastLarge, mLastSmall);
                    if (listOfWordsFormed.get(mLastLarge) != null) {
                        if (listOfWordsFormed.get(mLastLarge).length() >= 3) {
                            Boolean isWordPresent = searchWordInMap(word);
                            if (isWordPresent) {
                                Log.d("WORD PRESENT: ", word);
                                isAWord[mLastLarge] = isWordPresent;
                                display();
                            }
                        }
                    }
                    ((ScroggleGameActivity) getActivity()).stopThinking();
                }
            }

        }, 1000);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                ScroggleTile tile = mSmallTiles[small][dest];
                if (!tile.getIsChosen()) {
                    addAvailable(tile);
                }
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][small];
                if(!tile.getIsChosen()) {
                    addAvailable(tile);
                }
            }
        }
    }


    /** Create a string containing the state of the game. */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(timeRemaning);
        builder.append(',');
        builder.append(phase);
        builder.append(',');
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getLetter());
                builder.append(',');
                builder.append(mSmallTiles[large][small].getIsChosen());
                builder.append(',');
                builder.append(letterState[large][small]);
                builder.append(',');
            }
            builder.append(listOfWordsFormed.get(large));
            builder.append(',');
            builder.append(isAWord[large]);
            builder.append(',');
        }
        builder.append(phaseTwoWord);
        builder.append(',');
        return builder.toString();
    }


    private void clearAvailable() {
        mAvailable.clear();
    }


    private void addAvailable(ScroggleTile tile) {
        tile.animate();
        mAvailable.add(tile);
    }

    private Boolean isAvailable(ScroggleTile tile){
        return mAvailable.contains(tile);
    }

    private  void clearMove(){
        nextMoves.clear();
    }

    private void addMove(ScroggleTile tile){
        nextMoves.add(tile);
    }

    private Boolean isValidMove(ScroggleTile tile){
        return nextMoves.contains(tile);
    }

    public class GameCountDownTimer extends CountDownTimer {

        public GameCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            //text.setText("Time is up");
            if (phase == 1) {
                if (listOfWordsFormed.size() > 0) {
                    text.setText("FINISH!!");
                    ((ScroggleGameActivity) getActivity()).setFragmentInvisible();
                    final Dialog mDialog = new Dialog(getActivity());
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mDialog.setTitle("END: PHASE 1");
                    mDialog.setContentView(R.layout.phase_two);
                    mDialog.setCancelable(false);

                    TextView textView = (TextView) getView().findViewById(R.id.TextView01);
                    textView.setText("BEGIN: PHASE TWO");

                    Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            phase = 2;
                            timeRemaning = 0;
                            Intent intent = new Intent(getActivity(), ScroggleGameActivity.class);
                            startActivity(intent);
                            intent.putExtra("gameData", getState());
                            intent.putExtra("phase", phase);
                            getActivity().finish();
                            //if (mDialog != null)
                            //    mDialog.dismiss();
                        }
                    });
                    //now that the dialog is set up, it's time to show it
                    mDialog.show();
                } else {
                    text.setText("GAME END");
                    final Dialog mDialog = new Dialog(getActivity());
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mDialog.setTitle("Game Over");
                    mDialog.setContentView(R.layout.phase_two);
                    mDialog.setCancelable(false);

                    TextView textView = (TextView) mDialog.findViewById(R.id.alert);
                    textView.setText("SCROGGLE: GAME OVER");

                    Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                            //if (mDialog != null)
                            //    mDialog.dismiss();
                        }
                    });
                    //now that the dialog is set up, it's time to show it
                    mDialog.show();
                }
            } else {
                //for phase two
                text.setText("GAME END");
                gameData = "";
                sFragment.getView().setVisibility(View.INVISIBLE);
                cFragment.getView().setVisibility(View.INVISIBLE);
                final Dialog mDialog = new Dialog(getActivity());
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setTitle("Game Over");
                mDialog.setContentView(R.layout.phase_two);
                mDialog.setCancelable(false);

                TextView textView = (TextView) getView().findViewById(R.id.TextView01);
                textView.setText("END OF SCROGGLE GAME");

                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                        //if (mDialog != null)
                        //    mDialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }

        }

        @Override
        public void onTick(long millisUnitFInished) {
            String time = String.format("%d : %d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUnitFInished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUnitFInished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUnitFInished)));
            if (time.equals("00:03") || time.equals("00:02") || time.equals("00:01")) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.blink);
                text.startAnimation(animation);
                text.setText("Time Remaining: " +time);

            } else {
                text.setText("Time Remaning: " + time);
            }
            timeRemaning = millisUnitFInished;
        }
    }

    public boolean isChosen(ScroggleTile tile) {
        return tile.getIsChosen();
    }

    public Boolean searchWordInMap(String word) {
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

        if(globalVariable.list.get(word.toLowerCase().substring(0,2)).contains(word.toLowerCase())){
            return true;
        }

        return false;

    }

    public void setPhase(int ph){
        this.phase = ph;
    }

    public void putState(String data){
        String[] fields = gameData.split(",");
        int index = 0;
        timeRemaning = Long.parseLong(fields[index++]);
        this.setTime(timeRemaning);
        phase = Integer.parseInt(fields[index++]);
        this.setPhase(phase);
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for(int large = 0; large < 9; large++){
            for(int small = 0; small < 9; small++){
                String letter = (fields[index++]);
                Boolean isChosen =  Boolean.valueOf(fields[index++]);
                mSmallTiles[large][small].setLetter(letter);
                mSmallTiles[large][small].setChosen(isChosen);
                char state = fields[index++].charAt(0);
                letterState[large][small] = state;
            }
            String sequenceOfLetters = (fields[index++]);
            listOfWordsFormed.put(large, sequenceOfLetters);
            Boolean isWord = Boolean.valueOf(fields[index++]);
            isAWord[large] = isWord;
        }
        phaseTwoWord = (fields[index++]);
       // boolean isGameEndFlag = Boolean.parseBoolean(fields[index++]);
       // ((ScroggleGameActivity) this.getActivity()).setGameEnd(isGameEndFlag);
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
        for(int large = 0; large <9; large++){
            final View outer = rView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            for(int small = 0; small <9; small++){
                final ScroggleTile smallTile = mSmallTiles[large][small];
                final Button inner = (Button) rView.findViewById(mSmallIds[small]);
                if(phase == 2){
                    if(smallTile.getIsChosen()){
                        if(isAWord[large]){
                            inner.setText(String.valueOf(smallTile.getLetter()));
                            inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_gray));
                        } else {
                            inner.setText("");
                            inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_avail));
                            inner.setEnabled(false);
                        }
                    } else {
                        inner.setText("");
                        inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_avail));
                        inner.setEnabled(false);
                    }
                } else {
                    inner.setText(String.valueOf(letterState[large][small]));
                    smallTile.setLetter(String.valueOf(letterState[large][small]));
                    if (smallTile.getIsChosen()) {
                        inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                    } else {
                        inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_gray));
                    }
                }
            }
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    public void setValidNextMove(int sLarge, int sSmall){
        //clear the nextmoves hashset
        clearMove();
        if(sLarge != -1 && sSmall != -1) {

            //get all the possible squares in all directions of the grid with value small
            List<Integer> moves = getPossibleMoves(sSmall);
            for (int i=0; i< moves.size();i++) {
                int gridId = moves.get(i);
                ScroggleTile smallTile = mSmallTiles[sLarge][gridId];
                if (!smallTile.getIsChosen()) {
                    addMove(smallTile);
                }
            }

            for (int large = 0; large < 9; large++) {
                for (int small = 0; small < 9; small++) {
                    if (large != sLarge) {
                        //   if(!mSmallTiles[large][small].getIsChosen()){
                        addMove(mSmallTiles[large][small]);
                        //   }
                    }
                }
            }
        }
        if(nextMoves.isEmpty()){
            setAllNextMoves();
        }
    }

    public void setAllNextMoves(){
        for(int large=0;large < 9; large++){
            for(int small = 0; small< 9 ; small++){
                addMove(mSmallTiles[large][small]);
            }
        }
    }

    public List<Integer> getPossibleMoves(int small){
        List<Integer> moves = new ArrayList<Integer>();
        if(small == 0){
            moves.addAll(Arrays.asList(1,3,4));
        } else if(small == 1){
            moves.addAll(Arrays.asList(0,2,3,4,5));
        } else if(small == 2){
            moves.addAll(Arrays.asList(1,4,5));
        } else if(small == 3){
            moves.addAll(Arrays.asList(0,1,4,6,7));
        } else if(small == 4){
            moves.addAll(Arrays.asList(0,1,2,3,5,6,7,8));
        } else if(small == 5){
            moves.addAll(Arrays.asList(1,2,4,7,8));
        } else if(small == 6){
            moves.addAll(Arrays.asList(3,4,7));
        } else if(small == 7){
            moves.addAll(Arrays.asList(3,4,5,6,8));
        } else if(small == 8){
            moves.addAll(Arrays.asList(4,5,7));
        }

        return moves;
    }


    public int getLetterScore(String letter){
        int point = 0;
        if(letter.equals("")){
            point=0;
        } else if(letter.equals("e") || letter.equals("a") || letter.equals("i") || letter.equals("o")
                || letter.equals("n") || letter.equals("r") || letter.equals("t") || letter.equals("l")
                || letter.equals("n")){
                    point = 1;
        } else if(letter.equals("d") || letter.equals("g")) {
            point = 2;
        } else if(letter.equals("b") || letter.equals("c") || letter.equals("m") || letter.equals("p")){
            point = 3;
        } else if(letter.equals("f") || letter.equals("h") || letter.equals("v") || letter.equals("w") || letter.equals("y")) {
            point = 4;
        } else if(letter.equals("k")){
            point = 5;
        } else if(letter.equals("j") || letter.equals("x")){
            point =8;
        } else if(letter.equals("q") || letter.equals("z")){
            point = 10;
        }
        return  point;
    }

    public int getTotalPhaseOnePoints(){
        return mPhaseOnePoints + pointsForNumberOfWordsFound + pointsForLetters + pointsForNineLetterWords;
    }

    public int getPointsForNumberOfWordsFound(){
        int point=0;
        return point;
    }

    public void setTime(long time){
        this.timeRemaning = time;
    }

}
