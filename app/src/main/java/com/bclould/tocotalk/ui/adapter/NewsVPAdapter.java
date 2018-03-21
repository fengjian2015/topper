package com.bclould.tocotalk.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tocotalk.ui.fragment.TextFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/3/21.
 */

public class NewsVPAdapter extends FragmentPagerAdapter{
    List<Fragment> mFragmentList = new ArrayList<>();
    public NewsVPAdapter(FragmentManager fm, ArrayList<String> textList) {
        super(fm);
        for (int i = 0; i < textList.size(); i++) {
            TextFragment textFragment = new TextFragment();
            textFragment.setText(textList.get(i));
            mFragmentList.add(textFragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
