package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.PublicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBPublicManage;
import com.bclould.tea.model.PublicDetailsInfo;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PublicDetailsActivity extends BaseActivity {
    @Bind(R.id.iv_head)
    ImageView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;

    private String id;
    private DBPublicManage mDBPublicManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_details);
        ButterKnife.bind(this);
        setTitle("",R.mipmap.icon_nav_more_selected);
        EventBus.getDefault().register(this);
        mDBPublicManage=new DBPublicManage(this);
        initGetintent();
        init();
    }

    private void setView(PublicDetailsInfo publicDetailsInfo){
        UtilTool.setCircleImg(this,publicDetailsInfo.getData().getLogo(),mIvHead);
        mTvName.setText(publicDetailsInfo.getData().getName());
        mTvDesc.setText(publicDetailsInfo.getData().getDesc());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void init() {
        setIvElse();
        setTvState();
        new PublicPresenter(this).publicDeltails(UtilTool.parseInt(id), new PublicPresenter.CallBack1() {
            @Override
            public void send(PublicDetailsInfo publicDetailsInfo) {
                mLlError.setVisibility(View.GONE);
                mLlData.setVisibility(View.VISIBLE);
                setView(publicDetailsInfo);
            }

            @Override
            public void error() {
                mLlError.setVisibility(View.VISIBLE);
                mLlData.setVisibility(View.GONE);
            }
        });
    }


    private void setIvElse(){
        if(mDBPublicManage.findPublic(id)){
            mImageView.setVisibility(View.VISIBLE);
        }else{
            mImageView.setVisibility(View.GONE);
        }
    }

    private void setTvState(){
        if(mDBPublicManage.findPublic(id)){
            mTvState.setText(getString(R.string.send_message));
        }else {
            mTvState.setText(getString(R.string.attention));
        }
    }

    @OnClick({R.id.bark, R.id.iv_more, R.id.tv_state,R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                //更多取關
                showDialog();
                break;
            case R.id.tv_state:
                //關注或者發消息
                if(mDBPublicManage.findPublic(id)) {
                    publicSend();
                }else {
                    publicAdd();
                }
                break;
            case R.id.ll_error:
                init();
                break;
        }
    }

    private void publicSend() {
        Intent intent=new Intent(this,ConversationPublicActivity.class);
        intent.putExtra("publicId",id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void publicAdd(){
        new PublicPresenter(this).publicAdd(UtilTool.parseInt(id), new PublicPresenter.CallBack1() {
            @Override
            public void send(PublicDetailsInfo publicDetailsInfo) {
                mDBPublicManage.addPublic(publicDetailsInfo.getData());
                EventBus.getDefault().post(new MessageEvent(getString(R.string.update_public_number)));
            }

            @Override
            public void error() {

            }
        });
    }

    private void initGetintent() {
        id = getIntent().getStringExtra("id");
    }

    private void showDialog() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.unfollow));
        final MenuListPopWindow menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        unfollow();
                        break;

                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void unfollow(){
        new PublicPresenter(this).publicUnfollow(UtilTool.parseInt(id), new PublicPresenter.CallBack2() {
            @Override
            public void send() {
                mDBPublicManage.deletePublic(id);
                Intent intent=new Intent(PublicDetailsActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                EventBus.getDefault().post(new MessageEvent(getString(R.string.update_public_number)));
                PublicDetailsActivity.this.finish();
            }

            @Override
            public void error() {

            }
        });
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.update_public_number))) {
            setIvElse();
            setTvState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
