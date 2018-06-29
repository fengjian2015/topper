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
import com.bclould.tea.ui.activity.PersonalDetailsActivity;
import com.bclould.tea.ui.activity.SystemSetActivity;
import com.bclould.tea.ui.activity.UserSafetyActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @Bind(R.id.tv_tocoid)
    TextView mTvTocoid;
    @Bind(R.id.rl_personal_data)
    RelativeLayout mRlPersonalData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.rl_security_center)
    RelativeLayout mRlSecurityCenter;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_new_update)
    TextView mTvNewUpdate;
    @Bind(R.id.rl_system_set)
    RelativeLayout mRlSystemSet;


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
        if (!MySharedPreferences.getInstance().getString(Constants.NEW_APK_URL).isEmpty()) {
            mTvNewUpdate.setVisibility(View.VISIBLE);
        } else {
            mTvNewUpdate.setVisibility(View.GONE);
        }
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
            mTvNewUpdate.setVisibility(View.VISIBLE);
        } else if (msg.equals(getString(R.string.refresh_the_interface))) {
            init();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mTvName.setText(UtilTool.getUser());
        mTvTocoid.setText(getString(R.string.id) + UtilTool.getTocoId());
        mMgr = new DBManager(getContext());
        /*Bitmap bitmap = UtilTool.getImage(mMgr, UtilTool.getTocoId(), getContext());
        if (bitmap != null)
            mIvTouxiang.setImageBitmap(bitmap);*/
        UtilTool.getImage(mMgr, UtilTool.getTocoId(), getContext(), mIvTouxiang);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_personal_data, R.id.rl_security_center, R.id.rl_system_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_personal_data:
                startActivity(new Intent(getActivity(), PersonalDetailsActivity.class));
                break;
            case R.id.rl_security_center:
                startActivity(new Intent(getActivity(), UserSafetyActivity.class));
                break;
            case R.id.rl_system_set:
                startActivity(new Intent(getActivity(), SystemSetActivity.class));
                break;
        }
    }
}
