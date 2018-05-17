package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.adapter.FriendListRVAdapter;
import com.bclould.tocotalk.ui.adapter.SelectFriendAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.MyListView;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.MessageManage;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectFriendActivity extends BaseActivity implements SelectFriendAdapter.OnItemListener{

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.listView)
    MyListView mListView;

    public static final String TEXT_PLAIN="text/plain";

    private List<UserInfo> mUsers = new ArrayList<>();
    private SelectFriendAdapter selectFriendAdapter;
    private DBManager mMgr;
    private String type;
    private Intent shareIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);
        initRecylerView();
        getShareIntent();
    }

    private void getShareIntent(){
        shareIntent = getIntent();
        String text = shareIntent.getAction();  //分享的action都是Intent.ACTION_SEND
        String type = shareIntent.getType();//获取分享来的数据类型，和上面<data android:mimeType="text/plain" />中的一致
        //具体还有其他的类型，请上网参考
        if (Intent.ACTION_SEND.equals(text) && type != null) {
            this.type=type;
        }
    }

    private void initRecylerView() {
        selectFriendAdapter = new SelectFriendAdapter(this, mUsers, mMgr);
        mListView.setAdapter(selectFriendAdapter);
        selectFriendAdapter.addOnItemListener(this);
        updateData();
    }

    private void updateData() {
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getJid())) {
                userInfo = info;
            } else if (info.getUser().isEmpty()) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUsers.addAll(userInfos);
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

    private void showDeleteDialog(final String name, final String user) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.confirm_send_to)+ name );
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
                sendMessage(user,name);
            }
        });
    }

    private void sendMessage(String user, String name){
        if(TEXT_PLAIN.equals(type)){
            String shareText= shareIntent.getStringExtra(Intent.EXTRA_TEXT);
            MessageInfo messageInfo= new MessageManage(new DBManager(SelectFriendActivity.this),user,SelectFriendActivity.this,name).sendMessage(shareText);
            if(messageInfo!=null){
                ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.share_complete));
                SelectFriendActivity.this.finish();
            }else{
                ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.share_failure));
            }
        }
    }

    @Override
    public void onItemClick(String name, String user) {
        showDeleteDialog(name,user);

    }
}
