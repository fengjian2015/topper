package com.bclould.tea.ui.activity.ftc.UpgradeNode;

import android.support.v4.app.FragmentManager;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.model.UpgradeInfo;
import com.bclould.tea.ui.adapter.NodePagerAdapter;

/**
 * Created by GIjia on 2018/12/25.
 */

public class UpgradeNodeContacts {
    interface Presenter extends BasePresenter {
        NodePagerAdapter getAdapter();
        void goHistoryActivity();
    }

    interface View extends BaseView {
        FragmentManager getFm();
        void setAdapter();
        void setView(UpgradeInfo upgradeInfo);
    }
}
