package com.tec.zhang.prv.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.tec.zhang.prv.JsonParser;
import com.tec.zhang.prv.R;
import com.tec.zhang.prv.databaseUtil.PartDetail;
import com.tec.zhang.prv.recyler.Item;
import com.tec.zhang.prv.recyler.ItemAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
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

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;
import static com.tec.zhang.prv.R.id.check_now;

/**
 * Created by zhang on 2017/4/27.
 */

public class SearchWithPerformance extends Fragment {
    private View view;
    private TextView textView;
    private MultiAutoCompleteTextView editText;
    private Button searchWithSound;
    private RecyclerView recyclerView;
    private FloatingActionButton confirm;
    private String[] columns;
    private List<Item> items;
    private ItemAdapter itemAdapter;
    private Random random = new Random(System.currentTimeMillis());
    private ExecutorService service;

    public static LinkedHashMap<String,Integer> pictures;

    private int[] cars= new int[]{
            R.drawable.ic_baoma,
            R.drawable.ic_asmd,
            R.drawable.ic_benzi,
            R.drawable.ic_chv,
            R.drawable.ic_eb,
            R.drawable.ic_buzhiming
    };

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };

    private LinkedHashMap<String,Object> mIatResults = new LinkedHashMap<>();
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_with_performance,container,false);

        searchWithSound  = (Button) view.findViewById(R.id.performance_listen);
        searchWithSound.setOnClickListener(v -> recgnizeThat());
        init();
        setList();
        service = Executors.newSingleThreadExecutor();
        service.execute(() -> new RunBackground().execute());
        return view;
    }

    private void init() {
        items = new ArrayList<>();
        textView = (TextView) view.findViewById(R.id.textView4);
        editText = (MultiAutoCompleteTextView) view.findViewById(R.id.check_by_performance);
        List<PartDetail> details = DataSupport.select("vehicleAirflow").find(PartDetail.class);
        List<String> numbers = new ArrayList<>();
        for (PartDetail detail : details){
            numbers.add(detail.getPartNumber());
        }
        columns = new String[details.size()];
        numbers.toArray(columns);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,columns);
        editText.setAdapter(adapter);
        searchWithSound = (Button) view.findViewById(R.id.performance_listen);
        recyclerView = (RecyclerView) view.findViewById(R.id.performance_list);
        confirm = (FloatingActionButton) view.findViewById(check_now);
    }
    private void setList(){
        itemAdapter = new ItemAdapter(getContext(),items,new ItemAdapter.OnItemsClickListener() {
            @Override
            public void onNameClick(String name) {

            }

            @Override
            public void onPictureClick(final int imageView,final String carnum) {
                final PopupWindow popupWindow = new PopupWindow(getContext());
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_window,null);
                final ImageView carImage = (ImageView) view.findViewById(R.id.car_picture);
                carImage.setImageResource(imageView);
                view.setOnTouchListener(new View.OnTouchListener(){
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        changeOrExit(carnum,popupWindow,view,carImage,event);
                        return false;
                    }
                });
                popupWindow.setContentView(view);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0808080));
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setWidth(getActivity().getWindow().getDecorView().getWidth());
                popupWindow.setHeight(getActivity().getWindow().getDecorView().getHeight());
                popupWindow.showAsDropDown(view);
                Log.d(TAG, "onPictureClick: 弹出窗口任务执行了一次");
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
            }

            @Override
            public void onVersionClick(String version) {

            }

            @Override
            public void onDateClick(String date) {

            }
        });
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
    private List<Item> createData(){
        List<Item> list = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        for (int i = 0 ; i< 20 ; i ++){
            Item item = new Item("" + System.currentTimeMillis(),random.nextInt(50)+ "","" + format.format(new Date(random.nextLong())),cars[random.nextInt(5)]);
            list.add(item);
        }
        return list;
    }

    private void recgnizeThat(){
        RecognizerDialog mDialog = new RecognizerDialog(getActivity(), initListener);
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

    private void printResult(RecognizerResult results) {
        //输出记录，保存在sd卡中
        File record = new File(Environment.getExternalStorageDirectory().toString() + "/PRV/" + System.currentTimeMillis() + ".txt");
        try {
            RandomAccessFile randomAccessFile  = new RandomAccessFile(record,"rw");
            randomAccessFile.write(results.getResultString().getBytes());
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
        editText.setText(resultBuffer.toString().substring(0,resultBuffer.toString().length()-1));
        editText.setSelection(editText.length());

    }

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

    private void initData(){
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
        List<PartDetail> details = DataSupport.select("partNumber","supplier","engineeringCost","projectNumber").find(PartDetail.class);
        for (PartDetail detail : details){
            if (detail.getPartNumber().length() > 8){
                detail.setPartNumber(detail.getPartNumber().substring(0,8));
            }
            Item item  = new Item(detail.getPartNumber() + " for " + detail.getProjectNumber(),"供应商：\n" + detail.getSupplier(),"工程成本：\n" + detail.getEngineeringCost(),selectPic(detail.getPartNumber()));
            items.add(item);
        }
    }

    private int selectPic(String name){
        for (String picName : pictures.keySet()){
            if (picName.contains(name)){
                return pictures.get(picName);
            }
        }
        return cars[random.nextInt(5)];
    }

    private class RunBackground extends AsyncTask<Void,Void,Void> {

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
}
