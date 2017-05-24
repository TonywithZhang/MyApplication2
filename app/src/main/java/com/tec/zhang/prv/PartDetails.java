package com.tec.zhang.prv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tec.zhang.prv.databaseUtil.LineData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.suitlines.SuitLines;
import tech.linjiang.suitlines.Unit;

public class PartDetails extends AppCompatActivity {
    private static final String TAG = "PartDetails";
    private SuitLines suitLines;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_detail);
        suitLines = (SuitLines) findViewById(R.id.suitlines);
        suitLines.setLineForm(false);
        suitLines.setLineSize(3f);
        Intent intent = getIntent();
        String partNum = intent.getStringExtra("part_num");
        Log.d(TAG, "onCreate: " + partNum);
        LineData lineData = DataSupport.where("partNum=?",partNum).findFirst(LineData.class);
        List<Unit> list= new ArrayList<>();
        list.add(new Unit(Float.parseFloat(lineData.getX0()),"0"));
        list.add(new Unit(Float.parseFloat(lineData.getX25().substring(0,lineData.getX25().indexOf(".") + 3)),"25"));
        list.add(new Unit(Float.parseFloat(lineData.getX50().substring(0,lineData.getX50().indexOf(".") + 3)),"50"));
        list.add(new Unit(Float.parseFloat(lineData.getX75().substring(0,lineData.getX75().indexOf(".") + 3)),"75"));
        list.add(new Unit(Float.parseFloat(lineData.getX100().substring(0,lineData.getX100().indexOf(".") + 3)),"100"));
        list.add(new Unit(Float.parseFloat(lineData.getX125().substring(0,lineData.getX125().indexOf(".") + 3)),"125"));
        list.add(new Unit(Float.parseFloat(lineData.getX150().substring(0,lineData.getX150().indexOf(".") + 3)),"150"));
        list.add(new Unit(Float.parseFloat(lineData.getX175().substring(0,lineData.getX175().indexOf(".") + 3)),"175"));
        list.add(new Unit(Float.parseFloat(lineData.getX200().substring(0,lineData.getX200().indexOf(".") + 3)),"200"));
        suitLines.feedWithAnim(list);
    }
}
