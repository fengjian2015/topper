package com.bclould.tocotalk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.RemarkActivity;
import com.bclould.tocotalk.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

public class RelateToTaFragment extends Fragment {

    private static final String MSGREMIND = "msg_remind";
    private static final String SHIELDMSG = "shield_msg";
    public static RelateToTaFragment instance = null;
    @Bind(R.id.rl_remark)
    RelativeLayout mRlRemark;
    @Bind(R.id.rl_zhuanzhang)
    RelativeLayout mRlZhuanzhang;
    @Bind(R.id.rl_group_chat)
    RelativeLayout mRlGroupChat;
    @Bind(R.id.msg_remind)
    ImageView mMsgRemind;
    @Bind(R.id.rl_message_remind)
    RelativeLayout mRlMessageRemind;
    @Bind(R.id.shield_msg)
    ImageView mShieldMsg;
    @Bind(R.id.rl_shield_message)
    RelativeLayout mRlShieldMessage;

    public static RelateToTaFragment getInstance() {

        if (instance == null) {

            instance = new RelateToTaFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_relate_to, container, false);

        ButterKnife.bind(this, view);

        initInterface();

        return view;
    }

    //初始化界面
    private void initInterface() {

        boolean b = MySharedPreferences.getInstance().getBoolean(MSGREMIND);

        isMsgRemind = b;

        mMsgRemind.setSelected(b);

        boolean b2 = MySharedPreferences.getInstance().getBoolean(SHIELDMSG);

        isShieldMsg = b2;

        mShieldMsg.setSelected(b2);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    boolean isMsgRemind = false;
    boolean isShieldMsg = false;

    @OnClick({R.id.rl_remark, R.id.rl_zhuanzhang, R.id.rl_group_chat, R.id.msg_remind, R.id.shield_msg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_remark:

                startActivity(new Intent(getActivity(), RemarkActivity.class));

                break;
            case R.id.rl_zhuanzhang:
                break;
            case R.id.rl_group_chat:
                break;
            case R.id.msg_remind:

                //消息提醒设置
                setMsgRemind();

                break;
            case R.id.shield_msg:

                //屏蔽消息设置
                setShieldMsg();

                break;
        }
    }

    //屏蔽消息设置
    private void setShieldMsg() {
        isShieldMsg = !isShieldMsg;

        if (isShieldMsg) {

            mShieldMsg.setSelected(true);

        } else {

            mShieldMsg.setSelected(false);

        }

        MySharedPreferences.getInstance().setBoolean(SHIELDMSG, isShieldMsg);
    }

    //消息提醒设置
    private void setMsgRemind() {
        isMsgRemind = !isMsgRemind;

        if (isMsgRemind) {

            mMsgRemind.setSelected(true);

        } else {

            mMsgRemind.setSelected(false);

        }

        MySharedPreferences.getInstance().setBoolean(MSGREMIND, isMsgRemind);
    }
}
