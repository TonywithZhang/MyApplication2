package com.tec.zhang.prv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.tec.zhang.prv.Fragments.FragmentIndicator;
import com.tec.zhang.prv.Fragments.MainPage;
import com.tec.zhang.prv.Fragments.SearchWithPartNumber;
import com.tec.zhang.prv.Fragments.SearchWithPerformance;
import com.tec.zhang.prv.Fragments.SearchWithProjectNumber;
import com.tec.zhang.prv.Fragments.SelectAutomation;
import com.tec.zhang.prv.databaseUtil.PartDetail;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Search extends BaseActivity {
    private TextView title;
    private Toolbar toolbar;
    private MainPage mainPage;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private SelectAutomation selectAutomation;
    private FlowingDrawer flowingDrawer;
    private NavigationView navi;
    private View header;
    private CircleImageView circleImageView;
    boolean granted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_sample);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=590daa60");
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        title = (TextView) findViewById(R.id.title_all);
        title.setText(getResources().getString(R.string.check_for));
        manager = getSupportFragmentManager();
        mainPage = new MainPage();
        selectAutomation = new SelectAutomation();
        showMainPage();

        flowingDrawer = (FlowingDrawer) findViewById(R.id.drawer_view);
        navi = (NavigationView) flowingDrawer.findViewById(R.id.navi);
        header = flowingDrawer.findViewById(R.id.header);
        navi.setNavigationItemSelectedListener(navigationItemSelectedListener);

        requestForPermission();
        createFolder();
    }

    private void showMainPage() {
        if (mainPage.isAdded()){
            if (mainPage.isHidden()){
                ft = manager.beginTransaction();
                ft.hide(selectAutomation).show(mainPage).commit();
            }
        }else {
            ft = manager.beginTransaction();
            ft.add(R.id.container,mainPage).commit();
        }
    }
    private void showAutoSelection(){
        if (selectAutomation.isAdded()){

            if (selectAutomation.isHidden()){
                ft = manager.beginTransaction();
                ft.hide(mainPage).show(selectAutomation).commit();
            }
        }else {
            ft = manager.beginTransaction();
            ft.hide(mainPage).add(R.id.container,selectAutomation).commit();
        }
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.auto:
                    navi.setCheckedItem(R.id.auto);
                    showAutoSelection();
                    title.setText(getResources().getString(R.string.app_name));
                    flowingDrawer.closeMenu(true);
                    break;
                case R.id.check_information:
                    navi.setCheckedItem(R.id.select_auto);
                    showMainPage();
                    title.setText(getResources().getString(R.string.check_for));
                    flowingDrawer.closeMenu(true);
                    break;
            }
            return true;
        }
    };

    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS,Manifest.permission_group.STORAGE,Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private boolean requestForPermission(){
        if (!checkPemission(permissions[0]) || !checkPemission(permissions[1]) || !checkPemission(permissions[2]) || !checkPemission(permissions[3]) || !checkPemission(permissions[4])){
            ActivityCompat.requestPermissions(this,permissions,0x1);
        }
        if (granted){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0x1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                } else Toasty.info(this, "您拒绝了授权，有部分功能可能无法使用", Toast.LENGTH_SHORT).show();
        }
    }
    private void createFolder(){
        File file = Environment.getExternalStorageDirectory();
        File owner = new File(file.toString()+ "/PRV");
        if (!owner.exists()){
            owner.mkdir();
        }
    }

    public static List<PartDetail> results;
    public static List<PartDetail> searchInDatabase(String s){
        DataSupport.where("partName like ?","'%" + s +"%'").findAsync(PartDetail.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                results = (List<PartDetail>) t;
            }
        });
        return results;
    }
    private boolean checkPemission(String s){
        if (ContextCompat.checkSelfPermission(this,s) != PackageManager.PERMISSION_GRANTED){
            return false;
        }else return true;
    }
}
