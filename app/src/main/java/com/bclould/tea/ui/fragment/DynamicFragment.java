package com.bclould.tea.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.activity.MainActivity;
import com.bclould.tea.ui.adapter.DynamicRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicFragment extends Fragment {
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.comment_et)
    EditText mCommentEt;
    @Bind(R.id.iv_selector_img)
    ImageView mIvSelectorImg;
    @Bind(R.id.send)
    TextView mSend;
    @Bind(R.id.rl_edit)
    RelativeLayout mRlEdit;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private DynamicPresenter mDynamicPresenter;
    private DynamicRVAdapter mDynamicRVAdapter;
    private DBManager mMgr;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage_id = 0;
    private int mPageSize = 10;
    public LinearLayoutManager mLinearLayoutManager;
    private MainActivity.MyOnTouchListener mTouchListener;
    private List<LocalMedia> selectList;
    private String mCommentId;
    private String mDynamicId;
    private boolean mType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dynamic_state, container, false);
        ButterKnife.bind(this, view);
        mDynamicPresenter = new DynamicPresenter(getContext());
        mMgr = new DBManager(getContext());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initListener();
        initData(PULL_DOWN);
        return view;
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.publish_dynamic))) {
            initData(PULL_DOWN);
        } else if (msg.equals(getString(R.string.delete_dynamic))) {
            String id = event.getId();
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == Integer.parseInt(id)) {
                    mDataList.remove(i);
                    mDynamicRVAdapter.notifyItemRemoved(i);
                    mDynamicRVAdapter.notifyItemRangeChanged(0, mDataList.size() - i);
                    return;
                }
            }
        } else if (msg.equals(getString(R.string.reward_succeed))) {
            showRewardSucceedDialog();
            int id = Integer.parseInt(event.getId());
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == id) {
                    mDataList.get(i).setRewardCount(mDataList.get(i).getRewardCount() + 1);
                    mDynamicRVAdapter.notifyItemChanged(i);
                    break;
                }
            }
        } else if (msg.equals(getString(R.string.shield_dy))) {
            initData(PULL_DOWN);
        } else if (msg.equals(getString(R.string.comment))) {
            mCommentId = event.getId();
            mDynamicId = event.getState();
            mType = event.isType();
            if (mType) {
                if (!event.getCoinName().equals(UtilTool.getUser())) {
                    mIvSelectorImg.setVisibility(View.GONE);
                    mCommentEt.setHint(getString(R.string.reply) + event.getCoinName());
                    mRlEdit.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    boolean isOpen = imm.isActive(mCommentEt);//isOpen若返回true，则表示输入法打开
                    if (!isOpen) {
                        mCommentEt.requestFocus();
                        imm.showSoftInput(mCommentEt, 0);
                    }
                } else {
                    showDeleteCommentDialog();
                }
            } else {
                mCommentEt.setHint(getString(R.string.et_comment));
                mIvSelectorImg.setVisibility(View.GONE);
                mRlEdit.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive(mCommentEt);//isOpen若返回true，则表示输入法打开
                if (!isOpen) {
                    mCommentEt.requestFocus();
                    imm.showSoftInput(mCommentEt, 0);
                }
            }
        } else if (msg.equals(getString(R.string.hide_keyboard))) {
            mRlEdit.setVisibility(View.GONE);
        }
    }

    private void showDeleteCommentDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, getContext(), R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.whether_delete_comment));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setText(getString(R.string.delete));
        confirm.setTextColor(getResources().getColor(R.color.red));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDynamicPresenter.deleteComment(Integer.parseInt(mCommentId), new DynamicPresenter.CallBack() {
                    @Override
                    public void send() {
                        for (int i = 0; i < mDataList.size(); i++) {
                            if (mDataList.get(i).getReviewList().size() != 0 && mDataList.get(i).getReview_count() != 0) {
                                if (mDataList.get(i).getId() == Integer.parseInt(mDynamicId)) {
                                    for (int j = 0; j < mDataList.get(i).getReviewList().size(); j++) {
                                        if (mDataList.get(i).getReviewList().get(j).getId() == Integer.parseInt(mCommentId)) {
                                            mDataList.get(i).getReviewList().remove(j);
                                            mDataList.get(i).setReview_count(mDataList.get(i).getReview_count() - 1);
                                            mDynamicRVAdapter.notifyItemChanged(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        deleteCacheDialog.dismiss();
                    }
                });
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void showRewardSucceedDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_reward, getContext(), R.style.dialog);
        deleteCacheDialog.show();
        new Handler() {
            public void handleMessage(Message msg) {
                deleteCacheDialog.dismiss();
            }
        }.sendEmptyMessageDelayed(0, 1500);
    }

    List<DynamicListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(final int type) {
        String userList = "";
        List<UserInfo> userInfos = mMgr.queryAllUser();
        for (int i = 0; i < userInfos.size(); i++) {
            if (i == 0) {
                userList = userInfos.get(i).getUser();
            } else {
                userList += "," + userInfos.get(i).getUser();
            }
        }
        if (type == PULL_DOWN) {
            mPage_id = 0;
        }
        isFinish = false;
        mDynamicPresenter.dynamicList(mPage_id, mPageSize, userList, new DynamicPresenter.CallBack2() {
            @Override
            public void send(List<DynamicListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (mRecyclerView != null) {
                        if (type == PULL_DOWN) {
                            mRefreshLayout.finishRefresh();
                        } else {
                            mRefreshLayout.finishLoadMore();
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || data.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                            if (type == PULL_DOWN) {
                                if (data.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    mLlNoData.setVisibility(View.VISIBLE);
                                    mLlError.setVisibility(View.GONE);
                                } else {
                                    mDataList.clear();
                                }
                            }
                            mDataList.addAll(data);
                            if (mDataList.size() != 0)
                                mPage_id = mDataList.get(mDataList.size() - 1).getId();
                            mDynamicRVAdapter.notifyDataSetChanged();
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            mLlNoData.setVisibility(View.VISIBLE);
                            mLlError.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (type == PULL_UP) {
                        mRefreshLayout.finishLoadMore();
                    } else {
                        mRefreshLayout.finishRefresh();
                    }
                    mRecyclerView.setVisibility(View.GONE);
                    mLlNoData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void finishRefresh() {
                if (type == PULL_UP) {
                    mRefreshLayout.finishLoadMore();
                } else {
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }


    boolean isFinish = true;

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mDynamicRVAdapter = new DynamicRVAdapter(getActivity(), mDataList, mDynamicPresenter);
        mRecyclerView.setAdapter(mDynamicRVAdapter);
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View view = mLinearLayoutManager.findViewByPosition(1);
                if (view != null) System.out.println(view.getMeasuredHeight());
            }
        });
        mDynamicRVAdapter.notifyDataSetChanged();
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive(mCommentEt);//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mCommentEt.getWindowToken(), 0);
                    mRlEdit.setVisibility(View.GONE);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    //拿到选择的图片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    try {
                        // 图片选择结果回调
                        selectList = PictureSelector.obtainMultipleResult(data);
                        if (selectList.size() != 0) {
                            mIvSelectorImg.setImageBitmap(BitmapFactory.decodeFile(selectList.get(0).getCompressPath()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.iv_selector_img, R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_selector_img:
                selectorImg();
                break;
            case R.id.send:
                if (mCommentEt.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.toast_comment), Toast.LENGTH_SHORT).show();
                } else {
                    if (mType) {
                        sendComment(mDynamicId, mCommentId, "", 0);
                    } else {
                        sendComment(mDynamicId, "0", "", 0);
                    }
                }
                break;
        }
    }

    private void selectorImg() {
        PictureSelector.create(this)
                .openGallery(ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                        .theme(R.style.picture_white_style)
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .openClickSound(true)// 是否开启点击声音 true or false
                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String key = (String) msg.obj;
                    sendComment(mDynamicId, "0", key, 1);
                    break;
                case 1:
                    Toast.makeText(getContext(), getString(R.string.comment_erorr), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private void upImg(String compressPath) {
        /*File file = new File(compressPath);
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + "compress" + UtilTool.getPostfix2(file.getName());
        //缩略图储存路径
        final File newFile = new File(Constants.PUBLICDIR + key);
        UtilTool.comp(BitmapFactory.decodeFile(compressPath), newFile);//压缩图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                            Constants.ACCESS_KEY_ID,
                            Constants.SECRET_ACCESS_KEY,
                            Constants.SESSION_TOKEN);
                    AmazonS3Client s3Client = new AmazonS3Client(
                            sessionCredentials);
                    Regions regions = Regions.fromName("ap-northeast-2");
                    Region region = Region.getRegion(regions);
                    s3Client.setRegion(region);
                    s3Client.addRequestHandler(new RequestHandler2() {
                        @Override
                        public void beforeRequest(Request<?> request) {

                        }

                        @Override
                        public void afterResponse(Request<?> request, Response<?> response) {
                            Message message = new Message();
                            message.obj = key;
                            message.what = 0;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void afterError(Request<?> request, Response<?> response, Exception e) {

                        }
                    });
                    PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, newFile);
                    s3Client.putObject(por);
                } catch (AmazonClientException e) {
                    mHandler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    private void sendComment(final String id, final String reply_id, String key, int key_type) {
        final String comment = mCommentEt.getText().toString();
        mDynamicPresenter.sendComment(id, comment, reply_id, key, key_type, new DynamicPresenter.CallBack5() {
            @Override
            public void send(List<DynamicListInfo.DataBean.ReviewListBean> data) {
                mCommentEt.setText("");
                mRlEdit.setVisibility(View.GONE);
                for (int i = 0; i < mDataList.size(); i++) {
                    if (mDataList.get(i).getId() == Integer.parseInt(mDynamicId)) {
                        mDataList.get(i).getReviewList().add(data.get(0));
                        mDataList.get(i).setReview_count(mDataList.get(i).getReview_count() + 1);
                        mDynamicRVAdapter.notifyItemChanged(i);
                        mDynamicRVAdapter.setLookCount(Integer.parseInt(mDynamicId));
                        break;
                    }
                }
            }
        });
    }
}
