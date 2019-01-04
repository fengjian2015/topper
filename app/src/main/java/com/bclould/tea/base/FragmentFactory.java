package com.bclould.tea.base;


import android.support.v4.app.Fragment;

import com.bclould.tea.ui.fragment.ConversationFragment;
import com.bclould.tea.ui.fragment.MallFragment;
import com.bclould.tea.ui.fragment.MyFragment;
import com.bclould.tea.ui.fragment.NewsFragment;
import com.bclould.tea.ui.fragment.WalletFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/1.
 * Fragment的工厂，保证创建的Fragment的唯一性
 */

public class FragmentFactory {
    private static FragmentFactory fragmentFactory;

    public static Map<Integer, Fragment> mMainMap = new HashMap<>();

    private FragmentFactory() {
    }

    public static FragmentFactory getInstanes() {
        if (fragmentFactory == null) {
            synchronized (FragmentFactory.class) {
                if (fragmentFactory == null) {
                    fragmentFactory =  new FragmentFactory();
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
            case 0:// 聊天页面
                fragment = ConversationFragment.getInstance();
                break;
            case 1:// 财富页面
                fragment = WalletFragment.getInstance();
                break;
            case 2:// 商城页面
                fragment = MallFragment.getInstance();
                break;
            case 3:// 新闻页面
                fragment = NewsFragment.getInstance();
                break;
            case 4:// 我的页面
                fragment = MyFragment.getInstance();
                break;
            default:
                fragment = NewsFragment.getInstance();
                break;
        }
        mMainMap.put(position, fragment);
        return fragment;
    }

    public void setNull() {
        mMainMap.clear();
    }
}
