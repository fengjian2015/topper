package com.bclould.tea.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.BlockchainGuessPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.GuessListInfo;
import com.bclould.tea.ui.adapter.GuessListRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
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
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/23.
 */

public class PersonageGuessFragment extends Fragment {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.cb_search)
    CardView mCbSearch;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;
    private GuessListRVAdapter mGuessListRVAdapter;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage = 1;
    private int mPageSize = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_personage_guess, container, false);
        ButterKnife.bind(this, view);
        mCbSearch.setVisibility(View.VISIBLE);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        init();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.push_guess))) {
            initData("", PULL_DOWN,1);
        } else if (msg.equals(getString(R.string.bet))) {
            initData("", PULL_DOWN,1);
        } else if (msg.equals(getString(R.string.guess_cancel))) {
            initData("", PULL_DOWN,1);
        }
    }

    private void init() {
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(getContext());
        initRecyclerView();
        initData("", PULL_DOWN,1);
        initListener();
    }

    boolean isFinish = true;

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isFinish)
                initData("", PULL_DOWN,1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (isFinish) {
                    initData("", PULL_UP,mPage+1);
                }
            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//修改回车键功能
                    // 隐藏键盘
                    ((InputMethodManager) mEtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String user = mEtSearch.getText().toString().trim();
                    initData(user, PULL_DOWN,1);
                    return true;
                }
                return false;
            }
        });
    }

    private void initRecyclerView() {
        mGuessListRVAdapter = new GuessListRVAdapter(mDataList, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mGuessListRVAdapter);
    }

    List<GuessListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(String user, final int type,int p) {
        isFinish = false;
        mGuessListRVAdapter.notifyDataSetChanged();
        mBlockchainGuessPresenter.getGuessList(p, mPageSize, 1, user, new BlockchainGuessPresenter.CallBack() {
            @Override
            public void send(List<GuessListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (mRecyclerView != null) {
                        if (type == PULL_DOWN) {
                            mRefreshLayout.finishRefresh();
                            mPage=1;
                        } else {
                            mPage++;
                            mRefreshLayout.finishLoadMore();
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || data.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                            if (type == PULL_DOWN) {
                                if (data.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    mLlNoData.setVisibility(View.VISIBLE);
                                    mLlError.setVisibility(View.GONE);
                                } else {
                                    mDataList.clear();
                                }
                            }
                            mDataList.addAll(data);
                            mGuessListRVAdapter.notifyDataSetChanged();
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            mLlNoData.setVisibility(View.VISIBLE);
                            mLlError.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    isFinish = true;
                    if (type == PULL_DOWN) {
                        mRefreshLayout.finishRefresh();
                    }else{
                        mRefreshLayout.finishLoadMore();
                    }
                    if (type == PULL_DOWN) {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void finishRefresh() {
                isFinish = true;
                if (type == PULL_DOWN) {
                    mRefreshLayout.finishRefresh();
                }else{
                    mRefreshLayout.finishLoadMore();
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

    @OnClick(R.id.iv_search)
    public void onViewClicked() {
        try {
            // 隐藏键盘
            if(getActivity()==null)return;
            ((InputMethodManager) mEtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            String user = mEtSearch.getText().toString().trim();
            initData(user, PULL_DOWN,1);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
