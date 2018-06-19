package com.bclould.tea.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tea.ui.fragment.MyGuessFragment;
import com.bclould.tea.ui.fragment.PersonageGuessFragment;
import com.bclould.tea.ui.fragment.PlatformGuessFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/4/23.
 */

public class BlockchainGambleVPAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentList = new ArrayList<>();

    public BlockchainGambleVPAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.add(new PlatformGuessFragment());
        mFragmentList.add(new PersonageGuessFragment());
        mFragmentList.add(new MyGuessFragment());
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
