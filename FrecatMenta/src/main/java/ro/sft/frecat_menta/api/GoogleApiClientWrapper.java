package ro.sft.frecat_menta.api;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import ro.sft.frecat_menta.FrecatMenta;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;

/**
 * Created by 286868 on 3/17/2016.
 */
public class GoogleApiClientWrapper  {

    private GoogleApiClient googleApiClient;

    private static GoogleApiClientWrapper instance;

    public static GoogleApiClientWrapper getInstance(FrecatMenta context) {
        if (instance == null) {
            instance = new GoogleApiClientWrapper(context);
        }
        return instance;
    }

    private GoogleApiClientWrapper(FrecatMenta context) {
        googleApiClient = new Builder(context)
                .addConnectionCallbacks(context)
                .addOnConnectionFailedListener(context)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }




    public GoogleApiClient getBaseApi() {
        return googleApiClient;
    }

    public boolean isConnected() {
        return googleApiClient.isConnected();
    }

    public boolean isConnecting() {
        return googleApiClient.isConnecting();
    }

    public void disconnect() {
        googleApiClient.disconnect();
    }

    public void connect() {
        googleApiClient.connect();
    }

    public boolean hasConnectedApi(Api<?> api) {
        return googleApiClient.hasConnectedApi(api);
    }

    public boolean isGamesApiAvailable() {
        return googleApiClient.isConnected()
                && googleApiClient.hasConnectedApi(Games.API)
                && googleApiClient.hasConnectedApi(Plus.API);
    }
}
