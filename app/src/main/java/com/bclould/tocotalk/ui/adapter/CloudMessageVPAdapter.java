package com.bclould.tocotalk.ui.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bclould.tocotalk.ui.activity.MainActivity;
import com.bclould.tocotalk.ui.fragment.ConversationFragment;
import com.bclould.tocotalk.ui.fragment.FriendListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CloudMessageVPAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList = new ArrayList<>();

    public CloudMessageVPAdapter(FragmentManager fm) {
        super(fm);

        MainActivity mainActivity = MainActivity.getInstance();

        mFragmentList.add(ConversationFragment.getInstance());
        mFragmentList.add(FriendListFragment.getInstance());

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
