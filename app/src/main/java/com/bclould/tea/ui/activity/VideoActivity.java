package com.bclould.tea.ui.activity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.VideoPlayer;
import com.bclould.tea.utils.UtilTool;
import com.danikula.videocache.HttpProxyCacheServer;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by GA on 2018/3/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class VideoActivity extends AppCompatActivity {


    Handler UIhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int currentProgress = mVideoPlayer.getCurrentPosition();
                changeTime(mTvCurrent, currentProgress);//计时读秒
                mItemTime.setProgress(currentProgress);//seekbar更新进度
                UIhandler.sendEmptyMessageDelayed(1, 10);//为了使seekbar移动平滑，每10毫秒更新一次
            } else if (msg.what == 0) {
                UIhandler.removeMessages(1);//取消更新进度
            }
        }
    };
    @Bind(R.id.tvPlay)
    TextView mTvPlay;
    @Bind(R.id.itemTime)
    SeekBar mItemTime;
    @Bind(R.id.tvCurrent)
    TextView mTvCurrent;
    @Bind(R.id.tvDuration)
    TextView mTvDuration;
    @Bind(R.id.linear_controll)
    LinearLayout mLinearControll;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.videoPlayer)
    VideoPlayer mVideoPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        setDimension();
        String uri = getIntent().getStringExtra("url");
        UtilTool.Log("日志", uri);
        //实现缓存加载

        if (uri.startsWith("https://") || uri.startsWith("http://")) {
            HttpProxyCacheServer proxy = MyApp.getInstance().getProxy(this);
            String proxyUrl = proxy.getProxyUrl(uri);
            mVideoPlayer.setVideoPath(proxyUrl);

//            mVideoPlayer.setVideoURI(Uri.parse(uri));
        } else {
            mVideoPlayer.setVideoPath(uri);
        }

        mVideoPlayer.setZOrderOnTop(true);
        mVideoPlayer.setZOrderMediaOverlay(true);

        mProgressBar.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(this);
        mediaController.setVisibility(View.GONE);
        mediaController.setMediaPlayer(mVideoPlayer);
        //mediaController和videoview互相关联
        mVideoPlayer.setMediaController(mediaController);
        mVideoPlayer.start();//开始播放
        mVideoPlayer.requestFocus();
        mVideoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.GONE);
                changeTime(mTvDuration, mVideoPlayer.getDuration());//显示video总时长的文本
                mItemTime.setMax(mVideoPlayer.getDuration());//设置seekbar最大值为video的时长

                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        //VideoView的seekTo是异步执行的，会有seek未完成但播放已经开始的现象。需要消除seekTo对恢复播放的影响，
                        // 应该在seek操作完成的seekComplete回调方法中执行start方法seekTo跳转的位置其实并不是参数所带的position，而是离position最近的关键帧,
                        if (!mVideoPlayer.isPlaying()) mVideoPlayer.start();
                    }
                });

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        int secondpercent = (int) (mVideoPlayer.getDuration() * percent * 0.01f);
                        mItemTime.setSecondaryProgress(secondpercent);//设置缓存进度
                    }
                });
            }
        });

        mItemTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //seekbar开始拖动的时候，停止播放
                mVideoPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //seekbar,拖动结束时，进度设置到拖动处
                mVideoPlayer.seekTo(seekBar.getProgress());
            }
        });

        mVideoPlayer.setOnStatueListener(new VideoPlayer.onStatueListener() {
            @Override
            public void onStart() {
                mTvPlay.setText(getString(R.string.stop));
                UIhandler.sendEmptyMessage(1);
            }

            @Override
            public void onPause() {
                mTvPlay.setText(getString(R.string.play));
                UIhandler.sendEmptyMessage(0);
            }
        });
        mVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

        mTvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoPlayer.isPlaying()) {
                    mVideoPlayer.pause();
                } else {
                    mVideoPlayer.start();
                }
            }
        });
    }


    public void changeTime(TextView tv, int time) {
        int second = time / 1000;
        int hh = second / 3600;//一个小时3600
        int mm = second % 3600 / 60;//一个小时3600取余秒除以60为分钟
        int ss = second % 60;//60秒取余
        String str = String.format("%02d:%02d", mm, ss);//至少2位十进制整数
        tv.setText(str);
    }

    int currentTime;

    //videoview在退到后台或者被覆盖时记录播放时间，回来继续播放
    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.seekTo(currentTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentTime = mVideoPlayer.getCurrentPosition();
        mVideoPlayer.pause();
    }

    private void setDimension() {
        // Adjust the size of the video
        // so it fits on the screen
        float videoProportion = getVideoProportion();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float screenProportion = (float) screenHeight / (float) screenWidth;
        android.view.ViewGroup.LayoutParams lp = mVideoPlayer.getLayoutParams();

        if (videoProportion < screenProportion) {
            lp.height = screenHeight;
            lp.width = (int) ((float) screenHeight / videoProportion);
        } else {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth * videoProportion);
        }
        mVideoPlayer.setLayoutParams(lp);
    }

    private float getVideoProportion() {
        return 1.5f;
    }
}
