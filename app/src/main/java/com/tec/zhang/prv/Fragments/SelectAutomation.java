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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * 自动选型的页面
 */

public class SelectAutomation extends Fragment {
    //两个输入变量
    private AutoCompleteTextView airflow,leakage;
    //住活动变量
    private Activity activity;
    //视图2变量
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.auto_select,container,false);
        //从初始化布局
        initLayoutComponents();
        return view;
    }
    private void initLayoutComponents(){
        //得到当前的主活动
        activity  = getActivity();
        //实例化三个变脸
        airflow = (AutoCompleteTextView) view.findViewById(R.id.auto_airflow);
        leakage = (AutoCompleteTextView) view.findViewById(R.id.auto_uncontrol_leakage);
        FloatingActionButton confirm = (FloatingActionButton) view.findViewById(R.id.auto_confirm);
        //得到以前输入的历史纪录
        List<LogInAutoCompute> autoComputes = DataSupport.findAll(LogInAutoCompute.class);
        if (autoComputes != null && autoComputes.size() != 0){
            //拿到历史记录的长度
            int length = autoComputes.size();
            //第一个参数的历史纪录所组成成的数组
            LinkedHashSet<String> airflowSet  = new LinkedHashSet<>();
            String[] airflows = null;
            //第二个参数的历史纪录组成的数组
            LinkedHashSet<String> leakageSet  = new LinkedHashSet<>();
            String[] leakages = null;
            //遍历拿到的集合，将历史纪录 依次装填进创建的两个数组
            for (int i = 0; i < length ; i ++){
                LogInAutoCompute autoCompute = autoComputes.get(i);
                airflowSet.add(autoCompute.getAirFlow());
                leakageSet.add(autoCompute.getUncontrolLeakage());
                //airflows[i] = autoComputes.get(i).getAirFlow();
                //leakages[i]  = autoComputes.get(i).getUncontrolLeakage();
            }
            airflows = new  String[airflowSet.size()];
            leakages = new String[leakageSet.size()];
            airflowSet.toArray(airflows);
            leakageSet.toArray(leakages);
            //设置两个自动填充组件的适配器
            airflow.setAdapter(new ArrayAdapter<>(activity,R.layout.support_simple_spinner_dropdown_item,airflows));
            leakage.setAdapter(new ArrayAdapter<>(activity,R.layout.support_simple_spinner_dropdown_item,leakages));
        }
        //设置监听器
        confirm.setOnClickListener(this::showComputedResults);
    }
    /**
     * 显示计算结果的方法
     * 根据用户输入的两个参数
     * 然后根据公式计算结果
     * */
    private void showComputedResults(View view){
        //当用户按下按键的时候隐藏软键盘
        hideSoftKeyBoard(activity);
        Log.d(TAG, "showComputedResults: 按键触发一次" );
        //判断是否两个参数都有值，当任意一个值为空的时候直接显示警告并退出方法
        if (airflow.getText().toString().length() == 0 || leakage.getText().toString().length() == 0){
            showWarn();
            return;
        }

        Log.d(TAG, "showComputedResults: " + airflow.getText().toString().length());
        //拿到第一个输入的参数
        int inputAirflow = Integer.parseInt(airflow.getText().toString());
        //拿到第二个输入的参数
        int inputLeakage = Integer.parseInt(leakage.getText().toString());
        //将当前输入的参数存入数据库，方便下次直接点击
        LogInAutoCompute  autoCompute = new LogInAutoCompute();
        autoCompute.setAirFlow(inputAirflow + "");
        autoCompute.setUncontrolLeakage(inputLeakage + "");
        autoCompute.save();
        //根据输入的值计算结果
        float  computeResult = (float) (Math.sqrt(25)/Math.sqrt(17)*inputAirflow - inputLeakage);
        computeResult /=2;
        Log.d(TAG, "showComputedResults: " + computeResult);
        //创建一个map，key为所有的零件号，value为75pa下的整车风量
        LinkedHashMap<String,Float> resultSet = new LinkedHashMap<>();
        //sql操作，拿到折线图中75pa下的数据
        List<LineData> lineDatas = DataSupport.select("partNum","x75").find(LineData.class);
        //创建一个表，存储所有 75pa下的整车风量
        List<Float> performancesOn75Pa = new ArrayList<>();
        //遍历lineData，将数据存入map和list
        for (int i = 0 ;i < lineDatas.size() ; i ++){
            //相减得出相差值
            float resultFloat = Math.abs(Float.valueOf(lineDatas.get(i).getX75().substring(0,6)) - computeResult);
            if (resultFloat > 0){
                resultSet.put(lineDatas.get(i).getPartNum(),resultFloat);
                performancesOn75Pa.add(resultFloat);
            }
        }
        Log.d(TAG, "showComputedResults: 计算结果的集合的长度" + performancesOn75Pa.size()  + "字典的容量为：" + resultSet.size());
        //排序，是相差最小的值位于最前面
        Collections.sort(performancesOn75Pa);
        Log.d(TAG, "showComputedResults: " + performancesOn75Pa);
        //新建列表，承载最小差值的havc值，数据库中，只有havc是独特的
        List<String> finalList = new ArrayList<>();
        //拿到keyset，便于 遍历集合，以便于拿到hvac
        Set<String> set = resultSet.keySet();
        //finalList中的索引值
        final int[] index = {0};
        final String[] buffer = {null,null,null};
        //遍历map，取出与最小差值对应的nvac值
        if (performancesOn75Pa.size() != 0) {
            Log.d(TAG, "showComputedResults: " + set);
            /*set.stream().filter( string -> resultSet.get(string).equals(performancesOn75Pa.get(0)))
                    .forEach(string ->{
                        finalList.add(string);
                        Log.d(TAG, "showComputedResults: " + string);
                        //resultSet.remove(string);
                        buffer[index[0]] = string;
                        index[0]++;
                    });*/
            for (String string: set){
                if (resultSet.get(string).equals(performancesOn75Pa.get(0))){
                    finalList.add(string);
                    Log.d(TAG, "showComputedResults: " + string);
                    //resultSet.remove(string);
                    buffer[index[0]] = string;
                    index[0]++;
                }
            }
        }
        if (buffer[0] != null) {
            set.remove(buffer[0]);
        }
        if (buffer[1] != null){
            set.remove(buffer[1]);
        }
        if (buffer[2] != null){
            set.remove(buffer[2]);
        }
        index[0] = 0;
        buffer[0] = null;
        buffer[1] = null;
        buffer[2] = null;
        //如果小于三个，则再次遍历 map，取出第二小的差值对应的hvac
        if (performancesOn75Pa.size() > 1) {
            Log.d(TAG, "showComputedResults:" + set);
            if (finalList.size() < 3){
                for (String string : set){
                    if (resultSet.get(string).equals(performancesOn75Pa.get(1))){
                        finalList.add(string);
                        Log.d(TAG, "showComputedResults: " + string);
                        //resultSet.remove(string);
                        buffer[index[0]] = string;
                        index[0] ++;
                    }
                }
                /*set.stream().filter(string -> resultSet.get(string).equals(performancesOn75Pa.get(1))).forEach(string -> {
                    finalList.add(string);
                    Log.d(TAG, "showComputedResults: " + string);
                    //resultSet.remove(string);
                    buffer[index[0]] = string;
                    index[0] ++;
                });*/
            }
        }
        if (buffer[0] != null) {
            set.remove(buffer[0]);
        }
        if (buffer[1] != null){
            set.remove(buffer[1]);
        }
        if (buffer[2] != null){
            set.remove(buffer[2]);
        }
        index[0] = 0;
        buffer[0] = null;
        buffer[1] = null;
        buffer[2] = null;
        //如果仍小于三个，再次遍历 map  ，取出第三小的差值对应的hvac
        if (performancesOn75Pa.size() > 2) {
            Log.d(TAG, "showComputedResults: " + set);
            if (finalList.size() < 3){
                for (String string : set){
                    if (resultSet.get(string).equals(performancesOn75Pa.get(2))){
                        finalList.add(string);
                        Log.d(TAG, "showComputedResults: " + string);
                        //resultSet.remove(string);
                        buffer[0] = string;
                        Log.d(TAG, "showComputedResults: buffer:" + buffer[0] );
                    }
                }
                /*set.stream().filter(string -> resultSet.get(string).equals(performancesOn75Pa.get(2))).forEach(string -> {
                    finalList.add(string);
                    Log.d(TAG, "showComputedResults: " + string);
                    //resultSet.remove(string);
                    buffer[0] = string;
                    Log.d(TAG, "showComputedResults: buffer:" + buffer[0] );
                });*/
            }
        }
        set.remove(buffer[0]);
        /*List<PartDetail> finalDetails = DataSupport.select("hvacNo","partNumber","projectNumber")
                .limit(3)
                .where("partNumber like ? or partNumber like ? or partNumber like ?","%"+finalList.get(0) + "%","%"+finalList.get(1) + "%","%" + finalList.get(2) + "%")
                .find(PartDetail.class);
        Log.d(TAG, "finalList第一个元素为：" + finalList.get(0) + "第二个元素为：" + finalList.get(1) + "第三个元素为：" + finalList.get(2));*/
        final PopupWindow window = new PopupWindow(activity);
        final View resultWindow = LayoutInflater.from(activity).inflate(R.layout.result_window,null);
        window.setContentView(resultWindow);

        final TextView resultDisplay = (TextView) resultWindow.findViewById(R.id.result_display);
        final TextView resultOne = (TextView) resultWindow.findViewById(R.id.result_one);
        final TextView resultTwo = (TextView) resultWindow.findViewById(R.id.result_two);
        final TextView resultThree = (TextView) resultWindow.findViewById(R.id.result_three);
        final View layout = resultWindow.findViewById(R.id.result_outside);

        resultDisplay.append( String.format(Locale.CHINA,"%s%.2f","Control Leakage (@ 125 pa):",computeResult * 2));
        if (performancesOn75Pa.size() != 0) {
            Log.d(TAG, "showComputedResults: " + finalList.get(0));
            resultOne.setText(finalList.get(0));
            if (performancesOn75Pa.get(0)>= 20){
                resultOne.setTextColor(Color.RED);
            }
            resultOne.setOnClickListener(this::showDetailPart);
        }
        if (performancesOn75Pa.size() > 1) {
            Log.d(TAG, "showComputedResults: " + finalList.get(1));
            resultTwo.setText(finalList.get(1));
            if (performancesOn75Pa.get(1)> 20){
                resultTwo.setTextColor(Color.RED);
            }
            resultTwo.setOnClickListener(this::showDetailPart);
        }
        if (performancesOn75Pa.size() > 2) {
            Log.d(TAG, "showComputedResults: " + finalList.get(2));
            resultThree.setText(finalList.get(2));
            if (performancesOn75Pa.get(2)>= 20){
                resultThree.setTextColor(Color.RED);
            }
            resultThree.setOnClickListener(this::showDetailPart);
        }
        //resultOne.setText(String.format("%s%s%s", trimFit(finalDetails.get(0).getPartNumber())," for ",finalDetails.get(0).getProjectNumber()));
        //resultTwo.setText(String.format("%s%s%s",trimFit(finalDetails.get(1).getPartNumber())," for ",finalDetails.get(1).getProjectNumber()));
        //resultThree.setText(String.format("%s%s%s",trimFit(finalDetails.get(2).getPartNumber())," for ",finalDetails.get(2).getProjectNumber()));
        //Log.d(TAG, "showComputedResults: 三个TextView的显示字符串为：" + resultOne.getText() + "," + resultTwo.getText() + "," + resultThree.getText());
        layout.setOnClickListener(v-> window.dismiss());

        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0808080")));
        window.setOutsideTouchable(true);
        window.setFocusable(true);

        window.setWidth(activity.getWindow().getDecorView().getWidth());
        window.setHeight(activity.getWindow().getDecorView().getHeight());
        window.showAsDropDown(resultWindow);
        window.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,0,0);
    }

    private void showDetailPart(View detailView){
        String item = ((TextView)detailView).getText().toString();
        //String[] items = item.split(" ");
        List<PartDetail> detailPart = DataSupport.select("hvacNo").where("partNumber like ?","%" + item + "%").find(PartDetail.class);
        for (PartDetail detail : detailPart){
            Intent intent = new Intent(activity, PartDetails.class);
            intent.putExtra("part_num",detail.getHvacNo());
            activity.startActivity(intent);
        }
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

    public static String trimFit(String originNumber){
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
