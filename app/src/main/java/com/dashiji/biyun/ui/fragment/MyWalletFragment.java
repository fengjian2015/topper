package com.dashiji.biyun.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dashiji.biyun.Presenter.MyWalletPresenter;
import com.dashiji.biyun.R;
import com.dashiji.biyun.model.MyAssetsInfo;
import com.dashiji.biyun.ui.adapter.MyWalletRVAapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

public class MyWalletFragment extends Fragment {

    public static MyWalletFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public static MyWalletFragment getInstance() {

        if (instance == null) {

            instance = new MyWalletFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_wallet, container, false);

        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        initData();

        return view;
    }

    //初始化RecyclerView
    private void initRecyclerView(List<MyAssetsInfo.LtcBean> beanList) {
        mRecyclerView.setAdapter(new MyWalletRVAapter(getActivity(), beanList));
    }

    private void initData() {
        MyWalletPresenter myWalletPresenter = new MyWalletPresenter(this, getContext());
        myWalletPresenter.getMyAssets();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setData(List<MyAssetsInfo.LtcBean> ltcBeanList) {
        initRecyclerView(ltcBeanList);
    }
}
