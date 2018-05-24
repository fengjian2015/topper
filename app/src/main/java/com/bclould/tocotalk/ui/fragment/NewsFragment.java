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
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.SpaceItemDecoration2;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
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
    private int mPageSize = 10;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private NewsNoticePresenter mNewsNoticePresenter;
    private int end = 0;

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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNewsNoticePresenter = new NewsNoticePresenter(getContext());
        initListener();
        initRecylerView();
        initData(PULL_DOWN);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                initData(PULL_DOWN);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                initData(PULL_UP);
            }
        });
    }

    List<NewsListInfo.ListsBean> mNewsList = new ArrayList<>();
    List<NewsListInfo.TopBean> mTopList = new ArrayList<>();

    private void initData(final int type) {
        if (type == PULL_DOWN) {
            mPage = 1;
            end = 0;
        }
        UtilTool.Log("分頁", mPage + "");
        mNewsNoticePresenter.getNewsList(mPage, mPageSize, new NewsNoticePresenter.CallBack() {
            @Override
            public void send(List<NewsListInfo.ListsBean> lists, List<NewsListInfo.TopBean> top) {
                if (lists != null) {
                    if (lists.size() != 0 || mNewsList.size() != 0 || top.size() != 0 || mTopList.size() != 0) {
                        if (type == PULL_UP) {
                            if (lists.size() == mPageSize) {
                                mPage++;
                                mNewsList.addAll(lists);
                                mNewsRVAdapter.notifyDataSetChanged();
                            } else {
                                if (end == 0) {
                                    end++;
                                    mNewsList.addAll(lists);
                                    mNewsRVAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (mPage == 1) {
                                mPage++;
                            }
                            mNewsList.clear();
                            mTopList.clear();
                            mNewsList.addAll(lists);
                            mTopList.addAll(top);
                            mNewsRVAdapter.notifyDataSetChanged();
                            List<String> imgList = new ArrayList<>();
                            List<String> titleList = new ArrayList<>();
                            for (NewsListInfo.TopBean info : top) {
                                imgList.add(info.getIndex_pic());
                                titleList.add(info.getTitle());
                            }
                            initBanner(imgList, titleList);
                        }
                    }
                }
            }
        });

    }

    private void initBanner(List<String> top, List<String> titleList) {
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImages(top)
                .setBannerTitles(titleList)
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
        intent.putExtra("type", Constants.NEWS_MAIN_TYPE);
        startActivity(intent);
    }
}
