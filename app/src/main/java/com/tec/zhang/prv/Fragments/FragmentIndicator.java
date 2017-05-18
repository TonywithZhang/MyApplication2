package com.tec.zhang.prv.Fragments;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by zhang on 2017/4/27.
 */

public class FragmentIndicator extends FragmentPagerAdapter{

    private List<Fragment> fragments;
    private List<String> tabNames;

    public FragmentIndicator(FragmentManager fragmentManager,List<Fragment> fragments,List<String> tabNames){
        super(fragmentManager);
        this.fragments = fragments;
        this.tabNames = tabNames;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position % tabNames.size());
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    @Override
    public int getCount() {
        return tabNames.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
