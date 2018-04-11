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
import com.bclould.tocotalk.model.PostInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tocotalk.R.id.refreshLayout;

/**
 * Created by GA on 2017/9/27.
 */

public class TaDynamicFragment extends Fragment {

    public static TaDynamicFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private ArrayList<PostInfo> mPostList;
    private String[] IMG_URL_LIST = {
            "http://t2.27270.com/uploads/tu/201706/9999/d38274f15c.jpg", "http://t2.27270.com/uploads/tu/201706/9999/061548f1fb.jpg",
            "http://t2.27270.com/uploads/tu/201706/9999/4a85dd9bd9.jpg", "http://t2.27270.com/uploads/tu/201706/9999/a6c57f438d.jpg",
            "http://t2.27270.com/uploads/tu/201706/9999/b6ae25c618.jpg", "http://t2.27270.com/uploads/tu/201612/562/lua4uwojfds.jpg",
            "http://t2.27270.com/uploads/tu/201612/562/4hp4d1fcocu.jpg", "http://t2.27270.com/uploads/tu/201612/562/d2madqozild.jpg",
            "http://www.netbian.com/desk/19517.htm", "http://www.netbian.com/desk/19516.htm",
    };

    public static TaDynamicFragment getInstance() {

        if (instance == null) {

            instance = new TaDynamicFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_ta_dynamic, container, false);

        ButterKnife.bind(this, view);

        initRecyclerView();

        return view;
    }

    //初始化条目
    private void initRecyclerView() {


        final LinearLayoutManager manager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(manager);

        mPostList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            List<String> imgUrls = new ArrayList<>();
            imgUrls.addAll(Arrays.asList(IMG_URL_LIST).subList(0, i % 9));
            PostInfo post = new PostInfo("Am I handsome? Am I handsome? Am I handsome?", imgUrls);
            mPostList.add(post);
        }
//        mRecyclerView.setAdapter(new DynamicRVAdapter(getActivity(), mPostList));
        manager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View view = manager.findViewByPosition(1);
            }
        });

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
