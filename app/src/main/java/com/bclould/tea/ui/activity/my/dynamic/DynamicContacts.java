package com.bclould.tea.ui.activity.my.dynamic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bclould.tea.base.BasePresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.ui.adapter.DynamicRVAdapter;

/**
 * Created by GIjia on 2018/12/25.
 */

public class DynamicContacts {
    interface Presenter extends BasePresenter {
        DynamicRVAdapter getAdapter();
        void setOnRefreshListener();
        void setOnLoadMoreListener();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void selectorImg();
        void dblclick();
        void comment();
    }

    interface View extends BaseView {
        void setIvSelectorImg(Bitmap bitmap);
        void setIvSelectorImgIsShow(boolean isShow);
        void setRecyclerViewPosition(int select);
        String getCommentEt();
        void setCommentEt(String content);
        void setCommentEtHint(String content);
        void setRlEditVisibility(boolean isShow);
        void setfinishLoadOrRefresh(boolean isRefresh);
        boolean isRecyclerViewNull();
        void setRecyclerViewVisibility(boolean isShow);
        void setLlNoDataVisibility(boolean isShow);
        void setLlErrorVisibility(boolean isShow);
        void isActive();
        void setRlPushDynamicStatusIsShow(boolean isShow);
    }
}
