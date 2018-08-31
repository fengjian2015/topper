package com.bclould.tea.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.utils.UtilTool;

import java.util.Map;

/**
 * Created by GIjia on 2018/8/7.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AlipayClient {
    private static AlipayClient mInstance;
    private Context mContext;

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2018082361123333";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "2088231381032191";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = UtilTool.createtFileName();

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCTJk2kdVww8Pbq6hu+orxKIpUPvoDsB+x1G0D2xh/gSQVqiobyiUMTOBghl44RHra3qKK3yuuJ/iCTN1cHSH9gnfROGsDN3HTTGjd6ZsrqO/MZzfjVJEptnhamyFi3GWjpnKbu5MpvbFcT1QTvbEVyjFhKxzFM1R+WLtHLct9dz18qrHKYBvrPN/HJi6NxhMtgikaWzB4t8WWkgkOr4+wk08Y8Zd9vTtJCpd+by7baQFQfOQa7eBqnfVVVYR70b7bIMshjUFq0hoPEpLnjcMmY5HDtmlktY5KFncbjCvFUjmToDHgHJ3wpazP6zRHGsnmy+TPzNQwEi0jaaYmU/9bpAgMBAAECggEAWWcqQUN00d91heDbHoYtNDWyrzAlkEJ4LeZzH/vcHh7/hW3I887ly+WG89Hq5QfuJ8PzqQtG9D5MeGVlIO2xRnhxmIq0nd7Vs/T3xAx0OMBXwVPdXVKBTfyA8rBDBmwPy+/7lKW5QN1oQopoRzdXMnOGqVhA4deTK2Ii4SNYBtwtoZ0mAlKwlj97fwkoj7E/b1EOSifYbjkPfMA4zYDHbvLmm5JlEbBkiKvUqYN6Umgg7vmHNfkJa8RmOxCNZbL0YJRGlcHXyDW7f56hbbt9Pu67PIclIUCDP4x0ahsS7eV9xC0vrfevMWiFHsK+wmQKnRQwrvxkxM6D7M9e6jQPAQKBgQDG2CzUaUTUVmOxFk0Av8s7cC2uPN2+QRimaXBGWA5fsH9yEbIiDGQSVqOF8U2SX39ePYhZt1WTNzGIbq3ZwbGZaNGZtw+24Dzn/baTyb1zI+0ArdUCshB108D6EDn/h76ExyXwRXfG1LH4kHzz00nqIAvEamE4PTcUvvOOQJvn2QKBgQC9cjPbplM9ZIZr/c6ThAbmIDfIo9qU6iZPtcsxiOdnMTSzH7OetJkBuZNmA62uc7ed2F1gk1amAtHm2mPAFdjpT71OV9i1JjmdSSJ56OjSpEKiSUvqygirxV7qYbC6MZGNcAaPz7tgWtvh4tl2N5eKE36YzX9wPv6p/5aRJZeNkQKBgQCrOaeQ2X0to56A9YUlagKlkjmj2kHP/NovIohN6rGl8KQuI9LSwvzBVd3GDz4PFzDkg/QP3XFRLav3oecOpl46jio/RYXAomOQGcfIh2Yd6gfCF+5fPdoyvD9mleZ7hfam+55NwmQb0TfXpQQUKhNyClpXros9FGmD8dJk4nneMQKBgB/lTGnc1QLvG9quvssDXY39OqcjPzf3PEALYYyTejJbjH3kBn/f9CrgLbrtP56xiOageQg7hkkpDGILvuOmSgDuFfdMvwWwy955Y76WwixEz5s3L/r1zQPulQ5tKC4RDmp7w/8OgIO2iHK9mNNYhMEQiy3620vwrzu79oO1ipChAoGBALrR3FM1fLvfsBV8L14MR5UrC5pIffSSwIy5oFk0kUq7URzHD0VXPkpZsf58afvz900wzIjqD/SeXNFxxZcKXAX3kyQDNuxDi9QriLe3XPBCeCusqVdcpMuijo5yhQELyHj9q8Utj68H58pEIRrelHKk/ue1To76iEzGsEpvkreL";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private PersonalDetailsPresenter.CallBack7 mCallBack7;

    public static AlipayClient getInstance(){
        if(mInstance == null){
            synchronized (AlipayClient.class){
                if(mInstance == null){
                    mInstance = new AlipayClient();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext=context;
        initAlipay(context);
    }

    private void initAlipay(Context context) {

    }

    /**
     * 支付宝支付业务
     */
    public void payV2(final Activity activity) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //

                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(mContext,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                        mCallBack7.send(authResult.getUserId());
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(mContext,
                                "授权失败" + String.format("authCode:%s", authResult.toString()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * 支付宝账户授权业务
     *  @param
     * @param callBack7
     */
    public void authV2(final Activity activity, PersonalDetailsPresenter.CallBack7 callBack7) {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
                || TextUtils.isEmpty(TARGET_ID)) {
            new AlertDialog.Builder(activity).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            return;
        }
        mCallBack7 = callBack7;
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(activity);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }

}
