package com.bclould.tea.ui.activity.ftc.acccountbinding;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;

/**
 * Created by kullo.me on 2017/9/19.
 */
public interface AccountBindingContacts {
    interface Presenter extends BasePresenter {
        void bind(String email,String password);
    }

    interface View extends BaseView {
        void setDesc(String desc);
    }
}