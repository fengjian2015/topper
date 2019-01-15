package com.bclould.tea.ui.activity.wallet.exchangefrc;

import android.text.InputFilter;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.ui.adapter.ExchangeFRCAdapter;
import com.bclould.tea.ui.adapter.TaskRecordAdapter;

/**
 * Created by fengjian on 2018/12/28.
 */

public class ExchangeFRCContacts {
    interface Presenter extends BasePresenter {
        void initHttp(boolean isRefresh);
        void exchange(String money);
        InputFilter getLengthFilter();
        String exchangeMoeny(String money);//兑换后的金额
    }

    interface View extends BaseView {
        void setAdapter(ExchangeFRCAdapter adapter);
        void resetRefresh(boolean isRefresh);
        void setEnableLoadMore(boolean istrue);
        void setmRlSuccessShow(int isShow);
        void setmTvSuccess(String content);
        void setmTvBalance(String content);
        void setmTvEchangeFrcHelp(String content);
        void setmTvProportion(String content);
        void setmTvExchangeRate(String content);
        void setmTvRemaining(String content);

    }
}

