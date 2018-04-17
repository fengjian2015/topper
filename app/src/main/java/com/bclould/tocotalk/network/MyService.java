package com.bclould.tocotalk.network;


import com.bclould.tocotalk.model.AwsInfo;
import com.bclould.tocotalk.model.BankCardInfo;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.CardListInfo;
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.model.CoinListInfo;
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.model.DynamicListInfo;
import com.bclould.tocotalk.model.ExchangeOrderInfo;
import com.bclould.tocotalk.model.GitHubInfo;
import com.bclould.tocotalk.model.GoogleInfo;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.InOutInfo;
import com.bclould.tocotalk.model.LikeInfo;
import com.bclould.tocotalk.model.LoginInfo;
import com.bclould.tocotalk.model.LoginRecordInfo;
import com.bclould.tocotalk.model.ModeOfPaymentInfo;
import com.bclould.tocotalk.model.MyAssetsInfo;
import com.bclould.tocotalk.model.OrderInfo;
import com.bclould.tocotalk.model.OrderInfo2;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.model.OutCoinSiteInfo;
import com.bclould.tocotalk.model.QuestionInfo;
import com.bclould.tocotalk.model.ReceiptInfo;
import com.bclould.tocotalk.model.RedRecordInfo;
import com.bclould.tocotalk.model.ReviewListInfo;
import com.bclould.tocotalk.model.StateInfo;
import com.bclould.tocotalk.model.TransRecordInfo;
import com.bclould.tocotalk.model.TransferInfo;
import com.bclould.tocotalk.model.TransferListInfo;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by GA on 2017/10/27.
 */

public interface MyService {

    //登录
    @FormUrlEncoded
    @POST("signIn")
    Observable<LoginInfo> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("code") String code
    );

    //图形验证码
    @GET
    Call<ResponseBody> getCaptcha(@Url String fileUrl);

    //发送注册验证码
    @FormUrlEncoded
    @POST("sendRegcode")
    Observable<BaseInfo> sendRegcode(
            @Field("email") String email
    );

    //验证用户名和邮箱
    @FormUrlEncoded
    @POST("signUpValidator")
    Observable<BaseInfo> signUpValidator(
            @Field("email") String email,
            @Field("name") String name
    );

    //发送找回密码验证码
    @FormUrlEncoded
    @POST("sendFindPasscode")
    Observable<BaseInfo> sendFindPasscode(
            @Field("email") String email
    );

    //找回密码
    @FormUrlEncoded
    @POST("finadPassword")
    Observable<BaseInfo> findPassword(
            @Field("vcode") String vcode,
            @Field("email") String email,
            @Field("password") String password
    );

    //注册
    @POST("signUp")
    @FormUrlEncoded
    Observable<BaseInfo> signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("vcode") String vcode,
            @Field("password") String password
    );

    //重置登录密码
    @POST("safty/modifyPassword")
    @FormUrlEncoded
    Observable<BaseInfo> modifyPassword(
            @Header("Authorization") String token,
            @Field("google_code") String google_code,
            @Field("new_password") String new_password,
            @Field("new_password_confirmation") String new_password_confirmation
    );

    //重置交易密码
    @POST("safty/modifySecondPassword")
    @FormUrlEncoded
    Observable<BaseInfo> modifySecondPassword(
            @Header("Authorization") String token,
            @Field("google_code") String google_code,
            @Field("second_password") String second_password,
            @Field("second_password_confirmation") String second_password_confirmation
    );

    //登录状态下 发送验证码
    @FormUrlEncoded
    @POST("sendVcode")
    Observable<BaseInfo> sendVcode(
            @Header("Cookie") String cookie,
            @Header("Authorization") String token,
            @Field("captcha") String captcha
    );

    //获取用户资产
    @POST("finance/getUserAssets")
    Observable<MyAssetsInfo> getMyAssets(
            @Header("Authorization") String token
    );

    //取消订阅资产
    @POST("finance/unsubscribeAsset")
    @FormUrlEncoded
    Observable<BaseInfo> unSubscribeAsset(
            @Header("Authorization") String token,
            @Field("subcribe_coin_id") int id
    );

    //订阅资产
    @POST("finance/subscribeAsset")
    @FormUrlEncoded
    Observable<BaseInfo> subscribeAsset(
            @Header("Authorization") String token,
            @Field("subcribe_coin_id") int id
    );

    //获取提币地址
    @POST("finance/withdrawalAddresses")
    @FormUrlEncoded
    Observable<OutCoinSiteInfo> withdrawalAddresses(
            @Header("Authorization") String token,
            @Field("coin_id") int id
    );

    //添加提币地址
    @POST("finance/addCoinOutAddress")
    @FormUrlEncoded
    Observable<BaseInfo> addCoinOutAddress(
            @Header("Authorization") String token,
            @Field("coin_id") int id,
            @Field("coinout_label") String label,
            @Field("coinout_address") String site,
            @Field("google_code") String google_code
    );

    //删除提币地址
    @POST("finance/deleteCoinOutAddress")
    @FormUrlEncoded
    Observable<BaseInfo> deleteCoinOutAddress(
            @Header("Authorization") String token,
            @Field("coin_id") int id,
            @Field("coinout_address_id") int coinout_address_id
    );

    //提币处理
    @POST("finance/coinoutAction")
    @FormUrlEncoded
    Observable<BaseInfo> coinOutAction(
            @Header("Authorization") String token,
            @Field("coin_id") String id,
            @Field("coinout_address_id") String coinout_address_id,
            @Field("coinout_number") String number,
            @Field("google_code") String google_code,
            @Field("second_password") String second_password,
            @Field("mark") String mark
    );

    //退出登录
    @POST("logout")
    Observable<BaseInfo> logout(
            @Header("Authorization") String token
    );

    //充币地址
    @POST("finance/coininAction")
    @FormUrlEncoded
    Observable<BaseInfo> getAddress(
            @Header("Authorization") String token,
            @Field("coin_id") int id
    );

    //转账
    @POST("finance/transfer")
    @FormUrlEncoded
    Observable<BaseInfo> transfer(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("account_email") String email,
            @Field("number") double number,
            @Field("google_code") String google_code,
            @Field("second_password") String second_password
    );

    //获取秘钥
    @GET("security/getGoogleKey")
    Observable<GoogleInfo> getGoogleKey(
            @Header("Authorization") String token
    );

    //绑定谷歌验证
    @POST("security/bindGoogleAuthenticator")
    @FormUrlEncoded
    Observable<BaseInfo> bindGoogle(
            @Header("Authorization") String token,
            @Field("google_code") String google_code
    );

    //解除绑定谷歌验证
    @POST("security/unBindGoogleAuthenticator")
    @FormUrlEncoded
    Observable<BaseInfo> unBindGoogle(
            @Header("Authorization") String token,
            @Field("vcode") String vcode
    );

    //创建红包
    @POST("redPacket/create")
    @FormUrlEncoded
    Observable<BaseInfo> sendRedPacket(
            @Header("Authorization") String token,
            @Field("for_id") String jid,
            @Field("type") int type,
            @Field("coin_name") String coin_name,
            @Field("intro") String intro,
            @Field("rp_type") int rp_type,
            @Field("red_packet_number") int red_packet_number,
            @Field("single_money") double single_money,
            @Field("total_money") double total_money,
            @Field("second_password") String second_password
    );

    //抢红包
    @POST("redPacket/grab")
    @FormUrlEncoded
    Observable<GrabRedInfo> grabRedPacket(
            @Header("Authorization") String token,
            @Field("rp_id") int rp_id
    );

    //红包记录
    @POST("redPacket/log")
    @FormUrlEncoded
    Observable<RedRecordInfo> redPacketLog(
            @Header("Authorization") String token,
            @Field("type") String type
    );

    //单个红包记录
    @POST("redPacket/signRpLog")
    @FormUrlEncoded
    Observable<GrabRedInfo> singleRpLog(
            @Header("Authorization") String token,
            @Field("rp_id") int rp_id
    );

    //发布交易
    @POST("trans/publish")
    @FormUrlEncoded
    Observable<BaseInfo> publishDeal(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("coin_name") String coin_name,
            @Field("country") String country,
            @Field("currency") String currency,
            @Field("price") double price,
            @Field("number") double number,
            @Field("deadline") int deadline,
            @Field("pay_type") String pay_type,
            @Field("min_amount") double min_amount,
            @Field("max_amount") double max_amount,
            @Field("remark") String remark,
            @Field("second_password") String second_password
    );

    //交易列表
    @POST("trans/lists")
    @FormUrlEncoded
    Observable<DealListInfo> getDealList(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("coin_name") String coin_name,
            @Field("country") String country
    );

    //买币
    @POST("trans/buyCoin")
    @FormUrlEncoded
    Observable<DealListInfo> buyCoin(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    //卖币
    @POST("trans/sellCoin")
    @FormUrlEncoded
    Observable<DealListInfo> sellCoin(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("second_password") String second_password
    );

    //生成订单
    @POST("trans/createOrder")
    @FormUrlEncoded
    Observable<OrderInfo> createOrder(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("number") double number,
            @Field("price") double price,
            @Field("trans_amount") double trans_amount,
            @Field("second_password") String second_password
    );

    //确认付款
    @POST("trans/payment")
    @FormUrlEncoded
    Observable<BaseInfo> payment(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("trans_order_id") int trans_amount
    );

    //完成交易
    @POST("trans/PaymentComplete")
    @FormUrlEncoded
    Observable<BaseInfo> paymentComplete(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("trans_order_id") int trans_amount
    );

    //取消交易
    @POST("trans/cancelPayment")
    @FormUrlEncoded
    Observable<BaseInfo> cancelPayment(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("trans_order_id") int trans_amount
    );

    //订单列表
    @POST("trans/orderLists")
    @FormUrlEncoded
    Observable<OrderListInfo> getOrderList(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("status") String status
    );

    //订单详情
    @POST("trans/orderInfo")
    @FormUrlEncoded
    Observable<OrderInfo2> orderInfo(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    //获取市场参考价
    @POST("trans/getCoinPrice")
    @FormUrlEncoded
    Observable<BaseInfo> getCoinPrice(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("to_coin_name") String to_coin_name
    );

    //获取市场参考价
    @POST("updateToken")
    Observable<BaseInfo> updataToken(
            @Header("Authorization") String token
    );

    //下载apk
    @GET
    Observable<GitHubInfo> checkVersion(
            @Url String url
    );

    //币种列表
    @POST("finance/AssetNameV2")
    Observable<CoinInfo> AssetName(
            @Header("Authorization") String token
    );

    //账单列表
    @POST("finance/transferDetails")
    Observable<TransferInfo> getTransfer(
            @Header("Authorization") String token
    );

    //账单明细
    @POST("finance/transferDetailsLog")
    @FormUrlEncoded
    Observable<TransferInfo> getTransferLog(
            @Header("Authorization") String token,
            @Field("to_id") int to_id
    );

    //aws
    @POST("awsS3/getSessionToken")
    Observable<AwsInfo> getSessionToken(
            @Header("Authorization") String token
    );

    //充币提币记录
    @POST("finance/coinOutLog")
    @FormUrlEncoded
    Observable<InOutInfo> coinOutLog(
            @Header("Authorization") String token,
            @Field("opt_type") String opt_type,
            @Field("coin_id") String coin_id
    );

    //实名认证
    @POST("user/realNameVerify")
    @FormUrlEncoded
    Observable<BaseInfo> realNameVerify(
            @Header("Authorization") String token,
            @Field("truename") String truename,
            @Field("card_number") String card_number,
            @Field("country_id") String country_id,
            @Field("type") String type
    );

    //验证实名是否通过
    @POST("user/realNameInfo")
    Observable<BaseInfo> realNameInfo(
            @Header("Authorization") String token
    );

    //实名绑定
    @POST("user/bindRealName")
    @FormUrlEncoded
    Observable<BaseInfo> bindRealName(
            @Header("Authorization") String token,
            @Field("key") String key
    );

    //绑定银行
    @POST("user/bindBankCard")
    @FormUrlEncoded
    Observable<BaseInfo> bindBankCard(
            @Header("Authorization") String token,
            @Field("truename") String truename,
            @Field("bank_name") String bank_name,
            @Field("bank_site") String bank_site,
            @Field("bank_number") String bank_number
    );

    //银行卡列表
    @POST("user/bankCardList")
    Observable<CardListInfo> bankCardList(
            @Header("Authorization") String token
    );

    //银行卡列表
    @POST("user/unBindBankCard")
    @FormUrlEncoded
    Observable<BaseInfo> unBindBankCard(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    //发布动态
    @POST("dynamic/publish")
    @FormUrlEncoded
    Observable<BaseInfo> publishDynamic(
            @Header("Authorization") String token,
            @Field("content") String content,
            @Field("key_type") String key_type,
            @Field("key") String key,
            @Field("key_compress") String key_compress,
            @Field("position") String position
    );

    //动态列表
    @POST("dynamic/dynamicList")
    @FormUrlEncoded
    Observable<DynamicListInfo> dynamicList(
            @Header("Authorization") String token,
            @Field("page") String page,
            @Field("page_size") String page_size,
            @Field("friends") String friends
    );

    //发表评论
    @POST("review/publish")
    @FormUrlEncoded
    Observable<ReviewListInfo> publishReview(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id,
            @Field("content") String content
    );

    //评论列表
    @POST("review/reviewList")
    @FormUrlEncoded
    Observable<ReviewListInfo> reviewList(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id
    );

    //赞
    @POST("dynamic/like")
    @FormUrlEncoded
    Observable<LikeInfo> like(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id
    );

    //评论点赞
    @POST("review/like")
    @FormUrlEncoded
    Observable<LikeInfo> reviewLike(
            @Header("Authorization") String token,
            @Field("review_id") String review_id
    );

    //生成收款二维码
    @POST("receipt/generateReceiptQrCode")
    @FormUrlEncoded
    Observable<BaseInfo> generateReceiptQrCode(
            @Header("Authorization") String token,
            @Field("coin_id") String coin_id,
            @Field("number") String number,
            @Field("mark") String mark
    );

    //扫收款码
    @POST("receipt/payment")
    @FormUrlEncoded
    Observable<ReceiptInfo> payment(
            @Header("Authorization") String token,
            @Field("id") String id,
            @Field("number") String number,
            @Field("coin_id") String coin_id,
            @Field("second_password") String second_password
    );

    //生成付款二维码
    @POST("receipt/generatePaymentQrCode")
    @FormUrlEncoded
    Observable<BaseInfo> generatePaymentQrCode(
            @Header("Authorization") String token,
            @Field("number") String number,
            @Field("coin_id") String coin_id,
            @Field("second_password") String second_password
    );

    //扫付款码，收款操作
    @POST("receipt/receipt")
    @FormUrlEncoded
    Observable<ReceiptInfo> receipt(
            @Header("Authorization") String token,
            @Field("data") String data
    );

    //获取国家列表
    @POST("common/getCountryList")
    Observable<StateInfo> getCountryList(
            @Header("Authorization") String token
    );

    //验证支付密码
    @POST("common/verifySecondPassword")
    @FormUrlEncoded
    Observable<BaseInfo> verifySecondPassword(
            @Header("Authorization") String token,
            @Field("second_password") String second_password
    );

    //获取银行卡信息
    @POST("user/bankCardInfo")
    @FormUrlEncoded
    Observable<BankCardInfo> bankCardInfo(
            @Header("Authorization") String token,
            @Field("bank_number") String bank_number
    );

    //获取支付记录
    @POST("common/transRecord")
    @FormUrlEncoded
    Observable<TransferListInfo> getTransRecord(
            @Header("Authorization") String token,
            @Field("page") String page,
            @Field("page_size") String page_size,
            @Field("type") String type,
            @Field("date") String date
    );

    //登录记录
    @POST("loginLog")
    Observable<LoginRecordInfo> loginRecord(
            @Header("Authorization") String token
    );

    //验证实名认证
    @POST("user/realNameStatus")
    Observable<BaseInfo> realNameStatus(
            @Header("Authorization") String token
    );

    //验证是否绑定银行卡
    @POST("user/bindBankStatus")
    Observable<BaseInfo> bindBankStatus(
            @Header("Authorization") String token
    );

    //登录设置
    @POST("loginValidateTypeSetting")
    @FormUrlEncoded
    Observable<BaseInfo> loginValidateTypeSetting(
            @Header("Authorization") String token,
            @Field("validate_type") String validate_type,
            @Field("second_password") String second_password,
            @Field("google_code") String google_code
    );

    //获取总估值
    @POST("finance/totalAssetsValuation")
    Observable<BaseInfo> totalAssetsValuation(
            @Header("Authorization") String token
    );

    //获取usdt的数量
    @POST("finance/currentUSD")
    Observable<BaseInfo> getUSDT(
            @Header("Authorization") String token
    );

    //获取币种估值
    @POST("finance/assetsValuation")
    @FormUrlEncoded
    Observable<BaseInfo> assetsValuation(
            @Header("Authorization") String token,
            @Field("to") String to
    );

    //获取币种估值
    @POST("finance/friendTransfer")
    @FormUrlEncoded
    Observable<BaseInfo> friendTransfer(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("user_name") String user_name,
            @Field("number") double number,
            @Field("second_password") String second_password,
            @Field("mark") String mark
    );

    //获取兑换币种列表
    @POST("common/coinLists")
    @FormUrlEncoded
    Observable<CoinListInfo> coinLists(
            @Header("Authorization") String token,
            @Field("type") String type
    );

    //币种兑换记录
    @POST("exchange/orders")
    @FormUrlEncoded
    Observable<ExchangeOrderInfo> exchangeOrders(
            @Header("Authorization") String token,
            @Field("market_coin_name") String market_coin_name,
            @Field("trade_coin_name") String trade_coin_name,
            @Field("page") String page,
            @Field("page_size") String page_size
    );

    //币种兑换
    @POST("exchange/sale")
    @FormUrlEncoded
    Observable<BaseInfo> exchangeSale(
            @Header("Authorization") String token,
            @Field("price") String price,
            @Field("number") String number,
            @Field("market_coin_name") String market_coin_name,
            @Field("trade_coin_name") String trade_coin_name,
            @Field("second_password") String second_password
    );

    //获取usdt的数量
    @POST("common/isBindBankPayWechat")
    Observable<ModeOfPaymentInfo> getModeOfPayment(
            @Header("Authorization") String token
    );

    //设置默认银行卡
    @POST("user/setDefaultBankCard")
    @FormUrlEncoded
    Observable<BaseInfo> setDefaultBankCard(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    //获取usdt的数量
    @POST("question/hopeCoin")
    @FormUrlEncoded
    Observable<BaseInfo> hopeCoin(
            @Header("Authorization") String token,
            @Field("content") String content,
            @Field("contact") String contact
    );

    //上传问题反馈图片
    @POST("question/feedbacks")
    @FormUrlEncoded
    Observable<BaseInfo> feedbacks(
            @Header("Authorization") String token,
            @Field("content") String content,
            @Field("key") String key
    );

    //获取问题列表
    @POST("question/lists")
    Observable<QuestionInfo> getQuestionList(
            @Header("Authorization") String token
    );

    //提幣說明
    @POST("finance/coinoutDesc")
    @FormUrlEncoded
    Observable<BaseInfo> outCoinDesc(
            @Header("Authorization") String token,
            @Field("coin_id") int coin_id
    );

    //支付記錄詳情
    @POST("common/transRecordInfo")
    @FormUrlEncoded
    Observable<TransRecordInfo> transRecord(
            @Header("Authorization") String token,
            @Field("log_id") String log_id,
            @Field("type_number") String type_number
    );
}
