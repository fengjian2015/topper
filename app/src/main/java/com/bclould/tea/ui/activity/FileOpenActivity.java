package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
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
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

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
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_FILE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_FILE_MSG;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_open);
        ButterKnife.bind(this);
        mDBManager=new DBManager(this);
        mFileDownloadPresenter = FileDownloadPresenter.getInstance(this);
        mFileDownloadPresenter.setOnDownloadCallbackListener(mDownloadCallback);
        getintent();
        init();
    }

    private void init() {
        mTvType.setImageResource(setImage(mMessageInfo.getContent()));
        String path=mMessageInfo.getVoice();
        key=mMessageInfo.getKey();

        mProgressBar.setMax(100);
        mFile= new File(Constants.DOWNLOAD +System.currentTimeMillis()+mMessageInfo.getTitle());
        if(StringUtils.isEmpty(path)){
            mBtnOpen.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mFileDownloadPresenter.dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
        }else{
            mFile=new File(path);
            if (mFile.exists()) {
                if (MySharedPreferences.getInstance().getLong(key) == mFile.length()||(MySharedPreferences.getInstance().getLong(key)==0&&mFile.length()!=0)) {
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
                openFile(mFile);
                break;
        }
    }

    /**
     * 打开文件
     * @param file
     */
    private void openFile(File file){
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性。
            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            //跳转
            startActivity(intent);
        }catch (Exception e){
            ToastShow.showToast2(FileOpenActivity.this,getString(R.string.no_file_open));
        }

    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */
    private String getMIMEType(File file) {

        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
    /* 获取文件的后缀名 */
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    FileDownloadPresenter.downloadCallback mDownloadCallback = new FileDownloadPresenter.downloadCallback() {
        @Override
        public void onSuccess(File file,String key) {
            if(!key.equals(key))return;
            mHandler.sendEmptyMessage(2);
        }

        @Override
        public void onFailure(String key) {
            if(!key.equals(key))return;
            mHandler.sendEmptyMessage(1);
        }

        @Override
        public void onSuccsetProgressListeneress(long currentSize, long totalSize,String key) {
            if(!key.equals(key))return;
            if (!mFile.exists()) {
                MySharedPreferences.getInstance().setLong(key, totalSize);
            }else {
                if(mFile.length() == 0){
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
                        mProgressBar.setVisibility(View.VISIBLE);
                        mBtnOpen.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    if (ActivityUtil.isActivityOnTop(FileOpenActivity.this)) {
                        mProgressBar.setVisibility(View.GONE);
                        mBtnOpen.setVisibility(View.VISIBLE);
                        mDBManager.updateVoice(mMessageInfo.getMsgId(),mFile.getAbsolutePath());
                        MessageEvent messageEvent=new MessageEvent(getString(R.string.update_file_message));
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
        } else if (FILE_TYPE_TXT.equals(fileType)||FILE_TYPE_LOG.equals(fileType)||FILE_TYPE_RTF.equals(fileType)) {
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

    private String[][] MIME_MapTable={
            //{后缀名， MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",    "image/bmp"},
            {".c",  "text/plain"},
            {".class",  "application/octet-stream"},
            {".conf",   "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls",    "application/vnd.ms-excel"},
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",   "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h",  "text/plain"},
            {".htm",    "text/html"},
            {".html",   "text/html"},
            {".jar",    "application/java-archive"},
            {".java",   "text/plain"},
            {".jpeg",   "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",   "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",   "video/mp4"},
            {".mpga",   "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop",   "text/plain"},
            {".rc", "text/plain"},
            {".rmvb",   "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh", "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",  "application/x-compress"},
            {".zip",    "application/x-zip-compressed"},
            {"",        "*/*"}
    };
}
