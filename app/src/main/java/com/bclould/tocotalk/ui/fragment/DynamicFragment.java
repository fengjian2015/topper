package com.bclould.tocotalk.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.DynamicListInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.MainActivity;
import com.bclould.tocotalk.ui.adapter.DynamicRVAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicFragment extends Fragment {
    public static DynamicFragment instance = null;
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
    private DynamicPresenter mDynamicPresenter;
    private DynamicRVAdapter mDynamicRVAdapter;
    private DBManager mMgr;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int end = 0;
    private int mPage = 1;
    private int mPageSize = 10;
    public LinearLayoutManager mLinearLayoutManager;
    private MainActivity.MyOnTouchListener mTouchListener;

    public static DynamicFragment getInstance() {

        if (instance == null) {

            instance = new DynamicFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dynamic_state, container, false);
        ButterKnife.bind(this, view);
        mDynamicPresenter = new DynamicPresenter(getContext());
        mMgr = new DBManager(getContext());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initListener();
        initData(PULL_DOWN);
        return view;
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.publish_comment))) {
            String id = event.getId();
            for (int i = 0; i < mDataList.size(); i++) {
                if ((mDataList.get(i).getId() + "").equals(id)) {
                    mDataList.get(i).setReview_count(Integer.parseInt(event.getReviewCount()));
                    DynamicListInfo.DataBean.ReviewListBean reviewListBean = new DynamicListInfo.DataBean.ReviewListBean();
                    reviewListBean.setContent(event.getFiltrate());
                    reviewListBean.setUser_name(event.getCoinName());
                    mDataList.get(i).getReviewList().add(0, reviewListBean);
                    if (mDataList.get(i).getReviewList().size() > 5) {
                        mDataList.get(i).getReviewList().remove(mDataList.get(i).getReviewList().size() - 1);
                    }
                    mDynamicRVAdapter.notifyItemChanged(i);
                    /*mDynamicRVAdapter.mDynamicReviewRVAdapter.notifyItemInserted(0);
                    mDynamicRVAdapter.mDynamicReviewRVAdapter.notifyItemRangeChanged(0, mDataList.get(i).getReviewList().size() - 0);*/
                    return;
                }
            }
        } else if (msg.equals(getString(R.string.zan))) {
            String id = event.getId();
            for (DynamicListInfo.DataBean info : mDataList) {
                if ((info.getId() + "").equals(id)) {
                    info.setLike_count(Integer.parseInt(event.getLikeCount()));
                    if (event.isType()) {
                        info.setIs_like(1);
                    } else {
                        info.setIs_like(0);
                    }
                }
            }
            mDynamicRVAdapter.notifyDataSetChanged();
        } else if (msg.equals(getString(R.string.publish_dynamic))) {
            initData(PULL_DOWN);
        } else if (msg.equals(getString(R.string.delete_dynamic))) {
            String id = event.getId();
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == Integer.parseInt(id)) {
                    mDataList.remove(i);
                    mDynamicRVAdapter.notifyItemRemoved(i);
                    mDynamicRVAdapter.notifyItemRangeChanged(0, mDataList.size() - i);
                    return;
                }
            }
        } else if (msg.equals(getString(R.string.reward_succeed))) {
            showRewardSucceedDialog();
            int id = Integer.parseInt(event.getId());
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == id) {
                    mDataList.get(i).setRewardCount(mDataList.get(i).getRewardCount() + 1);
                    mDynamicRVAdapter.notifyItemChanged(i);
                    break;
                }
            }
        } else if (msg.equals(getString(R.string.shield_dy))) {
            initData(PULL_DOWN);
        } else if (msg.equals(getString(R.string.comment))) {
            String id = event.getId();
            String username = event.getCoinName();
            if (username != null && !username.isEmpty()) {
                mCommentEt.setHint(getString(R.string.reply) + username);
            }
            mRlEdit.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = imm.isActive(mCommentEt);//isOpen若返回true，则表示输入法打开
            if (!isOpen) {
                mCommentEt.requestFocus();
                imm.showSoftInput(mCommentEt, 0);
            }
        } else if (msg.equals(getString(R.string.hide_keyboard))) {
            mRlEdit.setVisibility(View.GONE);
        }
    }

    @SuppressLint("HandlerLeak")
    private void showRewardSucceedDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_reward, getContext(), R.style.dialog);
        deleteCacheDialog.show();
        new Handler() {
            public void handleMessage(Message msg) {
                deleteCacheDialog.dismiss();
            }
        }.sendEmptyMessageDelayed(0, 1500);
    }

    List<DynamicListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(final int type) {
        String userList = "";
        List<UserInfo> userInfos = mMgr.queryAllUser();
        for (int i = 0; i < userInfos.size(); i++) {
            if (i == 0) {
                userList = userInfos.get(i).getUser();
            } else {
                userList += "," + userInfos.get(i).getUser();
            }
        }
        if (type == PULL_DOWN) {
            mPage = 1;
            end = 0;
        }
        isFinish = false;
        mDynamicPresenter.dynamicList(mPage, mPageSize, userList, new DynamicPresenter.CallBack2() {
            @Override
            public void send(List<DynamicListInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (mDataList.size() != 0 || data.size() != 0) {
                        isFinish = true;
                        if (type == PULL_UP) {
                            if (data.size() == mPageSize) {
                                mPage++;
                                mDataList.addAll(data);
                                mDynamicRVAdapter.notifyDataSetChanged();
                            } else {
                                if (end == 0) {
                                    end++;
                                    mDataList.addAll(data);
                                    mDynamicRVAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (mPage == 1) {
                                mPage++;
                            }
                            mDataList.clear();
                            mDataList.addAll(data);
                            mDynamicRVAdapter.notifyDataSetChanged();
                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    boolean isFinish = true;

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mDynamicRVAdapter = new DynamicRVAdapter(getActivity(), mDataList, mMgr, mDynamicPresenter);
        mRecyclerView.setAdapter(mDynamicRVAdapter);
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View view = mLinearLayoutManager.findViewByPosition(1);
                if (view != null) System.out.println(view.getMeasuredHeight());
            }
        });
        mDynamicRVAdapter.notifyDataSetChanged();
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive(mCommentEt);//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mCommentEt.getWindowToken(), 0);
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
                refreshlayout.finishRefresh(2000);
                initData(PULL_DOWN);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (isFinish) {
                    initData(PULL_UP);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
