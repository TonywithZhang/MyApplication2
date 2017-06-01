package com.tec.zhang.prv;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.tec.zhang.prv.Fragments.SelectAutomation;
import com.tec.zhang.prv.databaseUtil.LineData;
import com.tec.zhang.prv.databaseUtil.PartDetail;
import com.tec.zhang.prv.databaseUtil.PartDimension;
import com.tec.zhang.prv.databaseUtil.PartMass;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import tech.linjiang.suitlines.SuitLines;
import tech.linjiang.suitlines.Unit;

public class PartDetails extends AppCompatActivity {
    private static final String TAG = "PartDetails";
    private SuitLines suitLines;
    private ImageView detailPic;
    private FloatingActionButton floatingActionButton;
    private LinkedHashMap<String,Integer> pictures;
    private TextView detailDimension,detailProject,detailCost,detailMass,detailSupplier,detailAirFlow,detailVehicleNum,detailBodyICD,detailUnit,detailFrameMaterial,detailFlipMaterial,detailSealMaterial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initComponent();
        suitLines.setLineSize(3f);
        Intent intent = getIntent();
        String partNum = intent.getStringExtra("part_num");
        Log.d(TAG, "onCreate: " + partNum);
        PartDetail detail = DataSupport.where("hvacNo like ?","%" +partNum + "%").findFirst(PartDetail.class);
        Log.d(TAG, "onCreate: detail 是否为空：" + (detail ==null) + "simpleNum = "+ partNum);
        if (detail != null) {
            Log.d(TAG, "onCreate: " + detail.getPartNumber());
            String simpleNum = detail.getPartNumber();
            if (simpleNum.length() > 8) {
                simpleNum = detail.getPartNumber().substring(0,detail.getPartNumber().indexOf(" "));
            }
            setupPic(simpleNum);
            setTitle(simpleNum + " for " + detail.getProjectNumber());
            String finalSimpleNum = simpleNum;
            floatingActionButton.setOnClickListener(v->changeAnotherPic(finalSimpleNum));
            LineData lineData = DataSupport.where("partNum=?",simpleNum).findFirst(LineData.class);
            if (lineData != null) {
                showSuitLine(lineData);
            }
            setDetailText(detail);
        }
    }
    private void  showSuitLine(LineData lineData){
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
    private void initComponent(){
        detailPic = (ImageView) findViewById(R.id.detail_pic);
        suitLines = (SuitLines) findViewById(R.id.suitline);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.detail_fab);
        detailAirFlow = (TextView) findViewById(R.id.detail_air_flow);
        detailBodyICD = (TextView) findViewById(R.id.detail_body_icd);
        detailCost = (TextView) findViewById(R.id.detail_cost);
        detailFlipMaterial = (TextView) findViewById(R.id.detail_flap_material);
        detailFrameMaterial = (TextView) findViewById(R.id.detail_frame_material);
        detailMass = (TextView) findViewById(R.id.detail_mass);
        detailProject = (TextView) findViewById(R.id.detail_project);
        detailSealMaterial = (TextView) findViewById(R.id.detail_seal_material);
        detailSupplier = (TextView) findViewById(R.id.detail_supplier);
        detailVehicleNum = (TextView) findViewById(R.id.vehicle_num);
        detailUnit = (TextView) findViewById(R.id.detail_unit);
        detailDimension = (TextView) findViewById(R.id.detail_dimension);
    }
    private void setupPic(String name){
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
        for (String string: pictures.keySet()){
            if (string.contains(name) && !string.contains(name +"s")){
                detailPic.setImageResource(pictures.get(string));
                return;
            }
        }
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor("#E9967A"));
        /*detailPic.setOnClickListener(v ->{
            Log.d(TAG, "setupPic: 图片被点击一次");
            changeAnotherPic(name);
        });*/
    }
    private int count = 0;
    private void changeAnotherPic(String name){
        switch (count){
            case 0:
                for (String string: pictures.keySet()){
                    if (string.contains(name +"s")){
                        Log.d(TAG, "changeAnotherPic: ");
                        changeWithAnimation(detailPic,pictures.get(string));
                        count ++;
                        return;
                    }
                }
            case 1:
                for (String string: pictures.keySet()){
                    if (string.contains(name) && !string.contains(name +"s")){
                        changeWithAnimation(detailPic,pictures.get(string));
                        count --;
                        return;
                }
                }
        }
    }
    private void setDetailText(PartDetail detail){
        detailAirFlow.setText(detail.getVehicleAirflow());
        detailFlipMaterial.setText(detail.getFlapMaterial());
        detailSealMaterial.setText(detail.getSealMaterial());
        detailUnit.setText(detail.getUnit());
        detailVehicleNum.setText(detail.getTotalVehicleNumber());
        detailFrameMaterial.setText(detail.getFrameMaterial());
        detailBodyICD.setText(detail.getBodyICD());
        detailSupplier.setText(detail.getSupplier());
        detailCost.setText(detail.getEngineeringCost());
        detailProject.setText(detail.getProjectNumber());
        PartDimension dimension  = DataSupport.findFirst(PartDimension.class);
        detailDimension.setText(dimension.getLength() + " * " + dimension.getWidth() + " * " + dimension.getHeight());
        Log.d(TAG, "setDetailText: "  + "%" + SelectAutomation.trimFit(detail.getPartNumber()) + "%");
        PartMass mass = DataSupport.select("mass")
                .where("partNum like ?      ","%" + SelectAutomation.trimFit(detail.getPartNumber()) + "%")
                .findFirst(PartMass.class);
        detailMass.setText(mass.getMass());
    }
    private void changeWithAnimation(ImageView imageview,int imageID){
        ValueAnimator animal = ValueAnimator.ofFloat(1.0f,0.0f,1.0f);
        animal.setDuration(1600);
        animal.addUpdateListener(v->{
            float value = (float)v.getAnimatedValue();
            Log.d(TAG, "changeWithAnimation: " + value);
            detailPic.setAlpha(value);
            if (value < 0.025){
                detailPic.setImageResource(imageID);
            }
        });
        animal.start();
        /*animal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                detailPic.setAlpha(value);
                if (value ==0f){
                    detailPic.setImageResource(imageID);
                }
            }
        });*/
    }
}
