package ro.sft.frecat_menta.messages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ro.sft.frecat_menta.FrecatMenta;
import ro.sft.frecat_menta.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 286868 on 2/28/2016.
 */
public class AlertMessageManager {

    private Context frecatMenta;
    private boolean funnyConnectedShown;
    private int rankDialogShownCnt;


    public AlertMessageManager(Context frecatMenta) {
        this.frecatMenta = frecatMenta;
    }

    public void showFunnyWelcome() {
    }

    public void registerPeriodicFunnyMessages() {
        // interogate server and show periodic funny messages
    }

    public void registerAchievementFunnyMessages() {
        // interogate server and show acheivement funny messages
    }

    public void showToastLong(int contentId, Object... param) {
        Toast.makeText(frecatMenta, frecatMenta.getString(contentId, param), Toast.LENGTH_LONG).show();
    }

    public void showFunnyConnected(String displayName) {
        if (!funnyConnectedShown) {
            showToastLong(R.string.signed_in_toast, displayName);
            funnyConnectedShown = true;
        }
    }

    public void showFunnyDialogOnRankRandomly(long rank, boolean forceShowMsg) {

        if (!forceShowMsg) {
            //show this dialog once every 7 attempts
            // unless user specifically asked for it
            if (rankDialogShownCnt++ % 7 != 0) {
                return;
            }
        }
        switch ((int) rank) {
            case 1:
                showShareDialog(R.drawable.leaderboard_icon, R.string.funny_first_place_title, R.string.funny_first_place_msg,
                        R.string.funny_first_place_share_msg, 1);
                break;
            case 2:
                showShareDialog(R.drawable.leaderboard_icon, R.string.funny_second_place_title, R.string.funny_second_place_msg,
                        R.string.funny_second_place_share_msg, 2);
                break;
            case 3:
                showShareDialog(R.drawable.leaderboard_icon, R.string.funny_third_place_title, R.string.funny_third_place_msg,
                        R.string.funny_third_place_share_msg, 3);
                break;
            default:
                if (forceShowMsg){
                    showShareDialog(R.drawable.leaderboard_icon, R.string.funny_x_place_title, R.string.funny_x_place_msg,
                            R.string.funny_x_place_share_msg, (int) rank);
                } else {
                    showToastLong(R.string.funny_encourage_msg, rank);
                }
                break;
        }
    }

    private Intent createShareIntent(int textToSend, int rank) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String subject = frecatMenta.getString(R.string.funny_share_title);
        String message = frecatMenta.getString(textToSend);
        Uri imageUri = createOverlayedShareImage(frecatMenta, R.drawable.mint_x_place_text, "" + rank);
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("image/png");
        return sendIntent;
    }

    public Uri createOverlayedShareImage(Context gContext, int gResId, String gText) {
        Bitmap image = drawTextToBitmap(gContext, gResId, gText);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "share_img_" + gText + ".jpg");
        if (f.exists()) {
            f.delete();
        }

        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            Log.e(FrecatMenta.TAG, "Exception while writing transformed congrats file", e);
        }

        return Uri.fromFile(f);
    }

    public Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

        Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);


        // text size in pixels
        float textSize = (int) (14 * scale);
        int textLength = gText.length();

        if (textLength == 1) {
            textSize *= 2;
        } else if (textLength == 2) {
            textSize *= 1.4;
        } else if (textLength == 3) {
            textSize *= 1.2;
        }

        paint.setTextSize(textSize);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, textLength, bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        y -= 8;
        x += 20;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    // alert dialog builders
    public void showDialog(String title, String message) {
        new AlertDialog.Builder(frecatMenta)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public void showShareDialog(int resId, int title, int msg, final int textToSend, final int rank) {
        final Dialog dialog = new Dialog(frecatMenta);
        dialog.setContentView(R.layout.sharedialog);
        dialog.setTitle(title);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.share_text);
        text.setText(msg);
        final ImageView image = (ImageView) dialog.findViewById(R.id.share_img);
        image.setImageResource(resId);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button shareButton = (Button) dialog.findViewById(R.id.dialogButtonShare);
        // if button is clicked, close the custom dialog
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = createShareIntent(textToSend, rank);
                frecatMenta.startActivity(sendIntent);
                dialog.dismiss();
            }
        });

        try {
            dialog.show();
        } catch (Exception e) {
            Log.e(FrecatMenta.TAG, "cannot show rank dialog", e);
        }
    }

    public void showDialog(int title, int message) {
        new AlertDialog.Builder(frecatMenta)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public void showDialogWithExit(int title, int message) {
        new AlertDialog.Builder(frecatMenta)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(1);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
