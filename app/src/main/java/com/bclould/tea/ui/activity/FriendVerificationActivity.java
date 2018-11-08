package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.Presenter.IndividualDetailsPresenter;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FriendVerificationActivity extends BaseActivity {
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_region)
    TextView mTvRegion;
    private String userId;
    private String name;
    private String avatar;
    private String country;
    private IndividualDetailsPresenter mPresenter;
    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_verification);
        ButterKnife.bind(this);
        mDBManager=new DBManager(this);
        initIntent();
        initView();
        initData();
    }

    private void initIntent() {
        userId = getIntent().getStringExtra("user");
        name = getIntent().getStringExtra("name");
        avatar = getIntent().getStringExtra("avatar");
    }

    private void initView() {
        mTvName.setText(name);
        mTvRegion.setText(country);
        if (StringUtils.isEmpty(avatar)) {
            UtilTool.setCircleImg(this, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
        } else {
            UtilTool.setCircleImg(this, avatar, mIvTouxiang);
        }
    }

    @OnClick({R.id.bark, R.id.rl_dynamic, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_dynamic:
                Intent intent = new Intent(this, PersonageDynamicActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("user", userId);
                startActivity(intent);
                break;
            case R.id.btn_send:
                agree();
                break;
        }
    }

    private void agree() {
        try {
            new PersonalDetailsPresenter(this).confirmAddFriend(userId, 1, new PersonalDetailsPresenter.CallBack() {
                @Override
                public void send() {
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.new_friend));
                    UserInfo userInfo=new UserInfo();
                    userInfo.setUser(userId);
                    userInfo.setUserName(name);
                    userInfo.setRemark(name);
                    userInfo.setPath(avatar);
                    mDBManager.addUser(userInfo);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        mPresenter = new IndividualDetailsPresenter(this);
        mPresenter.getIndividual(userId, true, new IndividualDetailsPresenter.CallBack() {
            @Override
            public void send(IndividualInfo.DataBean data) {
                if (!FriendVerificationActivity.this.isDestroyed()) {
                    if (data == null) return;
                    name = data.getName();
                    country = data.getCountry();
                    avatar = data.getAvatar();
                    initView();
                }
            }

            @Override
            public void error() {

            }
        });
    }
}
