package com.bclould.tea.ui.fragment;

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

import com.bclould.tea.Presenter.RealNamePresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.QuestionInfo;
import com.bclould.tea.ui.adapter.AllProblemRVAdapter;
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AllProblemFragment extends Fragment {

    public static AllProblemFragment instance = null;
    @Bind(R.id.ll_search)
    LinearLayout mLlSearch;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.et_search)
    ClearEditText mEtSearch;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private AllProblemRVAdapter mAllProblemRVAdapter;
    private RealNamePresenter mRealNamePresenter;

    public static AllProblemFragment getInstance() {

        if (instance == null) {

            instance = new AllProblemFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_all_problem, container, false);

        ButterKnife.bind(this, view);
        mRealNamePresenter = new RealNamePresenter(getActivity());
        initRecyclerView();

        initData();

        return view;
    }

    List<QuestionInfo.DataBean> mDataBeanList = new ArrayList<>();

    private void initData() {
        mDataBeanList.clear();
        mRealNamePresenter.getQuestionList(new RealNamePresenter.CallBack3() {
            @Override
            public void send(List<QuestionInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlSearch.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mDataBeanList.addAll(data);
                    mAllProblemRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
                mRecyclerView.setVisibility(View.GONE);
                mLlSearch.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAllProblemRVAdapter = new AllProblemRVAdapter(getActivity(), mDataBeanList);

        mRecyclerView.setAdapter(mAllProblemRVAdapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.ll_error)
    public void onViewClicked() {
        initData();
    }
}
