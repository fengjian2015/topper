package com.bclould.tocotalk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.adapter.BillDataRVAapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

public class BillDataFragment extends Fragment {

    public static BillDataFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    //对外提供方法获取对象
    public static BillDataFragment getInstance() {

        if (instance == null) {

            instance = new BillDataFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bill_data, container, false);

        ButterKnife.bind(this, view);

        initListView();

        return view;
    }

    //初始化条目
    private void initListView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setAdapter(new BillDataRVAapter(getActivity()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
