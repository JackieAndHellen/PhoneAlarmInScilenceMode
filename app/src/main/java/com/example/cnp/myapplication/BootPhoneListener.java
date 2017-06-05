package com.example.cnp.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fanchangfa
 *    广播接受者-开机时启动电话监听器
 */
public class BootPhoneListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Intent phone_listener = new Intent(context , Phone_listener.class);
        context.startService(phone_listener);

        Intent i = new Intent("refresh_ui");
        i.putExtra("id",R.id.fab);
        context.sendBroadcast(i);
    }

}