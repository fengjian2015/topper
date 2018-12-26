package com.bclould.tea.ui.activity.ftc.MyTeam;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.ui.adapter.MyTeamAdapter;

/**
 * Created by GIjia on 2018/12/25.
 */

public class MyTeamContacts {
    interface Presenter extends BasePresenter {
        void initRecyclerView();
        void initHttp(final boolean isRefresh,int p);
        int getPage();
        MyTeamAdapter getMyTeamAdapter();
        void setUserId(int userId);
    }

    interface View extends BaseView {
        void setEnableLoadMore(boolean istrue);
        void setResetRecycler(boolean isRefresh);
        void setNumberView(MyTeamInfo baseInfo);
    }
}
