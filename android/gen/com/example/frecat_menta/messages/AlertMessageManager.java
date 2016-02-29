package com.example.frecat_menta.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.frecat_menta.FrecatMenta;

/**
 * Created by 286868 on 2/28/2016.
 */
public class AlertMessageManager {
    SharedPreferences mPrefs;
    final String WELCOME_MSG_SHOWN = "welcomeScreenShown";
    private FrecatMenta frecatMenta;

    public AlertMessageManager(FrecatMenta frecatMenta) {
        this.frecatMenta = frecatMenta;
    }

    public void registerFunnyMessages() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(frecatMenta);

        // second argument is the default to use if the preference can't be found
        int welcomeMessageShown = mPrefs.getInt(WELCOME_MSG_SHOWN, 0);

        if (welcomeMessageShown == 0) {
            new AlertDialog.Builder(frecatMenta)
                    .setTitle("Bine ai venit!")
                    .setMessage("Stim ca ai avut o zi grea pana acum si vrem sa iti oferim un moment de destindere! " +
                            "Ne bucuram ca esti alaturi de noi!")
                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }

        increaseWelcomeMsgCounter(welcomeMessageShown);
    }

    public void showFunnyWelcomeBack() {
        int msgCnt = mPrefs.getInt(WELCOME_MSG_SHOWN, 0);
        Boolean showReWelcome = (msgCnt % 3) == 0;

        if (msgCnt > 0 && showReWelcome) {
            new AlertDialog.Builder(frecatMenta)
                    .setTitle("Bine ai revenit!")
                    .setMessage("Ne era teama ca te-ai lasat de frecat menta ... ")
                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        increaseWelcomeMsgCounter(msgCnt);
    }

    private void increaseWelcomeMsgCounter(int welcomeMessageShown) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(WELCOME_MSG_SHOWN, ++welcomeMessageShown);
        editor.commit(); // Very important to save the preference
    }
}
