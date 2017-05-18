package com.tec.zhang.prv.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.zhang.prv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/28.
 */

public class MainPage extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;

    private List<Fragment> fragments;
    private List<String> tabNames;

    private SearchWithPartNumber searchWithPartNumber;
    private SearchWithPerformance searchWithPerformance;
    private SearchWithProjectNumber searchWithProjectNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabs_page,container,false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs1);
        viewPager = (ViewPager) view.findViewById(R.id.pagers1);

        searchWithPartNumber = new SearchWithPartNumber();
        searchWithPerformance = new SearchWithPerformance();
        searchWithProjectNumber = new SearchWithProjectNumber();

        fragments = new ArrayList<>();
        fragments.add(searchWithPartNumber);
        fragments.add(searchWithPerformance);
        fragments.add(searchWithProjectNumber);

        tabNames = new ArrayList<>();
        tabNames.add("按零件编号");
        tabNames.add("按性能");
        tabNames.add("按项目编号");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(tabNames.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabNames.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabNames.get(2)));

        adapter = new FragmentIndicator(getActivity().getSupportFragmentManager(),fragments,tabNames);
        viewPager.setAdapter(adapter);tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
