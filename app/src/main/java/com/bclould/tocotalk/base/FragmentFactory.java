package com.bclould.tocotalk.base;


import android.support.v4.app.Fragment;

import com.bclould.tocotalk.ui.activity.MainActivity;
import com.bclould.tocotalk.ui.fragment.CloudCoinFragment;
import com.bclould.tocotalk.ui.fragment.CloudMessageFragment;
import com.bclould.tocotalk.ui.fragment.DynamicStateFragment;
import com.bclould.tocotalk.ui.fragment.MyFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/1.
 * Fragment的工厂，保证创建的Fragment的唯一性
 */

public class FragmentFactory {
    private static FragmentFactory fragmentFactory;

    public static Map<Integer, Fragment> mMainMap = new HashMap<>();
    private static MainActivity sMainActivity;

    private FragmentFactory() {
    }

    public static FragmentFactory getInstanes(MainActivity mainActivity) {
        sMainActivity = mainActivity;
        if (fragmentFactory == null) {
            synchronized (FragmentFactory.class) {
                if (fragmentFactory == null) {
                    return new FragmentFactory();
                }
            }
        }
        return fragmentFactory;
    }

    /**
     * 存储Fragment
     *
     * @param position
     * @return Fragment
     */
    public Fragment createMainFragment(int position) {

        // 如果内存中有，那么就从内存中获取，否则就重新创建
        if (mMainMap.containsKey(position)) {
            return mMainMap.get(position);
        }

        Fragment fragment = null;
        switch (position) {
            case 0:// 云信页面
                fragment = new CloudMessageFragment();
                break;
            case 1:// 云币页面
                fragment = DynamicStateFragment.getInstance();
                break;
            case 2:// 云圈页面
                fragment = CloudCoinFragment.getInstance(sMainActivity);
                break;
            case 3:// 我的页面
                fragment = new MyFragment();
                break;
            default:
                fragment = new CloudMessageFragment();
                break;
        }
        mMainMap.put(position, fragment);
        return fragment;
    }

    public void setNull() {
        mMainMap.clear();
    }
}
