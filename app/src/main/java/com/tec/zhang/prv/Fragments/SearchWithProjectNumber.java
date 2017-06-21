package com.tec.zhang.prv.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.tec.zhang.prv.Abandon;
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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;
import static com.tec.zhang.prv.R.id.check_now;

/**
 * Created by zhang on 2017/4/27.
 */

public class SearchWithProjectNumber extends Fragment {
    private Activity activity;
    private View view;
    private TextView textView;
    private AutoCompleteTextView multiAutoCompleteTextView;
    private FloatingActionButton confirm;
    private RecyclerView recyclerView;
    private List<Item> items;
    private ItemAdapter itemAdapter;
    private Random random = new Random(System.currentTimeMillis());
    private LinkedHashMap<String,Integer> rowPics;

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
        view = inflater.inflate(R.layout.search_with_project_number,container,false);
        textView = (TextView) view.findViewById(R.id.textView3);
        activity = getActivity();
        init();
        setList();
        new RunBackground().execute();

        return view;
    }

    private void confirmCheck() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(true)
                .setIcon(R.drawable.ic_warning_outline_white)
                .setMessage("您要查找的项目号为" + multiAutoCompleteTextView.getText().toString())
                .setPositiveButton("确定", null)
                .show();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String projectNumber = multiAutoCompleteTextView.getText().toString();
            int length = projectNumber.length();
            if (length != 8){
                textView.setText("位数不对,目前已输入" + length + "位");
            }
        }
    };
    private void init() {
        items = new ArrayList<>();
        confirm = (FloatingActionButton) view.findViewById(R.id.confirm);
        textView = (TextView) view.findViewById(R.id.textView3);
        multiAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.auto_complete);

        View root = view.findViewById(R.id.root_view2);
        root.getViewTreeObserver()
                .addOnGlobalLayoutListener(() ->{
                    Rect rect = new Rect();
                    root.getWindowVisibleDisplayFrame(rect);
                    int height = view.getRootView().getHeight() - rect.bottom;
                    if (height  > 80){
                        ViewCompat.animate(confirm)
                                .translationY(-height)
                                .setDuration(300)
                                .setListener(null)
                                .setInterpolator(new FastOutSlowInInterpolator())
                                .withLayer()
                                .start();
                    }else {
                        ViewCompat.animate(confirm)
                                .translationY(0)
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .setDuration(300)
                                .withLayer()
                                .setListener(null)
                                .start();
                    }
                });
        List<PartDetail> details = DataSupport.select("projectNumber").find(PartDetail.class);
        List<String> numbers = new ArrayList<>();
        for (PartDetail detail : details){
            numbers.add(detail.getProjectNumber());
        }
        String[] columns = new String[details.size()];
        numbers.toArray(columns);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item, columns);
        multiAutoCompleteTextView.setAdapter(adapter);
        //multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        Button searchWithSound = (Button) view.findViewById(R.id.project_listen);
        searchWithSound.setOnClickListener(v -> recognizeThat());
        recyclerView = (RecyclerView) view.findViewById(R.id.project_recycle);
    }
    private void setList(){
        itemAdapter = new ItemAdapter(getContext(),items,new ItemAdapter.OnItemsClickListener() {
            @Override
            public void onNameClick(String name) {
                showDetail(name);
            }

            @Override
            public void onPictureClick(final String name,final String carnum) {
                final PopupWindow popupWindow = new PopupWindow(getContext());
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_window,null);
                final ImageView carImage = (ImageView) view.findViewById(R.id.car_picture);
                carImage.setImageResource(selectRowPic(name));
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
                popupWindow.setWidth(activity.getWindow().getDecorView().getWidth());
                popupWindow.setHeight(activity.getWindow().getDecorView().getHeight());
                popupWindow.showAsDropDown(view);
                Log.d(TAG, "onPictureClick: 弹出窗口任务执行了一次");
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
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
                /*Intent intent = new Intent(getActivity(), PartDetail.class);
                intent.putExtra("partNum",partNum);
                Log.d(TAG, "onBindViewHolder: 创建表格任务执行一次");
                getActivity().startActivity(intent);*/
            }
        });
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
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
        LinkedHashMap<String,String> links = new LinkedHashMap<>();
        List<PartDetail> details = DataSupport.select("hvacNo","partNumber","supplier","engineeringCost","projectNumber").find(PartDetail.class);
        for (PartDetail detail : details){
            links.put(detail.getHvacNo(),detail.getProjectNumber());
            if (detail.getPartNumber().length() > 8){
                detail.setPartNumber(detail.getPartNumber().substring(0,8));
            }
            Item item  = new Item(detail.getHvacNo(),detail.getPartNumber() + " for " + detail.getProjectNumber(),"供应商：\n" + detail.getSupplier(),"工程成本：\n" + detail.getEngineeringCost(),selectPic(detail.getPartNumber()));
            items.add(item);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           String inputed = multiAutoCompleteTextView.getText().toString();
                                           for (String string : links.keySet()){
                                               if (links.get(string).contains(inputed)){
                                                   Intent intent = new Intent(activity, PartDetails.class);
                                                   intent.putExtra("part_num", string);
                                                   activity.startActivity(intent);
                                               }
                                           }
                                       }
                                   }
                /*v ->{
            String inputed = multiAutoCompleteTextView.getText().toString();
            links.keySet().stream().filter(name -> links.get(name).contains(inputed)).forEach(name -> {
                Intent intent = new Intent(activity, PartDetails.class);
                intent.putExtra("part_num", name);
                activity.startActivity(intent);
            });
        }*/);
        multiAutoCompleteTextView.setOnKeyListener((v,code,event) -> {
            if (code == KeyEvent.KEYCODE_ENTER){
                String inputed = multiAutoCompleteTextView.getText().toString();
                links.keySet().stream().filter(name -> links.get(name).contains(inputed)).forEach(name -> {
                    Intent intent = new Intent(activity, PartDetails.class);
                    intent.putExtra("part_num", name);
                    activity.startActivity(intent);
                });
            }
            return  false;
        });
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
    private void showDetail(String s){
        Intent intent = new Intent(activity, PartDetails.class);
        intent.putExtra("part_num",s);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,itemAdapter.getImageView(),getString(R.string.image));
        ActivityCompat.startActivity(activity,intent,compat.toBundle());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeOrExit(String name, final PopupWindow popupWindow, final View view, final ImageView carImage, final MotionEvent event){
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

    private void recognizeThat(){
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
}
