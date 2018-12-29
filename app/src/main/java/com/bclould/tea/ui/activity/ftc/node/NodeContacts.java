package com.bclould.tea.ui.activity.ftc.node;

import android.support.v4.app.FragmentManager;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.ui.adapter.NodePagerAdapter;

/**
 * Created by GIjia on 2018/12/25.
 */

public class NodeContacts {
    interface Presenter extends BasePresenter {
        int getVpContentHight();
        NodePagerAdapter getAdapter();
        NodeInfo getNodeInfo();
    }

    interface View extends BaseView {
        FragmentManager getFm();
        void setAdapter();

        /**
         * 接受数据返回修改界面
         */
        void setViewData(NodeInfo nodeInfo);
    }
}
