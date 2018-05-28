package com.bclould.tocotalk.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import static com.bclould.tocotalk.Presenter.LoginPresenter.XMPP_SERVER;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.ACCESSKEYID;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SECRETACCESSKEY;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SESSIONTOKEN;

/**
 * Created by GA on 2017/10/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class Constants {
    public static final String ADMINISTRATOR_NAME = "TOCOTALK";
    public static int BET_ARR_COUNT = 4;
    private final Context mContext;

    public Constants(Context context) {
        mContext = context;
    }

    //    public static final int OPENFIRE_PORT = 5288;
    public static final int OPENFIRE_PORT = 2018;
    public static final String BASE_URL = "https://www.bclould.com:8112/api/";//测试
//            public static final String BASE_URL = "https://api.cnblocklink.com/api/";
//        public static String DOMAINNAME = "xmpp.bclould.com";//测试
    public static String DOMAINNAME = "xmpp.coingbank.com";
    public static String DOMAINNAME2 = MySharedPreferences.getInstance().getString(XMPP_SERVER);
    //    public static String DOMAINNAME2 = "xmpp.bclould.com";//测试
    public static String CHUANCODE = "爨^(&";
    public static String CARD = "[card]";
    public static String REDBAG = "[redBag]";
    public static String TRANSFER = "[transfer]";
    public static String REDPACKAGE = "redPackage";
    public static String MONEYIN = "moneyIn";
    public static String MONEYOUT = "moneyOut";
    public static String MYUSER = UtilTool.getJid();
    public static String BUSINESSCARD = "businessCard";
    public static String ACCESS_KEY_ID = MySharedPreferences.getInstance().getString(ACCESSKEYID);
    public static String SECRET_ACCESS_KEY = MySharedPreferences.getInstance().getString(SECRETACCESSKEY);
    public static String SESSION_TOKEN = MySharedPreferences.getInstance().getString(SESSIONTOKEN);
    public static String BUCKET_NAME = "bclould";
    public static String PUBLICDIR = "/sdcard/tocotalk/tocotalk_images/";
    public static String LOG_DIR = "/sdcard/tocotalk/log/";
    public static String QRMONEYIN = "qrMoneyIn";
    public static String DATAMONEYIN = "dataMoneyIn";
    public static String HINT_TYPE = "hint_type";
    public static String OTC_DISCLAIMER = "otc_disclaimer";
    public static String OTC_ORDER = "[OtcOrder]:";
    public static String RED_PACKET_EXPIRED = "[RedPacketExpired]:";
    public static String AUTH_STATUS = "[AuthStatus]:";
    public static String QRCODE_RECEIPT_PAYMENT = "[QrcodeReceiptPayment]:";
    public static String TRANSFER_INFORM = "[Transfer]:";
    public static String INOUT_COIN_INFORM = "[CoinOut]:";
    public static String EXCHANGE_DISCLAIMER = "exchange_disclaimer";
    public static String NEW_APK_URL = "new_apk_url";
    public static String NEW_APK_NAME = "new_apk_name";
    public static String NEW_APK_BODY = "new_apk_body";
    public static String KEFU = "W2tlZnVdOkp1bXBjdXN0b21lcnNlcnZpY2VpbnRlcmZhY2U=";
    public static String GUESS_WEB_URL = "bet/rule";
    public static String NEWS_WEB_URL = "news/info/";
    public static String GONGGAO_WEB_URL = "bulletin/info/";
    public static String OTR_REQUEST = "?OTRv2?";
    public static String NEWS_EDIT_WEB_URL = "news/publish";
    public static int NEW_BROWSE_TYPE = 2;
    public static int NEW_MY_TYPE = 0;
    public static int GONGGAO_TYPE = 1;
    public static int NEWS_MAIN_TYPE = 4;
    public static int NEW_DRAFTS_TYPE = 3;
    public static String NEWS_DRAFTS_URL = "news/edit/";
    public static int UPDATE_LOG_TYPE = 5;
    public static String UPDATE_LOG_URL = "updateLogInfo/";
}