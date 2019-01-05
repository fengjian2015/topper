package com.bclould.tea.ui.activity.my.dynamic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by fengjian on 2018/12/29.
 */

public class DynamicPresenter implements DynamicContacts.Presenter{
    private DynamicContacts.View mView;
    private Activity mActivity;

    private com.bclould.tea.Presenter.DynamicPresenter mDynamicPresenter;
    private DynamicRVAdapter mDynamicRVAdapter;
    private DBManager mMgr;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage_id = 0;
    private int mPageSize = 10;

    private MainActivity.MyOnTouchListener mTouchListener;
    private List<LocalMedia> selectList;
    private String mCommentId;
    private String mDynamicId;
    private boolean mType;
    boolean isFinish = true;
    boolean isDblclick = false;
    List<DynamicListInfo.DataBean> mDataList = new ArrayList<>();

    @Override
    public void bindView(BaseView view) {
        mView= (DynamicContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        init();
        mView.initView();
        initData(PULL_DOWN);
    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(this);
    }

    private void init(){
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mDynamicPresenter = new com.bclould.tea.Presenter.DynamicPresenter(mActivity);
        mMgr = new DBManager(mActivity);
        mDynamicRVAdapter = new DynamicRVAdapter(mActivity, mDataList, mDynamicPresenter);
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(mActivity.getResources().getString(R.string.publish_dynamic))) {
            initData(PULL_DOWN);
        } else if (msg.equals(mActivity.getResources().getString(R.string.delete_dynamic))) {
            String id = event.getId();
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == Integer.parseInt(id)) {
                    mDataList.remove(i);
                    mDynamicRVAdapter.notifyItemRemoved(i);
                    mDynamicRVAdapter.notifyItemRangeChanged(0, mDataList.size() - i);
                    return;
                }
            }
        } else if (msg.equals(mActivity.getResources().getString(R.string.reward_succeed))) {
            showRewardSucceedDialog();
            int id = Integer.parseInt(event.getId());
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == id) {
                    mDataList.get(i).setRewardCount(mDataList.get(i).getRewardCount() + 1);
                    mDynamicRVAdapter.notifyItemChanged(i);
                    break;
                }
            }
        } else if (msg.equals(mActivity.getResources().getString(R.string.shield_dy))) {
            initData(PULL_DOWN);
        } else if (msg.equals(mActivity.getResources().getString(R.string.comment))) {
            mCommentId = event.getId();
            mDynamicId = event.getState();
            mType = event.isType();
            if (mType) {
                if (!event.getCoinName().equals(UtilTool.getUser())) {
                    mView.setIvSelectorImgIsShow(false);
                    mView.setCommentEtHint(mActivity.getResources().getString(R.string.reply) + event.getCoinName());
                    mView.setRlEditVisibility(true);
                    mView.isActive();
                } else {
                    showDeleteCommentDialog();
                }
            } else {
                mView.setCommentEtHint(mActivity.getResources().getString(R.string.et_comment));
                mView.setIvSelectorImgIsShow(false);
                mView.setRlEditVisibility(true);
                mView.isActive();
            }
        } else if (msg.equals(mActivity.getResources().getString(R.string.hide_keyboard))) {
            mView.setRlEditVisibility(false);
        } else if (msg.equals(mActivity.getResources().getString(R.string.start_service))) {
            mView.setRlPushDynamicStatusIsShow(true);
        } else if (msg.equals(mActivity.getResources().getString(R.string.destroy_service))) {
            mView.setRlPushDynamicStatusIsShow(false);
        }
    }

    private void showDeleteCommentDialog() {
        if(ActivityUtil.isActivityOnTop(mActivity)) {
            final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mActivity, R.style.dialog);
            deleteCacheDialog.show();
            deleteCacheDialog.setTitle(mActivity.getResources().getString(R.string.whether_delete_comment));
            Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCacheDialog.dismiss();
                }
            });
            Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
            confirm.setText(mActivity.getResources().getString(R.string.delete));
            confirm.setTextColor(mActivity.getResources().getColor(R.color.red));
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDynamicPresenter.deleteComment(Integer.parseInt(mCommentId), new com.bclould.tea.Presenter.DynamicPresenter.CallBack() {
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
    }

    private void showRewardSucceedDialog() {
        if(ActivityUtil.isActivityOnTop(mActivity)){
            final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_reward, mActivity, R.style.dialog);
            deleteCacheDialog.show();
            new Handler() {
                public void handleMessage(Message msg) {
                    deleteCacheDialog.dismiss();
                }
            }.sendEmptyMessageDelayed(0, 1500);
        }
    }



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
        mDynamicPresenter.dynamicList(mPage_id, mPageSize, userList, new com.bclould.tea.Presenter.DynamicPresenter.CallBack2() {
            @Override
            public void send(List<DynamicListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(mActivity)) {
                    if (!mView.isRecyclerViewNull()) {
                        if (type == PULL_DOWN) {
                            mView.setfinishLoadOrRefresh(true);
                        } else {
                            mView.setfinishLoadOrRefresh(false);
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || data.size() != 0) {
                            mView.setRecyclerViewVisibility(true);
                            mView.setLlNoDataVisibility(false);
                            mView.setLlErrorVisibility(false);
                            if (type == PULL_DOWN) {
                                if (data.size() == 0) {
                                    mView.setRecyclerViewVisibility(false);
                                    mView.setLlNoDataVisibility(true);
                                    mView.setLlErrorVisibility(false);

                                } else {
                                    mDataList.clear();
                                }
                            }
                            mDataList.addAll(data);
                            if (mDataList.size() != 0)
                                mPage_id = mDataList.get(mDataList.size() - 1).getId();
                            mDynamicRVAdapter.notifyDataSetChanged();
                        } else {
                            mView.setRecyclerViewVisibility(false);
                            mView.setLlNoDataVisibility(true);
                            mView.setLlErrorVisibility(false);
                        }
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(mActivity)) {
                    isFinish = true;
                    if (type == PULL_UP) {
                        mView.setfinishLoadOrRefresh(false);
                    } else {
                        mView.setfinishLoadOrRefresh(true);
                    }
                    mView.setRecyclerViewVisibility(false);
                    mView.setLlNoDataVisibility(false);
                    mView.setLlErrorVisibility(true);
                }
            }

            @Override
            public void finishRefresh() {
                if (ActivityUtil.isActivityOnTop(mActivity)) {
                    isFinish = true;
                    if (type == PULL_UP) {
                        mView.setfinishLoadOrRefresh(false);
                    } else {
                        mView.setfinishLoadOrRefresh(true);
                    }
                }
            }
        });
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
                    Toast.makeText(mActivity, mActivity.getString(R.string.comment_erorr), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private void sendComment(final String id, final String reply_id, String key, int key_type) {
        final String comment = mView.getCommentEt();
        mDynamicPresenter.sendComment(id, comment, reply_id, key, key_type, new com.bclould.tea.Presenter.DynamicPresenter.CallBack5() {
            @Override
            public void send(List<DynamicListInfo.DataBean.ReviewListBean> data) {
                mView.setCommentEt("");
                mView.setRlEditVisibility(false);
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



    @Override
    public void selectorImg() {
        PictureSelector.create(mActivity)
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


    @Override
    public void dblclick() {
        Timer tExit = null;
        if (isDblclick == false) {
            isDblclick = true; // 准备退出
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isDblclick = false; // 取消退出
                }
            }, 1000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            mView.setRecyclerViewPosition(0);
            initData(PULL_DOWN);
        }
    }

    @Override
    public void comment() {
        if (mType) {
            sendComment(mDynamicId, mCommentId, "", 0);
        } else {
            sendComment(mDynamicId, "0", "", 0);
        }
    }

    @Override
    public DynamicRVAdapter getAdapter() {
        return mDynamicRVAdapter;
    }

    @Override
    public void setOnRefreshListener() {
        if (isFinish)
            initData(PULL_DOWN);
    }

    @Override
    public void setOnLoadMoreListener() {
        if (isFinish) {
            initData(PULL_UP);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    try {
                        // 图片选择结果回调
                        selectList = PictureSelector.obtainMultipleResult(data);
                        if (selectList.size() != 0) {
                            mView.setIvSelectorImg(BitmapFactory.decodeFile(selectList.get(0).getCompressPath()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
