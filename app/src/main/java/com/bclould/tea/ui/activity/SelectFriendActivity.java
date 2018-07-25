package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.adapter.SelectFriendAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.activity.SelectConversationActivity.IMAGE_TYPE;
import static com.bclould.tea.ui.activity.SelectConversationActivity.TEXT_PLAIN;
import static com.bclould.tea.ui.activity.SelectConversationActivity.TYPE_HTML;
import static com.bclould.tea.ui.activity.SelectConversationActivity.VIDEO_TYPE;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_FILE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_HTML_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_INVITE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_FILE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_HTML_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_INVITE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VIDEO_MSG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectFriendActivity extends BaseActivity implements SelectFriendAdapter.OnItemListener, MessageManageListener {

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.image)
    ImageView mImage;

    private List<UserInfo> mUsers = new ArrayList<>();
    private SelectFriendAdapter selectFriendAdapter;
    private DBManager mMgr;
    private String shareType;
    private Intent shareIntent;
    private String filePath;
    private String shareText;
    private int msgType;
    private MessageInfo messageInfo;
    private LoadingProgressDialog mProgressDialog;

    //系統信息

    private int type = 0;//默認0 代表外部分享過來， 1表示轉發 2表示內部請求分享

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);
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
        filePath=getIntent().getStringExtra("filePath");
        shareType=getIntent().getStringExtra("shareType");
        shareText=getIntent().getStringExtra("shareText");
//        //具体还有其他的类型，请上网参考
//        if (Intent.ACTION_SEND.equals(text) && type != null) {
//            this.shareType = type;
//        }else {
//            return;
//        }
//
//        if (shareType.contains(IMAGE_TYPE)) {
//            Glide.with(this).load(uri).listener(new RequestListener<Drawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    return false;
//                }
//                @Override
//                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            filePath = UtilTool.getImgPathFromCache(uri,SelectFriendActivity.this);
//                        }
//                    }.start();
//                    return false;
//                }
//            }).into(mImage);
//        }else if(shareType.contains(VIDEO_TYPE)){
//            //系統分享視頻
//            filePath=UtilTool.getRealFilePath(this,uri);
//        }
    }


    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectFriendAdapter = new SelectFriendAdapter(this, mUsers, mMgr);
        mRecyclerView.setAdapter(selectFriendAdapter);
        selectFriendAdapter.addOnItemListener(this);
        updateData();
    }

    private void updateData() {
        mUsers.clear();
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getTocoId())) {
                userInfo = info;
            } else if (info.getUser().isEmpty()) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUsers.addAll(userInfos);
        Collections.sort(mUsers);
        selectFriendAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;

        }
    }

    private void showDeleteDialog(String remark, final String name, final String user) {
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
                sendMessage(user, name);
                deleteCacheDialog.dismiss();
            }
        });
    }


    private Room mRoom;

    private void sendMessage(String user, String name) {
        mRoom = RoomManage.getInstance().addSingleMessageManage(user, name);
        mRoom.addMessageManageListener(this);
        if (type == 0) {
            if (shareType.contains(TEXT_PLAIN)) {
                messageInfo = mRoom.sendMessage(shareText);
                if (messageInfo != null) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_complete));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_failure));
                }
            } else if (shareType.contains(IMAGE_TYPE)) {
//                showDialog();
                if(filePath==null){
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_failure));
//                    hideDialog();
                    return;
                }
                mRoom.Upload(filePath);
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_complete));
                close();
            }else if(shareType.contains(VIDEO_TYPE)){
//                showDialog();
                if(filePath==null){
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_failure));
//                    hideDialog();
                    return;
                }
                mRoom.Upload(filePath);
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_complete));
                close();
            }else if(shareType.contains(TYPE_HTML)){
                MessageInfo messageInfo=new MessageInfo();
                messageInfo.setMessage(shareText);
                mRoom.sendHtml(messageInfo);
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.share_complete));
                close();
            }
        } else if (type == 1) {
            if (msgType == TO_TEXT_MSG || msgType == FROM_TEXT_MSG) {
                String message = messageInfo.getMessage();
                messageInfo = mRoom.sendMessage(message);
                if (messageInfo != null) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_success));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_failure));
                }
            } else if (msgType == FROM_IMG_MSG || msgType == TO_IMG_MSG) {
//                showDialog();
                if (messageInfo.getMessage().startsWith("http")) {
                    mRoom.transmitImage(messageInfo);
                }else {
                    mRoom.Upload(messageInfo.getVoice());
                }
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_success));
                close();
            } else if (msgType == FROM_VIDEO_MSG || msgType == TO_VIDEO_MSG) {
//                showDialog();
                if (messageInfo.getMessage().startsWith("http")) {
                    mRoom.transmitVideo(messageInfo);
                } else {
                    mRoom.Upload(messageInfo.getMessage());
                }
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_success));
                close();
            }else if(msgType==FROM_FILE_MSG||msgType==TO_FILE_MSG){
                if(messageInfo.getVoice()!=null&&!messageInfo.getVoice().startsWith("http")){
                    mRoom.uploadFile(messageInfo.getVoice());
                }else if (messageInfo.getMessage().startsWith("http")) {
                    mRoom.transmitFile(messageInfo);
                } else {
                    mRoom.uploadFile(messageInfo.getMessage());
                }
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_success));
                SelectFriendActivity.this.finish();
            }else if(msgType==TO_HTML_MSG||msgType==FROM_HTML_MSG){
                mRoom.sendHtml(messageInfo);
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_success));
                close();
            }
        } else if (type == 2) {
            if (TO_CARD_MSG == msgType || msgType == FROM_CARD_MSG) {
                if (mRoom.sendCaed(messageInfo)) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_error));
                }
            } else if (TO_LINK_MSG == msgType || msgType == FROM_LINK_MSG) {
                if (mRoom.sendShareLink(messageInfo)) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_error));
                }
            } else if (TO_GUESS_MSG == msgType || msgType == FROM_GUESS_MSG) {
                if (mRoom.sendShareGuess(messageInfo)) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_error));
                }
            }else if (msgType == FROM_IMG_MSG || msgType == TO_IMG_MSG) {
//                showDialog();
                mRoom.Upload(messageInfo.getVoice());
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                close();
            }else if(msgType == FROM_INVITE_MSG || msgType == TO_INVITE_MSG){
                mRoom.sendInviteGroup(messageInfo);
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                close();
            }else if (msgType == TO_TEXT_MSG || msgType == FROM_TEXT_MSG) {
                String message = messageInfo.getMessage();
                messageInfo = mRoom.sendMessage(message);
                if (messageInfo != null) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_error));
                }
            }else if(msgType==TO_HTML_MSG||msgType==FROM_HTML_MSG){
                mRoom.sendHtml(messageInfo);
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.send_succeed));
                close();
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
    public void onItemClick(String remark, String name, String user) {
        showDeleteDialog(remark, name, user);

    }

    @Override
    public void refreshAddData(MessageInfo messageInfo) {

    }

    @Override
    public void sendError(int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_failure));
            }
        });
    }

    @Override
    public void sendFileResults(String newFile2, final boolean isSuccess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                if (isSuccess) {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_success));
                    close();
                } else {
                    ToastShow.showToast2(SelectFriendActivity.this, getString(R.string.forward_failure));
                }
            }
        });
    }

    @Override
    public void sendFile(String msgId, boolean isSuccess) {

    }

    private void close() {
        EventBus.getDefault().post(new MessageEvent(getString(R.string.close_activity)));
        SelectFriendActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        if (mRoom != null) {
            mRoom.removerMessageManageListener(this);
        }
        super.onDestroy();
    }
}
