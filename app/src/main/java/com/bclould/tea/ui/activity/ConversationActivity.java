package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.filepicker.YsFilePicker;
import com.bclould.tea.filepicker.YsFilePickerActivity;
import com.bclould.tea.filepicker.YsFilePickerParcelObject;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.ChatAdapter;
import com.bclould.tea.ui.widget.ImageDialog;
import com.bclould.tea.ui.widget.SimpleAppsGridView;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.AudioModeManger;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.MessageManageListener;
import com.bclould.tea.xmpp.Room;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import sj.keyboard.utils.MenuGridListPopWindow;
import sj.keyboard.utils.RecordUtil;
import sj.keyboard.widget.EmoticonPageView;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;
import sj.keyboard.widget.RecordIndicator;


/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener, XhsEmoticonsKeyBoard.OnResultOTR, MessageManageListener, TextView.OnEditorActionListener {

    private static final int CODE_TAKE_PHOTO_SHOOTING = 100;
    private static final int FILE_SELECT_CODE = 2;
    public static final int ACTION_SET_AT = 101;
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
    @Bind(R.id.iv_backgound)
    ImageView mIvBackgound;

    private String roomId;//房間id
    private String mName;
    private List<MessageInfo> mMessageList = new ArrayList<>();
    private DBManager mMgr;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
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
    private int currentPosition;//記錄刷新位置
    private AudioModeManger audioModeManger;
    private Map<String, String> atMap = new HashMap<>();//@人的列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);//初始化数据库管理类
        mDBRoomMember = new DBRoomMember(this);
        mDBRoomManage = new DBRoomManage(this);
        ActivityUtil.isGoStartActivity(this);
        EventBus.getDefault().register(this);//初始化EventBus
        initIntent();//初始化intent事件
        initEmoticonsKeyboard();//初始化功能盘
        MyApp.getInstance().addActivity(this);//打开添加activity
        initAdapter();//初始化适配器
        initData(null, true);//初始化数据
        setBackgound();
        mMgr.updateNumber(roomId, 0);//更新未读消息条数
        mMgr.updateAtme(roomId, "");
        EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));//发送更新未读消息通知
        if (audioModeManger == null) {
            audioModeManger = new AudioModeManger();
        }
        audioModeManger.register(this);
        setOnClick();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("MessageInfo");
        if (messageInfo == null) {
            return;
        }
        mRefreshLayout.setEnableLoadMore(true);
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

    MenuGridListPopWindow.ListOnClick mListOnClick = new MenuGridListPopWindow.ListOnClick() {
        @Override
        public void onclickitem(String name) {
            if (getString(R.string.red_package).equals(name)) {
                if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
                    Intent intent = new Intent(ConversationActivity.this, SendRedGroupActivity.class);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ConversationActivity.this, SendRedPacketActivity.class);
                    intent.putExtra("user", roomId);
                    startActivity(intent);
                }
            } else if (getString(R.string.image).equals(name)) {
                EventBus.getDefault().post(new MessageEvent(getString(R.string.open_photo_album)));
            } else if (getString(R.string.file).equals(name)) {
                EventBus.getDefault().post(new MessageEvent(getString(R.string.open_file_manage)));
            } else if (getString(R.string.location).equals(name)) {
                Intent intent = new Intent(ConversationActivity.this, LocationActivity.class);
                intent.putExtra("user", roomId);
                intent.putExtra("type", 1);
                startActivity(intent);
            } else if (getString(R.string.transfer).equals(name)) {
                Intent intent = new Intent(ConversationActivity.this, ChatTransferActivity.class);
                intent.putExtra("user", roomId);
                startActivity(intent);
            } else if (getString(R.string.shooting).equals(name)) {
                EventBus.getDefault().post(new MessageEvent(getString(R.string.open_shooting)));
            } else if (getString(R.string.collect).equals(name)) {
                Intent intent = new Intent(ConversationActivity.this, CollectActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            } else if (getString(R.string.business_card).equals(name)) {
                Intent intent = new Intent(ConversationActivity.this, SelectFriendGetActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        }
    };

    //初始化表情盘
    private void initEmoticonsKeyboard() {
        //设置房间类型
        mEkbEmoticonsKeyboard.setRoomType(roomType);
        SimpleAppsGridView simpleAppsGridView = new SimpleAppsGridView(this);
        simpleAppsGridView.setData(roomId, roomType);

        mEkbEmoticonsKeyboard.setListMenu(ConversationActivity.this).setListOnClick(mListOnClick);
        mEkbEmoticonsKeyboard.setListOnClick(mListOnClick);

        mEkbEmoticonsKeyboard.addFuncView(simpleAppsGridView);
        mEkbEmoticonsKeyboard.addOnFuncKeyBoardListener(this);
        mEkbEmoticonsKeyboard.getEtChat().setText(MySharedPreferences.getInstance().getString(UtilTool.getTocoId() + roomId));
        mEkbEmoticonsKeyboard.getEtChat().setSelection(mEkbEmoticonsKeyboard.getEtChat().getText().length());
        mEkbEmoticonsKeyboard.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        mEkbEmoticonsKeyboard.getEtChat().setOnTextContextMenuItem(new EmoticonsEditText.OnTextContextMenuItem() {
            @Override
            public void onCopyUri(Uri uri) {
                final ImageDialog imageDialog=new ImageDialog(ConversationActivity.this);
                imageDialog.setImage(uri);
                imageDialog.setOnClickListener(new ImageDialog.OnClickListener() {
                    @Override
                    public void onClick(String path) {
                        roomManage.Upload(path);
                    }
                });
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
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_NULL) {
                    sendMessage(mEkbEmoticonsKeyboard.getEtChat().getText().toString());
                    mEkbEmoticonsKeyboard.getEtChat().setText("");
                }
                return true;
            }
        });
        mEkbEmoticonsKeyboard.getEtChat().setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() == 1 && source.toString().equals("@")
                        && RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {//是群聊，输入了单独一个@时
//                    int index = mEkbEmoticonsKeyboard.getEtChat().getSelectionStart();
//                    if (index == 0) {//如果第一个字符就会@，让他跳
                    intentToAt();
//                        return source;
//                    }
//                    Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
//                    String preChar = mEkbEmoticonsKeyboard.getEtChat().getText().toString().charAt(index - 1) + "";
//                    if (!pattern.matcher(preChar).matches()) {
//                        intentToAt();
//                    }
                    return source;
                }
                return source;
            }
        }});

        mEkbEmoticonsKeyboard.getEditText().addTextChangedListener(new TextWatcher() {
            int delIndex = -1;
            int delLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0 && after == 0) {//说明，是删除
                    CharSequence temp = mEkbEmoticonsKeyboard.getEditText().getText();
                    if (temp.charAt(start) == ' ') {
                        //检查是不是@
                        checkIsAt(start);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable edi) {
                if (delIndex >= 0 && delLength > 0) {
                    int temp1 = delIndex;
                    int temp2 = delLength;
                    delIndex = -1;
                    delLength = 0;
                    edi.delete(temp1 - temp2, temp1);
                }
            }

            private void checkIsAt(int index) {
                if (index <= 0) {
                    return;
                }
                String sub = mEkbEmoticonsKeyboard.getEditText().getText().toString().substring(0, index);
                if (sub.length() == 0) {//如果子串是空，返回
                    return;
                }
                int atIndex = sub.lastIndexOf('@');
                if (atIndex < 0) {//如果没有@，返回
                    return;
                }
                if (index - atIndex <= 1) {//空格前面就是@，返回
                    return;
                }
                String name = sub.substring(atIndex + 1, index);//+1是因为去掉@
                for (String value : atMap.values()) {
                    if (value.equals(name)) {
                        delIndex = index;
                        delLength = name.length() + 1;//+1是因为把@也要删除掉
                        return;
                    }
                }
            }
        });

        /*//实例化录音管理类
        recordIndicator = new RecordIndicator(this, new RecordIndicator.CallBack() {
            @Override
            public void send() {
                cancelRecord();
            }
        });*/
        //给功能盘添加录音监听
//        mEkbEmoticonsKeyboard.setRecordIndicator(recordIndicator);
        mEkbEmoticonsKeyboard.setMediaPlayer(mediaPlayer);
        try {
            mEkbEmoticonsKeyboard.setOnRecordListener(new XhsEmoticonsKeyBoard.OnRecordListener() {
                @Override
                public void recordStart() {
                    startRecord();//开始录音
                    scrollToBottom();
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
    }

    private void intentToAt() {
        Intent intent = new Intent(this, SelectGroupMemberActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("type", 3);
        startActivityForResult(intent, ACTION_SET_AT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void sendMessage(String message) {
        if (StringUtils.isEmpty(message)) {
            return;
        }
        if (atMap.size() <= 0 || !(RoomManage.ROOM_TYPE_MULTI.equals(roomType))) {
            // 发送消息
            roomManage.sendMessage(message);
        } else {
            //发送At
            roomManage.sendATMessage(message, atMap);
            atMap.clear();
        }

    }


    //开始录音
    private void startRecord() {
        if (null == recordUtil) {
            recordUtil = new RecordUtil(this);
            mEkbEmoticonsKeyboard.setRecordUtil(recordUtil);
        }
        recordUtil.start();
        mChatAdapter.stopVoicePlay();
    }

    //完成了录音
    private void finishRecord() {
        recordUtil.finish();
        int duration = recordUtil.getVoiceDuration();
        String fileName = recordUtil.getFileName();
        if (duration <= 0) {
            return;
        }
        roomManage.sendVoice(duration, fileName);
    }

    //录音取消处理
    private void cancelRecord() {
        recordUtil.cancel();
        Toast.makeText(this, getString(R.string.cancel_record), Toast.LENGTH_SHORT).show();
    }

    //界面销毁隐藏软键盘
    @Override
    public void onDestroy() {
        String draft = mEkbEmoticonsKeyboard.getEditText().getText().toString();
        mMgr.updataConversationDraft(roomId, draft);
        MySharedPreferences.getInstance().setString(UtilTool.getTocoId() + roomId, draft);
        EventBus.getDefault().post(new MessageEvent(getString(R.string.refresh)));
        if (audioModeManger != null)
            audioModeManger.unregister();
        roomManage.removerMessageManageListener(this);
        mediaPlayer.release();
        mediaPlayer = null;
        EventBus.getDefault().unregister(this);
        mEkbEmoticonsKeyboard.onDestroy();
        super.onDestroy();

    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.msg_database_update)) {
            initData(event.getId(), false);
            mMgr.updateNumber(roomId, 0);
            mMgr.updateAtme(roomId, "");
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
        } else if (msg.equals(EventBusUtil.send_red_packet_le)) {
            initData(event.getId(), true);
        } else if (msg.equals(getString(R.string.transfer))) {
            initData(event.getId(), true);
        } else if (msg.equals(getString(R.string.open_shooting))) {
            openShooting();
        } else if (msg.equals(getString(R.string.open_photo_album))) {
            selectorImages();
        } else if (msg.equals(getString(R.string.open_file_manage))) {
            showFileChooser();
        } else if (msg.equals(getString(R.string.look_original))) {
            String id = event.getId();
            changeUrl(id, event.getFilepath());
        } else if (msg.equals(getString(R.string.otr_isopen))) {
            mEkbEmoticonsKeyboard.changeOTR(OtrChatListenerManager.getInstance().getOTRState(roomId.toString()));
        } else if (msg.equals(EventBusUtil.delete_friend)) {
            finish();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            setTitleName();
        } else if (msg.equals(getString(R.string.start_otr_timeout))) {
            UtilTool.Log("fengjian---", "加密超时");
            mEkbEmoticonsKeyboard.timeoutOTR();
        } else if (msg.equals(getString(R.string.start_otr))) {
            mEkbEmoticonsKeyboard.startOTR();
            UtilTool.Log("fengjian---", "开启加密");
        } else if (msg.equals(EventBusUtil.change_msg_state)) {
            changeMsgState(event.getId());
            UtilTool.Log("fengjian---", "改變消息狀態");
        } else if (msg.equals(EventBusUtil.quit_group)) {
            if (roomId.equals(event.getId())) {
                finish();
            }
        } else if (msg.equals(EventBusUtil.refresh_group_members)) {
            setTitleName();
            mChatAdapter.notifyDataSetChanged();
        } else if (msg.equals(getString(R.string.modify_group_name))) {
            mName = event.getName();
            setTitleName();
            mChatAdapter.notifyDataSetChanged();
        } else if (msg.equals(EventBusUtil.refresh_group_room)) {
            if (roomId.equals(event.getId())) {
                mName = mDBRoomManage.findRoomName(roomId);
                setTitleName();
            }
        } else if (msg.equals(EventBusUtil.kick_out_success)) {
            if (roomId.equals(event.getId())) {
                finish();
            }
        } else if (msg.equals(getString(R.string.update_file_message))) {
            changeMsgFile(event.getId(), event.getFilepath());
        } else if (msg.equals(EventBusUtil.withdrew_a_message)) {
            deleteMsg(event.getId());
        }else if(msg.equals(getString(R.string.conversation_backgound))){
            setBackgound();
        }else if(msg.equals(getString(R.string.automatic_download_complete))){
            downloadMsg(event.getUrl(),event.getFilepath());
        }
    }

    private void downloadMsg(String file, String filepath) {
        for (MessageInfo info : mMessageList) {
            if (info.getMessage().equals(file)) {
                info.setStatus(1);
                mMgr.updateMessageState(info.getId()+"",1);
                if(!StringUtils.isEmpty(filepath)){
                    info.setMessage(filepath);
                    mMgr.updateMessage(info.getId(),filepath);
                }
                mChatAdapter.notifyItemChanged(mMessageList.indexOf(info));
                return;
            }
        }
    }

    private void deleteMsg(String msgId) {
        for (MessageInfo info : mMessageList) {
            if (info.getMsgId().equals(msgId)) {
                mMessageList.remove(info);
                mChatAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void changeUrl(String id, String url) {
        for (MessageInfo info : mMessageList) {
            if (info.getId() == UtilTool.parseInt(id)) {
                info.setVoice(url);
                info.setImageType(1);
                mChatAdapter.notifyItemChanged(mMessageList.indexOf(info));
            }
        }
    }

    private void changeMsgFile(String msgId, String filepath) {
        for (MessageInfo info : mMessageList) {
            if (info.getMsgId().equals(msgId)) {
                info.setVoice(filepath);
                mChatAdapter.notifyItemChanged(mMessageList.indexOf(info));
            }
        }
    }

    private void changeMsgState(String id) {
        for (int i = 0; i < mMessageList.size(); i++) {
            if (id.equals(mMessageList.get(i).getMsgId())) {
                mMessageList.get(i).setSendStatus(1);
                mChatAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    //打開拍攝
    private void openShooting() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, CODE_TAKE_PHOTO_SHOOTING);
    }

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
                if (data != null) {
                    YsFilePickerParcelObject object = (YsFilePickerParcelObject) data.getParcelableExtra(YsFilePickerParcelObject.class.getCanonicalName());
                    if (object.count > 0 && object.count <= 1) {
                        String path = object.path + object.names.get(0);
                        roomManage.uploadFile(path);
                    } else if (object.count > 1) {
                        for (int i = 0; i < object.count; i++) {
                            String path = object.path + object.names.get(i);
                            roomManage.uploadFile(path);
                        }
                    }
                }

//                //如果是文件选择模式，需要获取选择的所有文件的路径集合
//                List<String> list = data.getStringArrayListExtra("paths");
//                for(String path:list){
//                    roomManage.uploadFile(path);
//                }
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() != 0) {
                    for (int i = 0; i < selectList.size(); i++) {
                        String postfix = UtilTool.getPostfix(selectList.get(i).getPath());
                        if (!postfix.equals("Video")) {
                            roomManage.Upload(selectList.get(i).getPath());
                        } else {
                            roomManage.Upload(selectList.get(i).getPath());
                        }
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
                if (!StringUtils.isEmpty(url))
                    roomManage.Upload(url);
            } else if (requestCode == ACTION_SET_AT) {
                String tocoid = data.getStringExtra("tocoid");
                String name = data.getStringExtra("name");
                int index = mEkbEmoticonsKeyboard.getEtChat().getSelectionStart();
                Editable editable = mEkbEmoticonsKeyboard.getEtChat().getText();
                editable.delete(index - 1, index);
                setAtPartner(tocoid, true);
            }
        }
    }

    private void setAtPartner(String tocoid, boolean isRepeat) {
        String partnerName = mDBRoomMember.findMemberName(roomId, tocoid);
        if (!isRepeat) {
            String compareStr = "@" + partnerName + " ";
            String preContent = mEkbEmoticonsKeyboard.getEtChat().getText().toString();
            if (!StringUtils.isEmpty(preContent) && preContent.contains(compareStr)) {
                return;
            }
        }
        atMap.put(tocoid, partnerName);
        String atContentStr;
        if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
            atContentStr = "@" + partnerName + " ";
        } else {
            atContentStr = partnerName + " ";
        }
        int index = mEkbEmoticonsKeyboard.getEtChat().getSelectionStart(); //获取光标所在位置
        Editable edit_text = mEkbEmoticonsKeyboard.getEtChat().getEditableText();
        edit_text.insert(index, atContentStr);
    }


    //调用文件选择软件来选择文件
    private void showFileChooser() {
//        new LFilePicker()
//                .withActivity(ConversationActivity.this)
//                .withRequestCode(FILE_SELECT_CODE)
//                .withStartPath( Environment.getExternalStorageDirectory().getAbsolutePath())//指定初始显示路径
//                .withIsGreater(false)//过滤文件大小 小于指定大小的文件
//                .withFileSize(10*1024 * 1024)//指定文件大小为10兆
//                .withMaxNum(10)
//                .withMutilyMode(true)
//                .start();
        Intent intent = new Intent(ConversationActivity.this, YsFilePickerActivity.class);
        YsFilePicker.initMulti(intent);
        ConversationActivity.this.startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mRefreshLayout.finishRefresh();
                    //下拉查询历史消息
                    if (mMessageList.size() == 0) return;
                    currentPosition = mMessageList.size();
                    int position = mLayoutManager.findFirstVisibleItemPosition();
                    View view = mRecyclerView.getChildAt(position);
                    int top = 0;
                    if (view != null) {
                        top = view.getTop();
                    }
                    List<MessageInfo> messageInfos = mMgr.queryRefreshMessage(roomId, mMessageList.get(0).getCreateTime(),0);
                    mMessageList.addAll(0, messageInfos);
                    currentPosition = mMessageList.size() - currentPosition;
                    mChatAdapter.notifyDataSetChanged();
                    mLayoutManager.scrollToPositionWithOffset(currentPosition, top);
                    break;
                case 4:
                    //上啦加載
//                    if (mMessageList.size() == 0) return;
                    mRefreshLayout.finishLoadMore();
                    Bundle bundle3 = (Bundle) msg.obj;
                    boolean isFist = bundle3.getBoolean("isFist");
                    List<MessageInfo> messageInfos1 = null;
                    if (isFist) {
                        MessageInfo messageInfo = (MessageInfo) bundle3.getSerializable("MessageInfo");
                        messageInfos1 = mMgr.queryLoadMessage(roomId, messageInfo.getCreateTime(), isFist,0);
                    } else {
                        if (mMessageList.size() == 0) {
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(0).getCreateTime(), isFist,0);
                        } else {
                            messageInfos1 = mMgr.queryLoadMessage(roomId, mMessageList.get(mMessageList.size() - 1).getCreateTime(), isFist,0);
                        }
                    }
                    if (messageInfos1.size() <= 0) {
                        mRefreshLayout.setEnableLoadMore(false);
                    }
                    List<MessageInfo> MessageList3 = new ArrayList<>();
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
    private void initData(String msgId, boolean isScroll) {
        if (StringUtils.isEmpty(msgId)) {
            List<MessageInfo> messageInfos = mMgr.queryMessage(roomId,0);
            mMessageList.clear();
            mMessageList.addAll(messageInfos);
            mChatAdapter.notifyDataSetChanged();
            mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
        } else {
            MessageInfo messageInfo = mMgr.queryMessageMsg(msgId);
            if (roomId.equals(messageInfo.getUsername())) {
                mMessageList.add(messageInfo);
                mChatAdapter.notifyItemInserted(mMessageList.indexOf(messageInfo));
                mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
            }
        }
    }

    private void setOnClick() {
        audioModeManger.setOnSpeakerListener(new AudioModeManger.onSpeakerListener() {
            @Override
            public void onSpeakerChanged(boolean isSpeakerOn) {
                mChatAdapter.refreshPlayVoice(isSpeakerOn);
            }
        });
        mEkbEmoticonsKeyboard.addOnResultOTR(this);
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
    }

    public void isPlayVoi1ce(boolean isPlayVoice) {
        audioModeManger.setIsPlayVoice(isPlayVoice);
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
                UtilTool.Log("fengjian", "添加群聊---" + roomId);
                roomManage = RoomManage.getInstance().addMultiMessageManage(roomId, mName);
            } else {
//               if (RoomManage.ROOM_TYPE_SINGLE.equals(roomType)) {
                UtilTool.Log("fengjian", "添加单聊---" + roomId);
                roomManage = RoomManage.getInstance().addSingleMessageManage(roomId, mName);
            }
        } else {
            UtilTool.Log("fengjian", "房间存在---" + roomType + "   " + roomId);
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
        if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
            mTitleName.setText(mDBRoomManage.findRoomName(roomId) + "(" + mDBRoomMember.queryAllRequest(roomId).size() + ")");
        } else {
            String remark = mMgr.queryRemark(roomId);
            if (!StringUtils.isEmpty(remark)) {
                mTitleName.setText(remark);
            } else {
                mTitleName.setText(mName);
            }
        }
    }

    //初始化适配器
    private void initAdapter() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChatAdapter = new ChatAdapter(this, mMessageList, roomId, mMgr, mediaPlayer, mName, roomType, mRlTitle, mDBRoomMember);
        mRecyclerView.setAdapter(mChatAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                handler.sendEmptyMessage(0);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFist", false);
                Message message = new Message();
                message.obj = bundle;
                message.what = 4;
                handler.sendMessage(message);
            }
        });
    }

    private void setBackgound() {
        String key = MySharedPreferences.getInstance().getString("backgroundu_file" + UtilTool.getTocoId());
        String urls=MySharedPreferences.getInstance().getString("backgroundu_url"+UtilTool.getTocoId());
        if (!StringUtils.isEmpty(key)||!StringUtils.isEmpty(urls)) {
            File file = new File(Constants.BACKGOUND+key);
            if (file.exists()) {
                Glide.with(this).load(new File(Constants.BACKGOUND+key)).apply(requestOptions).into(mIvBackgound);
            }else{
                Glide.with(this).load(urls).apply(requestOptions).into(mIvBackgound);
            }
        }
    }

    RequestOptions requestOptions = new RequestOptions()
            .centerCrop();

    public View getItemView(int position) {
        int firstItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        View view = null;
        if (position - firstItemPosition >= 0)
            view = mRecyclerView.getChildAt(position - firstItemPosition);
        return view;
    }

    @OnClick({R.id.bark, R.id.iv_else})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive(mEkbEmoticonsKeyboard.getEtChat());//isOpen若返回true，则表示输入法打开
                if (isOpen) {
                    imm.hideSoftInputFromWindow(mEkbEmoticonsKeyboard.getEtChat().getWindowToken(), 0);
                }
                break;
            case R.id.iv_else:
                goDetails();
                break;
        }
    }

    private void goDetails() {
        if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
            Intent intent = new Intent(this, ConversationGroupDetailsActivity.class);
            intent.putExtra("roomId", roomId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ConversationDetailsActivity.class);
            intent.putExtra("user", roomId);
            intent.putExtra("name", mName);
            intent.putExtra("roomId", roomId);
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

    public void scrollToBottom() {
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
                mChatAdapter.notifyItemInserted(mMessageList.size());
                scrollToBottom();
            }
        });
    }

    @Override
    public void sendError(final int id) {
        ConversationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MessageInfo info : mMessageList) {
                    if (info.getId() == id) {
                        info.setSendStatus(2);
                        mChatAdapter.notifyItemChanged(mMessageList.indexOf(info));
                    }
                }
            }
        });
    }

    @Override
    public void sendFileResults(final String newFile2, final boolean isSuccess) {
        ConversationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MessageInfo info : mMessageList) {
                    if (info.getVoice() != null) {
                        if (info.getVoice().equals(newFile2)) {
                            if (!isSuccess) {
                                info.setSendStatus(2);
                                mMgr.updateMessageHint(info.getId(), 2);
                            }
                            mChatAdapter.notifyItemChanged(mMessageList.indexOf(info));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void sendFile(final String msgId, final boolean isSuccess) {
        ConversationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MessageInfo info : mMessageList) {
                    if (info.getVoice() != null) {
                        if (info.getMsgId().equals(msgId)) {
                            if (!isSuccess) {
                                info.setSendStatus(2);
                                mMgr.updateMessageHint(info.getId(), 2);
                            }
                            mChatAdapter.notifyItemChanged(mMessageList.indexOf(info));
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

    public void onResume() {
        super.onResume();
        mEkbEmoticonsKeyboard.onResume();
    }

    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mEkbEmoticonsKeyboard.onPause();
    }
}
