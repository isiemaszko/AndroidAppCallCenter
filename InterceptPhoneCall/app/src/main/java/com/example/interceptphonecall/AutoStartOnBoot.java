package com.example.interceptphonecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Intent newIntent= new Intent( context,MainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }

    }
}
