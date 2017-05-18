package com.tec.zhang.prv.Fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
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

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

/**
 * 使用零件号作为检索的条件
 * 最下面的控件为RecyclerView
 * 集成讯飞语音输入
 */

public class SearchWithPartNumber extends Fragment {
    private TextView textView;
    private MultiAutoCompleteTextView multiAutoCompleteTextView;
    private Button recgnize;
    private String[] columns;
    private RecyclerView recyclerView;
    private List<Item> items;
    private ItemAdapter itemAdapter;
    private Random random = new Random(System.currentTimeMillis());
    private ExecutorService service;

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
        recgnize = (Button) view.findViewById(R.id.recgnize_sound);
        recgnize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recgnizeThat();
            }
        });

        List<PartDetail> details = DataSupport.select("partNumber").find(PartDetail.class);
        List<String> numbers = new ArrayList<>();
        for (PartDetail detail : details){
            numbers.add(detail.getPartNumber());
        }
        columns = new String[details.size()];
        numbers.toArray(columns);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,columns);
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) view.findViewById(R.id.find_by_number);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                items = new ArrayList<>();
                itemAdapter = new ItemAdapter(getContext(), items, new ItemAdapter.OnItemsClickListener() {
                    @Override
                    public void onNameClick() {

                    }

                    @Override
                    public void onPictureClick() {

                    }

                    @Override
                    public void onVersionClick() {

                    }

                    @Override
                    public void onDateClick() {

                    }
                });
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                initData();
            }
        });

        return view;
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
        List<PartDetail> details = DataSupport.select("partNumber","supplier","engineeringCost","projectNumber").find(PartDetail.class);
        for (PartDetail detail : details){
            if (detail.getPartNumber().length() > 8){
                if (detail.getPartNumber().contains("per")) {
                    detail.setPartNumber(detail.getPartNumber().substring(detail.getPartNumber().length() - 9,detail.getPartNumber().length() - 1));
                } else {
                    detail.setPartNumber(detail.getPartNumber().substring(detail.getPartNumber().length() - 8,detail.getPartNumber().length() - 1));
                }
            }
            Item item  = new Item(detail.getPartNumber() + " for " + detail.getProjectNumber(),"供应商：\n" + detail.getSupplier(),"工程成本：\n" + detail.getEngineeringCost(),selectPic(detail.getPartNumber()));
            items.add(item);
        }
        itemAdapter.notifyDataSetChanged();
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
        for (String picName : pictures.keySet()){
            if (picName.contains(name)){
                return pictures.get(picName);
            }else{
                return cars[random.nextInt(5)];
            }
        }
        return R.mipmap.ic_launcher_round;
    }
}
