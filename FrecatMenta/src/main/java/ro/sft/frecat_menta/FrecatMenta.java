package ro.sft.frecat_menta;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.Leaderboards;
import ro.sft.frecat_menta.algorithm.ScoreCalculator;
import ro.sft.frecat_menta.api.GoogleApiClientWrapper;
import ro.sft.frecat_menta.base.BaseGameUtils;
import ro.sft.frecat_menta.leaderboard.LeaderboardManager;
import ro.sft.frecat_menta.messages.AlertMessageManager;

public class FrecatMenta extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    public static final String TAG = "Frecat Menta";
    public static final int RC_SIGN_IN = 9001;


    private AlertMessageManager messageManager;
    private LeaderboardManager leaderboardManager;
    private ScoreCalculator scoreCalculator;
    private GoogleApiClientWrapper googleApiClient;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        googleApiClient = GoogleApiClientWrapper.getInstance(this);
        messageManager = new AlertMessageManager(this);
        leaderboardManager = new LeaderboardManager(this);
        scoreCalculator = new ScoreCalculator(this);
        initShowLeaderBoardButton();
        initShareScoreButton();
        initScore();
    }

    private void initShowLeaderBoardButton() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (googleApiClient.isGamesApiAvailable())
                    leaderboardManager.showBoard();
                else
                    messageManager.showToastLong(R.string.wait_for_play_connection);
            }
        });
    }

    private void initShareScoreButton() {
        findViewById(R.id.button_share).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (googleApiClient.isGamesApiAvailable())
                    leaderboardManager.checkPlayerAllTimeFirst(new LoadPlayerScoreCallback(true));
                else
                    messageManager.showToastLong(R.string.wait_for_play_connection);
            }
        });
    }

    private void initScore() {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainView);
        mainLayout.setOnTouchListener(scoreCalculator);
        scoreCalculator.displayScore();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected())
            googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isGamesApiAvailable())
            leaderboardManager.submitScore(scoreCalculator.getScore());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.connection_failed);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Player p = Games.Players.getCurrentPlayer(googleApiClient.getBaseApi());
        String displayName = p == null ? "???" : p.getDisplayName();
        messageManager.showFunnyConnected(displayName);
        leaderboardManager.checkPlayerAllTimeFirst(new LoadPlayerScoreCallback(false));
    }

    @Override
    public void onConnectionSuspended(int i) {
        messageManager.showDialog("Error", getString(R.string.connection_suspended));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            messageManager.showDialogWithExit(R.string.error, R.string.play_service_update_required);
            return;
        }
        try {
            connectionResult.startResolutionForResult(this, RC_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Error while resolving sign in", e);
        }
    }


    public class LoadPlayerScoreCallback implements ResultCallback<Leaderboards.LoadPlayerScoreResult> {

        private final boolean forceShowMsg;

        public LoadPlayerScoreCallback(boolean forceShowMsg) {
            this.forceShowMsg = forceShowMsg;
        }
        @Override
        public void onResult(Leaderboards.LoadPlayerScoreResult loadScoreResult) {
            if (loadScoreResult == null) {
                return;
            }

            if (!loadScoreResult.getStatus().isSuccess()) {
                messageManager.showToastLong(R.string.error_fetch_result);
                return;
            }

            if (loadScoreResult.getScore() != null) {
                LeaderboardScore score = loadScoreResult.getScore();
                long rank = score.getRank();
                messageManager.showFunnyDialogOnRankRandomly(rank, forceShowMsg);
            }
        }
    }
}
