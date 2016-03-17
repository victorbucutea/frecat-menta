package ro.sft.frecat_menta.leaderboard;

import android.content.Intent;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult;
import ro.sft.frecat_menta.FrecatMenta;
import ro.sft.frecat_menta.R;
import ro.sft.frecat_menta.api.GoogleApiClientWrapper;

import static com.google.android.gms.games.Games.Leaderboards;

/**
 * Created by 286868 on 3/6/2016.
 */
public class LeaderboardManager {
    public static final int RC_SHOW_LEADERBOARD = 5001;

    private GoogleApiClientWrapper apiClient;
    private FrecatMenta menta;
    private static int showAllTime = LeaderboardVariant.TIME_SPAN_ALL_TIME;
    private static int showToday = LeaderboardVariant.TIME_SPAN_DAILY;
    private static int showPublicLb = LeaderboardVariant.COLLECTION_PUBLIC;

    public LeaderboardManager(FrecatMenta menta) {
        this.apiClient = GoogleApiClientWrapper.getInstance(menta);
        this.menta = menta;
    }


    public void submitScore(final long score) {
        try {
            Leaderboards.submitScore(apiClient.getBaseApi(), menta.getString(R.string.top_frecatori_leaderboard_id), score);
        } catch (IllegalStateException e) {
            Log.e(FrecatMenta.TAG, "Error while submitting results to play services", e);
        }
    }

    public void showBoard() {
        try {
            Intent leaderboardIntent =
                    Leaderboards.getLeaderboardIntent(apiClient.getBaseApi(), menta.getString(R.string.top_frecatori_leaderboard_id), showAllTime, showPublicLb);
            menta.startActivityForResult(leaderboardIntent, RC_SHOW_LEADERBOARD);
        } catch (IllegalStateException e) {
            Log.e(FrecatMenta.TAG, "Error while getting leaderboard", e);
        }
    }


    public void checkPlayerAllTimeFirst(ResultCallback<LoadPlayerScoreResult> resultCalback) {
        PendingResult<LoadPlayerScoreResult> loadScoresResult =
                Leaderboards.loadCurrentPlayerLeaderboardScore(apiClient.getBaseApi(), menta.getString(R.string.top_frecatori_leaderboard_id), showAllTime, showPublicLb);
        loadScoresResult.setResultCallback(resultCalback);
    }

}
