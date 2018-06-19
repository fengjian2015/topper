package com.bclould.tea.topperchat;

/**
 * Created by GIjia on 2018/6/6.
 */

public class WsContans {
    public static final String PASSWORD="password";
    public static final String TOCOID="toco_id";
    public static final String CONTENT="content";
    public static final String DEVICE="device";
    public static final int DEVICE_ID=1;
    public static final String TYPE="type";

    //消息類型
    public static final int MSG_TEXT=1;//文本
    public static final int MSG_AUDIO=2;//語音
    public static final int MSG_IMAGE=3;//圖片
    public static final int MSG_VIDEO=4;//視頻
    public static final int MSG_SHARE_GUESS=5;//競猜分享
    public static final int MSG_SHARE_LINK=6;//鏈接分享
    public static final int MSG_CARD=7;//個人名片
    public static final int MSG_REDBAG=8;//紅包
    public static final int MSG_TRANSFER=9;//轉賬
    public static final int MSG_LOCATION=10;//定位

    //廣播類型
    public static final int BC_FRIEND_REQUEST=9;//好友請求
    public static final int BC_FRIEND_COMMIT=10;//好友請求確認
    public static final int BC_FRIEND_REJECT=25;//轉賬
    public static final int BC_OFFLINE=26;//離線消息

    public static final int BC_OTC_ORDER=1;//OTC訂單信息
    public static final int BC_RED_PACKET_EXPIRED=2;//紅包過期
    public static final int BC_TRANSFER_INFORM=3;//轉賬通知
    public static final int BC_QRCODE_RECEIPT_PAYMENT=4;//收付款通知
    public static final int BC_COIN_IN_BROAD=5;//充幣通知
    public static final int BC_INOUT_COIN_INFORM=6;//提筆通知
    public static final int BC_AUTH_STATUS=7;//實名認證
    public static final int BC_RED_GET=8;//紅包領取


}