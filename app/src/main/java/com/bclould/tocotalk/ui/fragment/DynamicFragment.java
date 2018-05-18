package com.bclould.tocotalk.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.DynamicListInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.adapter.DynamicRVAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
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
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private DynamicPresenter mDynamicPresenter;
    private String mPage = "1";
    private String mPageSize = "100";
    private DynamicRVAdapter mDynamicRVAdapter;
    private DBManager mMgr;

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
        initData(mPage, mPageSize);
        return view;
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.publish_comment))) {
            String id = event.getId();
            for (DynamicListInfo.DataBean info : mDataList) {
                if ((info.getId() + "").equals(id)) {
                    info.setReview_count(Integer.parseInt(event.getReviewCount()));
                }
            }
            mDynamicRVAdapter.notifyDataSetChanged();
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
            initData(mPage, mPageSize);
        } else if (msg.equals(getString(R.string.start_service))) {

        }
    }

    List<DynamicListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(String page, String pageSize) {
        String userList = "";
        List<UserInfo> userInfos = mMgr.queryAllUser();
        for (int i = 0; i < userInfos.size(); i++) {
            if (i == 0) {
                userList = userInfos.get(i).getUser();
            } else {
                userList += "," + userInfos.get(i).getUser();
            }
        }
        mDynamicPresenter.dynamicList(page, pageSize, userList, new DynamicPresenter.CallBack2() {
            @Override
            public void send(List<DynamicListInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(data);
                        mDynamicRVAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    private void initRecyclerView() {

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(manager);
        mDynamicRVAdapter = new DynamicRVAdapter(getActivity(), mDataList, mMgr, mDynamicPresenter);
        mRecyclerView.setAdapter(mDynamicRVAdapter);
        manager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View view = manager.findViewByPosition(1);
                if (view != null) System.out.println(view.getMeasuredHeight());
            }
        });
        mDynamicRVAdapter.notifyDataSetChanged();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData(mPage, mPageSize);
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
