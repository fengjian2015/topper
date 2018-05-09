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
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.adapter.GonggaoManagerRVAdapter;
import com.bclould.tocotalk.utils.SpaceItemDecoration;
import com.bclould.tocotalk.utils.UtilTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/7.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GonggaoFragment extends Fragment {
    private static GonggaoFragment instance;
    @Bind(R.id.tv_kaifa)
    TextView mTvKaifa;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    public static GonggaoFragment getInstance() {
        if (instance == null) {
            instance = new GonggaoFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gonggao, container, false);
        ButterKnife.bind(this, view);
        if (UtilTool.getUser().equals("liaolinan2") || UtilTool.getUser().equals("conn") || UtilTool.getUser().equals("raymond") || UtilTool.getUser().equals("154323555") || UtilTool.getUser().equals("dev2018") || UtilTool.getUser().equals("xihongwei")) {
            mTvKaifa.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTvKaifa.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GonggaoManagerRVAdapter gonggaoManagerRVAdapter = new GonggaoManagerRVAdapter(getContext());
        mRecyclerView.setAdapter(gonggaoManagerRVAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(40));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
