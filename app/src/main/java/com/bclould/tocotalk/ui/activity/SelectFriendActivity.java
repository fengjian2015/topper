package com.bclould.tocotalk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.adapter.SelectFriendAdapter;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.ui.widget.MyListView;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.MessageManage;
import com.bclould.tocotalk.xmpp.Room;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_VIDEO_MSG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectFriendActivity extends BaseActivity implements SelectFriendAdapter.OnItemListener,Room{

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.listView)
    MyListView mListView;

    public static final String TEXT_PLAIN="text/plain";
    public static final String IMAGE_TYPE="image";

    private List<UserInfo> mUsers = new ArrayList<>();
    private SelectFriendAdapter selectFriendAdapter;
    private DBManager mMgr;
    private String shareType;
    private Intent shareIntent;
//    private Uri uri;
//    private String filePath;

    private int msgType;
    private MessageInfo messageInfo;
    private LoadingProgressDialog mProgressDialog;

    private int type=0;//默認0 代表分享過來， 1表示轉發
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);
        initRecylerView();
        type=getIntent().getIntExtra("type",0);
        if(type==0){
            getShareIntent();
        }else{
            getChatIntent();
        }
    }

    private void getChatIntent() {
        messageInfo= (MessageInfo) getIntent().getSerializableExtra("messageInfo");
        msgType=getIntent().getIntExtra("msgType",0);
    }

    private void getShareIntent(){
        shareIntent = getIntent();
        Bundle bundle=shareIntent.getExtras();
        String text = shareIntent.getAction();  //分享的action都是Intent.ACTION_SEND
        String type = shareIntent.getType();//获取分享来的数据类型，和上面<data android:mimeType="text/plain" />中的一致
//        uri =bundle.getParcelable(Intent.EXTRA_STREAM);

        //具体还有其他的类型，请上网参考
        if (Intent.ACTION_SEND.equals(text) && type != null) {
            this.shareType=type;
        }
    }

    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
//            if (isExternalStorageDocument(imageUri)) {
//                String docId = DocumentsContract.getDocumentId(imageUri);
//                String[] split = docId.split(":");
//                String type = split[0];
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//            } else if (isDownloadsDocument(imageUri)) {
//                String id = DocumentsContract.getDocumentId(imageUri);
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                return getDataColumn(context, contentUri, null, null);
//            } else if (isMediaDocument(imageUri)) {
            String docId = DocumentsContract.getDocumentId(imageUri);
            String[] split = docId.split(":");
            String type = split[0];
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            String selection = MediaStore.Images.Media._ID + "=?";
            String[] selectionArgs = new String[] { split[1] };
            return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
        }else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
//            if (isGooglePhotosUri(imageUri))
//                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Activity context, Uri contentUri, String selection, String[] selectionArgs) {
        Cursor cursor = context.getContentResolver().query(contentUri, null,
                selection, selectionArgs, null);
        if (cursor==null) {
            String path = contentUri.getPath();
            return path;
        }
        if(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        cursor.close();
        return null;
    }

    private void initRecylerView() {
        selectFriendAdapter = new SelectFriendAdapter(this, mUsers, mMgr);
        mListView.setAdapter(selectFriendAdapter);
        selectFriendAdapter.addOnItemListener(this);
        updateData();
    }

    private void updateData() {
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getJid())) {
                userInfo = info;
            } else if (info.getUser().isEmpty()) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUsers.addAll(userInfos);
        selectFriendAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;

        }
    }

    private void showDeleteDialog(final String name, final String user) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.confirm_send_to)+ name );
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(user,name);
                deleteCacheDialog.dismiss();
            }
        });
    }


    private MessageManage messageManage;
    private void sendMessage(String user, String name){
        messageManage= new MessageManage(new DBManager(SelectFriendActivity.this),user,SelectFriendActivity.this,name);
        messageManage.addRoom(this);
        if(type==0){
            if(TEXT_PLAIN.equals(shareType)){
                String shareText= shareIntent.getStringExtra(Intent.EXTRA_TEXT);
                messageInfo=messageManage.sendMessage(shareText);
                if(messageInfo!=null){
                    ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.share_complete));
                    SelectFriendActivity.this.finish();
                }else{
                    ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.share_failure));
                }
            }
        }else if(type==1){
            if(msgType==TO_TEXT_MSG||msgType==FROM_TEXT_MSG){
                String message=messageInfo.getMessage();
                messageInfo=messageManage.sendMessage(message);
                if(messageInfo!=null){
                    ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.forward_success));
                    SelectFriendActivity.this.finish();
                }else{
                    ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.forward_failure));
                }
            }else if(msgType==FROM_IMG_MSG||msgType==TO_IMG_MSG){
                showDialog();
                messageManage.Upload(messageInfo.getVoice());
            }else if(msgType==FROM_VIDEO_MSG||msgType==TO_VIDEO_MSG){
                showDialog();
                messageManage.Upload(messageInfo.getMessage());

            }
        }
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(this.getString(R.string.send_in_progress));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onItemClick(String name, String user) {
        showDeleteDialog(name,user);

    }

    @Override
    public void refreshAddData(MessageInfo messageInfo) {

    }

    @Override
    public void sendError(int id) {
        hideDialog();
        ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.forward_failure));
    }

    @Override
    public void sendFileResults(String newFile2, boolean isSuccess) {
        hideDialog();
        if(isSuccess){
            ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.forward_success));
            SelectFriendActivity.this.finish();
        }else{
            ToastShow.showToast2(SelectFriendActivity.this,getString(R.string.forward_failure));
        }
    }
}
