package com.example.interceptphonecall;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PopupWindowActivity extends Service {


   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
      //  getWindow(). addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
*//*        getWindow().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        getWindow().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*//*





    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);

        Bundle extras = intent.getExtras();
        String text = extras.getString("INFO_CLIENT");
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, text.length(), 0);

        Toast mToastToShow = Toast.makeText(this, biggerText, Toast.LENGTH_LONG);
        mToastToShow.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);

        mToastToShow.show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}