package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ModificationNameActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    private int type = 0;//1修改我在本群暱稱，2修改群暱稱
    private String content;
    private String roomId;
    private String tocoId;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_modification_name);
        ButterKnife.bind(this);
        mDBRoomMember=new DBRoomMember(this);
        mDBRoomManage=new DBRoomManage(this);
        mDBManager=new DBManager(this);
        initIntent();
        initData();
    }

    private void initData() {
        if (type == 1) {
            mTvTitle.setText(getString(R.string.my_nickname_group));
        } else if (type == 2) {
            mTvTitle.setText(getString(R.string.modify_group_name));
        }
        mEtName.setText(content);
        mEtName.setSelection(mEtName.getText().length());
    }

    private void initIntent() {
        type = getIntent().getIntExtra("type", 0);
        content=getIntent().getStringExtra("content");
        roomId=getIntent().getStringExtra("roomId");
        tocoId=getIntent().getStringExtra("tocoId");
    }

    @OnClick({R.id.bark, R.id.tv_hint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_hint:
                commit();
                break;

        }
    }

    private void commit() {
        final String name=mEtName.getText().toString();
        if(type==1){
            new GroupPresenter(this).updataGroupMemberName(Integer.parseInt(roomId), name, new GroupPresenter.CallBack() {
                @Override
                public void send() {
                    mDBRoomMember.updateRoom(roomId,tocoId,name);
                    MessageEvent messageEvent= new MessageEvent(getString(R.string.my_nickname_group));
                    messageEvent.setName(name);
                    EventBus.getDefault().post(messageEvent);
                    finish();
                }
            });
        }else if(type==2){
            new GroupPresenter(this).updataGroupName(Integer.parseInt(roomId), name, new GroupPresenter.CallBack() {
                @Override
                public void send() {
                    mDBRoomManage.updateRoom(roomId,name);
                    mDBManager.updateConversationFriend(roomId,name);
                    MessageEvent messageEvent= new MessageEvent(getString(R.string.modify_group_name));
                    messageEvent.setName(name);
                    EventBus.getDefault().post(messageEvent);
                    finish();
                }
            });
        }
    }
}