package com.bclould.tea.network;


import com.bclould.tea.model.AuatarListInfo;
import com.bclould.tea.model.BankCardInfo;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.BetInfo;
import com.bclould.tea.model.BindingInfo;
import com.bclould.tea.model.CardListInfo;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.model.DealListInfo;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.model.ExchangeOrderInfo;
import com.bclould.tea.model.GonggaoListInfo;
import com.bclould.tea.model.GoogleInfo;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.model.GroupCreateInfo;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.GroupMemberInfo;
import com.bclould.tea.model.GuessInfo;
import com.bclould.tea.model.GuessListInfo;
import com.bclould.tea.model.HistoryInfo;
import com.bclould.tea.model.InOutInfo;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.model.LikeInfo;
import com.bclould.tea.model.LoginInfo;
import com.bclould.tea.model.LoginRecordInfo;
import com.bclould.tea.model.MessageTopInfo;
import com.bclould.tea.model.ModeOfPaymentInfo;
import com.bclould.tea.model.MyAdListInfo;
import com.bclould.tea.model.MyAssetsInfo;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.model.NewFriendInfo;
import com.bclould.tea.model.NewsListInfo;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.model.OSSInfo;
import com.bclould.tea.model.OrderInfo;
import com.bclould.tea.model.OrderInfo2;
import com.bclould.tea.model.OrderListInfo;
import com.bclould.tea.model.OrderStatisticsInfo;
import com.bclould.tea.model.OutCoinSiteInfo;
import com.bclould.tea.model.PublicDetailsInfo;
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.model.QuestionInfo;
import com.bclould.tea.model.ReceiptInfo;
import com.bclould.tea.model.RemarkListInfo;
import com.bclould.tea.model.ReviewInfo;
import com.bclould.tea.model.ReviewListInfo;
import com.bclould.tea.model.RpRecordInfo;
import com.bclould.tea.model.StateInfo;
import com.bclould.tea.model.TransRecordInfo;
import com.bclould.tea.model.TransferInfo;
import com.bclould.tea.model.TransferListInfo;
import com.bclould.tea.model.UnclaimedRedInfo;
import com.bclould.tea.model.UpdateLogInfo;
import com.bclould.tea.model.UpgradeInfo;
import com.bclould.tea.model.UserDataInfo;
import com.bclould.tea.model.VersionInfo;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by GA on 2017/10/27.
 */

public interface MyService {

    //登录
    @FormUrlEncoded
    @POST("api/signIn")
    Observable<LoginInfo> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("code") String code,
            @Field("device") int device,
            @Field("lang") String lang,
            @Field("captcha") String captcha
    );

    //图形验证码
    @GET
    Call<ResponseBody> getCaptcha(@Url String fileUrl);

    //发送注册验证码
    @FormUrlEncoded
    @POST("api/sendRegcode")
    Observable<BaseInfo> sendRegcode(
            @Field("email") String email,
            @Field("lang") String lang
    );

    //验证用户名和邮箱
    @FormUrlEncoded
    @POST("api/signUpValidator")
    Observable<BaseInfo> signUpValidator(
            @Field("email") String email,
            @Field("name") String name
    );

    //发送找回密码验证码
    @FormUrlEncoded
    @POST("api/sendFindPasscode")
    Observable<BaseInfo> sendFindPasscode(
            @Field("email") String email
    );

    //找回密码
    @FormUrlEncoded
    @POST("api/finadPassword")
    Observable<BaseInfo> findPassword(
            @Field("vcode") String vcode,
            @Field("email") String email,
            @Field("password") String password
    );

    //注册
    @POST("api/signUp")
    @FormUrlEncoded
    Observable<BaseInfo> signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("vcode") String vcode,
            @Field("password") String password
    );

    //重置登录密码
    @POST("api/safty/modifyPassword")
    @FormUrlEncoded
    Observable<BaseInfo> modifyPassword(
            @Header("Authorization") String token,
            @Field("google_code") String google_code,
            @Field("new_password") String new_password,
            @Field("new_password_confirmation") String new_password_confirmation
    );

    //重置交易密码
    @POST("api/safty/modifySecondPassword")
    @FormUrlEncoded
    Observable<BaseInfo> modifySecondPassword(
            @Header("Authorization") String token,
            @Field("google_code") String google_code,
            @Field("second_password") String second_password,
            @Field("second_password_confirmation") String second_password_confirmation
    );

    //登录状态下 发送验证码
    @FormUrlEncoded
    @POST("api/sendVcode")
    Observable<BaseInfo> sendVcode(
            @Header("Cookie") String cookie,
            @Header("Authorization") String token,
            @Field("captcha") String captcha
    );

    //获取用户资产
    @POST("api/finance/getUserAssets")
    Observable<MyAssetsInfo> getMyAssets(
            @Header("Authorization") String token
    );

    //取消订阅资产
    @POST("api/finance/unsubscribeAsset")
    @FormUrlEncoded
    Observable<BaseInfo> unSubscribeAsset(
            @Header("Authorization") String token,
            @Field("subcribe_coin_id") int id
    );

    //订阅资产
    @POST("api/finance/subscribeAsset")
    @FormUrlEncoded
    Observable<BaseInfo> subscribeAsset(
            @Header("Authorization") String token,
            @Field("subcribe_coin_id") int id
    );

    //获取提币地址
    @POST("api/finance/withdrawalAddresses")
    @FormUrlEncoded
    Observable<OutCoinSiteInfo> withdrawalAddresses(
            @Header("Authorization") String token,
            @Field("coin_id") int id
    );

    //添加提币地址
    @POST("api/finance/addCoinOutAddress")
    @FormUrlEncoded
    Observable<BaseInfo> addCoinOutAddress(
            @Header("Authorization") String token,
            @Field("coin_id") int id,
            @Field("coinout_label") String label,
            @Field("coinout_address") String site,
            @Field("google_code") String google_code
    );

    //删除提币地址
    @POST("api/finance/deleteCoinOutAddress")
    @FormUrlEncoded
    Observable<BaseInfo> deleteCoinOutAddress(
            @Header("Authorization") String token,
            @Field("coin_id") int id,
            @Field("coinout_address_id") int coinout_address_id
    );

    //提币处理
    @POST("api/finance/coinoutAction")
    @FormUrlEncoded
    Observable<BaseInfo> coinOutAction(
            @Header("Authorization") String token,
            @Field("coin_id") String id,
            @Field("address") String address,
            @Field("coinout_number") String number,
            @Field("google_code") String google_code,
            @Field("second_password") String second_password,
            @Field("mark") String mark
    );

    //退出登录
    @POST("api/logout")
    Observable<BaseInfo> logout(
            @Header("Authorization") String token
    );

    //充币地址
    @POST("api/finance/coininAction")
    @FormUrlEncoded
    Observable<BaseInfo> getAddress(
            @Header("Authorization") String token,
            @Field("coin_id") int id
    );

    //转账
    @POST("api/finance/transfer")
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
    @GET("api/security/getGoogleKey")
    Observable<GoogleInfo> getGoogleKey(
            @Header("Authorization") String token
    );

    //绑定谷歌验证
    @POST("api/security/bindGoogleAuthenticator")
    @FormUrlEncoded
    Observable<BaseInfo> bindGoogle(
            @Header("Authorization") String token,
            @Field("google_code") String google_code
    );

    //解除绑定谷歌验证
    @POST("api/security/unBindGoogleAuthenticator")
    @FormUrlEncoded
    Observable<BaseInfo> unBindGoogle(
            @Header("Authorization") String token,
            @Field("vcode") String vcode
    );

    //创建红包
    @POST("api/redPacket/createV2")
    @FormUrlEncoded
    Observable<BaseInfo> sendRedPacket(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id,
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
    @POST("api/redPacket/grab")
    @FormUrlEncoded
    Observable<GrabRedInfo> grabRedPacket(
            @Header("Authorization") String token,
            @Field("rp_id") int rp_id
    );

    //红包记录
    @POST("api/redPacket/log")
    @FormUrlEncoded
    Observable<RpRecordInfo> redPacketLog(
            @Header("Authorization") String token,
            @Field("type") String type,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("year") String year
    );

    //单个红包记录
    @POST("api/redPacket/signRpLog")
    @FormUrlEncoded
    Observable<GrabRedInfo> singleRpLog(
            @Header("Authorization") String token,
            @Field("rp_id") int rp_id
    );

    //发布交易
    @POST("api/trans/publish")
    @FormUrlEncoded
    Observable<BaseInfo> publishDeal(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("coin_name") String coin_name,
            @Field("country") int country,
            @Field("currency") String currency,
            @Field("price") double price,
            @Field("number") double number,
            @Field("deadline") int deadline,
            @Field("pay_type") int pay_type,
            @Field("min_amount") double min_amount,
            @Field("max_amount") double max_amount,
            @Field("remark") String remark,
            @Field("second_password") String second_password,
            @Field("mobile") String mobile
    );

    //交易列表
    @POST("api/trans/lists")
    @FormUrlEncoded
    Observable<DealListInfo> getDealList(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("coin_name") String coin_name,
            @Field("country") int country,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size
    );

    //买币
    @POST("api/trans/buyCoin")
    @FormUrlEncoded
    Observable<DealListInfo> buyCoin(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    //卖币
    @POST("api/trans/sellCoin")
    @FormUrlEncoded
    Observable<DealListInfo> sellCoin(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("second_password") String second_password
    );

    //生成订单
    @POST("api/trans/createOrder")
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
    @POST("api/trans/payment")
    @FormUrlEncoded
    Observable<BaseInfo> payment(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("trans_order_id") int trans_amount,
            @Field("second_password") String second_password
    );

    //完成交易
    @POST("api/trans/PaymentComplete")
    @FormUrlEncoded
    Observable<BaseInfo> paymentComplete(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("trans_order_id") int trans_amount,
            @Field("second_password") String second_password
    );

    //取消交易
    @POST("api/trans/cancelPayment")
    @FormUrlEncoded
    Observable<BaseInfo> cancelPayment(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("trans_order_id") int trans_amount,
            @Field("second_password") String second_password
    );

    //订单列表
    @POST("api/trans/orderLists")
    @FormUrlEncoded
    Observable<OrderListInfo> getOrderList(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("status") String status,
            @Field("user_name") String user_name,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size
    );

    //订单详情
    @POST("api/trans/orderInfo")
    @FormUrlEncoded
    Observable<OrderInfo2> orderInfo(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    //获取市场参考价
    @POST("api/trans/getCoinPrice")
    @FormUrlEncoded
    Observable<BaseInfo> getCoinPrice(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("to_coin_name") String to_coin_name
    );

    //更新token
    @POST("api/updateToken")
    Observable<BaseInfo> updataToken(
            @Header("Authorization") String token
    );

    //下载apk
    @GET("chat/latest/version/{type}")
    Observable<VersionInfo> checkVersion(
            @Header("Authorization") String token,
            @Path("type") int type
    );

    //币种列表
    @POST("api/finance/AssetNameV2")
    Observable<CoinListInfo> AssetName(
            @Header("Authorization") String token
    );

    //账单列表
    @POST("api/finance/transferDetails")
    Observable<TransferInfo> getTransfer(
            @Header("Authorization") String token
    );

    //账单明细
    @POST("api/finance/transferDetailsLog")
    @FormUrlEncoded
    Observable<TransferInfo> getTransferLog(
            @Header("Authorization") String token,
            @Field("to_id") int to_id
    );

    //aws
    @POST("api/oss/getSessionToken")
    Observable<OSSInfo> getSessionToken(
            @Header("Authorization") String token
    );

    //充币提币记录
    @POST("api/finance/coinOutLog")
    @FormUrlEncoded
    Observable<InOutInfo> coinOutLog(
            @Header("Authorization") String token,
            @Field("opt_type") String opt_type,
            @Field("coin_id") String coin_id
    );

    //实名认证
    @POST("api/user/realNameVerify")
    @FormUrlEncoded
    Observable<BaseInfo> realNameVerify(
            @Header("Authorization") String token,
            @Field("truename") String truename,
            @Field("card_number") String card_number,
            @Field("country_id") String country_id,
            @Field("type") String type
    );

    //验证实名是否通过
    @POST("api/user/realNameInfo")
    Observable<BaseInfo> realNameInfo(
            @Header("Authorization") String token
    );

    //实名绑定
    @POST("api/user/bindRealName")
    @FormUrlEncoded
    Observable<BaseInfo> bindRealName(
            @Header("Authorization") String token,
            @Field("key") String key
    );

    //绑定银行
    @POST("api/user/bindBankCard")
    @FormUrlEncoded
    Observable<BaseInfo> bindBankCard(
            @Header("Authorization") String token,
            @Field("truename") String truename,
            @Field("bank_name") String bank_name,
            @Field("bank_site") String bank_site,
            @Field("bank_number") String bank_number,
            @Field("country_id") int country_id
    );

    //银行卡列表
    @POST("api/user/bankCardList")
    Observable<CardListInfo> bankCardList(
            @Header("Authorization") String token
    );

    //银行卡列表
    @POST("api/user/unBindBankCard")
    @FormUrlEncoded
    Observable<BaseInfo> unBindBankCard(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    //发布动态
    @POST("api/dynamic/publish")
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
    @POST("api/dynamic/dynamicListV2")
    @FormUrlEncoded
    Observable<DynamicListInfo> dynamicList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("friends") String friends
    );

    //個人動態列表
    @POST("api/dynamic/myDynamicListV2")
    @FormUrlEncoded
    Observable<DynamicListInfo> taDynamicList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("toco_id") String toco_id
    );

    //发表评论
    @POST("api/review/publish")
    @FormUrlEncoded
    Observable<ReviewListInfo> publishReview(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id,
            @Field("content") String content,
            @Field("reply_id") String reply_id,
            @Field("key") String key,
            @Field("key_type") int key_type
    );

    //评论列表
    @POST("api/review/reviewList")
    @FormUrlEncoded
    Observable<ReviewListInfo> reviewList(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id
    );

    //赞
    @POST("api/dynamic/like")
    @FormUrlEncoded
    Observable<LikeInfo> like(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id
    );

    //评论点赞
    @POST("api/review/like")
    @FormUrlEncoded
    Observable<LikeInfo> reviewLike(
            @Header("Authorization") String token,
            @Field("review_id") String review_id
    );

    //生成收款二维码
    @POST("api/receipt/generateReceiptQrCode")
    @FormUrlEncoded
    Observable<BaseInfo> generateReceiptQrCode(
            @Header("Authorization") String token,
            @Field("coin_id") String coin_id,
            @Field("number") String number,
            @Field("mark") String mark
    );

    //扫收款码
    @POST("api/receipt/payment")
    @FormUrlEncoded
    Observable<ReceiptInfo> payment(
            @Header("Authorization") String token,
            @Field("id") String id,
            @Field("number") String number,
            @Field("coin_id") String coin_id,
            @Field("second_password") String second_password
    );

    //生成付款二维码
    @POST("api/receipt/generatePaymentQrCode")
    @FormUrlEncoded
    Observable<BaseInfo> generatePaymentQrCode(
            @Header("Authorization") String token,
            @Field("number") String number,
            @Field("coin_id") String coin_id,
            @Field("second_password") String second_password
    );

    //扫付款码，收款操作
    @POST("api/receipt/receipt")
    @FormUrlEncoded
    Observable<ReceiptInfo> receipt(
            @Header("Authorization") String token,
            @Field("data") String data
    );

    //获取国家列表
    @POST("api/common/getCountryList")
    Observable<StateInfo> getCountryList(
            @Header("Authorization") String token
    );

    //验证支付密码
    @POST("api/common/verifySecondPassword")
    @FormUrlEncoded
    Observable<BaseInfo> verifySecondPassword(
            @Header("Authorization") String token,
            @Field("second_password") String second_password
    );

    //获取银行卡信息
    @POST("api/user/bankCardInfo")
    @FormUrlEncoded
    Observable<BankCardInfo> bankCardInfo(
            @Header("Authorization") String token,
            @Field("bank_number") String bank_number,
            @Field("country_id") int country_id
    );

    //获取支付记录
    @POST("api/common/transRecord")
    @FormUrlEncoded
    Observable<TransferListInfo> getTransRecord(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("type") String type,
            @Field("date") String date
    );

    //登录记录
    @POST("api/loginLog")
    Observable<LoginRecordInfo> loginRecord(
            @Header("Authorization") String token
    );

    //验证实名认证
    @POST("api/user/realNameStatus")
    Observable<BaseInfo> realNameStatus(
            @Header("Authorization") String token
    );

    //验证是否绑定银行卡
    @POST("api/user/bindBankStatus")
    Observable<BaseInfo> bindBankStatus(
            @Header("Authorization") String token
    );

    //登录设置
    @POST("api/loginValidateTypeSetting")
    @FormUrlEncoded
    Observable<BaseInfo> loginValidateTypeSetting(
            @Header("Authorization") String token,
            @Field("validate_type") String validate_type,
            @Field("second_password") String second_password,
            @Field("google_code") String google_code
    );

    //获取总估值
    @POST("api/finance/totalAssetsValuation")
    Observable<BaseInfo> totalAssetsValuation(
            @Header("Authorization") String token
    );

    //获取usdt的数量
    @POST("api/finance/currentUSD")
    Observable<BaseInfo> getUSDT(
            @Header("Authorization") String token
    );

    //获取币种估值
    @POST("api/finance/assetsValuation")
    @FormUrlEncoded
    Observable<BaseInfo> assetsValuation(
            @Header("Authorization") String token,
            @Field("to") String to
    );

    //获取币种估值
    @POST("api/finance/friendTransferV2")
    @FormUrlEncoded
    Observable<BaseInfo> friendTransfer(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name,
            @Field("toco_id") String toco_id,
            @Field("number") double number,
            @Field("second_password") String second_password,
            @Field("mark") String mark
    );

    //获取兑换币种列表
    @POST("api/common/coinLists")
    @FormUrlEncoded
    Observable<CoinListInfo> coinLists(
            @Header("Authorization") String token,
            @Field("type") String type
    );

    //币种兑换记录
    @POST("api/exchange/orders")
    @FormUrlEncoded
    Observable<ExchangeOrderInfo> exchangeOrders(
            @Header("Authorization") String token,
            @Field("market_coin_name") String market_coin_name,
            @Field("trade_coin_name") String trade_coin_name,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size
    );

    //币种兑换
    @POST("api/exchange/sale")
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
    @POST("api/common/isBindBankPayWechat")
    Observable<ModeOfPaymentInfo> getModeOfPayment(
            @Header("Authorization") String token
    );

    //设置默认银行卡
    @POST("api/user/setDefaultBankCard")
    @FormUrlEncoded
    Observable<BaseInfo> setDefaultBankCard(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    //获取usdt的数量
    @POST("api/question/hopeCoin")
    @FormUrlEncoded
    Observable<BaseInfo> hopeCoin(
            @Header("Authorization") String token,
            @Field("content") String content,
            @Field("contact") String contact
    );

    //上传问题反馈图片
    @POST("api/question/feedbacks")
    @FormUrlEncoded
    Observable<BaseInfo> feedbacks(
            @Header("Authorization") String token,
            @Field("content") String content,
            @Field("key") String key
    );

    //获取问题列表
    @POST("api/question/lists")
    Observable<QuestionInfo> getQuestionList(
            @Header("Authorization") String token
    );

    //提幣說明
    @POST("api/finance/coinoutDesc")
    @FormUrlEncoded
    Observable<BaseInfo> outCoinDesc(
            @Header("Authorization") String token,
            @Field("coin_id") int coin_id
    );

    //支付記錄詳情
    @POST("api/common/transRecordInfo")
    @FormUrlEncoded
    Observable<TransRecordInfo> transRecord(
            @Header("Authorization") String token,
            @Field("log_id") String log_id,
            @Field("type_number") String type_number,
            @Field("id") String id
    );

    //支付記錄詳情
    @POST("api/trans/cancelTrans")
    @FormUrlEncoded
    Observable<BaseInfo> cancelTrans(
            @Header("Authorization") String token,
            @Field("trans_id") String trans_id
    );

    //修改頭像
    @POST("api/user/uploadAvatar")
    @FormUrlEncoded
    Observable<BaseInfo> uploadAvatar(
            @Header("Authorization") String token,
            @Field("content") String content
    );

    //獲取頭像
    @POST("api/user/avatarListV2")
    @FormUrlEncoded
    Observable<AuatarListInfo> avatarList(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id
    );

    //競猜列表
    @POST("api/bet/list")
    @FormUrlEncoded
    Observable<GuessListInfo> GuessList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("type") int type,
            @Field("user_name") String user_name
    );

    //發佈競猜
    @POST("api/bet/publish")
    @FormUrlEncoded
    Observable<BaseInfo> publishGuess(
            @Header("Authorization") String token,
            @Field("title") String title,
            @Field("limit_people_number") String limit_people_number,
            @Field("single_coin") String single_coin,
            @Field("coin_id") String coin_id,
            @Field("deadline") String deadline,
            @Field("second_password") String second_password,
            @Field("password") String password
    );

    //競猜詳情
    @POST("api/bet/info")
    @FormUrlEncoded
    Observable<GuessInfo> GuessInfo(
            @Header("Authorization") String token,
            @Field("bet_id") int bet_id,
            @Field("period_qty") int period_qty
    );

    //獲取隨機數組
    @POST("api/bet/randomNumber")
    Observable<BaseInfo> getRandom(
            @Header("Authorization") String token
    );

    //投注
    @POST("api/bet/coin")
    @FormUrlEncoded
    Observable<BetInfo> bet(
            @Header("Authorization") String token,
            @Field("bet_id") int bet_id,
            @Field("period_qty") int period_qty,
            @Field("coin_id") String coin_id,
            @Field("bet_number") String bet_number,
            @Field("second_password") String second_password
    );

    //我發佈的競猜列表
    @POST("api/bet/userPublishList")
    @FormUrlEncoded
    Observable<GuessListInfo> getMyStart(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("status") int status
    );

    //我參與的競猜列表
    @POST("api/bet/userJoinList")
    @FormUrlEncoded
    Observable<GuessListInfo> getMyJoin(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("status") int status
    );


    //我的廣告列表
    @POST("api/trans/userLists")
    @FormUrlEncoded
    Observable<MyAdListInfo> getMyAdList(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("coin_name") String coin_name,
            @Field("status") int status
    );

    //歷史競猜
    @POST("api/bet/history")
    @FormUrlEncoded
    Observable<GuessListInfo> getGuessHistory(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size
    );

    //新聞列表
    @POST("api/news/list")
    @FormUrlEncoded
    Observable<NewsListInfo> getNewsList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("lang") String lang
    );

    //我發佈的新聞列表
    @POST("api/news/myNews")
    @FormUrlEncoded
    Observable<GonggaoListInfo> myNewsList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("status") int status
    );

    //草稿箱列表
    @POST("api/news/myNewsDraft")
    @FormUrlEncoded
    Observable<GonggaoListInfo> NewsDraftList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size
    );

    //刪除新聞
    @POST("api/news/delete")
    @FormUrlEncoded
    Observable<BaseInfo> deleteNews(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    //公告列表
    @POST("api/bulletin/list")
    @FormUrlEncoded
    Observable<GonggaoListInfo> GonggaoList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("status") int status
    );

    //瀏覽記錄列表
    @POST("api/news/history")
    @FormUrlEncoded
    Observable<GonggaoListInfo> NewsHistoryList(
            @Header("Authorization") String token,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size
    );

    //個人資料
    @FormUrlEncoded
    @POST("chat/user/profile")
    Observable<IndividualInfo> getIndividual(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id
    );

    //修改備註
    @FormUrlEncoded
    @POST("chat/user/remark")
    Observable<IndividualInfo> getChangeRemark(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id,
            @Field("remark") String remark
    );

    //獲取好友備註名
    @FormUrlEncoded
    @POST("api/user/getUserRemark")
    Observable<RemarkListInfo> getRemarkList(
            @Header("Authorization") String token,
            @Field("name") String name
    );

    //修改備註
    @FormUrlEncoded
    @POST("api/trans/total")
    Observable<OrderStatisticsInfo> getTotal(
            @Header("Authorization") String token,
            @Field("coin_name") String coin_name
    );

    //瀏覽記錄列表
    @POST("api/trans/myOrderLists")
    @FormUrlEncoded
    Observable<OrderListInfo> getMyOrderList(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("page_id") int page_id,
            @Field("page_size") int page_size,
            @Field("coin_name") String coin_name
    );

    //清除新聞瀏覽記錄
    @POST("api/news/deleteHistory")
    Observable<BaseInfo> deleteDrowsingHistory(
            @Header("Authorization") String token
    );

    //發佈新聞費用
    @POST("api/news/ad_cost")
    Observable<BaseInfo> getAdCost(
            @Header("Authorization") String token
    );

    //刪除動態
    @FormUrlEncoded
    @POST("api/dynamic/destroy")
    Observable<BaseInfo> deleteDynamic(
            @Header("Authorization") String token,
            @Field("dynamic_id") String dynamic_id
    );

    //搜索用戶
    @FormUrlEncoded
    @POST("chat/search/friend")
    Observable<BaseInfo> searchUser(
            @Header("Authorization") String token,
            @Field("username") String username
    );

    //刪除動態
    @FormUrlEncoded
    @POST("api/updateLog")
    Observable<UpdateLogInfo> getUpdateLogList(
            @Header("Authorization") String token,
            @Field("type") int type
    );

    //掃描商家二維碼
    @FormUrlEncoded
    @POST("api/merchant/recharge_code_info")
    Observable<BaseInfo> rechargeInfo(
            @Header("Authorization") String token,
            @Field("code") int code
    );

    //創建群聊房間
    @FormUrlEncoded
    @POST("chat/room/create")
    Observable<GroupCreateInfo> createGroup(
            @Header("Authorization") String token,
            @Field("name") String name,
            @Field("toco_ids") String toco_ids,
            @Field("description") String description
    );

    //動態打賞
    @FormUrlEncoded
    @POST("api/dynamic/reward")
    Observable<BaseInfo> giveReward(
            @Header("Authorization") String token,
            @Field("dynamic_id") int dynamic_id,
            @Field("number") String number,
            @Field("coin_id") int coin_id,
            @Field("second_password") String second_password);

    //獲取群聊房間
    @POST("chat/room/query")
    Observable<GroupInfo> getGroup(
            @Header("Authorization") String token
    );

    //不看他的動態
    @FormUrlEncoded
    @POST("api/dynamic/shieldV2")
    Observable<BaseInfo> noLookTaDy(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("toco_id") String toco_id
    );

    //獲取好友列表
    @POST("chat/friend/list")
    Observable<AuatarListInfo> getFriendsList(
            @Header("Authorization") String token
    );

    //添加好友
    @FormUrlEncoded
    @POST("chat/add/friend")
    Observable<BaseInfo> addFriend(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id,
            @Field("friend_label") String friend_label
    );

    //刪除好友
    @FormUrlEncoded
    @POST("chat/del/friend")
    Observable<BaseInfo> deleteFriend(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id
    );

    //確認添加好友
    @FormUrlEncoded
    @POST("chat/confirm/add/friend")
    Observable<BaseInfo> confirmAddFriend(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id,
            @Field("status") int status
    );

    //退出群房間
    @FormUrlEncoded
    @POST("chat/room/quit")
    Observable<BaseInfo> deleteGroup(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id,
            @Field("group_id") int group_id
    );

    //刪除自己的評論
    @FormUrlEncoded
    @POST("api/review/del")
    Observable<BaseInfo> deleteComment(
            @Header("Authorization") String token,
            @Field("review_id") int review_id
    );

    //根据群组ID查询所有成员
    @FormUrlEncoded
    @POST("chat/room/query/id")
    Observable<GroupMemberInfo> selectGroupMember(
            @Header("Authorization") String token,
            @Field("group_id") int group_id
    );

    //邀請加入群聊
    @FormUrlEncoded
    @POST("chat/room/join")
    Observable<BaseInfo> inviteGroup(
            @Header("Authorization") String token,
            @Field("group_id") String group_id,
            @Field("toco_ids") String toco_ids
    );

    //邀請加入群聊
    @FormUrlEncoded
    @POST("api/user/profile/by_id")
    Observable<UserDataInfo> getUserData(
            @Header("Authorization") String token,
            @Field("user_id") int user_id
    );

    //修改群名稱
    @FormUrlEncoded
    @POST("chat/room/update/name")
    Observable<BaseInfo> updataGroupName(
            @Header("Authorization") String token,
            @Field("name") String name,
            @Field("group_id") int group_id
    );

    //修改自己在群的昵称
    @FormUrlEncoded
    @POST("chat/room/update/remark")
    Observable<BaseInfo> updataGroupMemberName(
            @Header("Authorization") String token,
            @Field("remark") String remark,
            @Field("group_id") int group_id
    );

    //轉讓群主
    @FormUrlEncoded
    @POST("chat/room/transfer/group")
    Observable<BaseInfo> transferGroup(
            @Header("Authorization") String token,
            @Field("toco_id") String toco_id,
            @Field("group_id") int group_id
    );

    //群主踢人
    @FormUrlEncoded
    @POST("chat/room/kick/out")
    Observable<BaseInfo> kickOutGroup(
            @Header("Authorization") String token,
            @Field("toco_ids") String toco_ids,
            @Field("group_id") int group_id
    );

    //修改群頭像
    @FormUrlEncoded
    @POST("chat/room/update/logo")
    Observable<BaseInfo> updateLogoGroup(
            @Header("Authorization") String token,
            @Field("content") String content,
            @Field("group_id") int group_id
    );

    //獲取請求列表
    @POST("chat/new/friend")
    Observable<NewFriendInfo> getNewFriendData(
            @Header("Authorization") String token
    );

    //修改群公告
    @FormUrlEncoded
    @POST("chat/room/update/bullet")
    Observable<BaseInfo> getNewFriendData(
            @Header("Authorization") String token,
            @Field("group_id") int group_id,
            @Field("bulletin") String bulletin
    );

    //設置手勢密碼
    @FormUrlEncoded
    @POST("api/user/setGesture")
    Observable<BaseInfo> setGesture(
            @Header("Authorization") String token,
            @Field("gesture") int gesture,
            @Field("second_password") String second_password,
            @Field("gesture_number") String gesture_number
    );

    //設置指紋密碼
    @FormUrlEncoded
    @POST("api/user/setFingerprint")
    Observable<BaseInfo> setFingerprint(
            @Header("Authorization") String token,
            @Field("fingerprint") int fingerprint,
            @Field("second_password") String second_password
    );

    //修改群頭像、名稱是否允許成員修改
    @FormUrlEncoded
    @POST("chat/room/update/setting")
    Observable<GroupInfo> setAllowModifyt(
            @Header("Authorization") String token,
            @Field("group_id") int group_id,
            @Field("is_allow_modify_data") int is_allow_modify_data

    );

    //修改群是否要審核
    @FormUrlEncoded
    @POST("chat/room/is_review")
    Observable<GroupInfo> setIsReview(
            @Header("Authorization") String token,
            @Field("group_id") int group_id,
            @Field("is_review") int is_review

    );

    //獲取群邀請列表
    @FormUrlEncoded
    @POST("chat/room/review/list")
    Observable<ReviewInfo> getReviewList(
            @Header("Authorization") String token,
            @Field("group_id") int group_id

    );

    //群主同意/拒絕加群
    @FormUrlEncoded
    @POST("chat/room/add/update_status")
    Observable<BaseInfo> setReview(
            @Header("Authorization") String token,
            @Field("group_id") int group_id,
            @Field("status") int status,
            @Field("toco_id") String toco_id
    );

    //收藏列表
    @POST("api/collect/list")
    Observable<CollectInfo> CollectList(
            @Header("Authorization") String token
    );

    //收藏列表
    @FormUrlEncoded
    @POST("api/collect/del")
    Observable<BaseInfo> deleteCollect(
            @Header("Authorization") String token,
            @Field("coll_id") int coll_id
    );

    //收藏列表
    @FormUrlEncoded
    @POST("api/collect/add")
    Observable<BaseInfo> addCollect(
            @Header("Authorization") String token,
            @Field("title") String title,
            @Field("url") String url,
            @Field("icon") String icon
    );

    //商戶信息
    @FormUrlEncoded
    @POST("api/offline/pay/info")
    Observable<UserDataInfo> getMerchantUser(
            @Header("Authorization") String token,
            @Field("code") String code
    );

    //支付給商家
    @FormUrlEncoded
    @POST("api/offline/pay/action")
    Observable<BaseInfo> payMerchant(
            @Header("Authorization") String token,
            @Field("code") String code,
            @Field("number") String number,
            @Field("second_password") String second_password,
            @Field("remark") String remark
    );

    //商戶信息
    @FormUrlEncoded
    @POST("api/collect/updateOrderBy")
    Observable<BaseInfo> saveSequence(
            @Header("Authorization") String token,
            @Field("orderby") String orderby
    );

    //待领取的红包
    @FormUrlEncoded
    @POST("chat/room/red_packet/un_receive")
    Observable<UnclaimedRedInfo> getUnclaimedRed(
            @Header("Authorization") String token,
            @Field("group_id") int group_id
    );

    //刪除加群請求
    @FormUrlEncoded
    @POST("chat/room/review/del")
    Observable<BaseInfo> deleteGroupReview(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("group_id") int group_id
    );

    //刪除添加好友請求
    @FormUrlEncoded
    @POST("chat/new/friend/del")
    Observable<BaseInfo> deleteReview(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    //上傳當前語言環境
    @FormUrlEncoded
    @POST("chat/update/lang")
    Observable<BaseInfo> postLanguage(
            @Header("Authorization") String token,
            @Field("lang") String lang
    );

    //更換聊天背景
    @FormUrlEncoded
    @POST("chat/update/chat_bg")
    Observable<BaseInfo> changeBackgound(
            @Header("Authorization") String token,
            @Field("content") String content
    );

    //獲取聊天beij背景
    @POST("chat/get/chat_bg")
    Observable<BaseInfo> getBackgound(
            @Header("Authorization") String token
    );

    //設置置頂消息
    @FormUrlEncoded
    @POST("chat/set/chat_top")
    Observable<BaseInfo> setTopMessage(
            @Header("Authorization") String token,
            @Field("for_id") String for_id,
            @Field("status") int status
    );

    //獲取置頂消息
    @POST("chat/get/chat_top")
    Observable<MessageTopInfo> getTopMessage(
            @Header("Authorization") String token
    );

    //獲取公眾號列表
    @POST("chat/public_number/list")
    Observable<PublicInfo> publicList(
            @Header("Authorization") String token
    );

    //搜索公眾號
    @FormUrlEncoded
    @POST("chat/public_number/search")
    Observable<PublicInfo> searchList(
            @Header("Authorization") String token,
            @Field("name") String name
    );

    //公眾號詳情
    @FormUrlEncoded
    @POST("chat/public_number/info")
    Observable<PublicDetailsInfo> publicDeltails(
            @Header("Authorization") String token,
            @Field("public_number_id") int public_number_id
    );

    //公眾號關注
    @FormUrlEncoded
    @POST("chat/public_number/attention")
    Observable<PublicDetailsInfo> publicAdd(
            @Header("Authorization") String token,
            @Field("public_number_id") int public_number_id
    );

    //公眾號取消關注
    @FormUrlEncoded
    @POST("chat/public_number/cancel/attention")
    Observable<BaseInfo> publicUnfollow(
            @Header("Authorization") String token,
            @Field("public_number_id") int public_number_id
    );

    //重置聊天背景
    @POST("chat/del/chat_bg")
    Observable<BaseInfo> deleteBackgound(
            @Header("Authorization") String token
    );
    //重置聊天背景
    @POST("api/image/coordinate")
    @FormUrlEncoded
    Observable<BaseInfo> coordinate(
            @Field("email") String email,
            @Field("coordinate") String coordinate
    );

    //支付宝绑定
    @POST("api/user/bind/alipay")
    @FormUrlEncoded
    Observable<BaseInfo> bindAlipay(
            @Header("Authorization") String token,
            @Field("uuid") String coordinate
    );

    //支付宝解绑
    @POST("api/user/unbind/alipay")
    @FormUrlEncoded
    Observable<BaseInfo> unbindAlipay(
            @Header("Authorization") String token,
            @Field("second_password") String second_password
    );

    //支付宝订单
    @POST("chat/alipay/generate/order")
    @FormUrlEncoded
    Observable<BaseInfo> alipayOrder(
            @Header("Authorization") String token,
            @Field("second_password") String second_password
    );
    //绑定分销账号
    @POST("team/bind")
    @FormUrlEncoded
    Observable<BaseInfo> bingTeam(
            @Header("Authorization") String token,
            @Field("nameOrEmail") String nameOrEmail,
            @Field("password") String password
    );

    //绑定账号说明
    @POST("team/bind/desc")
    Observable<BaseInfo> bingDesc(
            @Header("Authorization") String token
    );

    //账号信息
    @POST("team/bind/info")
    Observable<BindingInfo> infoFTC(
            @Header("Authorization") String token
    );

    //我的团队
    @POST("team/my/team")
    @FormUrlEncoded
    Observable<MyTeamInfo> myTeam(
            @Header("Authorization") String token,
            @Field("user_id") int user_id,
            @Field("page") int page
    );

    //我的团队
    @POST("team/node/dividend")
    @FormUrlEncoded
    Observable<NodeInfo> nodelist(
            @Header("Authorization") String token,
            @Field("type") int type,
            @Field("page") int page
    );

    //升级节点数据
    @POST("team/node/buy")
    Observable<UpgradeInfo> nodeBuy(
            @Header("Authorization") String token
    );

    //升级购买
    @POST("team/node/buy/action")
    @FormUrlEncoded
    Observable<BaseInfo> nodeBuyAction(
            @Header("Authorization") String token,
            @Field("second_password") String second_password,
            @Field("number") String number,
            @Field("type") int type
    );

    //购买记录
    @POST("team/node/buy/history")
    @FormUrlEncoded
    Observable<HistoryInfo> historyFtc(
            @Header("Authorization") String token,
            @Field("page") int page
    );
}
