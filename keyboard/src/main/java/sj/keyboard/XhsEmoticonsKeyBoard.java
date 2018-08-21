package sj.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlos.voiceline.mylibrary.VoiceLineView;
import com.keyboard.view.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.PageSetEntity;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.utils.MenuGridListPopWindow;
import sj.keyboard.utils.RecordUtil;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.EmoticonsFuncView;
import sj.keyboard.widget.EmoticonsIndicatorView;
import sj.keyboard.widget.EmoticonsToolBarView;
import sj.keyboard.widget.FuncLayout;
import sj.keyboard.widget.RecordIndicator;

public class XhsEmoticonsKeyBoard extends AutoHeightLayout implements View.OnClickListener, EmoticonsFuncView.OnEmoticonsPageViewListener,
        EmoticonsToolBarView.OnToolBarItemClickListener, EmoticonsEditText.OnBackKeyClickListener, FuncLayout.OnFuncChangeListener {

    public static final int FUNC_TYPE_EMOTION = -1;
    public static final int FUNC_TYPE_APPPS = -2;
    public final static String ROOM_TYPE_SINGLE = "single";
    public final static String ROOM_TYPE_MULTI = "multi";

    protected LayoutInflater mInflater;

    //    protected ImageView mbtnOtrText;
    protected ImageView mBtnVoice;
    protected EmoticonsEditText mEtChat;
    protected ImageView mBtnFace;
    protected RelativeLayout mRlInput;
    protected ImageView mBtnMultimedia;
    protected Button mBtnSend;
    protected FuncLayout mLyKvml;

    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;
    protected EmoticonsToolBarView mEmoticonsToolBarView;

    protected boolean mDispatchKeyEventPreImeLock = false;
    private RecordIndicator recordIndicator;
    private boolean initRecordIndicator = false;
    private OnResultOTR onResultOTR;
    private String roomType;//房间类型
    private boolean isBurnReading;//是否是閱後即焚
    private MenuGridListPopWindow menu;
    private TextView mTvCancel;
    private TextView mTvRecordingSend;
    private TextView mTvStop;
    private TextView mTvPlay;
    private RelativeLayout mRlRecording;
    private RelativeLayout mRlText;
    private String mFileName;
    private MediaPlayer mMediaPlayer;
    private OnRecordListener mOnRecordListener;
    private RecordUtil mRecordUtil;
    private VoiceLineView mViewWave;
    private Timer mTimer;
    private TextView mTvTime;
    private TextView mTvPause;
    private ImageView mBtnCamera;
    private MenuGridListPopWindow.ListOnClick mListOnClick;
    private DecibelThread mDecibelThread;

    public XhsEmoticonsKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
        initFuncView();
    }

    protected void inflateKeyboardBar() {
        mInflater.inflate(R.layout.view_keyboard_xhs, this);
    }

    protected View inflateFunc() {
        return mInflater.inflate(R.layout.view_func_emoticon, null);
    }

    protected void initView() {
//        mbtnOtrText=(ImageView)findViewById(R.id.btn_otr_text);
        mEtChat = ((EmoticonsEditText) findViewById(R.id.et_chat));
        mBtnFace = ((ImageView) findViewById(R.id.btn_face));
        mRlInput = ((RelativeLayout) findViewById(R.id.rl_input));
        mBtnMultimedia = ((ImageView) findViewById(R.id.btn_multimedia));
        mBtnSend = ((Button) findViewById(R.id.btn_send));
        mLyKvml = ((FuncLayout) findViewById(R.id.ly_kvml));

        //新增
        mBtnVoice = ((ImageView) findViewById(R.id.btn_voice_or_text));
        mTvCancel = ((TextView) findViewById(R.id.tv_cancel));
        mTvRecordingSend = ((TextView) findViewById(R.id.tv_recording_send));
        mTvStop = ((TextView) findViewById(R.id.tv_stop));
        mTvPlay = ((TextView) findViewById(R.id.tv_play));
        mViewWave = ((VoiceLineView) findViewById(R.id.view_wave));
        mRlRecording = (RelativeLayout) findViewById(R.id.rl_recording);
        mRlText = (RelativeLayout) findViewById(R.id.rl_text);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvPause = (TextView) findViewById(R.id.tv_pause);
        mBtnCamera = (ImageView) findViewById(R.id.btn_camera);

        mBtnFace.setOnClickListener(this);
        mBtnMultimedia.setOnClickListener(this);
        mEtChat.setOnBackKeyClickListener(this);
        //新增
        mTvCancel.setOnClickListener(this);
        mTvRecordingSend.setOnClickListener(this);
        mTvStop.setOnClickListener(this);
        mTvPlay.setOnClickListener(this);
        mBtnVoice.setOnClickListener(this);
        mTvPause.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);

        /*if (recordIndicator != null && !initRecordIndicator) {
            initRecordIndicator = true;
            recordIndicator.setRecordButton(mBtnVoice, mTvCancel, mTvRecordingSend, mTvStop, mTvPlay);
        }*/
    }

    protected void initFuncView() {
        initEmoticonFuncView();
        initEditView();
    }

    protected void initEmoticonFuncView() {
        View keyboardView = inflateFunc();
        mLyKvml.addFuncView(FUNC_TYPE_EMOTION, keyboardView);
        mEmoticonsFuncView = ((EmoticonsFuncView) findViewById(R.id.view_epv));
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) findViewById(R.id.view_eiv));
        mEmoticonsToolBarView = ((EmoticonsToolBarView) findViewById(R.id.view_etv));
        mEmoticonsFuncView.setOnIndicatorListener(this);
        mEmoticonsToolBarView.setOnToolBarItemClickListener(this);
        mLyKvml.setOnFuncChangeListener(this);
    }

    protected void initEditView() {
        mEtChat.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mEtChat.isFocused()) {
                    mEtChat.setFocusable(true);
                    mEtChat.setFocusableInTouchMode(true);
                }
                return false;
            }
        });

        mEtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnSend.setVisibility(VISIBLE);
                } else {
                    mBtnSend.setVisibility(GONE);
                }
            }
        });
    }

    public void setAdapter(PageSetAdapter pageSetAdapter) {
        if (pageSetAdapter != null) {
            ArrayList<PageSetEntity> pageSetEntities = pageSetAdapter.getPageSetEntityList();
            if (pageSetEntities != null) {
                for (PageSetEntity pageSetEntity : pageSetEntities) {
                    mEmoticonsToolBarView.addToolItemView(pageSetEntity);
                }
            }
        }
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    public void addFuncView(View view) {
        mLyKvml.addFuncView(FUNC_TYPE_APPPS, view);
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
//        if (ROOM_TYPE_MULTI.equals(roomType)) {
//            mbtnOtrText.setVisibility(View.GONE);
//        }else{
//            mbtnOtrText.setVisibility(View.VISIBLE);
//        }
    }

    public void setIsBurnReading(boolean isBurnReading) {
        this.isBurnReading = isBurnReading;
    }

    public MenuGridListPopWindow setListMenu(Context context) {
        this.mContext = context;
        menu = new MenuGridListPopWindow(mContext, mEtChat, roomType, isBurnReading);
        menu.setColor(Color.BLACK);
        return menu;
    }

    private void show() {
        menu.showAtLocation();
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        mLyKvml.hideAllFuncView();
//        mBtnFace.setImageResource(com.keyboard.view.R.drawable.icon_face_nomal);
    }

    protected void showVoice() {
        /*mRlInput.setVisibility(GONE);
        mBtnVoice.setVisibility(VISIBLE);*/
        mRlText.setVisibility(GONE);
        mRlRecording.setVisibility(VISIBLE);
        reset();
    }


    protected void showText() {
        /*mRlInput.setVisibility(VISIBLE);
        mBtnVoice.setVisibility(GONE);*/
        mRlText.setVisibility(VISIBLE);
        mRlRecording.setVisibility(GONE);
    }

    protected void toggleFuncView(int key) {
        if (FUNC_TYPE_APPPS == key) {
            showVoice();
            show();
        } else {
            mLyKvml.toggleFuncView(key, isSoftKeyboardPop(), mEtChat);
        }
        showText();
    }


    @Override
    public void onFuncChange(int key) {
        /*if (FUNC_TYPE_EMOTION == key) {
            mBtnFace.setImageResource(R.drawable.icon_face_pop);
        } else {
            mBtnFace.setImageResource(R.drawable.icon_face_nomal);
        }*/
    }

    protected void setFuncViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLyKvml.getLayoutParams();
        params.height = height;
        mLyKvml.setLayoutParams(params);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        mLyKvml.updateHeight(height);
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        mLyKvml.setVisibility(true);
        onFuncChange(mLyKvml.DEF_KEY);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        if (mLyKvml.isOnlyShowSoftKeyboard()) {
            reset();
        } else {
            onFuncChange(mLyKvml.getCurrentFuncKey());
        }
    }

    public void addOnFuncKeyBoardListener(FuncLayout.OnFuncKeyBoardListener l) {
        mLyKvml.addOnKeyBoardListener(l);
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_voice_or_text) {
            cutKeyboard();
        } else if (i == R.id.btn_face) {
            toggleFuncView(FUNC_TYPE_EMOTION);
        } else if (i == R.id.btn_multimedia) {
            toggleFuncView(FUNC_TYPE_APPPS);
        } else if (i == R.id.tv_cancel) {
            cancelRecord();
        } else if (i == R.id.tv_recording_send) {
            sendRecord();
        } else if (i == R.id.tv_stop) {
            stopRecord();
        } else if (i == R.id.tv_play) {
            playRecord();
        } else if (i == R.id.tv_pause) {
            pauseRecord();
        } else if (i == R.id.btn_camera) {
            mListOnClick.onclickitem(mContext.getString(R.string.shooting));
        }/*else if(i==R.id.btn_otr_text){
            onResultOTR.resultOTR();
        }*/
    }

    private volatile boolean running = true;

    private class DecibelThread extends Thread {

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            super.run();
            while (running) {
                if (mOnRecordListener == null || !running) {
                    break;
                }
                mViewWave.post(new Runnable() {
                    @Override
                    public void run() {
                        setRecordDecibel(mOnRecordListener.getRecordDecibel());
                    }
                });
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRecordDecibel(int decibelRank) {
        if (mViewWave != null) {
            if (decibelRank < 0 || decibelRank > 6) {
                decibelRank = 0;
            }
            mViewWave.setVolume(decibelRank);
        }
    }

    private void cutKeyboard() {
        mTvTime.setText("00:00");
        showVoice();
        mOnRecordListener.recordStart();
        mViewWave.run();
        mDecibelThread = new DecibelThread();
        mDecibelThread.start();
        timekeeping();
    }

    private void cancelRecord() {
        mOnRecordListener.recordCancel();
        showText();
        closeTimer(0);
        mDecibelThread.exit();
        mTvPause.setVisibility(GONE);
        mTvPlay.setVisibility(GONE);
        mTvStop.setVisibility(VISIBLE);
    }

    private void sendRecord() {
        mOnRecordListener.recordFinish();
        showText();
        closeTimer(1);
        mDecibelThread.exit();
        mTvPause.setVisibility(GONE);
        mTvPlay.setVisibility(GONE);
        mTvStop.setVisibility(VISIBLE);
    }

    private void pauseRecord() {
        mMediaPlayer.stop();
        mTvPause.setVisibility(GONE);
        mTvPlay.setVisibility(VISIBLE);
    }

    private void playRecord() {
        mTvPlay.setVisibility(GONE);
        mTvPause.setVisibility(VISIBLE);
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mFileName);     //设置资源目录
            mMediaPlayer.prepare();//缓冲
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    mTvPause.setVisibility(GONE);
                    mTvPlay.setVisibility(VISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void stopRecord() {
        mRecordUtil.finish();
        closeTimer(2);
        mDecibelThread.exit();
        mTvStop.setVisibility(GONE);
        mTvPlay.setVisibility(VISIBLE);
        mFileName = mRecordUtil.getFileName();
    }

    private int mRecLen = 0;

    private void timekeeping() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(new Runnable() {      // UI thread
                    @Override
                    public void run() {
                        mRecLen++;
                        int m = mRecLen / 60;
                        int s = mRecLen % 60;
                        if (m != 0) {
                            if (s > 9) {
                                mTvTime.setText("0" + m + ":" + s);
                            } else {
                                mTvTime.setText("0" + m + ":0" + s);
                            }
                        } else {
                            if (s > 9) {
                                mTvTime.setText("00:" + s);
                            } else {
                                mTvTime.setText("00:0" + s);
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    private void closeTimer(int type) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mRecLen = 0;
        }
    }

    public void changeOTR(String isopen) {
//        if(ROOM_TYPE_MULTI.equals(roomType))return;
//        if("true".equals(isopen)){
//            mbtnOtrText.setImageResource(R.drawable.icon_encrypt_ch);
//            mEtChat.setHint(R.string.intput_otr_ch);
//        }else {
//            if(mContext.getString(R.string.start_otr).equals(mEtChat.getHint())){
//                return;
//            }
//            mbtnOtrText.setImageResource(R.drawable.icon_encrypt_no);
//            mEtChat.setHint(R.string.intput_otr_no);
//        }
    }

    public void timeoutOTR() {
        mEtChat.setHint(mContext.getString(R.string.intput_otr_no));
    }

    public void startOTR() {
        mEtChat.setHint(mContext.getString(R.string.start_otr));
    }

    public void addOnResultOTR(OnResultOTR onResultOTR) {
        this.onResultOTR = onResultOTR;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public void setRecordUtil(RecordUtil recordUtil) {
        mRecordUtil = recordUtil;
    }

    public void setListOnClick(MenuGridListPopWindow.ListOnClick listOnClick) {
        mListOnClick = listOnClick;
    }

    public void onResume() {
//        mViewWave.onResume();
    }

    public void onPause() {
//        mViewWave.onPause();
    }

    public void onDestroy() {
//        mViewWave.release();
    }

    public interface OnResultOTR {
        void resultOTR();
    }

    @Override
    public void onToolBarItemClick(PageSetEntity pageSetEntity) {
        mEmoticonsFuncView.setCurrentPageSet(pageSetEntity);
    }

    @Override
    public void onBackKeyClick() {
        if (mLyKvml.isShown()) {
            mDispatchKeyEventPreImeLock = true;
            reset();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false;
                    return true;
                }
                if (mLyKvml.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return false;
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return;
        }
        super.requestChildFocus(child, focused);
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext()) && mLyKvml.isShown()) {
                    reset();
                    return true;
                }
            default:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    boolean isFocused;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        isFocused = mEtChat.getShowSoftInputOnFocus();
                    } else {
                        isFocused = mEtChat.isFocused();
                    }
                    if (isFocused) {
                        mEtChat.onKeyDown(event.getKeyCode(), event);
                    }
                }
                return false;
        }
    }

    public EmoticonsEditText getEtChat() {
        return mEtChat;
    }

    public ImageView getBtnVoice() {
        return mBtnVoice;
    }

    public Button getBtnSend() {
        return mBtnSend;
    }

    public EditText getEditText() {
        return mEtChat;
    }

    public void setRecordIndicator(RecordIndicator recordIndicator) {
        this.recordIndicator = recordIndicator;
        if (mBtnVoice != null && !initRecordIndicator) {
            recordIndicator.setRecordButton(mBtnVoice, mTvCancel, mTvRecordingSend, mTvStop, mTvPlay);
        }
    }

    public EmoticonsFuncView getEmoticonsFuncView() {
        return mEmoticonsFuncView;
    }

    public FuncLayout getFuncView() {
        return mLyKvml;
    }

    public EmoticonsIndicatorView getEmoticonsIndicatorView() {
        return mEmoticonsIndicatorView;
    }

    public EmoticonsToolBarView getEmoticonsToolBarView() {
        return mEmoticonsToolBarView;
    }


    /**
     * 录音接口
     */
    public interface OnRecordListener {
        /**
         * 开始录音
         */
        void recordStart();

        /**
         * 结束录音
         */
        void recordFinish();

        /**
         * 取消录音
         */
        void recordCancel();

        /**
         * 获取录音时长
         *
         * @return
         */
        long getRecordTime();

        /**
         * 获取分贝等级
         *
         * @return
         */
        int getRecordDecibel();
    }
}
