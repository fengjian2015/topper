package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.Presenter.IndividualDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.MYUSERNAME;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ModificationNameActivity extends BaseActivity {

    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.et_announcement)
    EditText mEtAnnouncement;
    private int type = 0;//1修改我在本群暱稱，2修改群暱稱 3.修改公告 4:修改自己昵称
    private String content;
    private String roomId;
    private String tocoId;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_name);
        ButterKnife.bind(this);
        setTitle(getString(R.string.my_nickname_group),getString(R.string.finish));
        mDBRoomMember = new DBRoomMember(this);
        mDBRoomManage = new DBRoomManage(this);
        mDBManager = new DBManager(this);
        initIntent();
        initData();
    }

    private void initData() {
        if (type == 1) {
            mTvTitleTop.setText(getString(R.string.my_nickname_group));
        } else if (type == 2) {
            mTvTitleTop.setText(getString(R.string.modify_group_name));
        }else if(type==3){
            mTvTitleTop.setText(getString(R.string.gonggao));
            mEtName.setVisibility(View.GONE);
            mEtAnnouncement.setVisibility(View.VISIBLE);
        }else if(type==4){
            mTvTitleTop.setText(getString(R.string.change_username));
            InputFilter[] filters = {new InputFilter.LengthFilter(20)};
            mEtName.setFilters(filters);
        }
        mEtName.setText(content);
        mEtName.setSelection(mEtName.getText().length());
        mEtAnnouncement.setText(content);
        mEtAnnouncement.setSelection(mEtAnnouncement.getText().length());
    }


    private void initIntent() {
        type = getIntent().getIntExtra("type", 0);
        content = getIntent().getStringExtra("content");
        roomId = getIntent().getStringExtra("roomId");
        tocoId = getIntent().getStringExtra("tocoId");
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                commit();
                break;

        }
    }

    private void commit() {
        final String name ;
        if(type==3){
            name=mEtAnnouncement.getText().toString();
        }else{
            name = mEtName.getText().toString();
        }
        if (type == 1) {
            new GroupPresenter(this).updataGroupMemberName(Integer.parseInt(roomId), name, new GroupPresenter.CallBack() {
                @Override
                public void send() {
                    mDBRoomMember.updateRoom(roomId, tocoId, name);
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.my_nickname_group));
                    messageEvent.setName(name);
                    EventBus.getDefault().post(messageEvent);
                    finish();
                }
            });
        } else if (type == 2) {
            new GroupPresenter(this).updataGroupName(Integer.parseInt(roomId), name, new GroupPresenter.CallBack() {
                @Override
                public void send() {
                    mDBRoomManage.updateRoom(roomId, name);
                    mDBManager.updateConversationFriend(roomId, name);
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.modify_group_name));
                    messageEvent.setName(name);
                    EventBus.getDefault().post(messageEvent);
                    finish();
                }
            });
        }else if(type==3){
            new GroupPresenter(this).updateBullet(Integer.parseInt(roomId), name, new GroupPresenter.CallBack() {
                @Override
                public void send() {
                    mDBRoomManage.updateDescription(roomId, name);
                    MessageEvent messageEvent = new MessageEvent(getString(R.string.modify_group_announcement));
                    messageEvent.setName(name);
                    EventBus.getDefault().post(messageEvent);
                    finish();
                }
            });
        }else if(type==4){
            new IndividualDetailsPresenter(this).getChangeName(name, new IndividualDetailsPresenter.CallBack1() {
                @Override
                public void send(BaseInfo baseInfo) {
                    MySharedPreferences.getInstance().setString(MYUSERNAME, baseInfo.getData().getName());
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.change_name));
                    finish();
                }

                @Override
                public void error() {

                }
            });


        }
    }
}
