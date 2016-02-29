package com.example.frecat_menta;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.example.frecat_menta.algorithm.ScoreCalculator;
import com.example.frecat_menta.messages.AlertMessageManager;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

public class FrecatMenta extends Activity {


    private AlertMessageManager messageManager;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageManager = new AlertMessageManager(this);
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        messageManager.registerFunnyMessages();
        initScore();

    }

    private void initScore() {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainView);
        mainLayout.setOnTouchListener(new ScoreCalculator(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageManager.showFunnyWelcomeBack();
    }
}
