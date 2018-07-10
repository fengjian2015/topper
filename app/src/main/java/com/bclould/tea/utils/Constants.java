package com.bclould.tea.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;


/**
 * Created by GA on 2017/10/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class Constants {
    public static final String ADMINISTRATOR_NAME = "TOPPERCHAT";
    public static int BET_ARR_COUNT = 4;
    private static final boolean isDebug = true;
    public static final String BASE_URL = isDebug ? "https://www.bclould.com:8112/" : "https://api.cnblocklink.com/";
    public static String DOMAINNAME3 = isDebug ? "wss://socket.bclould.com:8179/toco_server" : "wss://socket.cnblocklink.com:2087/toco_server";
    public static String MSG_OFFLINE = isDebug ? "wss://socket.bclould.com:8180/users_offline_msg" : "wss://offline.cnblocklink.com:8443/users_offline_msg";

    public static String CHUANCODE = "爨^(&";
    public static String REDBAG = "[redBag]";
    public static String TRANSFER = "[transfer]";
    public static String REDPACKAGE = "redPackage";
    public static String MONEYIN = "moneyIn";
    public static String MONEYOUT = "moneyOut";
    public static String BUSINESSCARD = "businessCard";
    public static String BUCKET_NAME = isDebug ? "toco--bucket" : "topper-bucket";
    public static String BUCKET_NAME2 = "topper-chat-bucket";
    public static String PUBLICDIR = "/sdcard/tocotalk/tocotalk_images/";
    public static String LOG_DIR = "/sdcard/tocotalk/log/";
    public static String QRMONEYIN = "qrMoneyIn";
    public static String DATAMONEYIN = "dataMoneyIn";
    public static String OTC_DISCLAIMER = "otc_disclaimer";
    public static String EXCHANGE_DISCLAIMER = "exchange_disclaimer";
    public static String NEW_APK_URL = "new_apk_url";
    public static String NEW_APK_NAME = "new_apk_name";
    public static String NEW_APK_BODY = "new_apk_body";
    public static String KEFU = "W2tlZnVdOkp1bXBjdXN0b21lcnNlcnZpY2VpbnRlcmZhY2U=";
    public static String GUESS_WEB_URL = "api/bet/rule";
    public static String NEWS_WEB_URL = "api/news/info/";
    public static String GONGGAO_WEB_URL = "api/bulletin/info/";
    public static String OTR_REQUEST = "?OTRv2?";
    public static String NEWS_EDIT_WEB_URL = "api/news/publish";
    public static int NEW_BROWSE_TYPE = 2;
    public static int NEW_MY_TYPE = 0;
    public static int GONGGAO_TYPE = 1;
    public static int NEWS_MAIN_TYPE = 4;
    public static int NEW_DRAFTS_TYPE = 3;
    public static String NEWS_DRAFTS_URL = "api/news/edit/";
    public static int UPDATE_LOG_TYPE = 5;
    public static String UPDATE_LOG_URL = "api/updateLogInfo/";
    public static String GUESS_DYNAMIC_SEPARATOR = "__";
    public static String QUES_WEB_URL = "api/question/";
    public static final String OSS_ENDOPINT = "oss-cn-shenzhen.aliyuncs.com";
    public static final String STS_SERVER = "api/oss/getSessionTokenV2";
    public static final String VERSION_UPDATE_URL = "https://api.github.com/repos/bclould/tocotalk/releases/latest";
    public static final String DOWNLOAD_APK_URL = "https://toco--bucket.oss-cn-shenzhen.aliyuncs.com/topperchat.apk";
}