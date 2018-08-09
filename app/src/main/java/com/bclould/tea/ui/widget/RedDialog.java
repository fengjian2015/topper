package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/8/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RedDialog extends Dialog {
    @Bind(R.id.tv_detail)
    TextView mTvDetail;
    @Bind(R.id.iv_bark)
    ImageView mIvBark;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_from)
    TextView mTvFrom;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.btn_open)
    Button mBtnOpen;
    private OnClickListener onClickListener;
    private Context mContext;
    MyYAnimation myYAnimation = new MyYAnimation();

    public RedDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mContext=context;
    }

    public RedDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_redpacket);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.CustomDialog);
        setCanceledOnTouchOutside(false);

        mIvBark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onClickListener.onBreak();
            }
        });

        myYAnimation.setRepeatCount(Animation.INFINITE);
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnOpen.startAnimation(myYAnimation);
                onClickListener.onOpen();
            }
        });
        mTvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onDetail();
            }
        });
    }

    public void stopAnimation(){
        mBtnOpen.clearAnimation();
        myYAnimation.cancel();
    }


    public void setCoin(boolean isGrabThe, boolean isOverdue, MessageInfo messageInfo, String mName, DBRoomMember mDBRoomMember, DBManager mMgr){
        if(isGrabThe){
            mTvFrom.setVisibility(View.GONE);
            mBtnOpen.setVisibility(View.GONE);
            mTvRemark.setText(mContext.getString(R.string.hand_slow_red_envelope_over));
            mTvDetail.setVisibility(View.VISIBLE);
        }else if(isOverdue){
            mBtnOpen.setVisibility(View.GONE);
            mTvFrom.setVisibility(View.GONE);
            mTvRemark.setText(mContext.getString(R.string.red_envelope_expired));
            mTvDetail.setVisibility(View.VISIBLE);
        }else{
            mTvFrom.setVisibility(View.VISIBLE);
            mBtnOpen.setVisibility(View.VISIBLE);
            mTvRemark.setText(messageInfo.getRemark());
            mTvDetail.setVisibility(View.GONE);
        }

        mTvFrom.setText(mContext.getString(R.string.red_package_hint) + messageInfo.getCoin() + mContext.getString(R.string.red_package));
        mTvName.setText(mName);
        UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember, mMgr, messageInfo.getSend());

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onOpen();
        void onDetail();
        void onBreak();
    }
}
