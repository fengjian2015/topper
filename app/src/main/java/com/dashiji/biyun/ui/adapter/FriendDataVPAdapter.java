package com.dashiji.biyun.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dashiji.biyun.ui.fragment.RelateToTaFragment;
import com.dashiji.biyun.ui.fragment.TaDynamicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/9/27.
 */

public class FriendDataVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public FriendDataVPAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.add(TaDynamicFragment.getInstance());
        mFragmentList.add(RelateToTaFragment.getInstance());
    }



    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
