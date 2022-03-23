package com.example.casodistudiomamange.connection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.casodistudiomamange.R;

public class NetworkChangedListener extends BroadcastReceiver {

    View layout_dialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {
            //internet is not connected


            Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null));
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(900,900);
            dialog.show();
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.setCancelable(false);
            AppCompatButton btnRetry = dialog.findViewById(R.id.buttonretry);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
