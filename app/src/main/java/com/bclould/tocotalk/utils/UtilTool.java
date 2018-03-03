package com.bclould.tocotalk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.impl.JidCreate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.bclould.tocotalk.Presenter.LoginPresenter.LOGINPW;
import static com.bclould.tocotalk.Presenter.LoginPresenter.MYUSERNAME;
import static com.bclould.tocotalk.Presenter.LoginPresenter.TOKEN;
import static com.bclould.tocotalk.Presenter.LoginPresenter.USERID;

/**
 * Created by GA on 2017/10/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UtilTool {
    private static final int QR_HEIGHT = 1000;
    private static final int QR_WIDTH = 1000;
    private static MediaPlayer mediaPlayer;

    public static byte[] readStream(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();
    }

    public static int getFileDuration(String fileName, Context context) {
        try {
            if (fileName != null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, Uri.parse(fileName));
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer.getDuration() / 1000;
    }

    public static synchronized String createtFileName() {
        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = fmt.format(dt);
        return fileName;
    }

    /**
     * 保存View为图片的方法
     */
    public static boolean saveBitmap(View v) {
        long time = System.currentTimeMillis();
        String fileName = "image" + time + ".png";
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        File f = new File("/sdcard/Pictures/Screenshots/", fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getVersionCode(Context mContext) {
        String versionCode = "";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void getPermissions(final Activity activity, String permission, String permission2, final String toast) {
        RxPermissions permissions = new RxPermissions(activity);
        if (permission2.isEmpty()) {
            permissions.request(permission).subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        PictureFileUtils.deleteCacheDirFile(activity);
                    } else {
                        Toast.makeText(activity,
                                toast, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            });
        } else {
            permissions.request(permission, permission2).subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        PictureFileUtils.deleteCacheDirFile(activity);
                    } else {
                        Toast.makeText(activity,
                                toast, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            });
        }
    }

    //要转换的地址或字符串,可以是中文
    public static Bitmap createQRImage(String url) {
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap setDefaultimage(Context context) {
        Drawable drawable = context.getDrawable(R.mipmap.img_nfriend_headshot1);
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static String saveImages(Bitmap bitmap, String user, Context context, DBManager mgr) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = context.getFilesDir().getAbsolutePath() + "/images/" + user + ".jpg";
            File image = new File(path);
            if (image.exists() && image.length() != bitmap.getByteCount()) {
                image.delete();
            }
            FileOutputStream fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (!mgr.findUser(user))
                mgr.addUser(user, path);
            UtilTool.Log("日志", "保存成功");

            return path;
        } catch (Exception e) {
            try {
                Drawable drawable = context.getResources().getDrawable(R.mipmap.img_nfriend_headshot1);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmap = bd.getBitmap();
                File file = new File(context.getFilesDir().getAbsolutePath() + "/images");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String path = context.getFilesDir().getAbsolutePath() + "/images/" + user + ".jpg";
                File image = new File(path);
                if (image.exists() && image.length() != bitmap.getByteCount()) {
                    image.delete();
                }
                FileOutputStream fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                if (!mgr.findUser(user))
                    mgr.addUser(user, path);
                UtilTool.Log("日志", "保存成功");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户头像信息
     *
     * @param connection
     * @param user
     * @return
     */
    public static byte[] getUserImage(XMPPConnection connection, String user) {
        VCard vcard = null;
        try {
            vcard = new VCard();
            ProviderManager.addIQProvider("vCard", "vcard-temp",
                    new VCardProvider());

            vcard.load(connection, JidCreate.entityBareFrom(user));

            if (vcard == null || vcard.getAvatar() == null)
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vcard.getAvatar();
    }

    public static String exChange(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(Character.toLowerCase(c));
                } else if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                }
            }
        }

        return sb.toString();
    }

    public static String getToken() {

        return "bearer" + MySharedPreferences.getInstance().getString(TOKEN);

    }

    public static int getUserId() {

        return MySharedPreferences.getInstance().getInteger(USERID);

    }

    public static String getMyUser() {

        return MySharedPreferences.getInstance().getString(MYUSERNAME);

    }

    public static String getpw() {

        return MySharedPreferences.getInstance().getString(LOGINPW);

    }

    //打印日志
    public static void Log(String clazzName, String s) {

        Log.e(clazzName, s);

    }

    //判斷是否快速點擊
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    //判断网络是否可用

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {

            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //判断Wifi是否打开
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    //提取字符串中的数字
    public static String getNum(String str) {
        String str2 = "";
        str = str.trim();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;

    }

    public static String base64PetToJson(String prefix, String key, String value, String message) {
        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            object.put(key, value);
            object.put("message", message);
            jsonresult = object.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str = Base64.encodeToString(jsonresult.getBytes(), Base64.DEFAULT);
        return prefix + str;
    }

    public static Bitmap getMyImage(DBManager mgr, String myUser) {
        List<UserInfo> info = mgr.queryUser(myUser);
        if (info.size() != 0)
            return BitmapFactory.decodeFile(info.get(0).getPath());
        return null;
    }

    public static void playHint(Context context) {
        try {
            AssetManager assetManager = context.getAssets();   ////获得该应用的AssetManager
            AssetFileDescriptor afd = assetManager.openFd("hint.mp3");   //根据文件名找到文件
            //对mediaPlayer进行实例化
            mediaPlayer = new MediaPlayer();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();    //如果正在播放，则重置为初始状态
            }
            mediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());     //设置资源目录
            mediaPlayer.prepare();//缓冲
            mediaPlayer.start();//开始或恢复播放
        } catch (IOException e) {
            Log("日志", "没有找到这个文件");
            e.printStackTrace();
        }
    }

    public static void playVoice(Context context, String fileName) {
        try {
            //对mediaPlayer进行实例化
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.setDataSource(fileName);     //设置资源目录
                mediaPlayer.prepare();//缓冲
                mediaPlayer.start();//开始或恢复播放
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (IOException e) {
            Log("日志", "没有找到这个文件");
            e.printStackTrace();
        }
    }
}