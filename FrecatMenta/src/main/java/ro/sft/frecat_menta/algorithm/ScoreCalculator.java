package ro.sft.frecat_menta.algorithm;

import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ro.sft.frecat_menta.FrecatMenta;
import ro.sft.frecat_menta.R;

/**
 * Created by 286868 on 2/28/2016.
 */
public class ScoreCalculator implements View.OnTouchListener {

    private static final String SCORE_PREF = "score_value";


    private final FrecatMenta menta;
    private final int SCORE_STEP = 100;
    private BonusFactor bonusFactor;
    private VelocityTracker mVelocityTracker = null;
    private float absoluteScore;
    private boolean workingHoursNotifShown;

    public ScoreCalculator(FrecatMenta menta) {
        this.menta = menta;
        bonusFactor = new BonusFactor();
        absoluteScore = PreferenceManager.getDefaultSharedPreferences(menta).getFloat(SCORE_PREF, 0);
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
                if (rawVelocity < 1500) {
                    rawVelocity = 1500;
                }
                absoluteScore += (rawVelocity / bonusFactor.get());
                displayScore();
                break;
            case MotionEvent.ACTION_UP:
                saveScore();
        }


        return true;
    }


    public long getScore() {
        return (long) Math.ceil(absoluteScore / SCORE_STEP);
    }

    public void displayScore() {
        final TextView scoreText = (TextView) menta.findViewById(R.id.scoreText);
        final ProgressBar bar = (ProgressBar) menta.findViewById(R.id.progressBar);
        bar.setMax(SCORE_STEP);
        bar.setProgress((int) (absoluteScore % SCORE_STEP));
        scoreText.setText("" + getScore());
    }

    private void saveScore() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(menta).edit();
        editor.putFloat(SCORE_PREF, absoluteScore);
        editor.apply();
        if (!workingHoursNotifShown && bonusFactor.isNineToFive()) {
            Toast.makeText(menta, menta.getString(R.string.funny_working_hours_msg), Toast.LENGTH_LONG).show();
            workingHoursNotifShown = true;
        }
    }

    public void saveScore(long rawScore) {
        long rawAbsoluteScore = rawScore * SCORE_STEP;
        if (rawAbsoluteScore > absoluteScore) {
            absoluteScore = rawAbsoluteScore;
        }
        Editor editor = PreferenceManager.getDefaultSharedPreferences(menta).edit();
        editor.putFloat(SCORE_PREF, absoluteScore);
        editor.apply();
        displayScore();
    }

}