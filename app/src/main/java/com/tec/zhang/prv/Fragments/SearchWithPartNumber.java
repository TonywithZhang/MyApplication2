package com.tec.zhang.prv.Fragments;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jiang.android.pbutton.CProgressButton;
import com.tec.zhang.prv.JsonParser;
import com.tec.zhang.prv.PartDetails;
import com.tec.zhang.prv.R;
import com.tec.zhang.prv.databaseUtil.PartDetail;
import com.tec.zhang.prv.recyler.Item;
import com.tec.zhang.prv.recyler.ItemAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

/**
 * 使用零件号作为检索的条件
 * 最下面的控件为RecyclerView
 * 集成讯飞语音输入
 */

public class SearchWithPartNumber extends Fragment {
    private  Activity activity;
    private TextView textView;
    private MultiAutoCompleteTextView multiAutoCompleteTextView;
    private RecyclerView recyclerView;
    private List<Item> items;
    private ItemAdapter itemAdapter;
    private Random random = new Random(System.currentTimeMillis());
    private LinkedHashMap<String,Integer> rowPics;

    private int[] cars= new int[]{
            R.drawable.ic_baoma,
            R.drawable.ic_asmd,
            R.drawable.ic_benzi,
            R.drawable.ic_chv,
            R.drawable.ic_eb,
            R.drawable.ic_buzhiming
    };
    public static LinkedHashMap<String,Integer> pictures;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_with_part_number,container,false);
        textView = (TextView) view.findViewById(R.id.textView2);
        Button recgnize = (Button) view.findViewById(R.id.recgnize_sound);
        activity = getActivity();
        FloatingActionButton confirm = (FloatingActionButton) view.findViewById(R.id.check_with_number);
        confirm.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: 原型按键被执行一次");
            String inputed = multiAutoCompleteTextView.getText().toString();
            for(int i = 0 ; i < items.size(); i ++){
                if (items.get(i).getPartNumber().contains(inputed)){
                    Intent intent = new Intent(activity,PartDetails.class);
                    intent.putExtra("part_num",items.get(i).getId());
                    activity.startActivity(intent);
                }
            }
            /*items.forEach(action ->{
                if (action.getPartNumber().contains(inputed)){
                    Intent intent = new Intent(activity,PartDetails.class);
                    intent.putExtra("part_num",action.getId());
                    activity.startActivity(intent);
                }
            });*/
        });
        recgnize.setOnClickListener(v -> recgnizeThat());

        List<PartDetail> details = DataSupport.select("partNumber").find(PartDetail.class);
        List<String> numbers = new ArrayList<>();
        for (PartDetail detail : details){
            numbers.add(SelectAutomation.trimFit(detail.getPartNumber()));
        }
        String[] columns = new String[details.size()];
        numbers.toArray(columns);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item, columns);
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) view.findViewById(R.id.find_by_number);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiAutoCompleteTextView.setOnKeyListener((v, keyCode, event) ->{
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                String inputed = multiAutoCompleteTextView.getText().toString();
                items.forEach(action ->{
                    if (action.getPartNumber().contains(inputed)){
                        SelectAutomation.hideSoftKeyBoard(activity);
                        Intent intent = new Intent(activity,PartDetails.class);
                        intent.putExtra("part_num",action.getId());
                        activity.startActivity(intent);
                    }
                });
                SelectAutomation.hideSoftKeyBoard(activity);
                Intent intent = new Intent(activity,PartDetails.class);

            }
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
            linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(() ->{
                Log.d(TAG, "onCreateView: global监听器执行了一次");
                Rect  rect = new Rect();
                linearLayout.getWindowVisibleDisplayFrame(rect);
                int height = linearLayout.getRootView().getHeight() - rect.bottom;
                if (height > 80){
                    ViewCompat.animate(confirm)
                            .setListener(null)
                            .withLayer()
                            .translationY(-height)
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .setDuration(300)
                            .start();
                }else {
                    ViewCompat.animate(confirm)
                            .translationY(0)
                            .setListener(null)
                            .withLayer()
                            .setDuration(300)
                            .setInterpolator(new AccelerateInterpolator())
                            .start();
                }
            });
            return false;
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            items = new ArrayList<>();
            itemAdapter = new ItemAdapter(getContext(), items, new ItemAdapter.OnItemsClickListener() {
                @Override
                public void onNameClick(String name) {
                    Log.d(TAG, "onNameClick: " + name);
                    showDetail(name);
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onPictureClick(final String name,final String carnum) {
                    final PopupWindow popupWindow = new PopupWindow(getContext());
                    final View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_window,null);
                    final ImageView carImage = (ImageView) view1.findViewById(R.id.car_picture);
                    carImage.setImageResource(selectRowPic(name));
                    view1.setOnTouchListener((v, event) -> {
                        changeOrExit(carnum,popupWindow, view1,carImage,event);
                        return false;
                    });
                    popupWindow.setContentView(view1);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0808080));
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setWidth(activity.getWindow().getDecorView().getWidth());
                    popupWindow.setHeight(activity.getWindow().getDecorView().getHeight());
                    popupWindow.showAsDropDown(view1);
                    Log.d(TAG, "onPictureClick: 弹出窗口任务执行了一次");
                    popupWindow.showAtLocation(view1, Gravity.CENTER,0,0);
                }

                @Override
                public void onVersionClick(String version) {
                    showDetail(version);
                }

                @Override
                public void onDateClick(String date) {
                    showDetail(date);
                }

                @Override
                public void onItemClick(String partNum) {
                    showDetail(partNum);
                }
            });
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        });
        new RunBackground().execute();
        return view;
    }
    private void initData(){
        rowPics = new LinkedHashMap<>();
        rowPics.put("p5492105",R.drawable.p5492105);
        rowPics.put("p9076499",R.drawable.p9076499);
        rowPics.put("p13502347",R.drawable.p13502347);
        rowPics.put("p13502348",R.drawable.p13502348);
        rowPics.put("p13588034",R.drawable.p13588034);
        rowPics.put("p13502349",R.drawable.p13502349);
        rowPics.put("p13597326",R.drawable.p13597326);
        rowPics.put("p22788177",R.drawable.p22788177);
        rowPics.put("p26204448",R.drawable.p26204448);
        rowPics.put("p26265005",R.drawable.p26265005);
        rowPics.put("p26689931",R.drawable.p26689931);
        rowPics.put("p90921822",R.drawable.p90921822);

        pictures = new LinkedHashMap<>();
        pictures.put("prv5492105",R.drawable.ic_prv5492105);
        pictures.put("prv9076499",R.drawable.ic_prv9076499);
        pictures.put("prv13502347",R.drawable.ic_prv13502347);
        pictures.put("prv13502348",R.drawable.ic_prv13502348);
        pictures.put("prv13502349",R.drawable.ic_prv13502349);
        pictures.put("prv13588034",R.drawable.ic_prv13588034);
        pictures.put("prv13597326",R.drawable.ic_prv13597326);
        pictures.put("prv22788177",R.drawable.ic_prv22788177);
        pictures.put("prv26204448",R.drawable.ic_prv26204448);
        pictures.put("prv26265005",R.drawable.ic_prv26265005);
        pictures.put("prv90921822",R.drawable.ic_prv90921822);
        pictures.put("prv26689931",R.drawable.ic_prv26689931);

        pictures.put("prv26689931s",R.drawable.ic_prv26689931s);
        pictures.put("prv5492105s",R.drawable.ic_prv5492105s);
        pictures.put("prv9076499s",R.drawable.ic_prv9076499s);
        pictures.put("prv13502347s",R.drawable.ic_prv13502347s);
        pictures.put("prv13502348s",R.drawable.ic_prv13502348s);
        pictures.put("prv13502349s",R.drawable.ic_prv13502349s);
        pictures.put("prv13588034s",R.drawable.ic_prv13588034s);
        pictures.put("prv13597326s",R.drawable.ic_prv13597326s);
        pictures.put("prv22788177s",R.drawable.ic_prv22788177s);
        pictures.put("prv26204448s",R.drawable.ic_prv26204448s);
        pictures.put("prv26265005s",R.drawable.ic_prv26265005s);
        pictures.put("prv90921822s",R.drawable.ic_prv90921822s);
        List<PartDetail> details = DataSupport.select("hvacNo","partNumber","supplier","engineeringCost","projectNumber").find(PartDetail.class);
        for (PartDetail detail : details){
            if (detail.getPartNumber().length() > 8){
                detail.setPartNumber(detail.getPartNumber().substring(0,8));
            }
            Item item  = new Item(detail.getHvacNo(),detail.getPartNumber() + " for " + detail.getProjectNumber(),"供应商：\n" + detail.getSupplier(),"工程成本：\n" + detail.getEngineeringCost(),selectPic(detail.getPartNumber()));
            items.add(item);
        }
    }

    private void recgnizeThat(){
        RecognizerDialog mDialog = new RecognizerDialog(activity, initListener);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT,"mandarin");
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }

            @Override
            public void onError(SpeechError speechError) {
                Toasty.error(getContext(),"发生未知错误，请联系开发者", Toast.LENGTH_SHORT).show();
            }
        });
        mDialog.show();
    }
    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };
    private LinkedHashMap<String,Object> mIatResults = new LinkedHashMap<>();
    private void printResult(RecognizerResult results) {
        //输出记录，保存在sd卡中
        File record = new File(Environment.getExternalStorageDirectory().toString() + "/PRV/" + System.currentTimeMillis() + ".txt");
        try {
            RandomAccessFile randomAccessFile  = new RandomAccessFile(record,"rw");
            randomAccessFile.write(results.getResultString().getBytes());
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        multiAutoCompleteTextView.setText(resultBuffer.toString().substring(0,resultBuffer.toString().length()-1));
        multiAutoCompleteTextView.setSelection(multiAutoCompleteTextView.length());

    }
    private int selectPic(String name){
        for (String picName :rowPics.keySet()){
            if (picName.contains(name)) return rowPics.get(picName);
        }
        /*for (String picName : pictures.keySet()){
            if (picName.contains(name)){
                return pictures.get(picName);
            }
        }*/
        Log.d(TAG, "selectPic: " + name);
        return cars[random.nextInt(5)];
    }
    private int selectRowPic(String name){
        if (name.length() > 8){
            name = name.substring(0,name.indexOf(" "));
        }
        for (String picName : pictures.keySet()){
            if (picName.contains(name)){
                return pictures.get(picName);
            }
        }
        return cars[random.nextInt(5)];
    }
    private class RunBackground extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            initData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemAdapter.notifyDataSetChanged();
        }
    }
    private int count = 0;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeOrExit(String name,final PopupWindow popupWindow,final View view,final ImageView carImage,final MotionEvent event){
        switch (count){
            case 0:
                Log.d(TAG, "changeOrExit: " + "prv" + name + "s");
                carImage.setImageResource(pictures.get("prv" + name.split(" ")[0] + "s"));
                count ++;
                break;
            case 1:
                ViewAnimationUtils.createCircularReveal(view,carImage.getWidth()/2,carImage.getHeight()/2,Math.max(carImage.getHeight(),carImage.getWidth()),0).setDuration(1000).start();
                new CountDownTimer(1000,100){
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        popupWindow.dismiss();
                    }
                }.start();
                count  --;
                break;
        }
        int height = carImage.getTop();
        int low = carImage.getBottom();
        if (event.getY() < height || event.getY() > low){
            ViewAnimationUtils.createCircularReveal(view,carImage.getWidth()/2,carImage.getHeight()/2,Math.max(carImage.getHeight(),carImage.getWidth()),0).setDuration(1000).start();
            new CountDownTimer(1000,100){
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    popupWindow.dismiss();
                }
            }.start();
        }
    }

    private void showDetail(String s){
        Intent intent = new Intent(activity, PartDetails.class);
        intent.putExtra("part_num",s);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,itemAdapter.getImageView(),getString(R.string.image));
        ActivityCompat.startActivity(activity,intent,compat.toBundle());
    }
}
