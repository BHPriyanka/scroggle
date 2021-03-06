package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.EnterWordToDatabaseActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleGameActivity;
import edu.neu.madcourse.priyankabh.tictactoe.ControlFragment;
import edu.neu.madcourse.priyankabh.twoplayergame.models.Player;

/**
 * Created by priya on 3/13/2017.
 */

public class TwoPlayerScroggleFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};

    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private Map<String,Object> dataMap = new HashMap<String,Object>();
    private Boolean isResume = ScroggleTwoPlayerGameActivity.isResume;
    private TwoPlayerScroggleTile mEntireBoard = new TwoPlayerScroggleTile(this);
    private int playerOneScore = 0;
    private int count = 0;
    private  TwoPlayerControlFragment controlFragment;
    private String getTokenInstance="";
    private TwoPlayerScroggleTile mLargeTiles[] = new TwoPlayerScroggleTile[9];
    private TwoPlayerScroggleTile mSmallTiles[][] = new TwoPlayerScroggleTile[9][9];
    private int mLastLarge;
    private int mLastSmall;
    private Set<TwoPlayerScroggleTile> mAvailable = new HashSet<TwoPlayerScroggleTile>();
    private Set<TwoPlayerScroggleTile> tAvailable = new HashSet<TwoPlayerScroggleTile>();
    static private List<List<Integer>> letterPostions = new ArrayList<List<Integer>>();
    static private List<String> listOfWords = new ArrayList<String>();
    public TwoPlayerScroggleFragment.GameCountDownTimer countDownTimer;
    public Handler mHandler = new Handler();
    private final int interval = 1000;
    public long timeRemaning = 0;
    private DatabaseReference mDatabase;
    private TextView text;
    public String word = "";
    private int player = 1;
    public Vibrator vibrator;
    private Map<Integer, String> listOfWordsFormed = new HashMap<Integer, String>();
    private Map<Integer, String> listOfPhaseTwoWordsFormed = new HashMap<Integer, String>();
    public int mSoundX, mSoundO, mSoundMiss;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private Set<TwoPlayerScroggleTile> nextMoves = new HashSet<TwoPlayerScroggleTile>();
    private Set<TwoPlayerScroggleTile> playerTwoNextMoves = new HashSet<TwoPlayerScroggleTile>();
    static private String gameData = "";
    private int[] scoreForPhaseOneWordsFormed = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private Map<String, Integer> scoresForWords = new HashMap<String, Integer>();
    private char[][] letterState = new char[9][9];
    private boolean[] isAWord = new boolean[9];
    private View rView;
    private Map<Integer, ArrayList<Integer>> smallIdsWhichFormWordPhaseOne = new HashMap<Integer, ArrayList<Integer>>();
    private Map<String, HashMap<Integer, ArrayList<Integer>>> listOfSmallIds = new HashMap<String, HashMap<Integer, ArrayList<Integer>>>();
    public int mPhaseOnePoints = 0;
    public MediaPlayer mediaPlayer;
    private String color="green";
    private int totalPlayerTime;
    private Bundle b;
    private boolean isPlayer1 = true;
    private int totalGameTime;
    public GlobalClass globalVariable;
    public char[][] finalLetters = new char[9][9];
    public StringBuilder finalLettersBuilder = new StringBuilder();
    private  View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getTokenInstance = FirebaseInstanceId.getInstance().getToken();

        b = getActivity().getIntent().getExtras();

        if (b != null) {
            totalPlayerTime = b.getInt("totalPlayerTime");
            totalGameTime = b.getInt("totalGameTime");
            isPlayer1 = b.getBoolean("isPlayer1");
           }

        try {
            if(isPlayer1) {
                listOfWords = new TwoPlayerScroggleFragment.getWords().execute().get();
                placeLettersInGrids();
               // char[][] cArray = new char[9][9];

                for (int large = 0; large < letterPostions.size(); large++) {
                    finalLettersBuilder.append(large);
                    finalLettersBuilder.append(',');
                    List<Integer> pos = letterPostions.get(large);
                    String s = listOfWords.get(large);
                    for (Integer small = 0; small < pos.size(); small++) {
                        int i = pos.get(small);
                        //finalLetters[large][i] = s.charAt(small);
                        finalLettersBuilder.append(i);
                        finalLettersBuilder.append(',');
                        finalLettersBuilder.append(s.charAt(i));
                        finalLettersBuilder.append(',');
                    }
                }

                // add list of Words and letterpositions to the firebase
                TwoPlayerScroggleFragment.this.onAddWordsAndPositions(mDatabase, getTokenInstance);
            }


            globalVariable = (GlobalClass) getActivity().getApplicationContext();

            if(!isPlayer1) {
                Map entry = (Map) globalVariable.usersMap.get(getTokenInstance);
                String opposite = (String) entry.get("opponent");

                Map opp_entry = (Map) globalVariable.usersMap.get(opposite);
           //     finalLetters= (char[][]) opp_entry.get("grids");
                finalLettersBuilder = new StringBuilder((String)opp_entry.get("grids"));
                TwoPlayerScroggleFragment.this.onAddWordsAndPositions(mDatabase, getTokenInstance);
            }

        } catch (InterruptedException ie) {
            System.err.print(ie);
        } catch (ExecutionException ce) {
            System.err.print(ce);
        }

        if (globalVariable.usersMap.containsKey(getTokenInstance)) {
            Map entry = (Map) globalVariable.usersMap.get(getTokenInstance);
                if (((String) entry.get("gameState")).equals("") || (String) entry.get("gameState") == null) {
                    if (b != null) {
                        gameData = b.getString("gameState");
                    }
                } else {
                    gameData = b.getString("gameState");
                }

            }


        initGame();

        vibrator = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.sergenious_moveo, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.happy_music);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

       rootView = inflater.inflate(R.layout.large_scroggle, container, false);
        rView = rootView;
        initViews(rootView);

        if (gameData != "" && gameData != null) {
            TwoPlayerScroggleFragment.this.onAddGameState(mDatabase, getTokenInstance);
//            TwoPlayerScroggleFragment.this.onAddGameState(mDatabase, globalVariable.pairPlayers.get(getTokenInstance));
            startGame(rootView);
            putState(gameData);
        } else {
            startGame(rootView);
        }
        updateAllTiles();
//        if (isPlayer1) {
//            if(totalGameTime == 165000 || totalGameTime == 135000 || totalGameTime == 105000 ||
//                    totalGameTime == 75000 || totalGameTime == 45000 || totalGameTime == 15000){
//                rootView.setVisibility(View.INVISIBLE);
//                /* View pause = TwoPlayerControlFragment.getRootView().findViewById(R.id.player_pause_button);
//                 pause.setVisibility(View.INVISIBLE);
//
//                View resume = TwoPlayerControlFragment.getRootView().findViewById(R.id.player_button_resume);
//                resume.setVisibility(View.INVISIBLE);*/
//
//            } else if(totalGameTime == 180000 || totalGameTime == 150000 || totalGameTime == 120000 || totalGameTime == 90000
//                    || totalGameTime == 60000 || totalGameTime == 30000 || totalGameTime == 0){
//                rootView.setVisibility(View.VISIBLE);
//                /*View pause = ((ScroggleTwoPlayerGameActivity) getActivity()).cFragment.getView().findViewById(R.id.player_pause_button);
//                pause.setVisibility(View.VISIBLE);
//                View resume = TwoPlayerControlFragment.getRootView().findViewById(R.id.player_button_resume);
//                resume.setVisibility(View.VISIBLE);*/
//            }
//
//            if (timeRemaning < 1000) {
//                countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(15000, interval);
//            } else {
//                countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(timeRemaning, interval);
//            }
//        } else {
//
//            if(totalGameTime == 180000 || totalGameTime == 150000 || totalGameTime == 120000 || totalGameTime == 90000
//                    || totalGameTime == 60000 || totalGameTime == 30000 || totalGameTime == 0){
//                //public void setFragmentInvisible() {
//                rootView.setVisibility(View.INVISIBLE);
//                /* View pause = controlFragment.getRootView().findViewById(R.id.player_pause_button);
//                pause.setVisibility(View.INVISIBLE);
//                View resume = controlFragment.getRootView().findViewById(R.id.player_button_resume);
//                resume.setVisibility(View.INVISIBLE);*/
//
//            } else  if(totalGameTime == 165000 || totalGameTime == 135000 || totalGameTime == 105000 ||
//                    totalGameTime == 75000 || totalGameTime == 45000 || totalGameTime == 15000){
//                rootView.setVisibility(View.VISIBLE);
////                ((ScroggleTwoPlayerGameActivity) getActivity()).cFragment.setControlFragmentVisible();
//               /* View pause = controlFragment.getRootView().findViewById(R.id.player_pause_button);
//                pause.setVisibility(View.VISIBLE);
//                View resume = controlFragment.getRootView().findViewById(R.id.player_button_resume);
//                resume.setVisibility(View.VISIBLE);*/
//            }
//
//            if (timeRemaning > 0) {
//                countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(timeRemaning, interval);
//            } else {
//                countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(15000, interval);
//            }
//        }
        text = (TextView) rootView.findViewById(R.id.timer);
        countDownTimer.start();
        return rootView;

    }

    private void startGame(View rootView) {
        String[] fields = finalLettersBuilder.toString().split(",");
        int index = 0;
        for(int large = 0; large<9;large++){
            int i = Integer.parseInt(fields[index++]);
            for(int small =0;small<9;small++){
                int j = Integer.parseInt(fields[index++]);
                finalLetters[i][j] = fields[index++].charAt(0);
            }
        }

        mEntireBoard.setView(rootView);
        for(int large = 0; large< finalLetters.length;large++){
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            for (Integer small = 0; small < finalLetters[0].length; small++) {
                Button inner = (Button) outer.findViewById(mSmallIds[small]);
                inner.setText(String.valueOf(finalLetters[large][small]));
                final TwoPlayerScroggleTile smallTile = mSmallTiles[large][small];
                letterState[large][small] = finalLetters[large][small];
                smallTile.setLetter(String.valueOf(finalLetters[large][small]));
            }
        }


    }

    public void initGame() {
        mEntireBoard = new TwoPlayerScroggleTile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[((int) large)] = new TwoPlayerScroggleTile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[((int) large)][((int) small)] = new TwoPlayerScroggleTile(this);
            }
            mLargeTiles[((int) large)].setSubTiles(mSmallTiles[((int) large)]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);
        // If the player moves first set which spots are available
        mLastLarge = -1;
        mLastSmall = -1;
        setAvailableFromLastMove(mLastSmall);
        setValidNextMove(mLastLarge, mLastSmall);
    }


    private void initViews(final View rootView) {
        mEntireBoard.setView(rootView);

        for (int large = 0; large < 9; large++) {
            final View outer = rootView.findViewById((int)(mLargeIds[((int) large)]));
            mLargeTiles[((int) large)].setView(outer);
            for (int small = 0; small < 9; small++) {
                final int fLarge = large;
                final int fSmall = small;
                final TwoPlayerScroggleTile smallTile = mSmallTiles[((int) large)][((int) small)];
                final Button inner = (Button) outer.findViewById((int)mSmallIds[((int) small)]);
                smallTile.setView(inner);

                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        smallTile.animate();
                        if (isValidMove(smallTile)) {
                            if (!isChosen(smallTile)) {
                                smallTile.setChosen(true);
                                if(color.equals("green")){
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                } else {
                                    inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_red));
                                }

                                formWord(String.valueOf(smallTile.getLetter()), fLarge, fSmall);
                            } else {
                                smallTile.setChosen(false);
                                word = delLastChar(listOfWordsFormed.get(fLarge));
                                listOfWordsFormed.put(fLarge, word);
                                inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_avail));
                            }
                            mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
                            vibrator.vibrate(15);
                            mLastLarge = fLarge;
                            mLastSmall = fSmall;
                            setValidNextMove(mLastLarge, mLastSmall);
                        }
                    }

                });

                inner.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (isValidMove(smallTile)) {
                            formWord(String.valueOf(smallTile.getLetter()), fLarge, fSmall);
                            if (word.length() >= 3) {
                                Boolean isWordPresent = searchWordInMap(word);
                                if (isWordPresent) {
                                       /* phaseOneScore = 0;
                                        for (int i = 0; i < listOfWordsFormed.get(mLastLarge).length(); i++) {
                                            phaseOneScore += getLetterScore(listOfWordsFormed.get(mLastLarge).charAt(i));
                                        }
                                        scoreForPhaseOneWordsFormed[mLastLarge] = phaseOneScore;
                                        isAWord[mLastLarge] = isWordPresent;
                                        listOfWordsFormed.put(fLarge, word);*/
//                                        word = "";
                                    mSmallTiles[((int) fLarge)][((int) fSmall)].setChosen(true);
                                    setAllNextMoves();
                                    for (int l = 0; l < 9; l++) {
                                        for (int small = 0; small < 9; small++) {
                                            final TwoPlayerScroggleTile otherTile = mSmallTiles[((int) l)][((int) small)];
                                            final Button innerButton = (Button) outer.findViewById((int)(mSmallIds[((int) small)]));
                                            otherTile.setView(innerButton);
                                            if (smallIdsWhichFormWordPhaseOne.get(l) != null && l == fLarge) {
                                                if (smallIdsWhichFormWordPhaseOne.get(l).contains(small)) {
                                                    innerButton.setEnabled(false);
                                                    innerButton.setClickable(false);
                                                    innerButton.setTextColor(getResources().getColor(R.color.black_color));
                                                    if(color.equals("green")){
                                                        inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                                    } else {
                                                        inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_red));
                                                    }
                                                    otherTile.animate();
                                                }
                                            } else if (smallIdsWhichFormWordPhaseOne.get(l) != null) {
                                                if (smallIdsWhichFormWordPhaseOne.get(l).contains(small)) {
                                                    if (!otherTile.getIsChosen()) {
                                                        innerButton.setEnabled(false);
                                                        innerButton.setClickable(false);
                                                        innerButton.setTextColor(getResources().getColor(R.color.black_color));
                                                        if(color.equals("green")){
                                                            inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                                                        } else {
                                                            inner.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_red));
                                                        }
                                                        otherTile.animate();
                                                    }
                                                }
                                            }

                                        }
                                    }
                                    playerOneScore = 0;
                                    for (int i = 0; i < word.length(); i++) {
                                        playerOneScore += getLetterScore(word.charAt(((int) i)));
                                    }
                                    scoresForWords.put(word, playerOneScore);

                                    word = "";
                                    mLastLarge = fLarge;
                                    mLastSmall = fSmall;
                                    calcPhaseOnePoints();
                                    ((ScroggleTwoPlayerGameActivity) getActivity()).setPhaseOnePoints(mPhaseOnePoints);
                                } else {
                                    setAllNextMoves();
                                    for (int l = 0; l < 9; l++) {
                                        for (int small = 0; small < 9; small++) {
                                            final TwoPlayerScroggleTile otherTile = mSmallTiles[l][small];
                                            final Button innerButton = (Button) outer.findViewById(mSmallIds[(small)]);
                                            otherTile.setView(innerButton);

                                            if (smallIdsWhichFormWordPhaseOne.get(l) != null && smallIdsWhichFormWordPhaseOne.get(l).contains(small)) {
                                                if (otherTile.getLetter().equals(word.substring(word.length() - 1)) && (small == fSmall)) {
                                                    word = delLastChar(listOfWordsFormed.get(l));
                                                    listOfWordsFormed.put(fLarge, word);
                                                    ArrayList<Integer> list = smallIdsWhichFormWordPhaseOne.get(l);
                                                    for (Integer i : list) {
                                                        if (i == small) {
                                                            list.remove(i);
                                                        }
                                                    }
                                                    ArrayList<Integer> ids = smallIdsWhichFormWordPhaseOne.get(l);
                                                    ids.remove(small);
                                                    smallIdsWhichFormWordPhaseOne.put(l, ids);
                                                    otherTile.animate();
                                                } else {
                                                    innerButton.setTextColor(getResources().getColor(R.color.black_color));
                                                }

                                            }

                                        }
                                    }
                                    setValidNextMove(mLastLarge, mLastSmall);

                                }
                                vibrator.vibrate(20);

                            } else {
                                setValidNextMove(mLastLarge, mLastSmall);
                                scoreForPhaseOneWordsFormed[((int) fLarge)] = 0;
                            }

                        }

                        clearMove();
                        for (int large = 0; large < 9; large++) {
                            for (int small = 0; small < 9; small++) {
                                if (!mSmallTiles[((int) large)][((int) small)].getIsChosen()) {
                                    addMove(mSmallTiles[((int) large)][((int) small)]);
                                }
                            }
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
        if (large == mLastLarge) {
            if (word == "") {
                word = "".concat(letter);
            } else {
                word = word.concat(letter);
            }
            if (listOfWordsFormed != null && listOfWordsFormed.get(large) != null) {
                word = listOfWordsFormed.get(large).concat(letter);
                listOfWordsFormed.put(large, word);

            } else {
                listOfWordsFormed.put(large, word);
            }
            if (smallIdsWhichFormWordPhaseOne.get(large) == null) {
                smallIdsWhichFormWordPhaseOne.put(large, new ArrayList<Integer>(Arrays.asList(small)));
            } else {
                ArrayList<Integer> smalls = smallIdsWhichFormWordPhaseOne.get(large);
                smalls.add(small);
            }
            if (listOfSmallIds.get(delLastChar(word)) != null) {
                HashMap<Integer, ArrayList<Integer>> hmap = listOfSmallIds.get(delLastChar(word));
                ArrayList<Integer> ids = new ArrayList<Integer>();
                if(hmap.get(large) != null) {
                    ids = hmap.get(large);
                }
                ids.add(small);
                hmap.put(large, ids);
                listOfSmallIds.put(word, hmap);
                listOfSmallIds.remove(delLastChar(word));
            } else {
                HashMap<Integer, ArrayList<Integer>> hh = new HashMap<Integer, ArrayList<Integer>>();
                hh.put(large, new ArrayList<Integer>(Arrays.asList(small)));
                listOfSmallIds.put(word, hh);

            }
        } else {

            if (word == "") {
                word = "".concat(letter);
            } else {
                word = word.concat(letter);
            }

            if (listOfPhaseTwoWordsFormed != null && listOfWordsFormed.get(large) != null) {
                word = listOfPhaseTwoWordsFormed.get(large).concat(letter);
                listOfPhaseTwoWordsFormed.put(large, word);
            } else {
                listOfPhaseTwoWordsFormed.put(large, word);
            }

            if (smallIdsWhichFormWordPhaseOne.get(large) == null) {
                smallIdsWhichFormWordPhaseOne.put(large, new ArrayList<Integer>(Arrays.asList(small)));
            } else {
                ArrayList<Integer> smalls = smallIdsWhichFormWordPhaseOne.get(large);
                smalls.add(small);
            }

            if (listOfSmallIds.get(delLastChar(word)) != null) {
                HashMap<Integer, ArrayList<Integer>> hmap = listOfSmallIds.get(delLastChar(word));
                ArrayList<Integer> ids = new ArrayList<Integer>();
                if(hmap.get(large) != null) {
                    ids = hmap.get(large);
                }
                ids.add(small);
                hmap.put(large, ids);
                listOfSmallIds.put(word, hmap);
                listOfSmallIds.remove(delLastChar(word));
            } else {
                HashMap<Integer, ArrayList<Integer>> hh = new HashMap<Integer, ArrayList<Integer>>();
                hh.put(large, new ArrayList<Integer>(Arrays.asList(small)));
                listOfSmallIds.put(word, hh);

            }
        }
    }


    private class getWords extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> str = new ArrayList<String>();
            final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
            Collections.shuffle(globalVariable.nineLetterWords);
            for (int i = 0; i < 9; i++) {
                Random randomGenerator;
                randomGenerator = new Random();
                int index = randomGenerator.nextInt(globalVariable.nineLetterWords.size());
                String item = globalVariable.nineLetterWords.get(((int) index));
                str.add(item);
            }
            return str;
        }

        protected void onProgressUpdate(Integer... params) {
        }

        protected void onPostExecute(Void v) {

        }
    }


    public void placeLettersInGrids() {
        List<List<Integer>> positions = new ArrayList<List<Integer>>();
        positions.addAll(Arrays.asList(
                Arrays.asList(0, 3, 6, 7, 5, 2, 1, 4, 8),
                Arrays.asList(0, 1, 4, 6, 3, 7, 8, 5, 2),
                Arrays.asList(2, 5, 7, 8, 4, 6, 3, 1, 0),
                Arrays.asList(2, 5, 1, 0, 3, 7, 6, 4, 8),
                Arrays.asList(3, 0, 4, 6, 7, 8, 5, 1, 2),
                Arrays.asList(5, 8, 4, 6, 7, 3, 0, 1, 2),
                Arrays.asList(8, 4, 0, 3, 6, 7, 5, 2, 1),
                Arrays.asList(1, 5, 8, 7, 6, 3, 0, 4, 2),
                Arrays.asList(1, 0, 3, 6, 4, 2, 5, 7, 8),
                Arrays.asList(6, 7, 8, 5, 1, 0, 3, 4, 2),
                Arrays.asList(6, 3, 1, 0, 4, 2, 5, 8, 7),
                Arrays.asList(3, 6, 4, 8, 7, 5, 2, 1, 0),
                Arrays.asList(5, 2, 4, 0, 1, 3, 6, 7, 8),
                Arrays.asList(4, 1, 2, 5, 8, 7, 6, 3, 0),
                Arrays.asList(5, 2, 1, 0, 3, 4, 6, 7, 8),
                Arrays.asList(8, 7, 6, 3, 4, 5, 2, 1, 0),
                Arrays.asList(6, 4, 3, 0, 1, 2, 5, 8, 7),
                Arrays.asList(4, 0, 1, 3, 6, 7, 8, 5, 2)
        ));

        for (int i = 0; i < 9; i++) {
            Random random = new Random();
            int index = random.nextInt(positions.size());
            letterPostions.add(positions.get(index));
        }
    }

    public void display() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) getView().findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("Word: " + word);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void think() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) return;
                setValidNextMove(mLastLarge, mLastSmall);
                if (listOfWordsFormed.get(mLastLarge) != null) {
                    if (listOfWordsFormed.get(mLastLarge).length() >= 3) {
                        Boolean isWordPresent = searchWordInMap(word);
                        if (isWordPresent) {
                            display();
                        } else {
                            scoreForPhaseOneWordsFormed[((int) mLastLarge)] = 0;
                        }
                        isAWord[((int) mLastLarge)] = isWordPresent;

                    }
                }

                ((ScroggleTwoPlayerGameActivity) getActivity()).stopThinking();
            }

        }, 1000);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                TwoPlayerScroggleTile tile = mSmallTiles[((int) small)][((int) dest)];
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
                TwoPlayerScroggleTile tile = mSmallTiles[((int) large)][((int) small)];
                if (!tile.getIsChosen()) {
                    addAvailable(tile);
                }
            }
        }
    }


    public long getRemainingTime(){
        long time;
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
      //  if (globalVariable.usersMap.containsKey(getTokenInstance)) {
            Map<String, Object> p = (Map<String, Object>) globalVariable.usersMap.get(getTokenInstance);
            time = (long) p.get("totalPlayerTime");
      //  }
        time = time - 15000;
        return time;
    }

    public long getGameRemainingTime(){
        long gameTime;
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        Map<String, Object> p = (Map<String, Object>) globalVariable.usersMap.get(getTokenInstance);
        gameTime = (long)p.get("totalGameTime");
        gameTime = gameTime - 15000;
        return gameTime;
    }

    /**
     *
     * Create a string containing the state of the game.
     */
    public String getState() {
        StringBuilder builder = new StringBuilder();

        builder.append(mPhaseOnePoints);
        builder.append(',');
        builder.append(word);
        builder.append(',');
        builder.append(timeRemaning);
        builder.append(',');
        builder.append(player);
        builder.append(',');

        builder.append(listOfSmallIds.size());
        builder.append(',');
        for(Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> hmap: listOfSmallIds.entrySet()){
            builder.append(hmap.getKey());
            builder.append(',');
            HashMap<Integer, ArrayList<Integer>> largeSmallIds = hmap.getValue();
            builder.append(largeSmallIds.size());
            builder.append(',');
            for(Map.Entry<Integer, ArrayList<Integer>> entry: largeSmallIds.entrySet()) {
                List<Integer> smallIds = entry.getValue();
                builder.append(entry.getKey());
                builder.append(',');
                builder.append(smallIds.toString().replaceAll(",",":"));
                builder.append(',');
                }
            }


        //for (int large = 0; large < 9; large++) {
        //for (int small = 0; small < 9; small++) {
        for(Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> hmap: listOfSmallIds.entrySet()){
            HashMap<Integer, ArrayList<Integer>> largeSmallIds = hmap.getValue();
            for(Map.Entry<Integer, ArrayList<Integer>> entry: largeSmallIds.entrySet()) {
                List<Integer> smallIds = entry.getValue();
                for(Integer i : smallIds) {
                    builder.append(mSmallTiles[entry.getKey()][i].getLetter());
                    builder.append(',');
                    builder.append(mSmallTiles[entry.getKey()][i].getIsChosen());
                    builder.append(',');
                    //builder.append(letterState[entry.getKey()][i]);
                    // builder.append(',');
                }
            }
        }
        //  }
        //}
        return builder.toString();
    }


    private void clearAvailable() {
        mAvailable.clear();
    }


    private void addAvailable(TwoPlayerScroggleTile tile) {
        tile.animate();
        mAvailable.add(tile);
    }

    private void clearMove() {
        nextMoves.clear();
    }

    private void addMove(TwoPlayerScroggleTile tile) {
        nextMoves.add(tile);
    }

    private Boolean isValidMove(TwoPlayerScroggleTile tile) {
        return nextMoves.contains(tile);
    }

    public class GameCountDownTimer extends CountDownTimer {

        public GameCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
            Log.d("countdowntimer", "GameCountDownTimer: ..........");
        }

        @Override
        public void onFinish() {
         //   if (player == 1) {
                if (listOfSmallIds.size() > 0) {
                    ((ScroggleTwoPlayerGameActivity) getActivity()).setFragmentInvisible();
                    final Dialog mDialog = new Dialog(getActivity());
                    mDialog.setTitle("SCROGGLE");
                    mDialog.setContentView(R.layout.phase_two);
                    mDialog.setCancelable(false);

                    TextView textView = (TextView) mDialog.findViewById(R.id.alert);
                    textView.setText("Turn Over: " + mPhaseOnePoints);

                    Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            timeRemaning = 0;
                            Intent intent = new Intent(getActivity(), ScroggleTwoPlayerGameActivity.class);
                            intent.putExtra("player", player);
                            intent.putExtra("gameState", getState());
                            intent.putExtra("totalPlayerTime", getRemainingTime());
                            intent.putExtra("totalGameTime", getGameRemainingTime());
                            startActivity(intent);
                            mediaPlayer.pause();
                            //getActivity().finish();
                        }
                    });
                    //now that the dialog is set up, it's time to show it
                    mDialog.show();
                } else {
                    final Dialog mDialog = new Dialog(getActivity());
                    mDialog.setTitle("SCROGGLE");
                    mDialog.setContentView(R.layout.phase_two);
                    mDialog.setCancelable(false);

                    TextView textView = (TextView) mDialog.findViewById(R.id.alert);
                    textView.setText("Turn Over");

                    Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            timeRemaning = 0;
                            Intent intent = new Intent(getActivity(), ScroggleTwoPlayerGameActivity.class);
                            intent.putExtra("player", player);
                            intent.putExtra("gameState", getState());
                            intent.putExtra("totalPlayerTime", getRemainingTime());
                            intent.putExtra("totalGameTime", getGameRemainingTime());
                            startActivity(intent);
                            mediaPlayer.pause();
                          //  getActivity().finish();
                        }
                    });
                    //now that the dialog is set up, it's time to show it
                    mDialog.show();
                }
            /*} else {

                if (listOfSmallIds.size() > 0) {
                    ((ScroggleTwoPlayerGameActivity) getActivity()).setFragmentInvisible();
                    final Dialog mDialog = new Dialog(getActivity());
                    mDialog.setTitle("SCROGGLE");
                    mDialog.setContentView(R.layout.phase_two);
                    mDialog.setCancelable(false);

                    TextView textView = (TextView) mDialog.findViewById(R.id.alert);
                    textView.setText("Turn Over" + mPhaseOnePoints);

                    Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            timeRemaning = 0;
                            Intent intent = new Intent(getActivity(), ScroggleTwoPlayerGameActivity.class);
                            intent.putExtra("player", player);
                            intent.putExtra("gameState", getState());
                            intent.putExtra("totalPlayerTime", getRemainingTime());
                            intent.putExtra("totalGameTime", getGameRemainingTime());
                            startActivity(intent);
                            mediaPlayer.pause();
                         //   getActivity().finish();
                        }
                    });
                    //now that the dialog is set up, it's time to show it
                    mDialog.show();
                } else
                {
                    gameData = "";
                    rView.setVisibility(View.INVISIBLE);
                    final Dialog mDialog = new Dialog(getActivity());
                    mDialog.setTitle("SCROGGLE");
                    mDialog.setContentView(R.layout.phase_two);
                    mDialog.setCancelable(false);

                    TextView textView = (TextView) mDialog.findViewById(R.id.alert);
                    int total = mPhaseOnePoints;// + mPhaseTwoPoints;
                    textView.setText("Turn Over: " + total);

                    Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                    ok_button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            timeRemaning = 0;
                            Intent intent = new Intent(getActivity(), ScroggleTwoPlayerGameActivity.class);
                            intent.putExtra("player", player);
                            intent.putExtra("gameState", getState());
                            intent.putExtra("totalPlayerTime", getRemainingTime());
                            intent.putExtra("totalGameTime", getGameRemainingTime());
                            startActivity(intent);
                            mediaPlayer.pause();
                         //   getActivity().finish();
                        }
                    });
                    //now that the dialog is set up, it's time to show it
                    mDialog.show();
                }
            }*/

        }

        @Override
        public void onTick(long millisUnitFInished) {
            String time = String.format("%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUnitFInished) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUnitFInished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUnitFInished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUnitFInished)));
            if (time.equals("00 : 05") || time.equals("00 : 04") || time.equals("00 : 03") || time.equals("00 : 02") || time.equals("00 : 01")) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.blink);
                text.startAnimation(animation);
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.sergenious_moveo);
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.start();
                text.setTextColor(getResources().getColor(R.color.red_color));
                text.setText("Time Remaining: " + time);

            } else {
                text.setText("Time Remaning: " + time);
            }
            timeRemaning = millisUnitFInished;

            if (isPlayer1) {
                if(millisUnitFInished == 165000 || millisUnitFInished == 135000 || millisUnitFInished == 105000 ||
                        millisUnitFInished == 75000 || millisUnitFInished == 45000 || millisUnitFInished == 15000){
                    rootView.setVisibility(View.INVISIBLE);
                /* View pause = TwoPlayerControlFragment.getRootView().findViewById(R.id.player_pause_button);
                 pause.setVisibility(View.INVISIBLE);

                View resume = TwoPlayerControlFragment.getRootView().findViewById(R.id.player_button_resume);
                resume.setVisibility(View.INVISIBLE);*/

                } else if(millisUnitFInished == 180000 || millisUnitFInished == 150000 || millisUnitFInished == 120000 || millisUnitFInished == 90000
                        || millisUnitFInished == 60000 || millisUnitFInished == 30000 || millisUnitFInished == 0){
                    rootView.setVisibility(View.VISIBLE);
                /*View pause = ((ScroggleTwoPlayerGameActivity) getActivity()).cFragment.getView().findViewById(R.id.player_pause_button);
                pause.setVisibility(View.VISIBLE);
                View resume = TwoPlayerControlFragment.getRootView().findViewById(R.id.player_button_resume);
                resume.setVisibility(View.VISIBLE);*/
                }

                if (timeRemaning < 1000) {
                    countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(15000, interval);
                } else {
                    countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(timeRemaning, interval);
                }
            } else {

                if(millisUnitFInished == 180000 || millisUnitFInished == 150000 || millisUnitFInished == 120000 || millisUnitFInished == 90000
                        || millisUnitFInished == 60000 || millisUnitFInished == 30000 || millisUnitFInished == 0){
                    //public void setFragmentInvisible() {
                    rootView.setVisibility(View.INVISIBLE);
                /* View pause = controlFragment.getRootView().findViewById(R.id.player_pause_button);
                pause.setVisibility(View.INVISIBLE);
                View resume = controlFragment.getRootView().findViewById(R.id.player_button_resume);
                resume.setVisibility(View.INVISIBLE);*/

                } else  if(millisUnitFInished == 165000 || millisUnitFInished == 135000 || millisUnitFInished == 105000 ||
                        millisUnitFInished == 75000 || millisUnitFInished == 45000 || millisUnitFInished == 15000){
                    rootView.setVisibility(View.VISIBLE);
//                ((ScroggleTwoPlayerGameActivity) getActivity()).cFragment.setControlFragmentVisible();
               /* View pause = controlFragment.getRootView().findViewById(R.id.player_pause_button);
                pause.setVisibility(View.VISIBLE);
                View resume = controlFragment.getRootView().findViewById(R.id.player_button_resume);
                resume.setVisibility(View.VISIBLE);*/
                }

                if (timeRemaning > 0) {
                    countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(timeRemaning, interval);
                } else {
                    countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(15000, interval);
                }
            }
        }
    }

    public boolean isChosen(TwoPlayerScroggleTile tile) {
        return tile.getIsChosen();
    }

    public Boolean searchWordInMap(String word) {
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        if (globalVariable.list.get(word.toLowerCase().substring(0, 2)).contains(word.toLowerCase())) {
            return true;
        }
        return false;
    }

    public void setPhase(int ph) {
        this.player = ph;
    }


    public void putState(String data) {
        String[] fields = data.split(",");
        int index = 0;

        mPhaseOnePoints = Integer.parseInt(fields[index++]);
        ((ScroggleTwoPlayerGameActivity) getActivity()).setPhaseOnePoints(mPhaseOnePoints);
        word = fields[index++];
        this.setWord(word);
        timeRemaning = Integer.parseInt(fields[index++]);
        this.setTime(timeRemaning);
        player = Integer.parseInt(fields[index++]);
        this.setPhase(player);

        int lengthOfListOfSmallIds = Integer.parseInt(fields[index++]);
        for(int i=0 ; i< lengthOfListOfSmallIds;i++){
            String wordFormed = fields[index++];
            int lenOfLargeOfSmallIds = Integer.parseInt(fields[index++]);
            HashMap<Integer, ArrayList<Integer>> largeSmallIds = new HashMap<>();
            for(int j = 0; j < lenOfLargeOfSmallIds;j ++){
                int large = Integer.parseInt(fields[index++]);
                ArrayList<Integer> listI = new ArrayList<Integer>();
                String favs = fields[index++];
                String[] favs2 = favs.replaceAll("\\[|\\]| ", "").split(":");
                for (int k = 0; k < favs2.length; k++) {
                    listI.add(Integer.parseInt(favs2[k]));
                }

                largeSmallIds.put(((int) large), listI);

            }
            listOfSmallIds.put(wordFormed,largeSmallIds);

        }


        for(Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> hmap: listOfSmallIds.entrySet()){
            HashMap<Integer, ArrayList<Integer>> largeSmallIds = hmap.getValue();
            for(Map.Entry<Integer, ArrayList<Integer>> entry: largeSmallIds.entrySet()) {
                List<Integer> smallIds = entry.getValue();
                for(Integer i : smallIds) {
                    String letter = (fields[index++]);
                    mSmallTiles[entry.getKey().intValue()][i.intValue()].setLetter(letter);
                    Boolean isChosen = Boolean.valueOf(fields[index++]);
                    mSmallTiles[entry.getKey().intValue()][i.intValue()].setChosen(isChosen);
                    //char state = fields[index++].charAt(0);
                    // letterState[entry.getKey()][i] = state;
                }
            }
        }

        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();

     for(Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> hmap: listOfSmallIds.entrySet()){
         if (hmap.getKey().length() >= 3) {
             Boolean isWordPresent = searchWordInMap(hmap.getKey());
             if (isWordPresent) {
                 HashMap<Integer, ArrayList<Integer>> largeSmallIds = hmap.getValue();
                 for(Map.Entry<Integer, ArrayList<Integer>> entry: largeSmallIds.entrySet()) {
                     smallIdsWhichFormWordPhaseOne.put(entry.getKey(), entry.getValue());
                 }
             }
         }
     }

        for (int l = 0; l < 9; l++) {
            mEntireBoard.setView(rView);
            final View outer = rView.findViewById((int)(mLargeIds[((int) l)]));
            mLargeTiles[((int) l)].setView(outer);
            for (int s = 0; s < 9; s++) {
                Button button = (Button) outer.findViewById((int)(mSmallIds[((int) s)]));
                TwoPlayerScroggleTile tinyTile = mSmallTiles[l][s];
                if (smallIdsWhichFormWordPhaseOne.get(l) != null) {
                    if (smallIdsWhichFormWordPhaseOne.get(l).contains(s)) {
                        mLastLarge = -1;
                        button.setText(String.valueOf(tinyTile.getLetter()));
                        button.setTextColor(getResources().getColor(R.color.black_color));
                        button.setClickable(false);
                        button.setEnabled(false);
                        if(player == 1) {
                       // if(isPlayer1){
                            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_green));
                        } else {
                            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.letter_red));
                        }
                        tinyTile.setIsEmpty(true);
                    } else {
                        button.setClickable(true);
                        button.setText(String.valueOf(tinyTile.getLetter()));
                        button.setEnabled(true);
                        tinyTile.setChosen(false);
                        tinyTile.setIsEmpty(false);
                    }
                } else {
                    button.setClickable(true);
                    button.setText(String.valueOf(tinyTile.getLetter()));
                    button.setEnabled(true);
                    tinyTile.setChosen(false);
                    tinyTile.setIsEmpty(false);
                }
                           }
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[((int) large)].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[((int) large)][((int) small)].updateDrawableState();
            }
        }
    }

    public void setValidNextMove(int sLarge, int sSmall) {
        clearMove();
        if (sLarge != -1 && sSmall != -1) {
            List<Integer> moves = getPossibleMoves(sSmall);
            for (int i = 0; i < moves.size(); i++) {
                int gridId = moves.get(((int) i));
                TwoPlayerScroggleTile smallTile = mSmallTiles[((int) sLarge)][((int) gridId)];
                if (!smallTile.getIsChosen()) {
                    addMove(smallTile);
                }
            }

            for (int large = 0; large < 9; large++) {
                for (int small = 0; small < 9; small++) {
                    if (large != sLarge) {
                        addMove(mSmallTiles[((int) large)][((int) small)]);
                    }
                }
            }

        }
        if (nextMoves.isEmpty()) {
            setAllNextMoves();
        }
    }

    public void setAllNextMoves() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                addMove(mSmallTiles[((int) large)][((int) small)]);
            }
        }
    }

    public List<Integer> getPossibleMoves(int small) {
        List<Integer> moves = new ArrayList<Integer>();
        if (small == 0) {
            moves.addAll(Arrays.asList(1, 3, 4));
        } else if (small == 1) {
            moves.addAll(Arrays.asList(0, 2, 3, 4, 5));
        } else if (small == 2) {
            moves.addAll(Arrays.asList(1, 4, 5));
        } else if (small == 3) {
            moves.addAll(Arrays.asList(0, 1, 4, 6, 7));
        } else if (small == 4) {
            moves.addAll(Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8));
        } else if (small == 5) {
            moves.addAll(Arrays.asList(1, 2, 4, 7, 8));
        } else if (small == 6) {
            moves.addAll(Arrays.asList(3, 4, 7));
        } else if (small == 7) {
            moves.addAll(Arrays.asList(3, 4, 5, 6, 8));
        } else if (small == 8) {
            moves.addAll(Arrays.asList(4, 5, 7));
        }

        return moves;
    }

    public int getLetterScore(char letter) {
        int point = 0;
        if ((letter == 'e') || (letter == 'a') || (letter == 'i') || (letter == 'o')
                || (letter == 'r') || (letter == 't') || (letter == 'l')
                || (letter == 'n') || (letter == 's') || (letter == 'u')) {
            point = 1;
        } else if (letter == ('d') || letter == ('g')) {
            point = 2;
        } else if (letter == ('b') || letter == ('c') || letter == ('m') || letter == ('p')) {
            point = 3;
        } else if (letter == ('f') || letter == ('h') || letter == ('v') || letter == ('w') || letter == ('y')) {
            point = 4;
        } else if (letter == ('k')) {
            point = 5;
        } else if (letter == ('j') || letter == ('x')) {
            point = 8;
        } else if (letter == ('q') || letter == ('z')) {
            point = 10;
        }
        return point;
    }

    public void setTime(long time) {
        this.timeRemaning = time;
    }

    public void setWord(String wo){
        this.word = wo;
    }

    public void onScrogglePause() {
        countDownTimer.cancel();
        mediaPlayer.pause();
        ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.getView().setVisibility(View.INVISIBLE);
        isResume = false;
        GlobalClass.activityPaused();// On Pause notify the Application
    }

    public void onScroggleResume() {
        countDownTimer = new TwoPlayerScroggleFragment.GameCountDownTimer(timeRemaning, interval);
        countDownTimer.start();
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.happy_music);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.getView().setVisibility(View.VISIBLE);
        isResume = true;
        GlobalClass.activityResumed();// On Resume notify the Application
    }

    private void calcPhaseOnePoints() {
        mPhaseOnePoints = 0;
        /*for (int i = 0; i < 9; i++) {
            if (isAWord[i]) {
                mPhaseOnePoints += scoreForPhaseOneWordsFormed[i];
            }
        }*/
        for(String s: scoresForWords.keySet()){
            mPhaseOnePoints += scoresForWords.get(s);
        }

        int scoreForLengthOfWords = 0;
        /*for (Integer large : listOfWordsFormed.keySet()) {
            int sco = getScoreForLengthOfWords(listOfWordsFormed.get(large));
            scoreForLengthOfWords += sco;
        }*/
        for(String s:scoresForWords.keySet()){
            int sco = getScoreForLengthOfWords(s);
            scoreForLengthOfWords += sco;
        }

        mPhaseOnePoints += scoreForLengthOfWords;
    }

    public int getScoreForLengthOfWords(String str) {
        //based on the length of the words, give each letter 2 points(Bonus)
        int s = 0;
        if (str.length() == 3) {
            s = 6;
        } else if (str.length() == 4) {
            s = 8;
        } else if (str.length() == 5) {
            s = 10;
        } else if (str.length() == 6) {
            s = 12;
        } else if (str.length() == 7) {
            s = 14;
        } else if (str.length() == 8) {
            s = 16;
        } else if (str.length() == 9) {//bonus if  words is 9 in length
            s = 25;
        }

        return s;
    }


    public void onScroggleQuit() {
        countDownTimer.cancel();
        mediaPlayer.pause();
        isResume = false;
        getActivity().finish();
    }

    public void mute() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

        /**
         * Called on word add
         * @param postRef
         */
    private void onAddGameState(DatabaseReference postRef, String player) {
        final String p =player;
        postRef
                .child("players")
                .child(player)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Player u = mutableData.getValue(Player.class);
                        if (u == null) {
                            return Transaction.abort();
                        }

                        u.totalGameTime = Integer.valueOf(totalGameTime);
                        if(p == getTokenInstance) {
                            u.totalPlayerTime = Integer.valueOf(totalPlayerTime);
                        }
                        u.gameState = String.valueOf(gameData);
                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                    }
                });
    }

    /**
     * Called on word add
     * @param postRef
     */
    private void onAddWordsAndPositions(DatabaseReference postRef, final String player) {
    //    final String p =player;
        postRef
                .child("players")
                .child(player)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Player u = mutableData.getValue(Player.class);
                        GenericTypeIndicator<Map<String,Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>(){};
                        Map<String,Object> map = mutableData.getValue(genericTypeIndicator);

                        if (u == null || map == null) {
                            Log.d("onAddWordsAndPositions","Transaction failed");
                            return Transaction.abort();
                        }


                        map.put("grids", finalLetters);
                        globalVariable.usersMap.put(player, map);

                        u.grids = finalLettersBuilder.toString();

                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                    }
                });
    }
}

