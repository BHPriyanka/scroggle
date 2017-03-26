package edu.neu.madcourse.priyankabh.twoplayergame;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleFragment;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleTile;

/**
 * Created by priya on 3/13/2017.
 */

public class TwoPlayerScroggleTile {
    private final TwoPlayerScroggleFragment mGame;
    private TwoPlayerScroggleTile mSubTiles[];
    private View mView;
    private String letter;
    private Boolean isChosen;
    private Boolean isEmpty;

    public TwoPlayerScroggleTile(TwoPlayerScroggleFragment game) {
        this.mGame = game;
        this.isChosen = false;
        this.isEmpty = false;
    }

    public Boolean getIsEmpty(){
        return this.isEmpty;
    }

    public void setIsEmpty(Boolean bool){
        this.isEmpty = bool;
    }

    public void setSubTiles(TwoPlayerScroggleTile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public String getLetter(){
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public View getView(){
        return mView;
    }

    public void setChosen(boolean bool){
        this.isChosen = bool;
    }

    public Boolean getIsChosen(){
        return isChosen;
    }


    public void animate() {
        Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
                R.animator.tictactoe);
        if (getView() != null) {
            anim.setTarget(getView());
            anim.start();
        }
    }


    public void updateDrawableState() {
        if (mView == null) return;
        int level = getLevel();
        Boolean flag = getIsChosen();
        if (mView.getBackground() != null) {
            if(flag == true) {
                mView.getBackground().setLevel(level);
            } else {
                mView.getBackground().setLevel(level);
            }
        }
        if (mView instanceof Button) {
            mView.getBackground().setLevel(level);
        }
    }

    private int getLevel() {
        int level;
        if(getIsChosen()){
            level = R.drawable.letter_green;
        }
        else {
            level = R.drawable.letter_avail;
        }

        if(mView instanceof Button){
            level = R.drawable.letter_gray;
        }
        return level;
    }

}
