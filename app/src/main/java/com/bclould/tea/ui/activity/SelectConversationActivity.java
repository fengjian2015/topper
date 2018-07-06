package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.adapter.SelectConversationAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.MessageManageListener;
import com.bclould.tea.xmpp.Room;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VIDEO_MSG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectConversationActivity extends BaseActivity implements SelectConversationAdapter.OnItemListener, MessageManageListener {

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_select_friend)
    TextView mTvSelectFriend;
    @Bind(R.id.image)
    ImageView mImage;

    public static final String TEXT_PLAIN = "text";
    public static final String IMAGE_TYPE = "image";
    public static final String VIDEO_TYPE= "video";


    private List<ConversationInfo> showlist = new ArrayList<>();
    private SelectConversationAdapter mSelectConversationAdapter;
    private DBManager mMgr;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private String shareType;
    private String shareText;
    private Intent shareIntent;
    private Uri uri;
    private String filePath;
    private int msgType;
    private MessageInfo messageInfo;
    private LoadingProgressDialog mProgressDialog;

    private int type = 0;//默認0 代表外部分享過來， 1表示轉發 2表示內部請求分享
    //修改本頁面的同時，需要修改SelectFriendActivity頁面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.isGoStartActivity(this);
        setContentView(R.layout.activity_select_conversation);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        EventBus.getDefault().register(this);//初始化EventBus
        mMgr = new DBManager(this);
        mDBRoomMember = new DBRoomMember(this);
        mDBRoomManage=new DBRoomManage(this);
        initRecylerView();
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            getShareIntent();
        } else {
            getChatIntent();
        }
    }


    private void getChatIntent() {
        messageInfo = (MessageInfo) getIntent().getSerializableExtra("messageInfo");
        msgType = getIntent().getIntExtra("msgType", 0);
    }

    private void getShareIntent() {
        shareIntent = getIntent();
        Bundle bundle = shareIntent.getExtras();
        String text = shareIntent.getAction();  //分享的action都是Intent.ACTION_SEND
        String type = shareIntent.getType();//获取分享来的数据类型，和上面<data android:mimeType="text/plain" />中的一致
        shareText = shareIntent.getStringExtra(Intent.EXTRA_TEXT);
        uri = bundle.getParcelable(Intent.EXTRA_STREAM);

        //具体还有其他的类型，请上网参考
        if (Intent.ACTION_SEND.equals(text) && type != null) {
            this.shareType = type;
        }
        if (shareType.contains(IMAGE_TYPE)) {
            Glide.with(this).load(uri).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    new Thread() {
                        @Override
                        public void run() {
                            filePath = UtilTool.getImgPathFromCache(uri,SelectConversationActivity.this);
                        }
                    }.start();
                    return false;
                }
            }).into(mImage);
        }else if(shareType.contains(VIDEO_TYPE)){
            //系統分享視頻
            filePath=UtilTool.getRealFilePath(this,uri);
        }
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSelectConversationAdapter = new SelectConversationAdapter(this, showlist, mMgr, mDBRoomMember,mDBRoomManage);
        mRecyclerView.setAdapter(mSelectConversationAdapter);
        mSelectConversationAdapter.addOnItemListener(this);
        updateData();
    }

    private void updateData() {
        if (mRecyclerView != null) {
            new RefreshList().run();
        }
    }

    class RefreshList implements Runnable {
        @Override
        public void run() {
            synchronized (mRecyclerView){
                List<ConversationInfo> conversationInfos = mMgr.queryConversation();
                showlist.removeAll(showlist);
                showlist.addAll(conversationInfos);
                sort();
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mSelectConversationAdapter.notifyDataSetChanged();
        }
    };

    private void sort() {
        //将showlist按时间排成倒序
        Collections.sort(showlist, new Comparator<ConversationInfo>() {
            @Override
            public int compare(ConversationInfo conversationInfo, ConversationInfo conversationInfo2) {
                String at = conversationInfo.getTime();
                String bt = conversationInfo2.getTime();
                String atop = conversationInfo.getIstop();
                String btop = conversationInfo2.getIstop();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if ("true".equals(btop) && !"true".equals(atop)) {
                        return 1;
                    } else if (!"true".equals(btop) && "true".equals(atop)) {
                        return -1;
                    } else if (formatter.parse(bt).getTime() > formatter.parse(at).getTime()) {
                        return 1;
                    } else if (formatter.parse(bt).getTime() < formatter.parse(at).getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_select_friend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_select_friend:
                Intent intent = new Intent(this, SelectFriendActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("msgType", msgType);
                intent.putExtra("messageInfo", messageInfo);
                intent.putExtra("shareText",shareText);
                intent.putExtra("filePath",filePath);
                intent.putExtra("shareType",shareType);
                startActivity(intent);
                break;
        }
    }

    private void showDeleteDialog(String remark, final String name, final String user, final String chatType) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.confirm_send_to) + remark);
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
                sendMessage(user, name, chatType);
                deleteCacheDialog.dismiss();
            }
        });
    }

    private Room mRoom;

    private void sendMessage(String user, String name, String chatType) {
        if (RoomManage.ROOM_TYPE_MULTI.equals(chatType)) {
            mRoom = RoomManage.getInstance().addMultiMessageManage(user, name);
        } else {
            mRoom = RoomManage.getInstance().addSingleMessageManage(user, name);
        }
        mRoom.addMessageManageListener(this);
        if (type == 0) {
            if (shareType.contains(TEXT_PLAIN)) {
                messageInfo = mRoom.sendMessage(shareText);
                if (messageInfo != null) {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.share_complete));
                    SelectConversationActivity.this.finish();
                } else {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.share_failure));
                }
            } else if (shareType.contains(IMAGE_TYPE)) {
//                showDialog();
                if(filePath==null){
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.share_failure));
//                    hideDialog();
                    return;
                }
                mRoom.Upload(filePath);
                ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.share_complete));
                SelectConversationActivity.this.finish();
            }else if(shareType.contains(VIDEO_TYPE)){
//                showDialog();
                if(filePath==null){
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.share_failure));
//                    hideDialog();
                    return;
                }
                mRoom.Upload(filePath);
                ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.share_complete));
                SelectConversationActivity.this.finish();
            }
        } else if (type == 1) {
            if (msgType == TO_TEXT_MSG || msgType == FROM_TEXT_MSG) {
                String message = messageInfo.getMessage();
                messageInfo = mRoom.sendMessage(message);
                if (messageInfo != null) {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_success));
                    SelectConversationActivity.this.finish();
                } else {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_failure));
                }
            } else if (msgType == FROM_IMG_MSG || msgType == TO_IMG_MSG) {
//                showDialog();
                mRoom.Upload(messageInfo.getVoice());
                ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_success));
                SelectConversationActivity.this.finish();
            } else if (msgType == FROM_VIDEO_MSG || msgType == TO_VIDEO_MSG) {
//                showDialog();
                if (messageInfo.getMessage().startsWith("http")) {
                    mRoom.transmitVideo(messageInfo);
                } else {
                    mRoom.Upload(messageInfo.getMessage());
                }
                ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_success));
                SelectConversationActivity.this.finish();
            }
        } else if (type == 2) {
            if (TO_CARD_MSG == msgType || msgType == FROM_CARD_MSG) {
                if (mRoom.sendCaed(messageInfo)) {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.send_succeed));
                    SelectConversationActivity.this.finish();
                } else {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.send_error));
                }
            } else if (TO_LINK_MSG == msgType || msgType == FROM_LINK_MSG) {
                if (mRoom.sendShareLink(messageInfo)) {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.send_succeed));
                    SelectConversationActivity.this.finish();
                } else {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.send_error));
                }
            } else if (TO_GUESS_MSG == msgType || msgType == FROM_GUESS_MSG) {
                if (mRoom.sendShareGuess(messageInfo)) {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.send_succeed));
                    SelectConversationActivity.this.finish();
                } else {
                    ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.send_error));
                }
            }
        }
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(this.getString(R.string.send_in_progress));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onItemClick(String remark, String name, String user, String chatType) {
        showDeleteDialog(remark, name, user, chatType);

    }

    @Override
    public void refreshAddData(MessageInfo messageInfo) {

    }

    @Override
    public void sendError(int id) {
        hideDialog();
        ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_failure));
    }

    @Override
    public void sendFileResults(String newFile2, boolean isSuccess) {
        hideDialog();
        if (isSuccess) {
            ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_success));
            SelectConversationActivity.this.finish();
        } else {
            ToastShow.showToast2(SelectConversationActivity.this, getString(R.string.forward_failure));
        }
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.close_activity))) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mRoom != null) {
            mRoom.removerMessageManageListener(this);
        }
        EventBus.getDefault().unregister(this);//初始化EventBus
        super.onDestroy();
    }
}
