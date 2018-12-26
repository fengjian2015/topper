package com.bclould.tea.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bclould.tea.Presenter.NewsNoticePresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.NewsListInfo;
import com.bclould.tea.model.QrRedInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.AddFriendActivity;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.activity.NewsDetailsActivity;
import com.bclould.tea.ui.activity.ReceiptPaymentActivity;
import com.bclould.tea.ui.activity.ScanQRCodeActivity;
import com.bclould.tea.ui.activity.SendQRCodeRedActivity;
import com.bclould.tea.ui.adapter.NewsRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.StatusBarCompat;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
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
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GA on 2018/3/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsFragment extends Fragment implements OnBannerListener {
    private static NewsFragment instance = null;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.iv_menu)
    ImageView mIvMenu;
    @Bind(R.id.ll_menu)
    LinearLayout mLlMenu;
    @Bind(R.id.banner)
    Banner mBanner;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_news)
    LinearLayout mLlNews;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv_loading)
    ImageView mIvLoading;
    private NewsRVAdapter mNewsRVAdapter;
    private int mPage_id = 0;
    private int mPageSize = 10;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private int QRCODE = 1;
    private NewsNoticePresenter mNewsNoticePresenter;
    public LinearLayoutManager mLinearLayoutManager;
    private RequestOptions mRequestOptions = new RequestOptions()
            .error(R.mipmap.img_banner_default)
            .centerCrop()
            .placeholder(R.mipmap.img_banner_default)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    ;

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
        init();
        getPhoneSize();
        return view;
    }

    private void init() {
        mLlMenu.bringToFront();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, StatusBarCompat.getStateBarHeight(getActivity()), 0, 0);
        mLlMenu.setLayoutParams(layoutParams);
    }

    //获取屏幕高度
    private void getPhoneSize() {

        mDm = new DisplayMetrics();

        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);

        mHeightPixels = mDm.heightPixels;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNewsNoticePresenter = new NewsNoticePresenter(getContext());
        initListener();
        initRecylerView();
        initData(PULL_DOWN);
    }

    boolean isFinish = true;

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isFinish)
                    initData(PULL_DOWN);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (isFinish) {
                    initData(PULL_UP);
                }
            }
        });
    }

    List<NewsListInfo.ListsBean> mNewsList = new ArrayList<>();
    List<NewsListInfo.TopBean> mTopList = new ArrayList<>();

    private void initData(final int type) {
        if (type == PULL_DOWN) {
            mPage_id = 0;
        }
        isFinish = false;
        UtilTool.Log("分頁", mPage_id + "");
        mNewsNoticePresenter.getNewsList(mPage_id, mPageSize,mIvLoading, new NewsNoticePresenter.CallBack() {
            @Override
            public void send(List<NewsListInfo.ListsBean> lists, List<NewsListInfo.TopBean> top) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (mRefreshLayout == null) return;
                    if (type == PULL_DOWN) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                    isFinish = true;
                    if (lists != null && top != null && lists.size() != 0 && top.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.GONE);
                        if (type == PULL_UP) {
                            mNewsList.addAll(lists);
                            if (mNewsList.size() != 0)
                                mPage_id = mNewsList.get(mNewsList.size() - 1).getId();
                            mNewsRVAdapter.notifyDataSetChanged();
                        } else {
                            mNewsList.clear();
                            mTopList.clear();
                            mNewsList.addAll(lists);
                            mTopList.addAll(top);
                            if (mNewsList.size() != 0)
                                mPage_id = mNewsList.get(mNewsList.size() - 1).getId();
                            mNewsRVAdapter.notifyDataSetChanged();
                            List<String> imgList = new ArrayList<>();
                            List<String> titleList = new ArrayList<>();
                            for (NewsListInfo.TopBean info : top) {
                                imgList.add(info.getIndex_pic());
                                titleList.add(info.getTitle());
                            }
                            initBanner(imgList, titleList);
                        }
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    isFinish = true;
                    if (type == PULL_DOWN) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                    mRecyclerView.setVisibility(View.GONE);
                    mLlNoData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void finishRefresh() {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    isFinish = true;
                    if (type == PULL_DOWN) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
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
                        Glide.with(getActivity())
                                .load(path)
                                .apply(mRequestOptions)
                                .into(imageView);
                    }
                })
                .setOnBannerListener(this)
                .start();
    }

    private void initRecylerView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mNewsRVAdapter = new NewsRVAdapter(getActivity(), mNewsList);
        mRecyclerView.setAdapter(mNewsRVAdapter);
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

    @OnClick({R.id.iv_search, R.id.iv_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                break;
            case R.id.iv_menu:
                initPopWindow();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (!result.isEmpty() && result.contains(Constants.REDPACKAGE)) {
                String base64 = result.substring(Constants.REDPACKAGE.length(), result.length());
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                String jsonresult = new String(bytes);
                Gson gson = new Gson();
                QrRedInfo qrRedInfo = gson.fromJson(jsonresult, QrRedInfo.class);
                UtilTool.Log("日志", qrRedInfo.getRedID());
                Intent intent = new Intent(getActivity(), GrabQRCodeRedActivity.class);
                intent.putExtra("id", qrRedInfo.getRedID());
                intent.putExtra("type", true);
                startActivity(intent);
            }
        }
    }

    private void initPopWindow() {

        int widthPixels = mDm.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.pop_news, null);

        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, (int) (getResources().getDimension(R.dimen.y400)), true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mPopupWindow.showAsDropDown(mLlMenu, (widthPixels - mPopupWindow.getWidth()), 0);

        popChildClick();
    }

    //给pop子控件设置点击事件
    private void popChildClick() {

        int childCount = mView.getChildCount();

        for (int i = 0; i < childCount; i++) {

            final View childAt = mView.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mView.indexOfChild(childAt);

                    switch (index) {
                        case 0:
                            if (!WsConnection.getInstance().getOutConnection()) {
                                if (!AuthorizationUserTools.isCameraCanUse(getActivity()))
                                    return;
                                Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                                intent.putExtra("code", QRCODE);
                                startActivityForResult(intent, 0);
                            } else {
                                startActivity(new Intent(getActivity(), InitialActivity.class));
                            }
                            mPopupWindow.dismiss();
                            break;
                        case 1:
                            if (!WsConnection.getInstance().getOutConnection()) {
                                startActivity(new Intent(getActivity(), ReceiptPaymentActivity.class));
                            } else {
                                startActivity(new Intent(getActivity(), InitialActivity.class));
                            }
                            mPopupWindow.dismiss();
                            break;
                        case 2:
                            if (!WsConnection.getInstance().getOutConnection()) {
                                startActivity(new Intent(getActivity(), SendQRCodeRedActivity.class));
                            } else {
                                startActivity(new Intent(getActivity(), InitialActivity.class));
                            }
                            mPopupWindow.dismiss();
                            break;
                        case 3:
                            if (!WsConnection.getInstance().getOutConnection()) {
                                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                            } else {
                                startActivity(new Intent(getActivity(), InitialActivity.class));
                            }
                            mPopupWindow.dismiss();
                            break;
                    }

                }
            });
        }
    }
}
