package com.example.frecat_menta.algorithm;

import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;
import com.example.frecat_menta.FrecatMenta;
import com.example.frecat_menta.R;

/**
 * Created by 286868 on 2/28/2016.
 */
public class ScoreCalculator implements View.OnTouchListener {

    private static final String SCORE_PREF = "score_value";


    private final FrecatMenta menta;
    private VelocityTracker mVelocityTracker = null;
    private float absoluteScore;

    public ScoreCalculator(FrecatMenta menta) {
        this.menta = menta;
        absoluteScore = PreferenceManager.getDefaultSharedPreferences(menta).getFloat(SCORE_PREF, 0);
        setScoreText();
    }


    public boolean onTouch(View v, MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity
                    // of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset thevelocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }

                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(500);

                // Log velocity of pixels per second
                float rawVelocity = Math.abs(mVelocityTracker.getXVelocity(pointerId) + mVelocityTracker.getYVelocity(pointerId));
                absoluteScore += (rawVelocity / 20000);

                setScoreText();

                break;
            case MotionEvent.ACTION_UP:
                saveScore();
        }


        return true;
    }

    private void setScoreText() {
        final TextView scoreText = (TextView) menta.findViewById(R.id.scoreText);
        scoreText.setText("" + Math.round(absoluteScore));
    }

    private void saveScore() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(menta).edit();
        editor.putFloat(SCORE_PREF, absoluteScore);
        editor.commit();
    }

}