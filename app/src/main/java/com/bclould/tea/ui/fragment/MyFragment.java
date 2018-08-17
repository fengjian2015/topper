package com.bclould.tea.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.CollectActivity;
import com.bclould.tea.ui.activity.DynamicActivity;
import com.bclould.tea.ui.activity.GuanYuMeActivity;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.activity.LoginActivity;
import com.bclould.tea.ui.activity.PersonalDetailsActivity;
import com.bclould.tea.ui.activity.SystemSetActivity;
import com.bclould.tea.ui.activity.UserSafetyActivity;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyFragment extends Fragment {

    public static MyFragment instance = null;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.rl_personal_data)
    RelativeLayout mRlPersonalData;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.rl_collect)
    RelativeLayout mRlCollect;
    @Bind(R.id.iv5)
    ImageView mIv5;
    @Bind(R.id.rl_dynamic)
    RelativeLayout mRlDynamic;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.rl_security_center)
    RelativeLayout mRlSecurityCenter;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_system_set)
    RelativeLayout mRlSystemSet;
    @Bind(R.id.iv_concern_we)
    ImageView mIvConcernWe;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_new_update)
    TextView mTvNewUpdate;
    @Bind(R.id.rl_concern_we)
    RelativeLayout mRlConcernWe;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.rl_already_login)
    RelativeLayout mRlAlreadyLogin;
    @Bind(R.id.iv_login)
    ImageView mIvLogin;
    @Bind(R.id.rl_no_login)
    RelativeLayout mRlNoLogin;


    private DBManager mMgr;

    public static MyFragment getInstance() {
        if (instance == null) {
            instance = new MyFragment();
        }
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        init();
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.xg_touxaing))) {
            /*Bitmap bitmap = UtilTool.getImage(mMgr, UtilTool.getTocoId(), getContext());
            if (bitmap != null)
                mIvTouxiang.setImageBitmap(bitmap);*/
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), getContext(), mIvTouxiang);
        } else if (msg.equals(getString(R.string.check_new_version))) {
            init();
        } else if (msg.equals(getString(R.string.refresh_the_interface))) {
            init();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        Locale locale = getResources().getConfiguration().locale;
        String country = locale.getCountry();
        if (UtilTool.compareVersion(getContext())) {
            mTvNewUpdate.setVisibility(View.VISIBLE);
        } else {
            mTvNewUpdate.setVisibility(View.GONE);
        }
        if (WsConnection.getInstance().getOutConnection()) {
            mRlAlreadyLogin.setVisibility(View.GONE);
            mRlNoLogin.setVisibility(View.VISIBLE);
        } else {
            mRlAlreadyLogin.setVisibility(View.VISIBLE);
            mRlNoLogin.setVisibility(View.GONE);
        }
        mTvName.setText(UtilTool.getUser());
//        mTvTocoid.setText(getString(R.string.id) + UtilTool.getTocoId());
        if (mMgr == null) {
            mMgr = new DBManager(getContext());
        }
        UtilTool.getImage(mMgr, UtilTool.getTocoId(), getContext(), mIvTouxiang);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.rl_dynamic, R.id.rl_collect, R.id.rl_already_login, R.id.rl_security_center, R.id.rl_system_set, R.id.rl_concern_we, R.id.rl_no_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_already_login:
                startActivity(new Intent(getActivity(), PersonalDetailsActivity.class));
                break;
            case R.id.rl_security_center:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), UserSafetyActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_system_set:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), SystemSetActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_concern_we:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), GuanYuMeActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_dynamic:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), DynamicActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_collect:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), CollectActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_no_login:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
        }
    }
}
