package com.bclould.tea.ui.activity.my;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.MyPersenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.base.BaseInfoConstants;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.ui.widget.NewYearDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewYearActivitiesActivity extends BaseActivity {


    private final int COUNT_COINS_RAIN = 50;
    @Bind(R.id.earn_cash_coins_rain)
    RelativeLayout mEarnCashCoinsRain;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.tv_day)
    TextView mTvDay;
    @Bind(R.id.tv_hour)
    TextView mTvHour;
    @Bind(R.id.tv_minute)
    TextView mTvMinute;
    @Bind(R.id.tv_second)
    TextView mTvSecond;
    @Bind(R.id.rl_content)
    RelativeLayout mRlContent;
    @Bind(R.id.tv_rule)
    TextView mTvRule;

    private final int START_ANIMATOR=1;
    private final int STOP_ANIMATOR=2;
    private final int CHANGE_TIME=3;
    private long timeRemaining;
    private MediaPlayer musicPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_year_activities);
        ButterKnife.bind(this);
        setTitle(getString(R.string.gold_delivery));
        initHttp();
    }

    private void initHttp() {
        new MyPersenter(this).activityInfo(new MyPersenter.CallBack() {
            @Override
            public void send(BaseMapInfo data) {
                setData(data);
            }

            @Override
            public void error() {

            }
        });
    }

    private void setData(BaseMapInfo data) {
        mTvRule.setText(Html.fromHtml(data.getData().get(BaseInfoConstants.RULE)+""));
        String result= String.format(getString(R.string.new_year_content) ,data.getData().get(BaseInfoConstants.RECOMMEND)+"",data.getData().get(BaseInfoConstants.LAST)+"",data.getData().get(BaseInfoConstants.GC_NUMBER)+"");
        mTvContent.setText(result);
        timeRemaining=(long) UtilTool.parseDouble(data.getData().get(BaseInfoConstants.GC_DELIVERY_LAST_TIME)+"");
        setTime();
        boolean isShowDialog= (boolean) data.getData().get(BaseInfoConstants.CAN_RECEIVE);
        if(isShowDialog){
            showAward(data.getData().get(BaseInfoConstants.TITLE)+"",data.getData().get(BaseInfoConstants.DESC)+"");
        }
    }

    private void setTime(){
        if(!ActivityUtil.isActivityOnTop(this))return;
        --timeRemaining;
        String time= UtilTool.getDateRemaining(timeRemaining);
        String[] split = time.split(":");
        mTvDay.setText(split[0]);
        mTvHour.setText(split[1]);
        mTvMinute.setText(split[2]);
        mTvSecond.setText(split[3]);
        if(timeRemaining>=0) {
//            mHandler.sendEmptyMessageAtTime(CHANGE_TIME,1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTime();
                }
            }, 1000);
        }
    }


    private void showAward(String title,String desc) {
        NewYearDialog diaolg = new NewYearDialog(this);
        diaolg.show();
        diaolg.setData(title, desc);
        diaolg.setOnClickListener(new NewYearDialog.OnClickListener() {
            @Override
            public void onClick() {
                receive();
            }
        });
    }

    private void receive(){
        new MyPersenter(this).activityReceive(new MyPersenter.CallBack() {
            @Override
            public void send(BaseMapInfo data) {
                animation();
            }

            @Override
            public void error() {

            }
        });
    }


    private void animation() {
        playGold();
        mEarnCashCoinsRain.setVisibility(View.VISIBLE);
        mEarnCashCoinsRain.removeAllViews();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screen_x = metric.widthPixels;     // 屏幕宽度（像素）
        int screen_y = metric.heightPixels;   // 屏幕高度（像素）

        int[] location = new int[2];
        mEarnCashCoinsRain.getLocationInWindow(location);
        long mIntervalTime = 0;//动画间隔时间
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < COUNT_COINS_RAIN; i++) {
            int s = UtilTool.getRandom(UtilTool.dip2px(this, 15), screen_x - UtilTool.dip2px(this, 15));
            params.setMargins(0, screen_y / 2, 0, 0);
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.mipmap.icon_activity_gold);
            imageView.setTranslationX(s);
            imageView.setLayoutParams(params);
            mEarnCashCoinsRain.addView(imageView);
            imageView.setVisibility(View.INVISIBLE);
            final ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", 0f, screen_y - location[1]);
            animator.setInterpolator(new AnticipateOvershootInterpolator());
            animator.setDuration(1500);
            mIntervalTime += 30;

            AnimatorInfo animatorInfo=new AnimatorInfo();
            animatorInfo.animator=animator;
            animatorInfo.view=imageView;
            animatorInfo.p=i;
            Message message=new Message();
            message.what=START_ANIMATOR;
            message.obj=animatorInfo;
            mHandler.sendMessageDelayed(message,mIntervalTime);

        }
    }


    class AnimatorInfo{
        ImageView view;
        ObjectAnimator animator;
        int p;
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START_ANIMATOR:
                    AnimatorInfo animatorInfo= (AnimatorInfo) msg.obj;
                    animatorInfo.animator.start();
                    animatorInfo.view.setVisibility(View.VISIBLE);
                    if(animatorInfo.p+1==COUNT_COINS_RAIN){
                        mHandler.sendEmptyMessageAtTime(STOP_ANIMATOR,1500);
                    }
                    break;
                case STOP_ANIMATOR:
                    stopGold();
                    break;
                case CHANGE_TIME:
                    setTime();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGold();
    }

    public void playGold() {
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.gold;
        Uri notification = Uri.parse(uri);
        musicPlayer = MediaPlayer.create(this, notification);
        musicPlayer.start();
        musicPlayer.setLooping(true);
    }

    private void stopGold(){
        if (musicPlayer!=null)
            musicPlayer.stop();
    }

}
