package com.bclould.tocotalk.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tocotalk.ui.fragment.MyNewsFragment;
import com.bclould.tocotalk.ui.fragment.NewsBrowseRecordFragment;
import com.bclould.tocotalk.ui.fragment.NewsDraftsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/5/8.
 */

public class NewsManagerPVAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentList = new ArrayList<>();

    public NewsManagerPVAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragmentList.add(new MyNewsFragment());
        mFragmentList.add(new NewsBrowseRecordFragment());
        mFragmentList.add(new NewsDraftsFragment());
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
