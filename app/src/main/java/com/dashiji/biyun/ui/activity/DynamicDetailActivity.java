package com.dashiji.biyun.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.ui.adapter.DynamicDetailRVAdapter;
import com.dashiji.biyun.utils.FullyLinearLayoutManager;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.enitity.ThumbViewInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/19.
 */

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //    初始化界面
    private void initInterface() {
        mImageList = getIntent().getStringArrayListExtra("imageList");//获取上个页面传递的数据

        if (mImageList != null) {//判断是否有数据，没有显示另一个状态

            mNglImages.setVisibility(View.VISIBLE);

            mDynamicText.setVisibility(View.GONE);

            mNglImages.setAdapter(mAdapter);

            mNglImages.setImagesData(mImageList);

            //九宫格图片填充数据
            mNglImages.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {

                    computeBoundsBackward(list);//组成数据
                    GPreviewBuilder.from((Activity) context)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(index)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();//启动

                }
            });

        } else {

            mNglImages.setVisibility(View.GONE);

            mDynamicText.setVisibility(View.VISIBLE);

        }

        //初始化列表
        initRecyclerView();

    }

    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(this));

        mRecyclerView.setAdapter(new DynamicDetailRVAdapter(this));

        mRecyclerView.setNestedScrollingEnabled(false);

    }

    boolean isZan = false;

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


                break;
            case R.id.ll_zan:

                isZan = !isZan;
                if (isZan)
                    mLlZan.setSelected(true);
                else
                    mLlZan.setSelected(false);
                break;
        }
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
