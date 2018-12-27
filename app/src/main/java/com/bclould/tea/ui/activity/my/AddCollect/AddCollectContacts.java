package com.bclould.tea.ui.activity.my.AddCollect;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;

/**
 * Created by GIjia on 2018/12/25.
 */

public class AddCollectContacts {
    interface Presenter extends BasePresenter {
        void tvAddOnClick();
        void hideDialog();
    }

    interface View extends BaseView {
        /**
         * 设置url
         * @param url
         */
        void setEtUrl(String url);

        /**
         * 获取eturl值
         * @return
         */
        String getEtUrl();

        /**
         * 抖动动画
         */
        void animatorEtUrl();

        /**
         * 获取文本内容
         * @return
         */
        String getEtTitles();
    }
}
