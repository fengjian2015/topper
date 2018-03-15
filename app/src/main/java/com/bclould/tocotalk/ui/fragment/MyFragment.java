package com.bclould.tocotalk.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.ui.activity.PersonalDetailsActivity;
import com.bclould.tocotalk.ui.activity.ShenFenVerifyActivity;
import com.bclould.tocotalk.ui.activity.SystemSetActivity;
import com.bclould.tocotalk.ui.activity.UserSafetyActivity;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

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
    @Bind(R.id.rl_personal_data)
    RelativeLayout mRlPersonalData;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.rl_autonym_attestation)
    RelativeLayout mRlAutonymAttestation;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.rl_security_center)
    RelativeLayout mRlSecurityCenter;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.rl_system_set)
    RelativeLayout mRlSystemSet;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.iv_id)
    TextView mIvId;
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
        EventBus.getDefault().register(this);
        init();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals("修改头像")) {
            Bitmap bitmap = UtilTool.getMyImage(mMgr, UtilTool.getMyUser());
            if (bitmap != null)
                mIvTouxiang.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mTvName.setText(UtilTool.getMyUser().substring(0, UtilTool.getMyUser().lastIndexOf("@")));
        mMgr = new DBManager(getContext());
        Bitmap bitmap = UtilTool.getMyImage(mMgr, UtilTool.getMyUser());
        if (bitmap != null)
            mIvTouxiang.setImageBitmap(bitmap);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.rl_personal_data, R.id.rl_autonym_attestation, R.id.rl_security_center, R.id.rl_system_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_personal_data:
                startActivity(new Intent(getActivity(), PersonalDetailsActivity.class));
                break;
            case R.id.rl_autonym_attestation:
                startActivity(new Intent(getActivity(), ShenFenVerifyActivity.class));
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
