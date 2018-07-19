package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.base.SwipeActivity;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.ui.widget.VideoPlayer;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.danikula.videocache.HttpProxyCacheServer;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VIDEO_MSG;


/**
 * Created by GA on 2018/3/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class VideoActivity extends SwipeActivity {


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
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    private MenuListPopWindow menu;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        setDimension();
        uri = getIntent().getStringExtra("url");
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
                mVideoPlayer.pause();
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
        mRlTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog();
                return false;
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
        ViewGroup.LayoutParams lp = mVideoPlayer.getLayoutParams();

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

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void showDialog() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.save_video));
        list.add(getString(R.string.transmit));
        menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        try {
                            //保存
                            String filePath;
                            if (uri.startsWith("https://") || uri.startsWith("http://")) {
                                HttpProxyCacheServer proxy = MyApp.getInstance().getProxy(VideoActivity.this);
                                filePath = new File(new URI(proxy.getProxyUrl(uri))).getAbsolutePath();
                            } else {
                                filePath = uri;
                            }
                            if (UtilTool.saveAlbumVideo(filePath, VideoActivity.this)) {
                                ToastShow.showToast2(VideoActivity.this, VideoActivity.this.getString(R.string.save_success));
                            } else {
                                ToastShow.showToast2(VideoActivity.this, VideoActivity.this.getString(R.string.save_error));
                            }
                            menu.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        //轉發
                        try{
                            Intent intent = new Intent(VideoActivity.this, SelectConversationActivity.class);
                            intent.putExtra("type", 1);
                            intent.putExtra("msgType", TO_VIDEO_MSG);
                            MessageInfo messageInfo = new MessageInfo();
                            if (uri.startsWith("https://") || uri.startsWith("http://")) {
                                HttpProxyCacheServer proxy = MyApp.getInstance().getProxy(VideoActivity.this);
                                String proxyUrl = new File(new URI(proxy.getProxyUrl(uri))).getAbsolutePath();
                                messageInfo.setMessage(proxyUrl);
                            } else {
                                messageInfo.setMessage(uri);
                            }
                            intent.putExtra("messageInfo", messageInfo);
                            startActivity(intent);
                            menu.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

}
