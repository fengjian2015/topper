package com.bclould.tocotalk.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import static com.bclould.tocotalk.ui.activity.ConversationActivity.ACCESSKEYID;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SECRETACCESSKEY;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SESSIONTOKEN;

/**
 * Created by GA on 2017/10/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class Constants {
    private final Context mContext;

    public Constants(Context context) {
        mContext = context;
    }

//    public static final String OPENFIRE_IP = "xmpp.bclould.com";
//    public static final int OPENFIRE_PORT = 5288;
        public static final int OPENFIRE_PORT = 2018;
//    public static final String BASE_URL = "https://www.bclould.com:8112/api/";
        public static final String BASE_URL = "https://api.cnblocklink.com/api/";
//    public static final String DOMAINNAME = "xmpp.bclould.com";
        public static final String DOMAINNAME = "xmpp.coingbank.com";
    public static final String CHUANCODE = "爨^(&";
    public static final String REDBAG = "[redBag]";
    public static final String TRANSFER = "[transfer]";
    public static final String REDPACKAGE = "redPackage";
    public static final String MONEYIN = "moneyIn";
    public static final String MONEYOUT = "moneyOut";
    public static final String MYUSER = UtilTool.getJid();
    public static final String BUSINESSCARD = "businessCard";
    public static final String ACCESS_KEY_ID = MySharedPreferences.getInstance().getString(ACCESSKEYID);
    public static final String SECRET_ACCESS_KEY = MySharedPreferences.getInstance().getString(SECRETACCESSKEY);
    public static final String SESSION_TOKEN = MySharedPreferences.getInstance().getString(SESSIONTOKEN);
    public static final String BUCKET_NAME = "bclould";
    public static final String PUBLICDIR = "/sdcard/tocotalk/images/";
    public static final String LOG_DIR = "/sdcard/tocotalk/log/";
    public static final String QRMONEYIN = "qrMoneyIn";
    public static final String DATAMONEYIN = "dataMoneyIn";
    public static final String HINT_TYPE = "hint_type";
    public static final String OTC_DISCLAIMER = "otc_disclaimer";
    public static final String EXCHANGE_DISCLAIMER = "exchange_disclaimer";
}
