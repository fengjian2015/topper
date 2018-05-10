package com.bclould.tocotalk.ui.fragment;

import android.content.Context;
import android.content.Intent;
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

import com.bclould.tocotalk.Presenter.NewsNoticePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.NewsListInfo;
import com.bclould.tocotalk.ui.activity.NewsDetailsActivity;
import com.bclould.tocotalk.ui.adapter.NewsRVAdapter;
import com.bclould.tocotalk.utils.SpaceItemDecoration2;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsFragment extends Fragment implements OnBannerListener {
    private static NewsFragment instance;
    @Bind(R.id.tv_kaifa)
    TextView mTvKaifa;
    @Bind(R.id.banner)
    Banner mBanner;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_news)
    LinearLayout mLlNews;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private NewsRVAdapter mNewsRVAdapter;
    private int mPage = 1;
    private int mPageSize = 1000;

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
        if (UtilTool.getUser().equals("liaolinan2") || UtilTool.getUser().equals("conn") || UtilTool.getUser().equals("raymond") || UtilTool.getUser().equals("154323555") || UtilTool.getUser().equals("dev2018") || UtilTool.getUser().equals("xihongwei")) {
            mTvKaifa.setVisibility(View.GONE);
            mLlNews.setVisibility(View.VISIBLE);
        } else {
            mTvKaifa.setVisibility(View.VISIBLE);
            mLlNews.setVisibility(View.GONE);
        }
//        initBanner();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initRecylerView();
        initData();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                initData();
            }
        });
    }

    List<NewsListInfo.ListsBean> mNewsList = new ArrayList<>();
    List<NewsListInfo.TopBean> mTopList = new ArrayList<>();

    private void initData() {
        NewsNoticePresenter newsNoticePresenter = new NewsNoticePresenter(getContext());
        newsNoticePresenter.getNewsList(mPage, mPageSize, new NewsNoticePresenter.CallBack() {
            @Override
            public void send(List<NewsListInfo.ListsBean> lists, List<NewsListInfo.TopBean> top) {
                if (lists != null) {
                    mNewsList.clear();
                    mTopList.clear();
                    mTopList.addAll(top);
                    List<String> imgList = new ArrayList<>();
                    for (NewsListInfo.TopBean info : top) {
                        imgList.add(info.getIndex_pic());
                    }
                    mNewsList.addAll(lists);
                    mNewsRVAdapter.notifyDataSetChanged();
                    initBanner(imgList);
                }
            }
        });

    }

    private void initBanner(List<String> top) {
        List<String> imgList = new ArrayList<>();
        imgList.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
        imgList.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg");
        imgList.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
        imgList.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg");
        mBanner.setImages(top)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        Glide.with(context.getApplicationContext())
                                .load(path)
                                .into(imageView);
                    }
                })
                .setOnBannerListener(this)
                .start();
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNewsRVAdapter = new NewsRVAdapter(getContext(), mNewsList);
        mRecyclerView.setAdapter(mNewsRVAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration2(40));
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void OnBannerClick(int position) {
        Intent intent = new Intent(getContext(), NewsDetailsActivity.class);
        intent.putExtra("id", mTopList.get(position).getId());
    }
}
