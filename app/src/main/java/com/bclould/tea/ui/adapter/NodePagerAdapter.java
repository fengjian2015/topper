package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by GIjia on 2018/8/22.
 */

public class NodePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> tabFragments;
    private List<Integer> tabIndicators;
    private Context context;
    public NodePagerAdapter(FragmentManager fm, List<Fragment> tabFragments, List<Integer> tabIndicators, Context context) {
        super(fm);
        this.tabIndicators=tabIndicators;
        this.tabFragments=tabFragments;
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabIndicators.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(tabIndicators.get(position));
    }


}
