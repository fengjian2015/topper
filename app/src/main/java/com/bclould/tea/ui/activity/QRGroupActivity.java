package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_INVITE_MSG;

public class QRGroupActivity extends BaseActivity {

    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.qr_code_iv)
    ImageView mQrCodeIv;

    private String roomId;
    private DBRoomManage mDBRoomManage;
    private Bitmap groupBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgroup);
        ButterKnife.bind(this);
        setTitle(getString(R.string.qr_code),R.mipmap.icon_nav_more_selected);
        mDBRoomManage=new DBRoomManage(this);
        getInitent();
        init();
    }


    private void init() {
        UtilTool.getGroupImage(mDBRoomManage,roomId,this,mTouxiang);
        mTvName.setText(mDBRoomManage.findRoomName(roomId));
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("roomId",roomId);
            jsonObject.put("roomName",mTvName.getText().toString());
            jsonObject.put("from_name",UtilTool.getUser());
            jsonObject.put("roomPath",mDBRoomManage.findRoomUrl(roomId));
            groupBitmap = UtilTool.createQRImage(UtilTool.base64ToJson(this, Constants.GROUPCARD, jsonObject.toString()));
            mQrCodeIv.setImageBitmap(groupBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getInitent() {
        roomId=getIntent().getStringExtra("roomId");
    }

    @OnClick({R.id.bark, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                goShare();
                break;
        }
    }

    private void goShare() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.save_qr_code));
        list.add(getString(R.string.share_group_link));
        final MenuListPopWindow menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        String path= UtilTool.saveBitmap(groupBitmap,QRGroupActivity.this,false);
                        if(UtilTool.saveAlbum(path,QRGroupActivity.this)){
                            Toast.makeText(QRGroupActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(QRGroupActivity.this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
                        }
                        menu.dismiss();
                        break;
                    case 2:

                        MessageInfo messageInfo=new MessageInfo();
                        messageInfo.setRoomName(mDBRoomManage.findRoomName(roomId));
                        messageInfo.setRoomId(roomId);
                        messageInfo.setInitiator(UtilTool.getUser());
                        messageInfo.setHeadUrl(mDBRoomManage.findRoomUrl(roomId));
                        Intent intent = new Intent(QRGroupActivity.this, SelectConversationActivity.class);
                        intent.putExtra("type", 2);
                        intent.putExtra("msgType", TO_INVITE_MSG);
                        intent.putExtra("messageInfo", messageInfo);
                        startActivity(intent);
                        menu.dismiss();
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }
}
