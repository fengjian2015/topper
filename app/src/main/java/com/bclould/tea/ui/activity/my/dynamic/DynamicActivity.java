package com.bclould.tea.ui.activity.my.dynamic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.activity.FileUploadingActivity;
import com.bclould.tea.ui.activity.MainActivity;
import com.bclould.tea.ui.activity.PersonageDynamicActivity;
import com.bclould.tea.ui.activity.PublicshDynamicActivity;
import com.bclould.tea.ui.adapter.DynamicRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2018/7/31.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicActivity extends BaseActivity implements DynamicContacts.View{
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.comment_et)
    EditText mCommentEt;
    @Bind(R.id.iv_selector_img)
    ImageView mIvSelectorImg;
    @Bind(R.id.send)
    TextView mSend;
    @Bind(R.id.rl_edit)
    RelativeLayout mRlEdit;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.rl_push_dynamic_status)
    RelativeLayout mRlPushDynamicStatus;
    @Bind(R.id.iv_push_dynamic)
    ImageView mIvPushDynamic;
    @Bind(R.id.iv_my_dynamic)
    ImageView mIvMyDynamic;

    public LinearLayoutManager mLinearLayoutManager;
    private DynamicContacts.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        ButterKnife.bind(this);
        mPresenter=new com.bclould.tea.ui.activity.my.dynamic.DynamicPresenter();
        mPresenter.bindView(this);
        mPresenter.start(this);
    }

    @Override
    public void initView() {
        initRecyclerView();
        initListener();
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mPresenter.getAdapter());
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View view = mLinearLayoutManager.findViewByPosition(1);
                if (view != null) System.out.println(view.getMeasuredHeight());
            }
        });
        mPresenter.getAdapter().notifyDataSetChanged();
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive(mCommentEt);//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mCommentEt.getWindowToken(), 0);
                    mRlEdit.setVisibility(View.GONE);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.setOnRefreshListener();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.setOnLoadMoreListener();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    //拿到选择的图片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode,resultCode,data);
    }


    @OnClick({R.id.bark, R.id.rl_push_dynamic_status, R.id.iv_push_dynamic, R.id.iv_my_dynamic, R.id.send, R.id.tv_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_title:
                mPresenter.dblclick();
                break;
            case R.id.rl_push_dynamic_status:
                startActivity(new Intent(this, FileUploadingActivity.class));
                break;
            case R.id.iv_push_dynamic:
                startActivity(new Intent(this, PublicshDynamicActivity.class));
                break;
            case R.id.iv_my_dynamic:
                Intent intent = new Intent(this, PersonageDynamicActivity.class);
                intent.putExtra("name", UtilTool.getUser());
                intent.putExtra("user", UtilTool.getTocoId());
                startActivity(intent);
                break;
            case R.id.iv_selector_img:
                mPresenter.selectorImg();
                break;
            case R.id.send:
                if (mCommentEt.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.toast_comment), Toast.LENGTH_SHORT).show();
                } else {
                    mPresenter.comment();
                }
                break;
        }
    }

    @Override
    public void setIvSelectorImg(Bitmap bitmap) {
        mIvSelectorImg.setImageBitmap(bitmap);
    }

    @Override
    public void setIvSelectorImgIsShow(boolean isShow) {
        if(isShow){
            mIvSelectorImg.setVisibility(View.VISIBLE);
        }else{
            mIvSelectorImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRecyclerViewPosition(int select) {
        mRecyclerView.scrollToPosition(select);
    }

    @Override
    public String getCommentEt() {
        return  mCommentEt.getText().toString();
    }

    @Override
    public void setCommentEt(String content) {
        mCommentEt.setText(content);
    }

    @Override
    public void setCommentEtHint(String content) {
        mCommentEt.setHint(content);
    }

    @Override
    public void setRlEditVisibility(boolean isShow) {
        if(isShow){
            mRlEdit.setVisibility(View.VISIBLE);
        }else{
            mRlEdit.setVisibility(View.GONE);
        }
    }

    @Override
    public void setfinishLoadOrRefresh(boolean isRefresh) {
        if(isRefresh){
            mRefreshLayout.finishRefresh();
        }else{
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public boolean isRecyclerViewNull() {
        if(mRecyclerView==null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void setRecyclerViewVisibility(boolean isShow) {
        if(isShow){
            mRecyclerView.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLlNoDataVisibility(boolean isShow) {
        if(isShow){
            mLlNoData.setVisibility(View.VISIBLE);
        }else {
            mLlNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLlErrorVisibility(boolean isShow) {
        if(isShow){
            mLlError.setVisibility(View.VISIBLE);
        }else {
            mLlError.setVisibility(View.GONE);
        }
    }

    @Override
    public void isActive() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen=imm.isActive(mCommentEt);
        if (!isOpen) {
            mCommentEt.requestFocus();
            imm.showSoftInput(mCommentEt, 0);
        }
    }

    @Override
    public void setRlPushDynamicStatusIsShow(boolean isShow) {
        if(isShow){
            mRlPushDynamicStatus.setVisibility(View.VISIBLE);
        }else {
            mRlPushDynamicStatus.setVisibility(View.GONE);
        }
    }
}
