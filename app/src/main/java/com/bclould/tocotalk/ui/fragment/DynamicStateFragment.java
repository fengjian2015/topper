package com.bclould.tocotalk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.PostInfo;
import com.bclould.tocotalk.ui.adapter.TaDynamicRVAdapter;
import com.bclould.tocotalk.utils.FullyLinearLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/19.
 */

public class DynamicStateFragment extends Fragment {

    private String[] IMG_URL_LIST = {
            "http://img.netbian.com/file/2017/0825/b5e535255dad4a8b22249521aede5165.jpg", "http://img.netbian.com/file/2016/0809/332a449674941a9646b5dedee2a43a8e.jpg",
            "http://img.netbian.com/file/2017/1109/aad904a83d48cb50e0ecfb5e09bb9c91.jpg", "http://img.netbian.com/file/2017/1104/f2847eeb0e32ea5282bb4420f7af15af.jpg",
            "http://img.netbian.com/file/2017/1030/d0309ccee563f8e364337e299d237952.jpg", "http://img.netbian.com/file/2017/1025/551a3b63f7d66b3c4fc72f24ecd3ce13.jpg",
            "http://img.netbian.com/file/2017/1012/86c58f3cf18a72b92a478654cb6b3b20.jpg", "http://img.netbian.com/file/2017/1012/06060f6fc9f9dae6b609bff31f5e42d7.jpg",
            "http://img.netbian.com/file/2017/0825/8ca1d5f4f8ee0953b9d76cf252d1cb72.jpg", "http://img.netbian.com/file/2017/0406/fb719c029e258f116469ac9476cd186d.jpg",
    };
    public static DynamicStateFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private ArrayList<PostInfo> mPostList;

    public static DynamicStateFragment getInstance() {

        if (instance == null) {

            instance = new DynamicStateFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dynamic_state, container, false);

        ButterKnife.bind(this, view);

        initRecyclerView();

        return view;
    }


    private void initRecyclerView() {

        final FullyLinearLayoutManager manager = new FullyLinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(manager);

        mPostList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            List<String> imgUrls = new ArrayList<>();
            imgUrls.addAll(Arrays.asList(IMG_URL_LIST).subList(0, i % 9));
            PostInfo post = new PostInfo("Am I handsome? Am I handsome? Am I handsome?", imgUrls);
            mPostList.add(post);
        }

        TaDynamicRVAdapter taDynamicRVAdapter = new TaDynamicRVAdapter(getActivity(), mPostList);

        mRecyclerView.setAdapter(taDynamicRVAdapter);
        manager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View view = manager.findViewByPosition(1);
                if (view != null) System.out.println(view.getMeasuredHeight());
            }
        });
        taDynamicRVAdapter.notifyDataSetChanged();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }
}
