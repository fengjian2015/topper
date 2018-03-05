package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.bclould.tocotalk.Presenter.DillDataPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AwsInfo;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.VoiceInfo;
import com.bclould.tocotalk.ui.adapter.ExampleAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadMoreListView;
import com.bclould.tocotalk.ui.widget.SimpleAppsGridView;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.RecordUtil;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.sj.emoji.DefEmoticons;
import com.sj.emoji.EmojiBean;
import com.sj.emoji.EmojiDisplay;
import com.sj.emoji.EmojiSpan;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jxmpp.jid.impl.JidCreate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sj.keyboard.XhsEmoticonsKeyBoard;
import sj.keyboard.adpater.EmoticonsAdapter;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.EmoticonPageEntity;
import sj.keyboard.data.EmoticonPageSetEntity;
import sj.keyboard.data.PageSetEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.interfaces.EmoticonDisplayListener;
import sj.keyboard.interfaces.EmoticonFilter;
import sj.keyboard.interfaces.PageViewInstantiateListener;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.EmoticonPageView;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;
import sj.keyboard.widget.RecordIndicator;

import static com.bclould.tocotalk.R.style.BottomDialog;
import static com.bclould.tocotalk.ui.fragment.ConversationFragment.TEXTMESSAGE;
import static com.bclould.tocotalk.ui.fragment.ConversationFragment.VOICEMESSAGE;
import static com.bclould.tocotalk.utils.Constants.ACCESS_KEY_ID;
import static com.bclould.tocotalk.utils.Constants.SECRET_ACCESS_KEY;
import static com.bclould.tocotalk.utils.Constants.SESSION_TOKEN;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationActivity extends AppCompatActivity implements FuncLayout.OnFuncKeyBoardListener {

    private static final int CODE_TAKE_PHOTO = 1;
    private static final int FILE_SELECT_CODE = 2;
    public static final String ACCESSKEYID = "access_key_id";
    public static final String SECRETACCESSKEY = "secret_access_key";
    public static final String SESSIONTOKEN = "session_token";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title_name)
    TextView mTitleName;
    @Bind(R.id.iv_else)
    ImageView mIvElse;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.lv_message)
    LoadMoreListView mLvMessage;
    @Bind(R.id.ekb_emoticons_keyboard)
    XhsEmoticonsKeyBoard mEkbEmoticonsKeyboard;
    @Bind(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @Bind(R.id.tv_pay_type)
    TextView mTvPayType;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_order_intro)
    RelativeLayout mRlOrderIntro;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_quota)
    TextView mTvQuota;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.tv_seller)
    TextView mTvSeller;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.ll_order_details)
    LinearLayout mLlOrderDetails;
    @Bind(R.id.ll_details)
    LinearLayout mLlDetails;
    @Bind(R.id.btn_look_order)
    Button mBtnLookOrder;
    @Bind(R.id.btn_cancel_order2)
    Button mBtnCancelOrder2;
    @Bind(R.id.btn_confirm_send_coin)
    Button mBtnConfirmSendCoin;
    @Bind(R.id.ll_seller)
    LinearLayout mLlSeller;
    @Bind(R.id.btn_cancel_order)
    Button mBtnCancelOrder;
    @Bind(R.id.btn_confirm_pay)
    Button mBtnConfirmPay;
    @Bind(R.id.ll_buyer)
    LinearLayout mLlBuyer;
    @Bind(R.id.rl_operability)
    RelativeLayout mRlOperability;
    @Bind(R.id.ll_order)
    LinearLayout mLlOrder;
    @Bind(R.id.iv_jiantou)
    ImageView mIvJiantou;
    @Bind(R.id.ll_chat)
    LinearLayout mLlChat;
    @Bind(R.id.rl_outer)
    RelativeLayout mRlOuter;
    private ExampleAdapter mExampleAdapter;
    private String mUser;
    private String mName;
    private List<MessageInfo> mMessageList = new ArrayList<>();
    private DBManager mMgr;
    private Dialog mBottomDialog;
    private Bitmap mUserImage;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String mType;
    private RecordIndicator recordIndicator;
    private RecordUtil recordUtil;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);//开始获取数据库数据
        EventBus.getDefault().register(this);
        initIntent();
        initEmoticonsKeyboard();
        MyApp.getInstance().addActivity(this);
        initAdapter();
        initData();
        mMgr.updateNumber(mUser, 0);
        EventBus.getDefault().post(new MessageEvent("处理未读消息"));
        mLvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mEkbEmoticonsKeyboard.getEtChat().getWindowToken(), 0);
                }
                return false;
            }
        });
    }


    //初始化表情盘
    private void initEmoticonsKeyboard() {
        SimpleAppsGridView simpleAppsGridView = new SimpleAppsGridView(this);
        simpleAppsGridView.setData(mUser);
        mEkbEmoticonsKeyboard.addFuncView(simpleAppsGridView);
        mEkbEmoticonsKeyboard.addOnFuncKeyBoardListener(this);
        mEkbEmoticonsKeyboard.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });

        ArrayList<EmojiBean> emojiArray = new ArrayList<>();
        Collections.addAll(emojiArray, DefEmoticons.getDefEmojiArray());
        // emoticon click
        final EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    int action = KeyEvent.ACTION_DOWN;
                    int code = KeyEvent.KEYCODE_DEL;
                    KeyEvent event = new KeyEvent(action, code);
                    mEkbEmoticonsKeyboard.getEtChat().onKeyDown(KeyEvent.KEYCODE_DEL, event);
                } else {
                    if (o == null) {
                        return;
                    }
                    String content = null;
                    if (o instanceof EmojiBean) {
                        content = ((EmojiBean) o).emoji;
                    }
                    int index = mEkbEmoticonsKeyboard.getEtChat().getSelectionStart();
                    Editable editable = mEkbEmoticonsKeyboard.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        };

        // emoticon instantiate
        final EmoticonDisplayListener emoticonDisplayListener = new EmoticonDisplayListener() {
            @Override
            public void onBindView(int i, ViewGroup viewGroup, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
                final EmojiBean emojiBean = (EmojiBean) object;
                if (emojiBean == null && !isDelBtn) {
                    return;
                }

                viewHolder.ly_root.setBackgroundResource(com.keyboard.view.R.drawable.bg_emoticon);

                if (isDelBtn) {
                    viewHolder.iv_emoticon.setImageResource(R.mipmap.keyboard_delete_img);
                } else {
                    viewHolder.iv_emoticon.setImageResource(emojiBean.icon);
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emoticonClickListener.onEmoticonClick(emojiBean, 0, isDelBtn);
                    }
                });
            }
        };

        //  page instantiate
        PageViewInstantiateListener pageViewInstantiateListener = new PageViewInstantiateListener<EmoticonPageEntity>() {
            @Override
            public View instantiateItem(ViewGroup viewGroup, int i, EmoticonPageEntity pageEntity) {
                if (pageEntity.getRootView() == null) {
                    EmoticonPageView pageView = new EmoticonPageView(viewGroup.getContext());
                    pageView.setNumColumns(pageEntity.getRow());
                    pageEntity.setRootView(pageView);
                    try {
                        EmoticonsAdapter adapter = new EmoticonsAdapter(viewGroup.getContext(), pageEntity, null);
                        // emoticon instantiate
                        adapter.setOnDisPlayListener(emoticonDisplayListener);
                        pageView.getEmoticonsGridView().setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return pageEntity.getRootView();
            }
        };

        // build
        PageSetEntity xhsPageSetEntity = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(emojiArray)
                .setIPageViewInstantiateItem(pageViewInstantiateListener)
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(R.mipmap.smile)
                .build();

        PageSetAdapter pageSetAdapter = new PageSetAdapter();
        pageSetAdapter.add(xhsPageSetEntity);
        mEkbEmoticonsKeyboard.setAdapter(pageSetAdapter);


        class EmojiFilter extends EmoticonFilter {

            private int emojiSize = -1;

            @Override
            public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
                emojiSize = emojiSize == -1 ? EmoticonsKeyboardUtils.getFontHeight(editText) : emojiSize;
                clearSpan(editText.getText(), start, text.toString().length());
                Matcher m = EmojiDisplay.getMatcher(text.toString().substring(start, text.toString().length()));
                if (m != null) {
                    while (m.find()) {
                        String emojiHex = Integer.toHexString(Character.codePointAt(m.group(), 0));
                        Drawable drawable = getDrawable(editText.getContext(), EmojiDisplay.HEAD_NAME + emojiHex);
                        if (drawable != null) {
                            int itemHeight;
                            int itemWidth;
                            if (emojiSize == EmojiDisplay.WRAP_DRAWABLE) {
                                itemHeight = drawable.getIntrinsicHeight();
                                itemWidth = drawable.getIntrinsicWidth();
                            } else {
                                itemHeight = emojiSize;
                                itemWidth = emojiSize;
                            }

                            drawable.setBounds(0, 0, itemHeight, itemWidth);
                            EmojiSpan imageSpan = new EmojiSpan(drawable);
                            editText.getText().setSpan(imageSpan, start + m.start(), start + m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }

            private void clearSpan(Spannable spannable, int start, int end) {
                if (start == end) {
                    return;
                }
                EmojiSpan[] oldSpans = spannable.getSpans(start, end, EmojiSpan.class);
                for (int i = 0; i < oldSpans.length; i++) {
                    spannable.removeSpan(oldSpans[i]);
                }
            }
        }
        // add a filter
        mEkbEmoticonsKeyboard.getEtChat().addEmoticonFilter(new EmojiFilter());

        mEkbEmoticonsKeyboard.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(mEkbEmoticonsKeyboard.getEtChat().getText().toString());
                mEkbEmoticonsKeyboard.getEtChat().setText("");
            }
        });

        recordIndicator = new RecordIndicator(this);
        mEkbEmoticonsKeyboard.setRecordIndicator(recordIndicator);
        try {
            recordIndicator.setOnRecordListener(new RecordIndicator.OnRecordListener() {
                @Override
                public void recordStart() {
                    startRecord();
                }

                @Override
                public void recordFinish() {
                    finishRecord();
                }

                @Override
                public void recordCancel() {
                    cancelRecord();
                }

                @Override
                public long getRecordTime() {
                    if (null == recordUtil) {
                        return 0;
                    }
                    return recordUtil.getVoiceDuration() * 1000;
                }

                @Override
                public int getRecordDecibel() {
                    if (null == recordUtil) {
                        return 0;
                    }
                    return recordUtil.getRecordDecibel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecord() {
        if (null == recordUtil) {
            recordUtil = new RecordUtil(this);
        }
        recordUtil.start();
    }

    private void finishRecord() {
        recordUtil.finish();
        sendVoice();
    }

    private void sendVoice() {
        int duration = recordUtil.getVoiceDuration();
        String fileName = recordUtil.getFileName();
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
            VoiceInfo voiceInfo = new VoiceInfo();
            byte[] bytes = UtilTool.readStream(fileName);
            String base64 = Base64.encodeToString(bytes);
            UtilTool.Log("日志", base64);
            voiceInfo.setElementText(base64);
            message.setBody("[audio]:" + duration + "秒");
            message.addExtension(voiceInfo);
            chat.sendMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage(message.getBody());
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(VOICEMESSAGE);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            mMgr.addMessage(messageInfo);
            mMessageList.add(messageInfo);
            mExampleAdapter.notifyDataSetChanged();
            scrollToBottom();
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, "[语音]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage("[语音]");
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent("自己发了消息"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "发送失败，请检查当前网络连接", Toast.LENGTH_SHORT).show();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage("[audio]:" + duration + "秒");
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(VOICEMESSAGE);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSendStatus(1);
            mMgr.addMessage(messageInfo);
            mMessageList.add(messageInfo);
            mExampleAdapter.notifyDataSetChanged();
            scrollToBottom();
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, "[语音]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage("[语音]");
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent("自己发了消息"));
        }

    }

    private void cancelRecord() {
        recordUtil.cancel();
        Toast.makeText(this, "取消录音", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals("消息数据库更新")) {
            initData();
            mMgr.updateNumber(mUser, 0);
            EventBus.getDefault().post(new MessageEvent("处理未读消息"));
        } else if (msg.equals("发红包了")) {
            initData();
        } else if (msg.equals("打开相册")) {
            selectorImages();
        } else if (msg.equals("打开相机")) {
            photograph();
        } else if (msg.equals("打开文件管理")) {
            showFileChooser();
        }
    }

    //选择图片
    private void selectorImages() {

        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                    .theme(R.style.picture_white_style)
                .maxSelectNum(9)// 最大图片 选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true) // 是否可播放音频
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(200, 200)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
                .selectionMedia(selectList)// 是否传入已选图片
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    //调用相机
    private void photograph() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 启动相机
        startActivityForResult(intent, CODE_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_TAKE_PHOTO) {

            } else if (requestCode == FILE_SELECT_CODE) {

            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() != 0) {
                    DillDataPresenter dillDataPresenter = new DillDataPresenter(this);
                    dillDataPresenter.getSessionToken(new DillDataPresenter.CallBack3() {
                        @Override
                        public void send(AwsInfo.DataBean data) {
                            MySharedPreferences.getInstance().setString(ACCESSKEYID, data.getAccessKeyId());
                            MySharedPreferences.getInstance().setString(SECRETACCESSKEY, data.getSecretAccessKey());
                            MySharedPreferences.getInstance().setString(SESSIONTOKEN, data.getSessionToken());
                        }
                    });
                    for (int i = 0; i < selectList.size(); i++) {
                        Upload(selectList.get(i).getPath());
                    }
                }
            }
        }
    }

    public void Upload(final String path) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                            ACCESS_KEY_ID,
                            SECRET_ACCESS_KEY,
                            SESSION_TOKEN);

                    AmazonS3Client s3Client = new AmazonS3Client(
                            sessionCredentials);
                    TransferManager manager = new TransferManager(s3Client);
                    PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, UtilTool.createtFileName() + ".png", path);
                    manager.upload(por);
                } catch (SdkClientException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //调用文件选择软件来选择文件
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mLvMessage.onRefreshComplete();
                List<MessageInfo> messageInfos = mMgr.pagingQueryMessage(mUser);
                List<MessageInfo> MessageList2 = new ArrayList<MessageInfo>();
                MessageList2.addAll(messageInfos);
                MessageList2.addAll(mMessageList);
                mMessageList.removeAll(mMessageList);
                mMessageList.addAll(MessageList2);
                mExampleAdapter.notifyDataSetChanged();
                mLvMessage.setSelection(4);
            }
        }
    };

    private void initData() {
        List<MessageInfo> messageInfos = mMgr.queryMessage(mUser);
        mMessageList.removeAll(mMessageList);
        mMessageList.addAll(messageInfos);
        mExampleAdapter.notifyDataSetChanged();
        mLvMessage.setSelection(mExampleAdapter.getCount());
    }

    //设置title
    private void initIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Drawable drawable = getDrawable(R.mipmap.img_nfriend_headshot1);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            mUserImage = bd.getBitmap();
            mName = "tester_001";
            mUser = Constants.MYUSER;
        } else {
            mName = bundle.getString("name");
            mUser = bundle.getString("user");
            byte[] images = bundle.getByteArray("image");
            if (images == null) {
                mUserImage = UtilTool.setDefaultimage(this);
            } else {
                mUserImage = BitmapFactory.decodeByteArray(images, 0, images.length);
            }
        }
        mType = intent.getStringExtra("type");
        if (mType != null)
            mLlOrder.setVisibility(View.VISIBLE);
        else
            mLlOrder.setVisibility(View.GONE);
        mTitleName.setText(mName);
    }

    private void initAdapter() {
        mExampleAdapter = new ExampleAdapter(this, mMessageList, mUserImage, mUser, mMgr, mediaPlayer);
        mLvMessage.setAdapter(mExampleAdapter);
        mLvMessage.setonRefreshListener(new LoadMoreListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });
        mLvMessage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        mEkbEmoticonsKeyboard.reset();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void showDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_conversation, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        dialogClick();
    }


    private void dialogClick() {
        LinearLayout help = (LinearLayout) mBottomDialog.findViewById(R.id.ll_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        LinearLayout groupChat = (LinearLayout) mBottomDialog.findViewById(R.id.ll_group_chat);
        groupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        LinearLayout delete = (LinearLayout) mBottomDialog.findViewById(R.id.ll_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
                mBottomDialog.dismiss();

            }
        });
        LinearLayout complain = (LinearLayout) mBottomDialog.findViewById(R.id.ll_complain);
        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
    }

    private void showDeleteDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle("确定要删除 " + mName + " 吗？");
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                    RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(mUser));
                    roster.removeEntry(entry);
                    Toast.makeText(ConversationActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    mMgr.deleteConversation(mUser);
                    mMgr.deleteMessage(mUser);
                    mMgr.deleteUser(mUser);
                    EventBus.getDefault().post(new MessageEvent("删除好友"));
                    deleteCacheDialog.dismiss();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilTool.Log("fsdafa", e.getMessage());
                }
            }
        });
    }


    private void sendMessage(String message) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(message);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TEXTMESSAGE);
            mMgr.addMessage(messageInfo);
            mMessageList.add(messageInfo);
            mExampleAdapter.notifyDataSetChanged();
            scrollToBottom();
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(message);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent("自己发了消息"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "发送失败，请检查当前网络连接", Toast.LENGTH_SHORT).show();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TEXTMESSAGE);
            messageInfo.setSendStatus(1);
            mMgr.addMessage(messageInfo);
            mMessageList.add(messageInfo);
            mExampleAdapter.notifyDataSetChanged();
            scrollToBottom();
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(message);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent("自己发了消息"));
        }
    }

    boolean isClick = false;

    @OnClick({R.id.rl_outer, R.id.bark, R.id.iv_else, R.id.ll_details, R.id.btn_look_order, R.id.btn_cancel_order2, R.id.btn_confirm_send_coin, R.id.btn_cancel_order, R.id.btn_confirm_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mEkbEmoticonsKeyboard.getEtChat().getWindowToken(), 0);
                }
                break;
            case R.id.rl_outer:
                /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mEkbEmoticonsKeyboard.getEtChat().getWindowToken(), 0);
                }*/
                break;
            case R.id.iv_else:
                showDialog();
                break;
            case R.id.ll_details:
                isClick = !isClick;
                if (isClick)
                    showOrderDetails();
                else
                    hideOrderDetails();
                break;
            case R.id.btn_look_order:
                isClick = !isClick;
                showOrderDetails();
                break;
            case R.id.btn_cancel_order2:
                break;
            case R.id.btn_confirm_send_coin:
                break;
            case R.id.btn_cancel_order:
                break;
            case R.id.btn_confirm_pay:
                break;
        }
    }

    private void hideOrderDetails() {
        mIvJiantou.setBackground(getDrawable(R.mipmap.icon_wallet_more_selected));
        mLlOrderDetails.setVisibility(View.GONE);
        mBtnLookOrder.setVisibility(View.VISIBLE);
        mLlBuyer.setVisibility(View.GONE);
        mLlSeller.setVisibility(View.GONE);
    }

    private void showOrderDetails() {
        mIvJiantou.setBackground(getDrawable(R.mipmap.icon_wallet_more));
        mLlOrderDetails.setVisibility(View.VISIBLE);
        mBtnLookOrder.setVisibility(View.GONE);
        if (mType.equals("买")) {
            mLlBuyer.setVisibility(View.VISIBLE);
            mLlSeller.setVisibility(View.GONE);
        } else {
            mLlBuyer.setVisibility(View.GONE);
            mLlSeller.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnFuncPop(int i) {
        scrollToBottom();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            boolean isConsum = mEkbEmoticonsKeyboard.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    private void scrollToBottom() {
        mLvMessage.requestLayout();
        mLvMessage.post(new Runnable() {
            @Override
            public void run() {
                mLvMessage.setSelection(mLvMessage.getBottom());
            }
        });
    }

    @Override
    public void OnFuncClose() {
    }
}
