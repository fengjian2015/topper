package com.bclould.tocotalk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.bclould.tocotalk.Presenter.LoginPresenter.LOGINPW;
import static com.bclould.tocotalk.Presenter.LoginPresenter.MYUSERNAME;
import static com.bclould.tocotalk.Presenter.LoginPresenter.TOCOID;
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

    public static File getVideoCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), "video-cache");
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap toturn(String imagePath, Bitmap img, int degress) {
        Matrix matrix = new Matrix();
        matrix.postRotate(+degress); /*翻转90度*/
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(imagePath));
            img.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
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

    //創建聊天時間
    public static Long createChatCreatTime() {
        return System.currentTimeMillis();
    }

    public static String createChatTime() {
        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static void comp(Bitmap image, File file) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 500f;//这里设置高度为800f
        float ww = 500f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        compressImage(bitmap, file);//压缩好比例大小后再进行质量压缩
    }

    private static void compressImage(Bitmap image, File file) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 80) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把batmap 转file
     *
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 保存View为图片的方法
     */
    public static boolean saveBitmap(View v, Context context) {
        String fileName = "image" + createtFileName() + ".png";
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        File file = new File(Constants.PUBLICDIR, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
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
        UtilTool.Log("保存頭像", user + "");
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = context.getFilesDir().getAbsolutePath() + "/images/" + createtFileName() + ".jpg";
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
            else
                mgr.updatePath(user, path);
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
                else
                    mgr.updatePath(user, path);
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


    public static String getTocoId() {

        return MySharedPreferences.getInstance().getString(TOCOID);

    }


    public static int getUserId() {

        return MySharedPreferences.getInstance().getInteger(USERID);

    }

    public static String getJid() {

        return MySharedPreferences.getInstance().getString(MYUSERNAME);

    }

    public static String getUser() {
        if (!getJid().isEmpty()) {
            return getJid().substring(0, getJid().indexOf("@"));
        }
        return "";
    }

    public static String getpw() {

        return MySharedPreferences.getInstance().getString(LOGINPW);

    }


    //打印日志
    public static void Log(String clazzName, String s) {
        String name = getFunctionName();
        if (name != null) {
            Log.e(clazzName, name + " - " + s);
        } else {
            Log.e(clazzName, s);
        }
    }


    /**
     * Get The Current Function Name
     *
     * @return
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals("com.bclould.tocotalk.utils.UtilTool")) {
                continue;
            }
            return "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    //判斷是否快速點擊
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 60000) {
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

    public static String base64PetToJson(Context context, String prefix, String key, String value, String message) {
        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            object.put(key, value);
            object.put("message", message);
            if (message.equals(context.getString(R.string.receipt_payment))) {
                object.put("user_name", getUser());
            }
            jsonresult = object.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str = Base64.encodeToString(jsonresult.getBytes(), Base64.DEFAULT);
        return prefix + str;
    }

    public static String base64PetToJson2(String prefix, String key, String value, String key2, String value2, String key3, String value3, String key4, String value4, String key5, String value5, String key6, String value6) {
        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            object.put(key, value);
            object.put(key2, value2);
            object.put(key3, value3);
            object.put(key4, value4);
            object.put(key5, value5);
            object.put(key6, value6);
            jsonresult = object.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str = Base64.encodeToString(jsonresult.getBytes(), Base64.DEFAULT);
        return prefix + str;
    }

    public static void setCircleImg(Context context, Object url, ImageView imageView) {
        if (Util.isOnMainThread() && context != null) {
            if (url instanceof String) {
                Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.img_nfriend_headshot1)).into(imageView);
            } else {
                Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop()).error(R.mipmap.img_nfriend_headshot1).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
            }
        }
    }

    public static Bitmap getImage(DBManager mgr, String myUser, Context context, ImageView imageView) {
        Bitmap bitmap = null;
        if (mgr.findUser(myUser)) {
            UserInfo info = mgr.queryUser(myUser);
            if (!info.getPath().isEmpty()) {
                UtilTool.Log("頭像", info.getPath());
                if (Util.isOnMainThread() && context != null) {
                    Glide.with(context).load(info.getPath()).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.img_nfriend_headshot1)).into(imageView);
                }
            } else {
                if (Util.isOnMainThread() && context != null) {
                    Glide.with(context).load(R.mipmap.img_nfriend_headshot1).apply(RequestOptions.bitmapTransform(new CircleCrop()).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
                }
            }
        } else {
            if (Util.isOnMainThread() && context != null) {
                Glide.with(context).load(R.mipmap.img_nfriend_headshot1).apply(RequestOptions.bitmapTransform(new CircleCrop()).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
            }
        }
        return bitmap;
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

    public static String getPostfix(String fileName) {
        String postfix = fileName.substring(fileName.lastIndexOf("."));
        if (postfix.equals(".png") || postfix.equals(".jpg") || postfix.equals(".jpeg") || postfix.equals(".gif") || postfix.equals(".JPEG") || postfix.equals(".PNG") || postfix.equals(".JPG") || postfix.equals(".GIF")) {
            return "Image";
        } else if (postfix.equals(".mp4") || postfix.equals(".mov") || postfix.equals(".MP4") || postfix.equals(".MOV")) {
            return "Video";
        } else {
            return "File";
        }
    }

    public static String getPostfix2(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getTitles() {
        String deadline = "";
        String mYear; // 当前年
        String mMonth; // 月
        String mDay;
        int current_day;
        int current_month;
        int current_year;

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        current_day = c.get(Calendar.DAY_OF_MONTH);
        current_month = c.get(Calendar.MONTH);
        current_year = c.get(Calendar.YEAR);
        for (int i = 0; i < 7; i++) {
            c.clear();//记住一定要clear一次
            c.set(Calendar.MONTH, current_month);
            c.set(Calendar.DAY_OF_MONTH, current_day);
            c.set(Calendar.YEAR, current_year);
            c.add(Calendar.DATE, +i);//j记住是DATE
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前日份的日期号码
            mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
            String date = mYear + "-" + mMonth + "-" + mDay;
            if (i == 6) {
                deadline = date;
            }
        }
        return deadline;
    }

    private static String[] mRandomArrs = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static int[] mRandomArr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static String[] getRandomArr(int betArrCount) {
        String[] arr = new String[betArrCount];
        for (int i = 0; i < betArrCount; i++) {
            arr[i] = getRandom(i);
        }
        return arr;
    }

    public static String getRandom(int index) {
        String random = "";
        for (int i = 0; i < 2; i++) {
            if (index <= 2) {
                int randomArrIndex = (int) (Math.random() * mRandomArr.length);
                random = random + mRandomArr[randomArrIndex];
            } else {
                int randomArrsIndex = (int) (Math.random() * mRandomArrs.length);
                random = random + mRandomArrs[randomArrsIndex];
            }
        }
        return random;
    }

    public static String removeZero(String sum) {
        if (sum.contains(".")) {
            char[] chars = sum.toCharArray();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < chars.length; i++) {
                list.add(chars[i] + "");
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).equals("0")) {
                    list.remove(i);
                } else if (list.get(i).equals(".")) {
                    list.remove(i);
                    break;
                } else {
                    break;
                }
            }
            String str = "";
            for (String s : list) {
                str += s;
            }
            return str;
        } else {
            return sum;
        }
    }

    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String createRootPath(Context context) {
        String cacheRootPath;
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    /**
     * sd卡是否可用
     *
     * @return
     */
    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String doubleMultiply(double a, double b) {
        BigDecimal a1 = new BigDecimal(Double.toString(a));
        BigDecimal b1 = new BigDecimal(Double.toString(b));
        BigDecimal result = a1.multiply(b1);// 相乘结果
        return removeZero(result.toString());
    }

    //文件轉二進制
    public static byte[] getFileToByte(File file) {
        byte[] by = new byte[(int) file.length()];
        try {
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            byte[] bb = new byte[2048];
            int ch;
            ch = is.read(bb);
            while (ch != -1) {
                bytestream.write(bb, 0, ch);
                ch = is.read(bb);
            }
            by = bytestream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return by;
    }

    // 判断是否为网址
    public static boolean checkLinkedExe(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            return false;
        }
        resultString = resultString.toLowerCase();
        String pattern = "^(http://|https://|ftp://|mms://|rtsp://){1}.*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(resultString);
        return m.matches();
    }

    public static Pattern searchUrl() {
//		String pattern1 = "((www|WWW)\\.){1}([a-zA-Z0-9\\u4e00-\\u9fa5\\.\\-\\~\\@\\#\\%\\^\\&\\+\\?\\:\\_\\/\\=\\<\\>]+)"; //有www.开头的
//		String pattern2 = "((((ht|f|Ht|F)tp(s?))|rtsp|Rtsp)\\://)(www\\.)?([a-zA-Z0-9\\u4e00-\\u9fa5\\.\\-\\~\\@\\#\\%\\^\\&\\+\\?\\:\\_\\/\\=\\<\\>]+)"; //有http等协议号开头
//		String pattern3 = "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)(\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|cn))(\\:((6553[0-5])|655[0-2]{2}\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|([1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|[0-9])))?(/([(a-zA-Z0-9)(\\u4e00-\\u9fa5)(\\.-~@#%^&+?:_/=<>)]*)*)"; //有.com等后缀的，并有参数
//		String pattern4 = "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)((\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|cn)))(\\:((6553[0-5])|655[0-2]{2}\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|([1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|[0-9])))?"; //有.com等后缀结尾的，无参数
//		String pattern5 = "((((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[1-9])\\.)((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){2}((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])))(\\:((6553[0-5])|655[0-2]{2}\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|([1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|[0-9])))?(/[a-zA-Z0-9\\u4e00-\\u9fa5\\.-~@#%^&+?:_/=<>]+)?)"; //ip地址的


        String port = "(\\:((6553[0-5])|655[0-2]{2}\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|([1-9]\\d{3}|[1-9]\\d{2}|[1-9]\\d|[0-9])))?";
        String domainName = "(\\.(com.cn|com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|cn|co|int|biz|CC|TV|pro|coop|aero|hk|tw|top))";
        String pattern1 = "((www|WWW)\\.){1}([a-zA-Z0-9\\u4e00-\\u9fa5\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]+)"; //有www.开头的
        String pattern2 = "((((ht|f|Ht|F)tp(s?))|rtsp|Rtsp)\\://)(www\\.)?(.+)"; //有http等协议号开头
        String pattern3 = pattern2 + "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)" + domainName + port + "(/(.*)*)"; //有.com等后缀的，并有参数
        String pattern4 = pattern2 + "([a-zA-Z0-9\\u4e00-\\u9fa5]+)([a-zA-Z0-9\\u4e00-\\u9fa5\\.]+)" + domainName + port; //有.com等后缀结尾的，无参数
        String pattern5 = "((((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[1-9])\\.)((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){2}((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])))" + port + "(\\/(.+))?)"; //ip地址的
        String pattern6 = "([a-zA-Z0-9\\.\\-\\~\\@\\#\\%\\^\\&\\+\\?\\:\\_\\/\\=\\<\\>]+)" + domainName + port; //有.com等后缀结尾的，无参数
        String pattern = pattern1 + "|" + pattern2 + "|" + pattern3 + "|" + pattern4 + "|" + pattern5 + "|" + pattern6;
        Pattern p = Pattern.compile(pattern);
        return p;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static String createMsgId(String from) {
        return UtilTool.getTocoId() + "to" + from + System.currentTimeMillis();
    }

    public static Long stringToLong(String time) {
        try {
            return Long.parseLong(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createChatCreatTime();
    }

    public static long getFolderSize(File file) {
        long size = 0;
        if (file.exists()) {
            size = 0;
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath
     * @param deleteThisPath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS)) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS / 1024)) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS / 1048576)) + "M";
        } else {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS / 1073741824)) + "G";
        }
        return fileSizeString;
    }
}
