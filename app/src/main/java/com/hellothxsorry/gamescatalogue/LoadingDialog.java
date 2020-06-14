package com.hellothxsorry.gamescatalogue;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_alert, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
        Log.i("Test", "Loading in a progress...");
    }

    void dissmissLoadingDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        } else {
            Log.i("Test", "Successfully loaded");
        }
    }
}
