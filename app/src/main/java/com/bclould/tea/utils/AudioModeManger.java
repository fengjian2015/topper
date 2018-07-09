package com.bclould.tea.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2016/11/24
 * 描    述:音频听筒扬声器切换控制器
 * ================================================
 */
public class AudioModeManger {

    private android.media.AudioManager audioManager;
    private SensorManager sensorManager;
    private Sensor mProximiny;
    private onSpeakerListener mOnSpeakerListener;
    private boolean isPlayVoice=false;
    /**
     * 扬声器状态监听器
     * 如果要做成类似微信那种切换后重新播放音频的效果，需要这个监听回调
     * isSpeakerOn 扬声器是否打开
     */
    public interface onSpeakerListener{
        void onSpeakerChanged(boolean isSpeakerOn);
    }


    public void setOnSpeakerListener(onSpeakerListener listener){
        if (listener != null){
            mOnSpeakerListener = listener;
        }
    }

    public AudioModeManger(){

    }

    public void setIsPlayVoice(boolean isPlayVoice){
        this.isPlayVoice=isPlayVoice;
    }

    /**
     * 距离传感器监听者
     */
    private SensorEventListener mDistanceSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(!isPlayVoice){
                return;
            }
            float f_proximiny = event.values[0];
            //扬声器模式
            //魅蓝E传感器得到的值竟然比最大值都要大？what fuck ？
            if (f_proximiny >= mProximiny.getMaximumRange()) {

                setSpeakerPhoneOn(true);
                if (mOnSpeakerListener != null){
                    mOnSpeakerListener.onSpeakerChanged(true);
                }

            } else {//听筒模式
                setSpeakerPhoneOn(false);
                if (mOnSpeakerListener != null){
                    mOnSpeakerListener.onSpeakerChanged(false);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    /**
     * 注册距离传感器监听
     */
    @SuppressLint("ServiceCast")
    public void register(Activity activity){

        audioManager = (android.media.AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) activity
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && mDistanceSensorListener != null) {
            mProximiny = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(mDistanceSensorListener, mProximiny,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    /**
     * 取消注册距离传感器监听
     */
    public void unregister(){

        if (sensorManager != null &&mDistanceSensorListener != null ) {
            sensorManager.unregisterListener(mDistanceSensorListener);
        }

    }


    /**
     * 听筒、扬声器切换
     *
     * 注释： 敬那些年踩过的坑和那些网上各种千奇百怪坑比方案！！
     *
     * AudioManager设置声音类型有以下几种类型（调节音量用的是这个）:
     *
     * STREAM_ALARM 警报
     * STREAM_MUSIC 音乐回放即媒体音量
     * STREAM_NOTIFICATION 窗口顶部状态栏Notification,
     * STREAM_RING 铃声
     * STREAM_SYSTEM 系统
     * STREAM_VOICE_CALL 通话
     * STREAM_DTMF 双音多频,不是很明白什么东西
     *
     * ------------------------------------------
     *
     * AudioManager设置声音模式有以下几个模式（切换听筒和扬声器时setMode用的是这个）
     *
     * MODE_NORMAL 正常模式，即在没有铃音与电话的情况
     * MODE_RINGTONE 铃响模式
     * MODE_IN_CALL 接通电话模式 5.0以下
     * MODE_IN_COMMUNICATION 通话模式 5.0及其以上
     *
     * @param on
     */
    private void setSpeakerPhoneOn(boolean on) {

        if (on) {
            audioManager.setMode(android.media.AudioManager.MODE_NORMAL);
            //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
            audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC,
                    audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC), android.media.AudioManager.FX_KEY_CLICK);
            audioManager.setSpeakerphoneOn(true);
        } else {
            //5.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                audioManager.setMode(android.media.AudioManager.MODE_IN_COMMUNICATION);
                //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
                audioManager.setStreamVolume(android.media.AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_VOICE_CALL), android.media.AudioManager.FX_KEY_CLICK);

            } else {
                audioManager.setMode(android.media.AudioManager.MODE_IN_CALL);
                audioManager.setStreamVolume(android.media.AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_VOICE_CALL), android.media.AudioManager.FX_KEY_CLICK);
            }
            audioManager.setSpeakerphoneOn(false);
        }

    }

}