package com.tec.zhang.prv;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.dalong.countdownview.CountDownView;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tec.zhang.prv.databaseUtil.LineData;
import com.tec.zhang.prv.databaseUtil.PartDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MAINACTIVITY";
    private Timer timer;
    private DonutProgress donutProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connector.getDatabase();
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        donutProgress = (DonutProgress) findViewById(R.id.donut);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean a = false;
                        if (a) {
                            ObjectAnimator anim = ObjectAnimator.ofInt(donutProgress, "progress", 0, 10);
                            anim.setInterpolator(new DecelerateInterpolator());
                            anim.setDuration(3500);
                            anim.start();
                        } else {
                            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.progress_animation);
                            set.setInterpolator(new DecelerateInterpolator());
                            set.setTarget(donutProgress);
                            set.setDuration(3500);
                            set.start();
                        }
                    }
                });
            }
        },0,3500);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,Search.class);
                startActivity(intent);
            }
        },3000);*/
        if (DataSupport.findAll(PartDetail.class).size() == 0){
            Log.d(TAG, "onCreate: 数据库为空");
            importData();
        }
        if (DataSupport.findAll(LineData.class).size() == 0){
            importChart();
        }else Log.d(TAG, "onCreate: 数据库不为空，且长度为" + DataSupport.findAll(PartDetail.class).size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CountDownTimer(3500,1000){
            int now = 3;
            @Override
            public void onTick(long millisUntilFinished) {
                donutProgress.setText("" + now);
                now--;
            }

            @Override
            public void onFinish() {
                donutProgress.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this,Search.class);
                startActivity(intent);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
    private void importData(){
        Log.d(TAG, "importData: 任务执行一次");
        JSONObject jo = null;
        try {
            InputStream is = getResources().getAssets().open("data.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder data = new StringBuilder();
            String item = null;
            while ((item = bufferedReader.readLine()) != null){
                data.append(item);
            }
            jo = new JSONObject(data.toString());
            JSONArray ja = jo.getJSONArray("data");
            for(int i = 0;i < ja.length() ; i ++){
                JSONObject  jsonObject = ja.getJSONObject(i);
                PartDetail detail = new PartDetail();
                detail.setHvacNo(jsonObject.optString("hvac_id"));
                detail.setPartNumber(jsonObject.optString("PN"));
                detail.setProjectNumber(jsonObject.optString("Program"));
                detail.setEngineeringCost(jsonObject.optString("Cost"));
                detail.setSupplier(jsonObject.optString("Supplier"));
                detail.setVehicleAirflow(jsonObject.optString("vehicle_air_flow_l/s"));
                detail.setTotalVehicleNumber(jsonObject.optString("Total_vehicle_number"));
                detail.setBodyICD(jsonObject.optString("Body_ICD"));
                detail.setUnit(jsonObject.optString("Units/Veh"));
                detail.setFlapMaterial(jsonObject.optString("Flap_Material"));
                detail.setFrameMaterial(jsonObject.optString("Frame_Material"));
                detail.setSealMaterial(jsonObject.optString("Seal_Type"));
                detail.save();
                Log.d(TAG, "importData: 导入成功一条");
            }
            bufferedReader.close();
            is.close();
            Toasty.info(this,"数据库创建成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void importChart(){
        try {
            InputStream is = getResources().getAssets().open("hvac_chart.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String content = null;
            while((content = br.readLine()) != null){
                sb.append(content);
            }
            JSONObject jo = new JSONObject(sb.toString());
            JSONArray  ja = jo.getJSONArray("data");
            for (int i = 0;i<ja.length() ; i ++){
                JSONObject item = ja.getJSONObject(i);
                LineData data = new LineData();
                data.setPartNum(item.getString("partNumber"));
                data.setX0(item.getString("0"));
                data.setX25(item.getString("25"));
                data.setX50(item.getString("50"));
                data.setX75(item.getString("75"));
                data.setX100(item.getString("100"));
                data.setX125(item.getString("125"));
                data.setX150(item.getString("150"));
                data.setX175(item.getString("175"));
                data.setX200(item.getString("200"));
                data.save();
            }
            br.close();
            is.close();
            Log.d(TAG, "importChart: " + DataSupport.findAll(LineData.class).size());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
