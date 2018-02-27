package com.bclould.tocotalk.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tocotalk.ui.fragment.EmitFragment;
import com.bclould.tocotalk.ui.fragment.ReceiveFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/1/3.
 */

public class RedRecordVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public RedRecordVPAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.add(ReceiveFragment.getInstance());
        mFragmentList.add(EmitFragment.getInstance());
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
