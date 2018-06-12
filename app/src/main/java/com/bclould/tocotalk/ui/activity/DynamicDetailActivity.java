/*
package com.bclould.tocotalk.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.LikeInfo;
import com.bclould.tocotalk.model.ReviewListInfo;
import com.bclould.tocotalk.ui.adapter.DynamicDetailRVAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.previewlibrary.enitity.ThumbViewInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

*/
/**
 * Created by GA on 2017/10/19.
 *//*


@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicDetailActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_delete)
    TextView mTvDelete;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.time)
    TextView mTime;
    @Bind(R.id.dynamic_text)
    TextView mDynamicText;
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_who)
    TextView mTvWho;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.cv_guess)
    CardView mCvGuess;
    @Bind(R.id.ngl_images)
    NineGridImageView mNglImages;
    @Bind(R.id.iv_video)
    ImageView mIvVideo;
    @Bind(R.id.iv_video_play)
    ImageView mIvVideoPlay;
    @Bind(R.id.rl_video)
    RelativeLayout mRlVideo;
    @Bind(R.id.tv_like_count)
    TextView mTvLikeCount;
    @Bind(R.id.ll_zan)
    LinearLayout mLlZan;
    @Bind(R.id.dynamic_content)
    RelativeLayout mDynamicContent;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
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
    private String mContent;
    private DBManager mMgr;
    private DynamicPresenter mDynamicPresenter;
    private String mId;
    private DynamicDetailRVAdapter mDynamicDetailRVAdapter;
    private ReviewListInfo.DataBean.InfoBean mInfo;
    private int mIs_self;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String mReply_id;
    private boolean mType;
    private DisplayMetrics mDm;
    private int mWidthPixels;
    private int mHeightPixels;
    private int mGuessId;
    private int mPeriod_aty;
    private String mGuessPw;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        getPhoneSize();
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mMgr = new DBManager(this);
        mDynamicPresenter = new DynamicPresenter(this);
        initInterface();
        initData();
        initListener();
        MyApp.getInstance().addActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.reply_comment))) {
            mReply_id = event.getId();
            String name = event.getCoinName();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                mType = true;
                mIvSelectorImg.setVisibility(View.GONE);
                mCommentEt.requestFocus();
                imm.toggleSoftInput(0, 0);
                mCommentEt.setHint(getString(R.string.reply) + name);
            }
        }
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
        mDynamicPresenter.reviewList(mId, new DynamicPresenter.CallBack3() {
            @Override
            public void send(ReviewListInfo.DataBean data) {
                if (!DynamicDetailActivity.this.isDestroyed()) {
                    if (!data.getInfo().getAvatar().isEmpty()) {
                        try {
                            UtilTool.setCircleImg(DynamicDetailActivity.this, data.getInfo().getAvatar(), mTouxiang);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        UtilTool.setCircleImg(DynamicDetailActivity.this, R.mipmap.img_nfriend_headshot1, mTouxiang);
                    }
                    mInfo = data.getInfo();
                    if (data.getInfo().getIs_like() == 1) {
                        mLlZan.setSelected(true);
                    } else {
                        mLlZan.setSelected(false);
                    }
                    mDataList.addAll(data.getList());
                    mTvLikeCount.setText(data.getInfo().getLike_count() + "");
                    mDynamicDetailRVAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //    初始化界面
    private void initInterface() {
        Bundle bundle = getIntent().getExtras();
        int type = bundle.getInt("type");
        mId = bundle.getString("id");
        mUserName = bundle.getString("name");
        mTimes = bundle.getString("time");
        mContent = bundle.getString("content");
        mIs_self = bundle.getInt("is_self", 0);
        if (mIs_self == 1) {
            mTvDelete.setVisibility(View.VISIBLE);
        } else {
            mTvDelete.setVisibility(View.GONE);
        }
        mName.setText(mUserName);
        mTime.setText(mTimes);
        switch (type) {
            case 1:
                mDynamicText.setText(mContent);
                break;
            case 2:
                mDynamicText.setText(mContent);
                mNglImages.setVisibility(View.VISIBLE);
                mImageList = bundle.getStringArrayList("imageList");//获取上个页面传递的数据
                mCompressImgList = bundle.getStringArrayList("compressImgList");
                mNglImages.setVisibility(View.VISIBLE);
                mNglImages.setAdapter(mAdapter);
                mNglImages.setImagesData(mCompressImgList);
                //九宫格图片填充数据
                mNglImages.setItemImageClickListener(new ItemImageClickListener<String>() {
                    @Override
                    public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {

                        Intent intent = new Intent(DynamicDetailActivity.this, PreviewImgActivity.class);
                        intent.putExtra("index", index);
                        intent.putStringArrayListExtra("imgList", mCompressImgList);
                        context.startActivity(intent);
                    }
                });
                break;
            case 3:
                mRlVideo.setVisibility(View.VISIBLE);
                mDynamicText.setText(mContent);
                mImageList = bundle.getStringArrayList("imageList");//获取上个页面传递的数据
                mCompressImgList = bundle.getStringArrayList("compressImgList");
                Glide.with(this).load((mCompressImgList).get(0)).into(mIvVideo);
                break;
            case 4:
                mCvGuess.setVisibility(View.VISIBLE);
                UtilTool.Log("競猜分享", mContent);
                String[] split = mContent.split(Constants.GUESS_DYNAMIC_SEPARATOR);
                if (split.length == 7) {
                    mGuessPw = split[6];
                }
                mGuessId = Integer.parseInt(split[4]);
                mPeriod_aty = Integer.parseInt(split[5]);
                mDynamicText.setText(split[0]);
                mTvTitle.setText(split[1]);
                mTvWho.setText(getString(R.string.fa_qi_ren) + ":" + split[2]);
                mTvCoin.setText(split[3] + getString(R.string.guess));
                mCvGuess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mGuessPw != null) {
                            showPWDialog(mGuessPw, mGuessId, mPeriod_aty);
                        } else {
                            Intent intent = new Intent(DynamicDetailActivity.this, GuessDetailsActivity.class);
                            intent.putExtra("bet_id", mGuessId);
                            intent.putExtra("period_qty", mPeriod_aty);
                            startActivity(intent);
                        }
                    }
                });
                break;
        }
        //初始化列表
        initRecyclerView();
    }

    private void showPWDialog(final String guessPw, final int guessId, final int period_aty) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_command, DynamicDetailActivity.this, R.style.dialog);
        deleteCacheDialog.show();
        final EditText etGuessPw = (EditText) deleteCacheDialog.findViewById(R.id.et_guess_password);
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = etGuessPw.getText().toString();
                if (pw.isEmpty()) {
                    AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                    Toast.makeText(DynamicDetailActivity.this, getString(R.string.toast_guess_pw), Toast.LENGTH_SHORT).show();
                } else {
                    if (guessPw.equals(pw)) {
                        Intent intent = new Intent(DynamicDetailActivity.this, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", guessId);
                        intent.putExtra("period_qty", period_aty);
                        intent.putExtra("guess_pw", guessPw);
                        startActivity(intent);
                        deleteCacheDialog.dismiss();
                    } else {
                        AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                        Toast.makeText(DynamicDetailActivity.this, getString(R.string.toast_guess_pw_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    List<ReviewListInfo.DataBean.ListBean> mDataList = new ArrayList<>();

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDynamicDetailRVAdapter = new DynamicDetailRVAdapter(this, mDataList, mMgr, mDynamicPresenter);
        mRecyclerView.setAdapter(mDynamicDetailRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    //获取屏幕高度
    private void getPhoneSize() {
        mDm = new DisplayMetrics();
        if (this != null)
            getWindowManager().getDefaultDisplay().getMetrics(mDm);
        mHeightPixels = mDm.heightPixels;
        mWidthPixels = mDm.widthPixels;
    }

    //初始化pop
    private ViewGroup mView;
    private PopupWindow mPopupWindow;

    private void initPopWindow() {
        mView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_img, null);
        mPopupWindow = new PopupWindow(mView, 200, 200, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(false);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.9f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        int[] location = new int[2];
        mXx2.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(mXx2, Gravity.NO_GRAVITY, location[0] + mWidthPixels / 2, location[1] - mPopupWindow.getHeight() - 10);
        ImageView img = (ImageView) mView.findViewById(R.id.iv_image);
        img.setImageBitmap(BitmapFactory.decodeFile(selectList.get(0).getCompressPath()));
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectList.clear();
                mPopupWindow.dismiss();
            }
        });
    }


   */
/* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mPopupWindow!=null&&mPopupWindow.isShowing()){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }*//*


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String key = (String) msg.obj;
                    sendComment(mId, 0, key, 1);
                    break;
                case 1:
                    Toast.makeText(DynamicDetailActivity.this, getString(R.string.comment_erorr), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private void upImg(String compressPath) {
        File file = new File(compressPath);
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
        }).start();
    }

    @OnClick({R.id.rl_video, R.id.bark, R.id.touxiang, R.id.send, R.id.ll_zan, R.id.tv_delete, R.id.iv_selector_img, R.id.dynamic_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_video:
                Intent intent = new Intent(this, VideoActivity.class);
                intent.putExtra("url", mImageList.get(0));
                startActivity(intent);
                break;
            case R.id.dynamic_content:
                mCommentEt.setHint(getString(R.string.et_comment));
                mIvSelectorImg.setVisibility(View.VISIBLE);
                mType = false;
                break;
            case R.id.iv_selector_img:
                selectorImg();
                break;
            case R.id.touxiang:
                //跳转个人资料页面
//                startActivity(new Intent(this, FriendDataActivity.class));
                break;
            case R.id.send:
                if (selectList.size() == 0) {
                    if (mCommentEt.getText().toString().isEmpty()) {
                        Toast.makeText(this, getString(R.string.toast_comment), Toast.LENGTH_SHORT).show();
                    } else {
                        if (mType) {
                            sendComment(mId, Integer.parseInt(mReply_id), "", 0);
                        } else {
                            sendComment(mId, 0, "", 0);
                        }
                    }
                } else {
                    if (mCommentEt.getText().toString().isEmpty()) {
                        Toast.makeText(this, getString(R.string.toast_comment), Toast.LENGTH_SHORT).show();
                    } else {
                        upImg(selectList.get(0).getCompressPath());
                    }
                }
                break;
            case R.id.ll_zan:
                like();
                break;
            case R.id.tv_delete:
                showDialog();
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

    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.delete_dynamic_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setTextColor(getResources().getColor(R.color.red));
        confirm.setText(getString(R.string.delete));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                deleteDynamic();
            }
        });
    }

    private void deleteDynamic() {
        mDynamicPresenter.deleteDynamic(mId);
    }

    private void sendComment(final String id, final int reply_id, String key, int key_type) {
        final String comment = mCommentEt.getText().toString();
        mDynamicPresenter.sendComment(id, comment, reply_id, key, key_type, new DynamicPresenter.CallBack5() {
            @Override
            public void send(List<ReviewListInfo.DataBean.ListBean> data) {
                mCommentEt.setText("");
                selectList.clear();
                if (mType) {
                    for (int i = 0; i < mDataList.size(); i++) {
                        if (mDataList.get(i).getId() == reply_id) {
                            ReviewListInfo.DataBean.ListBean.ReplyListsBean replyListsBean = new ReviewListInfo.DataBean.ListBean.ReplyListsBean();
                            replyListsBean.setContent(data.get(0).getContent());
                            replyListsBean.setUser_name(data.get(0).getUser_name());
                            if (mDataList.get(i).getReply_lists() == null) {
                                List<ReviewListInfo.DataBean.ListBean.ReplyListsBean> list = new ArrayList<>();
                                list.add(replyListsBean);
                                mDataList.get(i).setReply_lists(list);
                            } else {
                                mDataList.get(i).getReply_lists().add(0, replyListsBean);
                            }
                            mDynamicDetailRVAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } else {
                    mDataList.add(0, data.get(0));
                    mDynamicDetailRVAdapter.notifyItemInserted(0);
                    mDynamicDetailRVAdapter.notifyItemRangeChanged(0, mDataList.size() - 0);
                    //发送消息通知
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.publish_comment));
                    messageEvent.setReviewCount(mDataList.size() + "");
                    messageEvent.setId(mId);
                    messageEvent.setCoinName(UtilTool.getUser());
                    messageEvent.setFiltrate(comment);
                    EventBus.getDefault().post(messageEvent);
                }
            }
        });
    }

    private void like() {
        mDynamicPresenter.like(mId, new DynamicPresenter.CallBack4() {
            @Override
            public void send(LikeInfo data) {
                mTvLikeCount.setText(data.getData().getLikeCounts() + "");
                if (data.getData().getStatus() == 1) {
                    mLlZan.setSelected(true);
                } else {
                    mLlZan.setSelected(false);
                }
                //发送消息通知
                MessageEvent messageEvent = new MessageEvent(getString(R.string.zan));
                messageEvent.setLikeCount(data.getData().getLikeCounts() + "");
                messageEvent.setId(mId);
                messageEvent.setType(data.getData().getStatus() == 1 ? true : false);
                EventBus.getDefault().post(messageEvent);
            }
        });
    }

    */
/**
     * 查找信息
     *
     * @param list 图片集合
     *//*

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}*/
