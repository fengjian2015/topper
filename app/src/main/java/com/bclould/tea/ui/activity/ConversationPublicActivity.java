package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBPublicManage;
import com.bclould.tea.model.PublicMenuInfo;
import com.bclould.tea.ui.widget.MenuLinearLayout;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationPublicActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.ll_chat)
    LinearLayout mLlChat;
    @Bind(R.id.ll_menu)
    MenuLinearLayout mLlMenu;

    private String publicID;
    private DBPublicManage mDBPublicManage;
    private PublicMenuInfo mPublicMenuInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_public);
        ButterKnife.bind(this);
        setTitle("",R.mipmap.icon_nav_more_selected);
        MyApp.getInstance().addActivity(this);
        EventBus.getDefault().register(this);//初始化EventBus
        mDBPublicManage=new DBPublicManage(this);
        initGetintent();
        initData();
    }

    private void initGetintent() {
        publicID=getIntent().getStringExtra("publicId");
    }

    private void initData() {
        mTvTitleTop.setText(mDBPublicManage.findPublicName(publicID));
        setMenu();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void setMenu(){
        String menu=mDBPublicManage.findPublicMenu(publicID);
        mLlMenu.setMenuData(menu);
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.update_public_number))) {
            //更新公眾號
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//
    }

    @OnClick({R.id.bark,R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                goPublicDetail();
                break;
        }
    }

    private void goPublicDetail() {
        Intent intent=new Intent(this,PublicDetailsActivity.class);
        intent.putExtra("id",publicID);
        startActivity(intent);
    }
}
