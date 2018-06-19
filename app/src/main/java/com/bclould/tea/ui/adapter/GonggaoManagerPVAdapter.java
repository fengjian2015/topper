package com.bclould.tea.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tea.ui.fragment.AllGonggaoFragment;
import com.bclould.tea.ui.fragment.NewGonggaoFragment;
import com.bclould.tea.ui.fragment.TopGonggaoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/5/9.
 */

public class GonggaoManagerPVAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentList = new ArrayList<>();

    public GonggaoManagerPVAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        mFragmentList.add(new AllGonggaoFragment());
        mFragmentList.add(new TopGonggaoFragment());
        mFragmentList.add(new NewGonggaoFragment());

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        if (mFragmentList.size() != 0) {
            return mFragmentList.size();
        }
        return 0;
    }
}
