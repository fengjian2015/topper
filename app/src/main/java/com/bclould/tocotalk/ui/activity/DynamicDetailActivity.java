package com.bclould.tocotalk.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.LikeInfo;
import com.bclould.tocotalk.model.ReviewListInfo;
import com.bclould.tocotalk.ui.adapter.DynamicDetailRVAdapter;
import com.bclould.tocotalk.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.enitity.ThumbViewInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicDetailActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.time_tv)
    ImageView mTimeTv;
    @Bind(R.id.time)
    TextView mTime;
    @Bind(R.id.dynamic_text)
    TextView mDynamicText;
    @Bind(R.id.dynamic_content)
    RelativeLayout mDynamicContent;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.comment_et)
    EditText mCommentEt;
    @Bind(R.id.rl_edit)
    RelativeLayout mRlEdit;
    @Bind(R.id.send)
    TextView mSend;
    @Bind(R.id.ll_zan)
    LinearLayout mLlZan;
    @Bind(R.id.ngl_images)
    NineGridImageView mNglImages;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.tv_like_count)
    TextView mTvLikeCount;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;

    private ArrayList<ThumbViewInfo> mThumbViewInfoList = new ArrayList<>();
    private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
        @Override
        protected void onDisplayImage(Context context, ImageView imageView, String s) {
            Glide.with(context).load(s).apply(new RequestOptions().placeholder(R.mipmap.ic_empty_photo)).into(imageView);
        }

        @Override
        protected ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }

        @Override
        protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
        }
    };
    private ArrayList<String> mImageList;
    private ArrayList<String> mCompressImgList;
    private String mUserName;
    private String mTimes;
    private String mLike;
    private String mContent;
    private DBManager mMgr;
    private DynamicPresenter mDynamicPresenter;
    private String mId;
    private DynamicDetailRVAdapter mDynamicDetailRVAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        mDynamicPresenter = new DynamicPresenter(this);
        initInterface();
        initData();
        initListener();
        MyApp.getInstance().addActivity(this);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData();
            }
        });
    }

    private void initData() {
        mDataList.clear();
        mData.clear();
        mDynamicPresenter.reviewList(mId, new DynamicPresenter.CallBack3() {
            @Override
            public void send(ReviewListInfo.DataBean data) {
                if(data.getInfo().getIs_like() == 1){
                    mLlZan.setSelected(true);
                }else {
                    mLlZan.setSelected(false);
                }
                mDataList.addAll(data.getList());
                mData.add(data);
                mDynamicDetailRVAdapter.notifyDataSetChanged();
            }
        });
    }

    //    初始化界面
    private void initInterface() {
        Intent intent = getIntent();
        mImageList = intent.getStringArrayListExtra("imageList");//获取上个页面传递的数据
        mCompressImgList = intent.getStringArrayListExtra("compressImgList");
        mUserName = intent.getStringExtra("name");
        mTimes = intent.getStringExtra("time");
        mLike = intent.getStringExtra("like");
        mContent = intent.getStringExtra("content");
        mId = intent.getStringExtra("id");
        mName.setText(mUserName);
        mTime.setText(mTimes);
        mDynamicText.setText(mContent);
        mTvLikeCount.setText(mLike);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(mMgr.queryUser(mUserName + "@" + Constants.DOMAINNAME).get(0).getPath());
            mTouxiang.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCompressImgList.size() != 0) {//判断是否有数据，没有显示另一个状态
            mNglImages.setAdapter(mAdapter);
            mNglImages.setImagesData(mCompressImgList);
            //九宫格图片填充数据
            mNglImages.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
                    computeBoundsBackward(mImageList);//组成数据
                    GPreviewBuilder.from((Activity) context)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(index)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();//启动
                }
            });

        }
        //初始化列表
        initRecyclerView();
    }

    List<ReviewListInfo.DataBean.ListBean> mDataList = new ArrayList<>();
    List<ReviewListInfo.DataBean> mData = new ArrayList<>();

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDynamicDetailRVAdapter = new DynamicDetailRVAdapter(this, mDataList, mMgr, mDynamicPresenter);
        mRecyclerView.setAdapter(mDynamicDetailRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @OnClick({R.id.bark, R.id.touxiang, R.id.send, R.id.ll_zan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.touxiang:
                //跳转个人资料页面
                startActivity(new Intent(this, FriendDataActivity.class));
                break;
            case R.id.send:
                if (mCommentEt.getText().toString().isEmpty()) {
                    Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    sendComment();
                }
                break;
            case R.id.ll_zan:
                like();
                break;
        }
    }

    private void sendComment() {
        String comment = mCommentEt.getText().toString();
        mDynamicPresenter.sendComment(mId, comment, new DynamicPresenter.CallBack5() {
            @Override
            public void send(List<ReviewListInfo.DataBean.ListBean> data) {
                mCommentEt.setText("");
                mDataList.add(0, data.get(0));
                mDynamicDetailRVAdapter.notifyItemInserted(0);
                mDynamicDetailRVAdapter.notifyItemRangeChanged(0, mDataList.size() - 0);
            }
        });
    }

    private void like() {
        mDynamicPresenter.like(mId, new DynamicPresenter.CallBack4() {
            @Override
            public void send(LikeInfo data) {
                mTvLikeCount.setText(data.getLikeCounts() + "");
                if (data.getStatus() == 1) {
                    mLlZan.setSelected(true);
                } else {
                    mLlZan.setSelected(false);
                }
            }
        });
    }

    /**
     * 查找信息
     *
     * @param list 图片集合
     */
    private void computeBoundsBackward(List<String> list) {
        ThumbViewInfo item;
        mThumbViewInfoList.clear();
        for (int i = 0; i < mNglImages.getChildCount(); i++) {
            View itemView = mNglImages.getChildAt(i);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView thumbView = (ImageView) itemView;
                thumbView.getGlobalVisibleRect(bounds);
            }
            item = new ThumbViewInfo(list.get(i));
            item.setBounds(bounds);
            mThumbViewInfoList.add(item);
        }
    }
}
