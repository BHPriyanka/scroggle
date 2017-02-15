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
    private String mLetter;
    private Boolean isFilled;
    private Boolean isEmpty;

    public ScroggleTile(ScroggleFragment game) {
        this.mGame = game;
        this.isFilled = false;
        this.isEmpty = false;
    }

    public void setSubTiles(ScroggleTile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public String getLetter(){
        return mLetter;
    }

    public void setmLetter(String letter) {
        this.mLetter = letter;
    }

    public View getView(){
        return mView;
    }

    public ScroggleTile[] getSubTiles(){
        return mSubTiles;
    }

    public Boolean getIsFilled(){
        return isFilled;
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
}
