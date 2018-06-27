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
    public static final String INDEX="index";

    //文檔定義消息類型
    public static final int MSG_SINGLER=3;//單聊消息
    public static final int MSG_PING=4;//ping
    public static final int MSG_GROUP=13;//群聊消息
    public static final int MSG_OFFINE=15;//離線消息
    public static final int MSG_LOGIN=16;//登錄反饋
    public static final int MSG_BROADCAST=18;//廣播消息
    public static final int MSG_OFFINE_RESULT=25;//離線消息反饋
    public static final int MSG_LOGINOUT=31;//其他賬號登錄
    public static final int MSG_SINGLER_RESULT=32;//單聊消息回執
    public static final int MSG_GROUP_RESULT=34;//群消息回執
    public static final int MSG_STEANGER=35;//不是好友單聊發送消息

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
    public static final int BC_OTC_ORDER=1;//OTC訂單信息
    public static final int BC_RED_PACKET_EXPIRED=2;//紅包過期
    public static final int BC_TRANSFER_INFORM=3;//轉賬通知
    public static final int BC_QRCODE_RECEIPT_PAYMENT=4;//收付款通知
    public static final int BC_COIN_IN_BROAD=5;//充幣通知
    public static final int BC_INOUT_COIN_INFORM=6;//提筆通知
    public static final int BC_AUTH_STATUS=7;//實名認證
    public static final int BC_RED_GET=8;//紅包領取

    public static final int BC_FRIEND_REQUEST=9;//好友請求
    public static final int BC_FRIEND_COMMIT=10;//好友請求確認
    public static final int BC_FRIEND_REJECT=25;//轉賬
    public static final int BC_OFFLINE=26;//離線消息
    public static final int BC_MEMBER_GROUP=31;//加入群組通知
    public static final int BC_QUIT_GROUP=32;//退出群組通知
    public static final int BC_ADD_GROUP=33;//創建群組通知
    public static final int BC_ENJOY_PLAYING=35;//打賞
    public static final int BC_UPDATE_GROUP_LOGO=36;//更新群头像
    public static final int BC_UPDATE_GROUP_REMARK=37;//更新群昵称
    public static final int BC_TRANSFER_GROUP_BROAD=39;//转让群组管理员
    public static final int BC_UPDATE_GROUP_NAME=40;//更新群名字
    public static final int BC_REFRESH_GROUP=41;//刷新房間成員
    public static final int BC_KICK_OUT_GROUP=42;//踢人




}