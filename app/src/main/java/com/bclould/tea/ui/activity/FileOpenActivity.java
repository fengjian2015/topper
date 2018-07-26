package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bclould.tea.Presenter.FileDownloadPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.DownloadInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_DOC;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_DOCX;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_LOG;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_PDF;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_PPT;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_PPTX;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_RAR;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_RTF;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_TXT;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_XLS;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_XLSX;
import static com.bclould.tea.topperchat.WsContans.FILE_TYPE_ZIP;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FileOpenActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_type)
    ImageView mTvType;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.btn_open)
    Button mBtnOpen;
    private MessageInfo mMessageInfo;
    private FileDownloadPresenter mFileDownloadPresenter;
    private File mFile;
    private String key;
    private DBManager mDBManager;
    private boolean isSuccess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_open);
        ButterKnife.bind(this);
        mDBManager = new DBManager(this);
        mFileDownloadPresenter = FileDownloadPresenter.getInstance(this);
        mFileDownloadPresenter.setOnDownloadCallbackListener(mDownloadCallback);
        getintent();
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void init() {
        mTvType.setImageResource(setImage(mMessageInfo.getContent()));
        String path = mMessageInfo.getVoice();
        key = mMessageInfo.getKey();

        mProgressBar.setMax(100);
        mFile = new File(Constants.DOWNLOAD + System.currentTimeMillis() + mMessageInfo.getTitle());
        if (StringUtils.isEmpty(path)) {
            mBtnOpen.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mFileDownloadPresenter.dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
        } else {
            mFile = new File(path);
            if (mFile.exists()) {
                if (MySharedPreferences.getInstance().getLong(key) == mFile.length() || (MySharedPreferences.getInstance().getLong(key) == 0 && mFile.length() != 0)) {
                    mBtnOpen.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    double currentSize = Double.parseDouble(mFile.length() + "");
                    double totleSize = Double.parseDouble(MySharedPreferences.getInstance().getLong(key) + "");
                    int progress = (int) (currentSize / totleSize * 100);
                    mProgressBar.setProgress(progress);
                    mBtnOpen.setVisibility(View.GONE);
                    mFileDownloadPresenter.dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
                }
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                mBtnOpen.setVisibility(View.GONE);
                mFileDownloadPresenter.dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
            }
        }
    }

    private void getintent() {
        mMessageInfo = (MessageInfo) getIntent().getSerializableExtra("messageInfo");
    }

    @OnClick({R.id.bark, R.id.btn_open})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_open:
                if (isSuccess) {
                    openFile(mFile);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mBtnOpen.setVisibility(View.GONE);
                    mFileDownloadPresenter.dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
                }
                break;
        }
    }

//    /**
//     * 打开文件
//     * @param file
//     */
//    private void openFile(File file){
//        try {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //设置intent的Action属性
//            intent.setAction(Intent.ACTION_VIEW);
//            //获取文件file的MIME类型
//            String type = getMIMEType(file);
//            //设置intent的data和Type属性。
//            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
//            //跳转
//            startActivity(intent);
//        }catch (Exception e){
//            ToastShow.showToast2(FileOpenActivity.this,getString(R.string.no_file_open));
//        }
//
//    }


    FileDownloadPresenter.downloadCallback mDownloadCallback = new FileDownloadPresenter.downloadCallback() {
        @Override
        public void onSuccess(File file, String key) {
            if (!key.equals(key)) return;
            mHandler.sendEmptyMessage(2);
        }

        @Override
        public void onFailure(String key) {
            if (!key.equals(key)) return;
            mHandler.sendEmptyMessage(1);
        }

        @Override
        public void onSuccsetProgressListeneress(long currentSize, long totalSize, String key) {
            if (!key.equals(key)) return;
            if (!mFile.exists()) {
                MySharedPreferences.getInstance().setLong(key, totalSize);
            } else {
                if (mFile.length() == 0) {
                    MySharedPreferences.getInstance().setLong(key, totalSize);
                }
            }
            Message message = new Message();
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setCurrent_size(currentSize);
            downloadInfo.setTotal_size(totalSize);
            message.obj = downloadInfo;
            message.what = 0;
            mHandler.sendMessage(message);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (ActivityUtil.isActivityOnTop(FileOpenActivity.this)) {
                        DownloadInfo downloadInfo = (DownloadInfo) msg.obj;
                        int progress = (int) (Double.parseDouble(downloadInfo.getCurrent_size() + MySharedPreferences.getInstance().getLong(key) + "") / Double.parseDouble(downloadInfo.getTotal_size() + MySharedPreferences.getInstance().getLong(key) + "") * 100);
                        UtilTool.Log("下載apk", progress + "");
                        UtilTool.Log("下載apk", "當前進度：" + downloadInfo.getCurrent_size() + "-----總進度：" + downloadInfo.getTotal_size());
                        mProgressBar.setProgress(progress);
                    }
                    break;
                case 1:
                    if (ActivityUtil.isActivityOnTop(FileOpenActivity.this)) {
                        mFile.delete();
                        if (StringUtils.isEmpty(mMessageInfo.getVoice())) {
                            mFile = new File(Constants.DOWNLOAD + System.currentTimeMillis() + mMessageInfo.getTitle());
                        }else{
                            mFile = new File(mMessageInfo.getVoice());
                        }
                        isSuccess = false;
                        mProgressBar.setVisibility(View.GONE);
                        mBtnOpen.setVisibility(View.VISIBLE);
                        mBtnOpen.setText(getString(R.string.down_fail_click));
                    }
                    break;
                case 2:
                    if (ActivityUtil.isActivityOnTop(FileOpenActivity.this)) {
                        isSuccess = true;
                        mProgressBar.setVisibility(View.GONE);
                        mBtnOpen.setVisibility(View.VISIBLE);
                        mBtnOpen.setText(getString(R.string.open_another_application));
                        mDBManager.updateVoice(mMessageInfo.getMsgId(), mFile.getAbsolutePath());
                        MessageEvent messageEvent = new MessageEvent(getString(R.string.update_file_message));
                        messageEvent.setId(mMessageInfo.getMsgId());
                        messageEvent.setFilepath(mFile.getAbsolutePath());
                        EventBus.getDefault().post(messageEvent);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mFileDownloadPresenter.removeDownloadCallbackListener(mDownloadCallback);
    }

    /**
     * 选择FILE_TYPE_xxx中的一种传入
     *
     * @param fileType
     */
    public int setImage(String fileType) {
        int resId = -1;
        if (FILE_TYPE_DOC.equals(fileType) || FILE_TYPE_DOCX.equals(fileType)) {
            resId = R.mipmap.type_doc;
        } else if (FILE_TYPE_XLS.equals(fileType) || FILE_TYPE_XLSX.equals(fileType)) {
            resId = R.mipmap.type_xls;
        } else if (FILE_TYPE_PPT.equals(fileType) || FILE_TYPE_PPTX.equals(fileType)) {
            resId = R.mipmap.type_ppt;
        } else if (FILE_TYPE_PDF.equals(fileType)) {
            resId = R.mipmap.type_pdf;
        } else if (FILE_TYPE_TXT.equals(fileType) || FILE_TYPE_LOG.equals(fileType) || FILE_TYPE_RTF.equals(fileType)) {
            resId = R.mipmap.type_txt;
        } else if (FILE_TYPE_ZIP.equals(fileType)) {
            resId = R.mipmap.type_zip;
        } else if (FILE_TYPE_RAR.equals(fileType)) {
            resId = R.mipmap.type_rar;
        } else {
            resId = R.mipmap.type_unknown;
        }
        return resId;
    }

    /**
     * 使用系统的应用 打开文件
     * 见 FILE_TYPE_WORD 等类型
     *
     * @return
     */
    public void openFile(File file) {
        boolean toOpenOrNot = true;
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), getApplicationContext().getPackageName() + ".provider");
        }

//        Uri uri = Uri.fromFile(file);
        String mimeStr = "";
        String postfixs = getFileExtension(file.getName());
        if ("doc".equalsIgnoreCase(postfixs) || "docx".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/msword");
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            mimeStr = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if ("ppt".equalsIgnoreCase(postfixs) || "pptx".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            mimeStr = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if ("xls".equalsIgnoreCase(postfixs) || "xlsx".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            mimeStr = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if ("pdf".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/pdf");
            mimeStr = "application/pdf";
        } else if ("txt".equalsIgnoreCase(postfixs) || "log".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "text/plain");
            mimeStr = "text/plain";
        } else if ("zip".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/x-zip-compressed");
            mimeStr = "application/x-zip-compressed";
        } else if ("rar".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/x-rar-compressed");
            mimeStr = "application/x-rar-compressed";
        } else if ("rtf".equalsIgnoreCase(postfixs)) {
            intent.setDataAndType(uri, "application/rtf");
            mimeStr = "application/rtf";
        } else {
            mimeStr = getMimeType(file);
            intent.setDataAndType(uri, mimeStr);
        }

        if (toOpenOrNot) {
            try {
                //可能会导致直接打开qq发送的bug
//                startActivity(intent);
                //也可能会导致直接打开qq发送的bug
//                startActivity(Intent.createChooser(intent, "请选择能够打开该文件的应用"));
                //解决↓--------------------------
                List<String> allpackages = getAllpackages(FileOpenActivity.this, uri, mimeStr);
                //移除需要剔除的包名
                allpackages = removeSomePackages(allpackages);
                if (allpackages.size() > 0) {
                    startActivity(Intent.createChooser(intent, "请选择能够打开该文件的应用"));
//                    startActivity(intent);
                } else {
                    ToastShow.showToast2(FileOpenActivity.this, getString(R.string.no_file_open));
                }
            } catch (Exception anfe) {
                ToastShow.showToast2(FileOpenActivity.this, getString(R.string.no_file_open));
//                ToastShow.showToast2(IMFileShowAndDownloadActivity.this, "暂时没有应用能够打开该文件!");
            }
        } else {
            ToastShow.showToast2(FileOpenActivity.this, getString(R.string.no_file_open));
//            ToastShow.showToast2(IMFileShowAndDownloadActivity.this, "暂时没有应用能够打开该文件!");
        }
    }

    /**
     * 获取文件的mime类型
     *
     * @param file
     * @return
     */
    public static String getMimeType(final File file) {
        String extension = getFileExtension(file.getName());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * 不包括点
     *
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) return "";
        return fileName.substring(index + 1, fileName.length()).toLowerCase(Locale.getDefault());
    }

    /**
     * 获取所有文件管理器功能的软件
     *
     * @param context
     * @return
     */
    public static List<String> getAllpackages(Context context, Uri uri, String mimeStr) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, mimeStr);
//        intent.setType(mimeStr);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        List<String> infosTmp = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++) {
            infosTmp.add(infos.get(i).activityInfo.packageName);
        }
        return infosTmp;
    }

    private String[] packageTsoExclude = {"com.tencent.mobileqq"};

    private List<String> removeSomePackages(List<String> allpackages) {
        List<String> indexToExclude = new ArrayList<>();
        if (allpackages.size() > 0) {
            for (int i = 0; i < allpackages.size(); i++) {
                String s = allpackages.get(i);
                for (String s1 : packageTsoExclude) {
                    if (s.equalsIgnoreCase(s1)) {
                        indexToExclude.add(allpackages.get(i));
                    }
                }
            }
            for (int i = 0; i < indexToExclude.size(); i++) {
                allpackages.remove(indexToExclude.get(i));
            }
            return allpackages;
        } else {
            return allpackages;
        }
    }

}
