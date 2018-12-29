package com.bclould.tea.ui.activity.my.systemxet;

import android.content.Intent;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;

/**
 * Created by fengjian on 2018/12/28.
 */

public class SystemSetContacts {
    interface Presenter extends BasePresenter {
        void onMyNewIntent(Intent intent);
        void rlInformClick();
        void showDialog();
        void goSelectorLanguageActivity();
        void rlPrivateClick();
        void rlCache();
        void showBackgoundDialog();
        void changeDownOnOff();
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    interface View extends BaseView {
        void setOnOffDownloadSelected(boolean istrue);
        void setTvCacheCount(String content);
        void setOnOffInformSelected(boolean isTrue);
        void setTvLanguageHint(String content);
        void setOnOffPrivateSelected(boolean isTrue);
    }
}
