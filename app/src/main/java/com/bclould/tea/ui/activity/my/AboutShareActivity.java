package com.bclould.tea.ui.activity.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.activity.SelectConversationActivity;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;

public class AboutShareActivity extends BaseActivity {

    @Bind(R.id.qr_code_iv)
    ImageView qrCodeIv;
    @Bind(R.id.touxiang)
    ImageView touxiang;
    @Bind(R.id.rl_qr)
    RelativeLayout rlQr;
    private MessageInfo mMessageInfo = new MessageInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_share);
        ButterKnife.bind(this);
        setTitle(getString(R.string.guanyu_me));
        init();
    }

    private void init() {
        try {
            DBManager mgr = new DBManager(this);
            UtilTool.getImage(mgr, UtilTool.getUser(), this, touxiang);
            HashMap map=new HashMap();
            map.put("user_id",UtilTool.getTocoId()+"");
            map.put("name",UtilTool.getUser());
            String jsonresult = com.alibaba.fastjson.JSONObject.toJSONString(map);
            String str = Base64.encodeToString(UtilTool.stringToJSON(jsonresult).getBytes(), Base64.DEFAULT);
            Bitmap bitmap = UtilTool.createQRImage( Constants.ABOUT_SHARE + str);
            qrCodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.bark, R.id.bt_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.bt_share:
                share();
                break;
        }
    }

    private void share() {
        List<String> list = Arrays.asList(new String[]{this.getString(R.string.share_to_top_chat), this.getString(R.string.save_to_photo_album)});
        final MenuListPopWindow menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        send();
                        break;
                    case 2:
                        menu.dismiss();
                        save();
                        break;

                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void send() {
        String path = UtilTool.saveBitmap(rlQr, this, false);
        if (path != null) {
            mMessageInfo.setVoice(path);
            Intent intent = new Intent(this, SelectConversationActivity.class);
            intent.putExtra("type", 2);
            intent.putExtra("msgType", TO_IMG_MSG);
            intent.putExtra("messageInfo", mMessageInfo);
            startActivity(intent);
        }
    }

    private void save() {
        if (UtilTool.saveBitmap(rlQr, this, true) != null) {
            Toast.makeText(AboutShareActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AboutShareActivity.this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
        }
    }
}
