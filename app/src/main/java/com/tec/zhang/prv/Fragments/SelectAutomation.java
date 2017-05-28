package com.tec.zhang.prv.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tec.zhang.prv.PartDetails;
import com.tec.zhang.prv.R;
import com.tec.zhang.prv.databaseUtil.LineData;
import com.tec.zhang.prv.databaseUtil.LogInAutoCompute;
import com.tec.zhang.prv.databaseUtil.PartDetail;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/4/28.
 */

public class SelectAutomation extends Fragment {
    private AutoCompleteTextView airflow,leakage;
    private Activity activity;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.auto_select,container,false);
        initLayoutComponents();
        return view;
    }
    private void initLayoutComponents(){
        activity  = getActivity();
        airflow = (AutoCompleteTextView) view.findViewById(R.id.auto_airflow);
        leakage = (AutoCompleteTextView) view.findViewById(R.id.auto_uncontrol_leakage);
        FloatingActionButton confirm = (FloatingActionButton) view.findViewById(R.id.auto_confirm);

        List<LogInAutoCompute> autoComputes = DataSupport.findAll(LogInAutoCompute.class);
        if (autoComputes != null && autoComputes.size() != 0){
            int length = autoComputes.size();
            String[] airflows = new String[length];
            String[] leakages = new String[length];
            for (int i = 0; i < length ; i ++){
                airflows[i] = autoComputes.get(i).getAirFlow();
                leakages[i]  = autoComputes.get(i).getUncontrolLeakage();
            }
            airflow.setAdapter(new ArrayAdapter<String>(activity,R.layout.support_simple_spinner_dropdown_item,airflows));
            leakage.setAdapter(new ArrayAdapter<String>(activity,R.layout.support_simple_spinner_dropdown_item,leakages));
        }
        confirm.setOnClickListener(this::showComputedResults);
    }
    private void showComputedResults(View view){
        hideSoftKeyBoard(activity);
        Log.d(TAG, "showComputedResults: 按键触发一次" );
        if (airflow.getText().toString().length() == 0 || leakage.getText().toString().length() == 0){
            showWarn();
            return;
        }

        Log.d(TAG, "showComputedResults: " + airflow.getText().toString().length());
        int inputAirflow = Integer.parseInt(airflow.getText().toString());
        int inputLeakage = Integer.parseInt(leakage.getText().toString());
        LogInAutoCompute  autoCompute = new LogInAutoCompute();
        autoCompute.setAirFlow(inputAirflow + "");
        autoCompute.setUncontrolLeakage(inputLeakage + "");
        autoCompute.save();
        float  computeResult = (float) (Math.sqrt(25)/Math.sqrt(17)*inputAirflow - inputLeakage);
        Log.d(TAG, "showComputedResults: " + computeResult);
        LinkedHashMap<String,Float> resultSet = new LinkedHashMap<>();
        List<LineData> lineDatas = DataSupport.select("partNum","x75").find(LineData.class);
        List<Float> performancesOn75Pa = new ArrayList<>();
        for (int i = 0 ;i < lineDatas.size() ; i ++){
            float resultFloat = Math.abs(Float.valueOf(lineDatas.get(i).getX75().substring(0,6)) - computeResult);
            Log.d(TAG, "");
            resultSet.put(lineDatas.get(i).getPartNum(),resultFloat);
            performancesOn75Pa.add(resultFloat);
        }
        Log.d(TAG, "showComputedResults: 计算结果的集合的长度" + performancesOn75Pa.size()  + "字典的容量为：" + resultSet.size());
        Collections.sort(performancesOn75Pa);
        List<String> finalList = new ArrayList<>();
        Set<String> set = resultSet.keySet();
        int x = 0;
        for (String string: set){
            Log.d(TAG, "showComputedResults: 字典里的元素：" + resultSet.get(string) + "列表里的元素：" + performancesOn75Pa.get(x));
            if (resultSet.get(string).equals(performancesOn75Pa.get(0))){
                finalList.add(string);
            }
            x ++;
        }
        if (finalList.size() < 3){
            for (String string: set){
                if (resultSet.get(string).equals(performancesOn75Pa.get(1))){
                    finalList.add(string);
                }
            }
        }
        if (finalList.size() < 3){
            for (String string : set){
                if (resultSet.get(string).equals(performancesOn75Pa.get(2))){
                    finalList.add(string);
                }
            }
        }
        Log.d(TAG, "showComputedResults: 最终结果结合的长度" + finalList.size());
        List<PartDetail> finalDetails = DataSupport.select("hvacNo","partNumber","projectNumber")
                .limit(3)
                .where("partNumber like ? or partNumber like ? or partNumber like ?","%"+finalList.get(0) + "%","%"+finalList.get(1) + "%","%" + finalList.get(2) + "%")
                .find(PartDetail.class);
        Log.d(TAG, "finalList第一个元素为：" + finalList.get(0) + "第二个元素为：" + finalList.get(1) + "第三个元素为：" + finalList.get(2));
        final PopupWindow window = new PopupWindow(activity);
        final View resultWindow = LayoutInflater.from(activity).inflate(R.layout.result_window,null);
        window.setContentView(resultWindow);
        final TextView resultOne = (TextView) resultWindow.findViewById(R.id.result_one);
        final TextView resultTwo = (TextView) resultWindow.findViewById(R.id.result_two);
        final TextView resultThree = (TextView) resultWindow.findViewById(R.id.result_three);
        final View layout = resultWindow.findViewById(R.id.result_outside);

        resultOne.setText(String.format("%s%s%s", trimFit(finalDetails.get(0).getPartNumber())," for ",finalDetails.get(0).getProjectNumber()));
        resultTwo.setText(String.format("%s%s%s",trimFit(finalDetails.get(1).getPartNumber())," for ",finalDetails.get(1).getProjectNumber()));
        resultThree.setText(String.format("%s%s%s",trimFit(finalDetails.get(2).getPartNumber())," for ",finalDetails.get(2).getProjectNumber()));
        Log.d(TAG, "showComputedResults: 三个Textview的显示字符串为：" + resultOne.getText() + "," + resultTwo.getText() + "," + resultThree.getText());
        resultOne.setOnClickListener(this::showDetailPart);
        resultTwo.setOnClickListener(this::showDetailPart);
        resultThree.setOnClickListener(this::showDetailPart);
        layout.setOnClickListener(v-> window.dismiss());

        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0808080")));
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        int maxWidth = Math.max(resultOne.getWidth(),resultTwo.getWidth());
        maxWidth = Math.max(maxWidth,resultThree.getWidth());
        window.setWidth(activity.getWindow().getDecorView().getWidth());
        window.setHeight(activity.getWindow().getDecorView().getHeight());
        window.showAsDropDown(resultWindow);
        window.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,0,0);
    }
    private void showDetailPart(View detailView){
        String item = ((TextView)detailView).getText().toString();
        String[] items = item.split(" ");
        PartDetail detailPart = DataSupport.select("hvacNo").where("partNumber like ? and projectNumber=?","%" + items[0] + "%",items[2]).findFirst(PartDetail.class);
        Intent intent = new Intent(activity, PartDetails.class);
        intent.putExtra("part_num",detailPart.getHvacNo());
        activity.startActivity(intent);
    }
    private void showWarn(){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setIcon(R.drawable.ic_warning_white_48dp)
                .setTitle("输入不完整！")
                .setMessage("请正确输入两个参数！")
                .setPositiveButton("好的", null);
        alert.show();
    }
    private String trimFit(String originNumber){
        if (originNumber.length() <= 8){
            return originNumber;
        }
        String trimedNumber = originNumber.substring(0,8);
        if (!trimedNumber.matches("\\d*")){
            trimedNumber = trimedNumber.substring(0,7);
        }
        return trimedNumber;
    }

    public static void hideSoftKeyBoard(Activity ac){
        InputMethodManager imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }
}
