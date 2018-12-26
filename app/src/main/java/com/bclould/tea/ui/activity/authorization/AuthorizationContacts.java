package com.bclould.tea.ui.activity.authorization;

import android.content.Intent;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;

/**
 * Created by kullo.me on 2017/9/19.
 */
public interface AuthorizationContacts {
    interface Presenter extends BasePresenter {
       void getUserInfo(String access_token);
       String getAccessToken();
    }

    interface View extends BaseView {
       void setllDataView();
    }
}
