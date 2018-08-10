package com.bclould.tea.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import java.util.Map;

/**
 * Created by GIjia on 2018/8/7.
 */

public class AlipayClient {
    private static AlipayClient mInstance;
    private Context mContext;

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2016090900468872";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCC0n6qMSERLPcRjR30jJZSPYW/czsgRkT/+NxX8BGH6MN7mq5qwmwv96b4suAFLVIwqHg1w0k6JNe6acfuowDnVL9VmaddwEShy59bQv9xKfRhuTHmfGcSEwGQHuHBojMUzVVTzbC0DC3e0fTah7GG6uS1pUCl/GK0PesYqLC13JlZdpWYO0jFvo9PlTU6rOC5QMw5k4JGiRREc3jXQKCXblZPJpI8UmO/5A6JMzM4F/PgWeVecpWeph4br1vTu4NsrJnjSMCGhF++sNZlLzDTO+124g2yOg25gdKTLMBOXpurNPpdCPwcQ2bkiScSeIsYW0j8CDpdnhawAh6eTdpXAgMBAAECggEAC0IiNHNV7Sx5dsE2Or1+kkVJJVi9VeG2PnEkRU0btR6K9DAzomxo8xfU/sImFUv5LsbQ0Y+k1aQmDJzd3+OXSFE6xhsbFC5fuazUYf46DXsW1PIjITrDhAKut8BwnUFgNAIXvcaN1nDV4b8f206iMII2fItkLOSzed96Q/0AeCYMZPCUt7BWR5HEsaj/T9bm7NFMbGcit5YUoyyZwZTFOXWoo6bSp1EMf5DdAtRVzJZAyGQgpk31PAU+gSN42kY0wg71nMbzkbHPJUyHAoLBo1Yz/Gron0hRdxoPqxKtTKtHwyPlafbEIdkxHi4SrX39KOS+KJ8Xqmgi1sfbMjHWAQKBgQC/RccRJxPRSl0Jo/57jYtilqXjjJF3ZeCamn24Id4XyuPpjAa0i68EFiAgoeDpfhJEhHgTCoFP1OiX8hftKnU4xBnPA3/AJCSlG42hP9+xbMV3ZmrpBvwkk/ffa2MkkDXxGhp1fcyT4VAe9hS0sqh8FB1UcNqKDtLgw/Le97LFdwKBgQCvF9E2W90ehYvQUUbLbmRc7CB7HiPaRwWbA5g0/VmCtM6Yo+unTm11bCeUaIgAVZ3zr+c4nhUPl+FbwDJk9ZKXQ9H9ipuYgKHHYw/EQkevDjtNo5RTxm7l56X3R6K80QPKOuDB+TDfq0aqAKXK0FmPnKqSQfPwgxeXSZ9RWcJKIQKBgBegJ9GPkRwV4l4RepqKRkA8OWtc7o7f75lJIeQ/kq4/ql0rpZxhcJHBpeBB/oT4xIrgDfDUKFrTApPaHGh4CRik6EcwuHPkQv5948WaHShONinkZao46aYe/MwE+K8IfRE11zTSABX4C6x3WuFi+/qvhVvrwKKy6AtJSNOPpu5LAoGAIfT2iBpMyeyQbvg0SKPCJxtMOVRhE6YOej+6NxyjTWwPXR79Rtc49zrgejHDnnz8QxRsPVi7MOj9tbOSkm3l3hwzD37gKjVYKrkVh7Rq/pI+AhKlY5HqVAAW+dwUvZWzrHa6vCO9zMME01eIz8qUc/uoQPE1hAGIg7itgzNrMMECgYBk3glVyb4fO4X8J4J2DtBXWv75tKo5oimZCP+qSTGVmCtIPbHLrNo0+kuWw88SsMtjGgECVfmiu4vXNC5LASN4JNJ2QibD89YdGv6LzhRLee73pXzdAguwLVBvMDvnJyipw5vZYjWBTcDrP8sP8yfVtthMJUDo2Gzrd5GpYSz7cg==";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

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
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(mContext,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

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
     *
     * @param v
     */
    public void authV2(final Activity activity) {
//        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
//                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
//                || TextUtils.isEmpty(TARGET_ID)) {
//            new AlertDialog.Builder(activity).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialoginterface, int i) {
//                        }
//                    }).show();
//            return;
//        }

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
