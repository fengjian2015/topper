package com.bclould.tea.ui.activity.my.taskcenter;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.ui.adapter.TaskCenterAdapter;
import com.bclould.tea.ui.adapter.TeamRewardAdapter;

/**
 * Created by fengjian on 2018/12/28.
 */

public class TaskCenterContacts {
    interface Presenter extends BasePresenter {
        void initOptionPicker();
        void initHttp(boolean isRefresh);
    }

    interface View extends BaseView {
        void setAdapter(TaskCenterAdapter adapter);
        void resetRefresh(boolean isRefresh);
        void setEnableLoadMore(boolean istrue);
    }
}

