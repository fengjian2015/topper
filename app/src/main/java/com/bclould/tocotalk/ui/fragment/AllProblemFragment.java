package com.bclould.tocotalk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.SearchActivity;
import com.bclould.tocotalk.ui.adapter.AllProblemRVAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/17.
 */

public class AllProblemFragment extends Fragment {

    public static AllProblemFragment instance = null;
    @Bind(R.id.ll_search)
    LinearLayout mLlSearch;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

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

        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setAdapter(new AllProblemRVAdapter(getActivity()));


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.ll_search)
    public void onViewClicked() {

        startActivity(new Intent(getActivity(), SearchActivity.class));

    }
}
