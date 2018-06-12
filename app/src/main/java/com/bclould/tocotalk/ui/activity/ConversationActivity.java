package com.bclould.tocotalk.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.adapter.ChatAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.SimpleAppsGridView;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.RecordUtil;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.MessageManageListener;
import com.bclould.tocotalk.xmpp.Room;
import com.bclould.tocotalk.xmpp.RoomManage;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sj.emoji.DefEmoticons;
import com.sj.emoji.EmojiBean;
import com.sj.emoji.EmojiDisplay;
import com.sj.emoji.EmojiSpan;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

import static com.bclould.tocotalk.Presenter.LoginPresenter.MYUSERNAME;
import static com.bclould.tocotalk.R.style.BottomDialog;


/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationActivity extends AppCompatActivity implements FuncLayout.OnFuncKeyBoardListener, XhsEmoticonsKeyBoard.OnResultOTR, MessageManageListener ,TextView.OnEditorActionListener {

    private static final int CODE_TAKE_PHOTO_SHOOTING = 100;
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
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.ll_chat)
    LinearLayout mLlChat;
    @Bind(R.id.ekb_emoticons_keyboard)
    XhsEmoticonsKeyBoard mEkbEmoticonsKeyboard;

    private String roomId;//房間id
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
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private Room roomManage;
    private String roomType;//房间类型

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        ButterKnife.bind(this);
        mMgr = new DBManager(this);//初始化数据库管理类
        EventBus.getDefault().register(this);//初始化EventBus
        initIntent();//初始化intent事件
        initEmoticonsKeyboard();//初始化功能盘
        MyApp.getInstance().addActivity(this);//打开添加activity
        initAdapter();//初始化适配器
        initData();//初始化数据
        mMgr.updateNumber(roomId, 0);//更新未读消息条数
        EventBus.getDefault().post(new MessageEvent(getString(R.string.dispose_unread_msg)));//发送更新未读消息通知
        //监听touch事件隐藏软键盘
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                FuncLayout funcView = mEkbEmoticonsKeyboard.getFuncView();
                if (funcView.isShown()) {
                    funcView.hideAllFuncView();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mEkbEmoticonsKeyboard.getEtChat().getWindowToken(), 0);
                }
                return false;
            }
        });
        mEkbEmoticonsKeyboard.addOnResultOTR(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("MessageInfo");
        if (messageInfo == null) {
            return;
        }
        //通過傳遞過來的消息，查找
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFist", true);
        bundle.putSerializable("MessageInfo", (Serializable) messageInfo);
        mMessageList.clear();
        Message message = new Message();
        message.obj = bundle;
        message.what = 4;
        handler.sendMessage(message);
    }

    //初始化表情盘
    private void initEmoticonsKeyboard() {
        SimpleAppsGridView simpleAppsGridView = new SimpleAppsGridView(this);
        simpleAppsGridView.setData(roomId, roomType);
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
        // 监听表情盘点击事件
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

        // 实例化表情盘
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

        //  实例化page
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

        // 构建
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

        //过滤表情
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
        // 输入框添加表情过滤
        mEkbEmoticonsKeyboard.getEtChat().addEmoticonFilter(new EmojiFilter());

        mEkbEmoticonsKeyboard.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(mEkbEmoticonsKeyboard.getEtChat().getText().toString());
                mEkbEmoticonsKeyboard.getEtChat().setText("");
            }
        });
        mEkbEmoticonsKeyboard.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(mEkbEmoticonsKeyboard.getEtChat().getText().toString());
                    mEkbEmoticonsKeyboard.getEtChat().setText("");
                }
                return false;
            }
        });

        //实例化录音管理类
        recordIndicator = new RecordIndicator(this, new RecordIndicator.CallBack() {
            @Override
            public void send() {
                cancelRecord();
            }
        });
        //给功能盘添加录音监听
        mEkbEmoticonsKeyboard.setRecordIndicator(recordIndicator);
        try {
            recordIndicator.setOnRecordListener(new RecordIndicator.OnRecordListener() {
                @Override
                public void recordStart() {
                    startRecord();//开始录音
                }

                @Override
                public void recordFinish() {
                    finishRecord();//完成录音
                }

                @Override
                public void recordCancel() {
                    cancelRecord();//取消录音
                }

                @Override
                public long getRecordTime() {
                    //获取录音时间
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
        //初始化OTR
        mEkbEmoticonsKeyboard.changeOTR(OtrChatListenerManager.getInstance().getOTRState(roomId.toString()));
        //设置房间类型
        mEkbEmoticonsKeyboard.setRoomType(roomType);
    }

    private void sendMessage(String message) {
        roomManage.sendMessage(message);
    }


    //开始录音
    private void startRecord() {
        if (null == recordUtil) {
            recordUtil = new RecordUtil(this);
        }
        recordUtil.start();
    }

    //完成了录音
    private void finishRecord() {
        recordUtil.finish();
        int duration = recordUtil.getVoiceDuration();
        String fileName = recordUtil.getFileName();
        roomManage.sendVoice(duration, fileName);
    }

    //录音取消处理
    private void cancelRecord() {
        recordUtil.cancel();
        Toast.makeText(this, getString(R.string.cancel_record), Toast.LENGTH_SHORT).show();
    }

    //界面失去焦点暂停语音播放
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    //界面销毁隐藏软键盘
    @Override
    public void onDestroy() {
        super.onDestroy();
        roomManage.removerMessageManageListener(this);
        mediaPlayer.release();
        mediaPlayer = null;
        EventBus.getDefault().unregister(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
        if (isOpen) {
            imm.hideSoftInputFromWindow(mEkbEmoticonsKeyboard.getEtChat().getWindowToken(), 0);
        }
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.msg_database_update))) {
            initData();
            mMgr.updateNumber(roomId, 0);
            EventBus.getDefault().post(new MessageEvent(getString(R.string.dispose_unread_msg)));
        } else if (msg.equals(getString(R.string.send_red_packet_le))) {
            initData();
        } else if (msg.equals(getString(R.string.transfer))) {
            initData();
        } else if (msg.equals(getString(R.string.open_shooting))) {
            openShooting();
        } else if (msg.equals(getString(R.string.open_photo_album))) {
            selectorImages();
        } else if (msg.equals(getString(R.string.open_file_manage))) {
            showFileChooser();
        } else if (msg.equals(getString(R.string.look_original))) {
            String id = event.getId();
            for (MessageInfo info : mMessageList) {
                info.setImageType(1);
                mChatAdapter.notifyDataSetChanged();
            }
        } else if (msg.equals(getString(R.string.otr_isopen))) {
            mEkbEmoticonsKeyboard.changeOTR(OtrChatListenerManager.getInstance().getOTRState(roomId.toString()));
        } else if (msg.equals(getString(R.string.delete_friend))) {
            finish();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            setTitleName();
        } else if (msg.equals(getString(R.string.start_otr_timeout))) {
            UtilTool.Log("fengjian---", "加密超时");
            mEkbEmoticonsKeyboard.timeoutOTR();
        } else if (msg.equals(getString(R.string.start_otr))) {
            mEkbEmoticonsKeyboard.startOTR();
            UtilTool.Log("fengjian---", "开启加密");
        }

    }

    //打開拍攝
    private void openShooting() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, CODE_TAKE_PHOTO_SHOOTING);
    }

//    //打开系统视频录制
//    private void openCameraShooting() {
//        Uri uri = null;
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
//        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 6000);
//        try {
//            File cacheDir = new File(getFilesDir().getAbsolutePath() + "/images");
//            if (!cacheDir.exists())
//                cacheDir.mkdirs();
//            mImagePath = ConversationActivity.this.getFilesDir().getAbsolutePath() + "/images/" + UtilTool.createtFileName() + ".mp4";
//            File file = new File(mImagePath);
//            uri = FileProvider.getUriForFile(this, "com.bclould.tocotalk.provider", file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
//    }

    //选择图片
    private void selectorImages() {

        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                    .theme(R.style.picture_white_style)
                .maxSelectNum(9)// 最大图片 选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true) // 是否可播放音频
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(false)// 是否显示拍照按钮
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


    //页面返回数据处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {

            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() != 0) {
                    for (int i = 0; i < selectList.size(); i++) {
                        roomManage.Upload(selectList.get(i).getPath());
                    }
                    selectList.clear();
                }
            } else if (requestCode == CODE_TAKE_PHOTO_SHOOTING) {
                String url = data.getStringExtra("path_url");
                String postfix = UtilTool.getPostfix(url);//获取文件后缀
                if (!postfix.equals("Video")) {
                    int degree = UtilTool.readPictureDegree(url);
                    UtilTool.toturn(url, BitmapFactory.decodeFile(url), degree);
                }
                if (!com.bclould.tocotalk.utils.StringUtils.isEmpty(url))
                    roomManage.Upload(url);
            }
        }
    }


    //调用文件选择软件来选择文件
    private void showFileChooser() {
        if (Build.VERSION.SDK_INT >= 24) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, getString(R.string.selector_file)),
                        FILE_SELECT_CODE);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, getString(R.string.toast_install_file_manage), Toast.LENGTH_SHORT)
                        .show();
            }
        } else {

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //下拉查询历史消息
                    if (mMessageList.size() == 0) return;
                    List<MessageInfo> messageInfos = mMgr.queryRefreshMessage(roomId, mMessageList.get(0).getId());
                    List<MessageInfo> MessageList2 = new ArrayList<MessageInfo>();
                    MessageList2.addAll(messageInfos);
                    MessageList2.addAll(mMessageList);
                    mMessageList.clear();
                    mMessageList.addAll(MessageList2);
                    mChatAdapter.notifyDataSetChanged();
                    mLayoutManager.scrollToPositionWithOffset(4, 0);
                    break;
                case 3:
                    mChatAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    //上啦加載
//                    if (mMessageList.size() == 0) return;
                    Bundle bundle3 = (Bundle) msg.obj;
                    boolean isFist = bundle3.getBoolean("isFist");
                    List<MessageInfo> messageInfos1 = null;
                    if (isFist) {
                        MessageInfo messageInfo = (MessageInfo) bundle3.getSerializable("MessageInfo");
                        messageInfos1 = mMgr.queryLoadMessage(roomId, messageInfo.getId(), isFist);
                    } else {
                        if(mMessageList.size()==0){
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(0).getId(), isFist);
                        }else{
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(mMessageList.size() - 1).getId(), isFist);
                        }

                    }
                    List<MessageInfo> MessageList3 = new ArrayList<MessageInfo>();
                    MessageList3.addAll(mMessageList);
                    MessageList3.addAll(messageInfos1);
                    mMessageList.clear();
                    mMessageList.addAll(MessageList3);
                    mChatAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    //初始化数据
    private void initData() {
        List<MessageInfo> messageInfos = mMgr.queryMessage(roomId);
        mMessageList.clear();
        mMessageList.addAll(messageInfos);
        mChatAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
    }

    //设置title
    private void initIntent() {
        if (StringUtils.isEmpty(UtilTool.getJid())) {
            MySharedPreferences.getInstance().setString(MYUSERNAME, UtilTool.getUser() + "@" + Constants.DOMAINNAME2);
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Drawable drawable = getDrawable(R.mipmap.img_nfriend_headshot1);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            mUserImage = bd.getBitmap();
            mName = "tester_001";
            roomId = UtilTool.getTocoId();
        } else {
            mName = bundle.getString("name");
            roomId = bundle.getString("user");
//            mUserImage = UtilTool.getImage(mMgr, mUser, this);
            clearNotification();
        }
        roomType = bundle.getString("chatType");
        if (RoomManage.getInstance().getRoom(roomId) == null) {
            if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
                roomManage = RoomManage.getInstance().addMultiMessageManage(roomId, mName);
            } else {
//               if (RoomManage.ROOM_TYPE_SINGLE.equals(roomType)) {
                UtilTool.Log("fengjian", "添加单聊---" + roomId);
                roomManage = RoomManage.getInstance().addSingleMessageManage(roomId, mName);
            }
        } else {
            UtilTool.Log("fengjian", "房间存在---" + roomId);
            roomManage = RoomManage.getInstance().getRoom(roomId);
        }
        roomManage.addMessageManageListener(this);
        setTitleName();
    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void setTitleName() {
        String remark = mMgr.queryRemark(roomId);
        if (!com.bclould.tocotalk.utils.StringUtils.isEmpty(remark)) {
            mTitleName.setText(remark);
        } else {
            mTitleName.setText(mName);
        }
    }

    //初始化适配器
    private void initAdapter() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChatAdapter = new ChatAdapter(this, mMessageList, roomId, mMgr, mediaPlayer,mName,roomType,mRlTitle);
        mRecyclerView.setAdapter(mChatAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
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
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFist", false);
                Message message = new Message();
                message.obj = bundle;
                message.what = 4;
                handler.sendMessage(message);
            }
        });
    }

    //显示更多dialog
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

    //处理更多Dialog的点击事件
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
        LinearLayout complain = (LinearLayout) mBottomDialog.findViewById(R.id.ll_complain);
        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
    }


    @OnClick({R.id.bark, R.id.iv_else})
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
            case R.id.iv_else:
//                showDialog();
                goDetails();
                break;
        }
    }

    private void goDetails() {
        // TODO: 2018/5/28 區分群聊和單聊
        if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {

        } else {
            Intent intent = new Intent(this, ConversationDetailsActivity.class);
            intent.putExtra("user", roomId);
            intent.putExtra("name", mName);
            intent.putExtra("roomId",roomId);
            startActivity(intent);
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
        /*mLvMessage.requestLayout();
        mLvMessage.post(new Runnable() {
            @Override
            public void run() {
                mLvMessage.setSelection(mLvMessage.getBottom());
            }
        });*/
        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
    }

    @Override
    public void OnFuncClose() {
//        mEkbEmoticonsKeyboard.OnSoftClose();
    }

    @Override
    public void resultOTR() {
        try {
            mEkbEmoticonsKeyboard.changeOTR(OtrChatListenerManager.getInstance().getOTRState(roomId.toString()));
            OtrChatListenerManager.getInstance().changeState(roomId.toString(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshAddData(MessageInfo messageInfo) {
        mMessageList.add(messageInfo);
        ConversationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    @Override
    public void sendError(int id) {
        for (MessageInfo info : mMessageList) {
            if (info.getId() == id) {
                info.setSendStatus(2);
                handler.sendEmptyMessage(3);
            }
        }
    }

    @Override
    public void sendFileResults(final String newFile2, final boolean isSuccess) {
        ConversationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MessageInfo info : mMessageList) {
                    if (info.getVoice() != null) {
                        if (info.getVoice().equals(newFile2)) {
                            if (isSuccess) {
                                mMgr.updateMessageHint(info.getId(), 1);
                                info.setSendStatus(1);
                            } else {
                                info.setSendStatus(2);
                                mMgr.updateMessageHint(info.getId(), 2);
                            }
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }
}
