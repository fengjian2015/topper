package com.dashiji.biyun.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dashiji.biyun.ui.activity.MainActivity;
import com.dashiji.biyun.ui.fragment.ConversationFragment;
import com.dashiji.biyun.ui.fragment.DynamicStateFragment;
import com.dashiji.biyun.ui.fragment.FriendListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/9/19.
 */

public class CloudMessageVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public CloudMessageVPAdapter(FragmentManager fm) {
        super(fm);

        MainActivity mainActivity = MainActivity.getInstance();

        mFragmentList.add(ConversationFragment.getInstance());
        mFragmentList.add(FriendListFragment.getInstance());
        mFragmentList.add(DynamicStateFragment.getInstance());

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
