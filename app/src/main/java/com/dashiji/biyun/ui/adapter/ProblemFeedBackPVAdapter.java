package com.dashiji.biyun.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dashiji.biyun.ui.fragment.AllProblemFragment;
import com.dashiji.biyun.ui.fragment.ProblemFeedBackFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/10/17.
 */

public class ProblemFeedBackPVAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public ProblemFeedBackPVAdapter(FragmentManager fm) {
        super(fm);

        mFragmentList.add(AllProblemFragment.getInstance());
        mFragmentList.add(ProblemFeedBackFragment.getInstance());

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
