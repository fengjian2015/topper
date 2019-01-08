package com.bclould.tea.ui.activity.ftc.teamreward;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.ui.adapter.TeamRewardAdapter;

/**
 * Created by fengjian on 2018/12/28.
 */

public class TeamRewardContacts {
    interface Presenter extends BasePresenter {
        void initOptionPicker();
        void initHttp(boolean isRefresh);
    }

    interface View extends BaseView {
        void setDateView(String time);
        void setAdapter(TeamRewardAdapter adapter);
        void resetRefresh(boolean isRefresh);
        void setEnableLoadMore(boolean istrue);
        void setViewIsGone(int isVmRecyclerView,int isVmLlNoData,int isVmLlError);
    }
}

