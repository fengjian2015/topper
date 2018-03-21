package com.bclould.tocotalk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.adapter.NewsRVAdapter;
import com.bclould.tocotalk.ui.adapter.NewsVPAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/21.
 */

public class NewsFragment extends Fragment {
    private static NewsFragment instance;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private NewsVPAdapter mNewsVPAdapter;
    private ArrayList<String> mTextList = new ArrayList<>();
    private NewsRVAdapter mNewsRVAdapter;

    public static NewsFragment getInstance() {
        if (instance == null) {
            instance = new NewsFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        initRecylerView();
        return view;
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNewsRVAdapter = new NewsRVAdapter(getContext());
        mRecyclerView.setAdapter(mNewsRVAdapter);
    }

   /* private void initData() {
        mTextList.add("放假快递费拉斯科技发送几啊六块腹肌房间爱是快乐监考老师大发");
        mTextList.add("发建设路口加哦我沃尔沃群殴IE鹅绒诶UR诶我去欧弱问饿哦为");
        mTextList.add("许昌金坷垃开了房接口理发师吉安解决接欧文 我我就日欧文加绒哦叫积极哦");
        mTextList.add("发了卡萨就发了洛克菲勒手机发拉进来姐夫我我温柔降温哦及的司法局阿里");
    }

    private void initViewPage() {
        mNewsVPAdapter = new NewsVPAdapter(getChildFragmentManager(), mTextList);
        mViewPager.setAdapter(mNewsVPAdapter);
        mViewPager.setCurrentItem(0);
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
