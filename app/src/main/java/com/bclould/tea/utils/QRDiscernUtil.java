package com.bclould.tea.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.widget.Toast;

import com.bclould.tea.Presenter.ReceiptPaymentPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.QrCardInfo;
import com.bclould.tea.model.QrPaymentInfo;
import com.bclould.tea.model.QrReceiptInfo;
import com.bclould.tea.model.QrRedInfo;
import com.bclould.tea.model.ReceiptInfo;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.GroupConfirmActivity;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.ui.activity.PayReceiptResultActivity;
import com.bclould.tea.ui.activity.PaymentActivity;
import com.bclould.tea.ui.activity.ProblemFeedBackActivity;
import com.bclould.tea.ui.activity.ScanQRCodeActivity;
import com.bclould.tea.ui.activity.ScanQRResultActivty;
import com.bclould.tea.ui.activity.SelectConversationActivity;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tea.utils.Constants.GROUPCARD;
import static com.bclould.tea.utils.Constants.REDPACKAGE;

/**
 * Created by GIjia on 2018/7/5.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QRDiscernUtil {
    private Activity mContext;
    private String url;
    private  Result re;
    private MenuListPopWindow menu;
    public QRDiscernUtil(Activity context){
        this.mContext=context;
    }

    public void discernQR(final String url){
        try {
            this.url=url;
            new Thread(){
                @Override
                public void run() {
                    Bitmap obmp= BitmapFactory.decodeFile(getImgPathFromCache(url));
                    int width = obmp.getWidth();
                    int height = obmp.getHeight();
                    int[] data = new int[width * height];
                    obmp.getPixels(data, 0, width, 0, 0, width, height);
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
                    BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                    QRCodeReader reader = new QRCodeReader();
                    try {
                        re = reader.decode(bitmap1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message=new Message();
                    message.what=1;
                    if (re != null) {
                        UtilTool.Log("fengjian","有二維碼"+re.toString());
                        message.obj=true;
                    }else {
                        message.obj=false;
                    }
                    mHandler.sendMessage(message);
                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取glide之前缓存过的图片地址
     * @param url 网络图片的地址
     * @return
     */
    private String getImgPathFromCache(String url) {
        FutureTarget<File> future = Glide.with(mContext)
                .load(url)
                .downloadOnly(400, 400);
        try {
            File cacheFile = future.get();
            String absolutePath = cacheFile.getAbsolutePath();
            UtilTool.Log("fengjian","圖片地址："+absolutePath);
            return absolutePath;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //解析二维码图片,返回结果封装在Result对象中
    private Result  parseQRcodeBitmap(String bitmapPath){
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //获取到待解析的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true;
        //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath,options);
        //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
        //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
        options.inSampleSize = options.outHeight / 400;
        if(options.inSampleSize <= 0){
            options.inSampleSize = 1; //防止其值小于或等于0
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[width * height];
        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(width, height, data);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    boolean isShowQr= (boolean) msg.obj;
                    showDialog(isShowQr);
                    break;
                case 2:
                    String filePath= (String) msg.obj;
                    if(StringUtils.isEmpty(filePath)){
                        ToastShow.showToast2(mContext,mContext.getString(R.string.picture_is_not_loaded));
                        return;
                    }
                    if(UtilTool.saveAlbum(filePath,mContext)){
                        ToastShow.showToast2(mContext,mContext.getString(R.string.save_success));
                    }else{
                        ToastShow.showToast2(mContext,mContext.getString(R.string.save_error));
                    }
                    menu.dismiss();
                    break;
                case 3:
                    String filePath1= (String) msg.obj;
                    if(StringUtils.isEmpty(filePath1)){
                        ToastShow.showToast2(mContext,mContext.getString(R.string.picture_is_not_loaded));
                        return;
                    }
                    Intent intent = new Intent(mContext, SelectConversationActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("msgType", TO_IMG_MSG);
                    MessageInfo messageInfo=new MessageInfo();
                    messageInfo.setVoice(filePath1);
                    intent.putExtra("messageInfo", messageInfo);
                    mContext.startActivity(intent);
                    menu.dismiss();
                    break;
            }
        }
    };

    private void showDialog(boolean isShowQr){
        List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.save_image));
        list.add(mContext.getString(R.string.transmit));
        if(isShowQr)
        list.add(mContext.getString(R.string.discern_qr));
        menu = new MenuListPopWindow(mContext, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position){
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        new Thread(){
                            @Override
                            public void run() {
                                String filePath = UtilTool.getImgPathFromCache(url, mContext);
                                Message message=new Message();
                                message.what=2;
                                message.obj=filePath;
                                mHandler.sendMessage(message);
                            }
                        }.start();
                        break;
                    case 2:
                        //轉發
                        new Thread(){
                            @Override
                            public void run() {
                                String filePath = UtilTool.getImgPathFromCache(url, mContext);
                                Message message=new Message();
                                message.what=3;
                                message.obj=filePath;
                                mHandler.sendMessage(message);
                            }
                        }.start();
                        break;
                    case 3:
                        //識別
                        menu.dismiss();
                        if(re!=null){
                            goActivity(re.toString());
                        }
//                        Result result= parseQRcodeBitmap(url);
                        UtilTool.Log("fengjian",re.toString());
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    public void goActivity(String result){
        if (result != null && !result.isEmpty()) {
            if (result.contains(Constants.BUSINESSCARD)) {
                String base64 = result.substring(Constants.BUSINESSCARD.length(), result.length());
                String jsonresult = new String(Base64.decode(base64,Base64.DEFAULT));
                UtilTool.Log("日志", jsonresult);

                Gson gson = new Gson();
                QrCardInfo qrCardInfo = gson.fromJson(jsonresult, QrCardInfo.class);
                String name = qrCardInfo.getName();
                Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("user", name);
                intent.putExtra("roomId", name);
                mContext.startActivity(intent);
                mContext.finish();
            } else if (result.contains(Constants.MONEYIN)) {
                try {
                    String base64 = result.substring(Constants.MONEYIN.length(), result.length());
                    String jsonresult =new String(Base64.decode(base64,Base64.DEFAULT));
                    UtilTool.Log("日志", jsonresult);
                    Gson gson = new Gson();
                    QrReceiptInfo qrReceiptInfo = gson.fromJson(jsonresult, QrReceiptInfo.class);
                    if (qrReceiptInfo.getCoin_id() == null && qrReceiptInfo.getCoin_name() == null && qrReceiptInfo.getNumber() == null) {
                        Intent intent = new Intent(mContext, PaymentActivity.class);
                        intent.putExtra("userId", qrReceiptInfo.getUser_id() + "");
                        intent.putExtra("username", qrReceiptInfo.getUser_name());
                        intent.putExtra("type", Constants.MONEYIN);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, PaymentActivity.class);
                        intent.putExtra("userId", qrReceiptInfo.getUser_id() + "");
                        intent.putExtra("username", qrReceiptInfo.getUser_name());
                        intent.putExtra("coinId", qrReceiptInfo.getCoin_id());
                        intent.putExtra("coinName", qrReceiptInfo.getCoin_name());
                        intent.putExtra("number", qrReceiptInfo.getNumber());
                        intent.putExtra("mark", qrReceiptInfo.getMark());
                        intent.putExtra("type", Constants.DATAMONEYIN);
                        mContext.startActivity(intent);
                    }
                    mContext.finish();
                } catch (Exception e) {
                    Toast.makeText(mContext, mContext.getString(R.string.scan_qr_code_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (result.contains(Constants.MONEYOUT)) {
                try {
                    String base64 = result.substring(Constants.MONEYOUT.length(), result.length());
                    String jsonresult = new String(Base64.decode(base64,Base64.DEFAULT));
                    UtilTool.Log("日志", jsonresult);
                    Gson gson = new Gson();
                    QrPaymentInfo qrPaymentInfo = gson.fromJson(jsonresult, QrPaymentInfo.class);
                    if (qrPaymentInfo.getStatus() == 1) {
                        new ReceiptPaymentPresenter(mContext).receipt(qrPaymentInfo.getData(), new ReceiptPaymentPresenter.CallBack5() {
                            @Override
                            public void send(ReceiptInfo.DataBean data) {
                                Intent intent = new Intent(mContext, PayReceiptResultActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("coinName", data.getCoin_name());
                                bundle.putString("date", data.getDate());
                                bundle.putString("name", data.getName());
                                bundle.putString("number", data.getNumber());
                                bundle.putString("type", Constants.MONEYOUT);
                                bundle.putString("avatar", data.getAvatar());
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                                mContext.finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, mContext.getString(R.string.scan_qr_code_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (result.equals(Constants.KEFU)) {
                try {
                    mContext.startActivity(new Intent(mContext, ProblemFeedBackActivity.class));
                    mContext.finish();
                } catch (Exception e) {
                    Toast.makeText(mContext, mContext.getString(R.string.scan_qr_code_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if(result.contains(REDPACKAGE)){
                String base64 = result.substring(Constants.REDPACKAGE.length(), result.length());
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                String jsonresult = new String(bytes);
                Gson gson = new Gson();
                QrRedInfo qrRedInfo = gson.fromJson(jsonresult, QrRedInfo.class);
                UtilTool.Log("日志", qrRedInfo.getRedID());
                Intent intent = new Intent(mContext, GrabQRCodeRedActivity.class);
                intent.putExtra("id", qrRedInfo.getRedID());
                intent.putExtra("type", true);
                mContext.startActivity(intent);
                mContext.finish();
            }else if(result.contains(GROUPCARD)){
                String base64 = result.substring(Constants.GROUPCARD.length(), result.length());
                String jsonresult = new String(Base64.decode(base64,Base64.DEFAULT));
                UtilTool.Log("日志", jsonresult);
                Gson gson = new Gson();
                QrCardInfo qrCardInfo = gson.fromJson(jsonresult, QrCardInfo.class);
                Intent intent=new Intent(mContext, GroupConfirmActivity.class);
                intent.putExtra("roomName",qrCardInfo.getRoomName());
                intent.putExtra("roomId",qrCardInfo.getRoomId());
                intent.putExtra("roomPath",qrCardInfo.getRoomPath());
                mContext.startActivity(intent);

            }else{
                Intent intent = new Intent(mContext, ScanQRResultActivty.class);
                intent.putExtra("result", result);
                mContext.startActivity(intent);
                mContext.finish();
            }
        }
    }
}
