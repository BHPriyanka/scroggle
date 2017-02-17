/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/
package edu.neu.madcourse.priyankabh.scroggle;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import edu.neu.madcourse.priyankabh.R;

public class ScroggleTile {
    private final ScroggleFragment mGame;
    private ScroggleTile mSubTiles[];
    private View mView;
    private String letter;
    private Boolean isChosen;
    private Boolean isEmpty;

    public ScroggleTile(ScroggleFragment game) {
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

    public void setSubTiles(ScroggleTile[] subTiles) {
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

    public ScroggleTile[] getSubTiles(){
        return mSubTiles;
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

    public void timer(View v){
        Animation blink = AnimationUtils.loadAnimation(mGame.getActivity().getApplicationContext(), R.anim.blink);
        v.startAnimation(blink);
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
