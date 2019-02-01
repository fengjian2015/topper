package com.bclould.tea.utils;


/**
 * Created by GA on 2017/10/27.
 */

public class Constants {
    public static final String ADMINISTRATOR_NAME = "TOPPERCHAT";
    public static final int BET_ARR_COUNT = 4;
    public static final boolean isDebug = false;
//    public static final String BASE_URL = isDebug ? "https://www.bclould.com:8112/" : "https://api.cnblocklink.com/";
    public static final String BASE_URL = isDebug ? "http://www.bclould.com:8191/" : "http://api.ftcep.com:33324/";
    public static final String NEW_BASE_URL = isDebug ? "https://www.bclould.com:8112/" : "http://api.ftcep.com:33324/";
    public static final String DOMAINNAME3 = isDebug ? "wss://39.105.50.169:8179/toco_server": "ws://39.105.50.169:2087/toco_server";
    public static final String MSG_OFFLINE = isDebug ? "wss://123.56.10.153:8180/users_offline_msg" : "ws://123.56.10.153:8443/users_offline_msg";
    //商城
    public static final String WEB_MALL=isDebug ? "http://www.bclould.com:8127/wap/" :"http://api.ftcep.com:8443/wap/";

    public static final String ORC_AK = isDebug ? "eNRbHUkGgFWlY89XGHsiQ36X" : "s6coTxHIryO2whcVmkkfwCSf";
    public static final String ORC_SK = isDebug ? "dob8UR9rhU8G6iIB5T16YrAiNyYbPZbG" : "TGMfYGkWvglX5KSzB9DgxKCbBFy0MYDz";

    public static final String CHUANCODE = "爨^(&";
    public static final String REDBAG = "[redBag]";
    public static final String TRANSFER = "[transfer]";
    public static final String REDPACKAGE = "redPackage";

    public static final String MONEYIN = "moneyIn";
    public static final String MONEYOUT = "moneyOut";
    public static final String BUSINESSCARD = "businessCard";
    public static final String COMMANDUSERNAME="CommandUserName";
    public static final String GROUPCARD = "groupCard";
    public static final String QOUCOIN="outcoin";
    public static final String BUCKET_NAME = isDebug ? "toco--bucket" : "topper-bucket";
    public static final String BUCKET_NAME2 = "topper-chat-bucket";
    public static final String APK_BUCKET_NAME = "toco--bucket";
    public static final String NEW_APK_KEY = "topperchat.apk";
    public static final String PUBLICDIR = "/sdcard/tocotalk/tocotalk_images/";
    public static final String LOG_DIR = "/sdcard/tocotalk/log/";
    public static final String BACKGOUND = "/sdcard/tocotalk/background/";
    public static final String ALBUM = "/sdcard/tocotalk/album/";
    public static final String DOWNLOAD = "/sdcard/tocotalk/download/";
    public static final String VIDEO = "/sdcard/tocotalk/video/";
    public static final String WEB_CACHE="/webcache/";
    public static final String QRMONEYIN = "qrMoneyIn";
    public static final String DATAMONEYIN = "dataMoneyIn";
    public static final String OTC_DISCLAIMER = "otc_disclaimer";
    public static final String EXCHANGE_DISCLAIMER = "exchange_disclaimer";
    public static final String NEW_APK_URL = "new_apk_url";
    public static final String NEW_APK_NAME = "new_apk_name";
    public static final String NEW_APK_BODY = "new_apk_body";
    public static final String APK_VERSIONS_TAG = "apk_versions_tag";
    public static final String KEFU = "W2tlZnVdOkp1bXBjdXN0b21lcnNlcnZpY2VpbnRlcmZhY2U=";
    public static final String GUESS_WEB_URL = "api/bet/rule";
    public static final String NEWS_WEB_URL = "api/news/info/";
    public static final String GONGGAO_WEB_URL = "api/bulletin/info/";
    public static final String OTR_REQUEST = "?OTRv2?";
    public static final String NEWS_EDIT_WEB_URL = "api/news/publish";
    public static final int NEW_BROWSE_TYPE = 2;
    public static final int NEW_MY_TYPE = 0;
    public static final int GONGGAO_TYPE = 1;
    public static final int NEWS_MAIN_TYPE = 4;
    public static final int NEW_DRAFTS_TYPE = 3;
    public static final String NEWS_DRAFTS_URL = "api/news/edit/";
    public static final int UPDATE_LOG_TYPE = 5;
    public static final String UPDATE_LOG_URL = "api/updateLogInfo/";
    public static final String GUESS_DYNAMIC_SEPARATOR = "__";
    public static final String QUES_WEB_URL = "api/question/";
    public static final String OSS_ENDOPINT = "oss-cn-shenzhen.aliyuncs.com";
    public static final String STS_SERVER = "api/oss/getSessionTokenV2";
    public static final String VERSION_UPDATE_URL = "https://api.github.com/repos/bclould/tocotalk/releases/latest";
    public static final String DOWNLOAD_APK_URL = "https://toco--bucket.oss-cn-shenzhen.aliyuncs.com/topperchat.apk";
    public static final String COMMERCIAL_TENANT_RECOGNITION_SYMBOL = "offline-pay";
    public static final String LANGUAGE = "language";
    // 简体中文
    public static final String SIMPLIFIED_CHINESE = "zh";
    // 英文
    public static final String ENGLISH = "en";
    // 繁体中文
    public static final String TRADITIONAL_CHINESE = "zh-hant";
    public static final String COUNTRY = "country";
    public static final String OFFICIAL_WEBSITE = "www.topperchat.com";
    public static final String CUSTOMER_SERVICE_EMAIL = "topperchat@outlook.com";

}