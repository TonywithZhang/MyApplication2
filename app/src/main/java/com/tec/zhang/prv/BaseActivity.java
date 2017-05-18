package com.tec.zhang.prv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Created by Administrator on 2017/4/25.
 */

public class BaseActivity extends AppCompatActivity {
    public final String FINISH_THIS = "con.tec.zhang.FINISH_THIS";
    private final String FINISH_ALL = "com.tec.zhang.FINISH_ALL";

    private BroadcastReceiver finishThis = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(finishThis,new IntentFilter(FINISH_THIS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishThis);
    }
    private long currentTime = 0L;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentTime < 2000){
            /*LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(new Intent(FINISH_THIS));*/
            sendBroadcast(new Intent(FINISH_THIS));
            finish();
        }else {
            currentTime = System.currentTimeMillis();
            Toasty.warning(this,"再次点击退出本程序!", Toast.LENGTH_SHORT).show();
        }
    }
}
