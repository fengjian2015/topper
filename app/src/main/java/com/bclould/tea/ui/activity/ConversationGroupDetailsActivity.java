package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.adapter.GroupDetailsMemberAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.ui.widget.MyGridView;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.GroupPresenter.CallBack;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationGroupDetailsActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_member_number)
    TextView mTvMemberNumber;
    @Bind(R.id.partner_detial_gridview)
    MyGridView mPartnerDetialGridview;
    @Bind(R.id.tvgroupr_name)
    TextView mTvgrouprName;
    @Bind(R.id.on_off_top)
    ImageView mOnOffTop;
    @Bind(R.id.on_off_message_free)
    ImageView mOnOffMessageFree;
    @Bind(R.id.tv_member_name)
    TextView mTvMemberName;
    @Bind(R.id.rl_group_management)
    RelativeLayout mRlGroupManagement;
    @Bind(R.id.iv_head)
    ImageView mIvHead;
    @Bind(R.id.tv_announcement)
    TextView mTvAnnouncement;

    private GroupDetailsMemberAdapter mAdapter;
    private List<RoomMemberInfo> mList = new ArrayList<>();
    private String roomId;
    private String roomName;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBManager mMgr;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_group_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        EventBus.getDefault().register(this);//初始化EventBus
        initIntent();
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String path = intent.getStringExtra("path");
        if (!StringUtils.isEmpty(path)) {
            try {
                upImage(path);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
    }

    private void init() {
        mDBRoomMember = new DBRoomMember(this);
        mMgr = new DBManager(this);
        mDBRoomManage = new DBRoomManage(this);
        setGroupMember(true);
        initView();
    }

    private void initView() {
        roomName = mDBRoomManage.findRoomName(roomId);
        changeTop();
        changeFree();
        setGroupName();
        setMemberName();
        setGroupManager();
        setGroupImage();
        setGroupAnnouncement();
    }

    private void setGroupAnnouncement() {
        if(!StringUtils.isEmpty(mDBRoomManage.findRoomDescription(roomId))) {
            mTvAnnouncement.setText(mDBRoomManage.findRoomDescription(roomId));
            mTvAnnouncement.setVisibility(View.VISIBLE);
        }else{
            mTvAnnouncement.setVisibility(View.GONE);
        }
    }

    private void setGroupImage() {
        UtilTool.getGroupImage(mDBRoomManage, roomId, this, mIvHead);
    }

    private void setGroupManager() {
        if (isOwner()) {
            mRlGroupManagement.setVisibility(View.VISIBLE);
        } else {
            mRlGroupManagement.setVisibility(View.GONE);
        }
    }

    private boolean isOwner() {
        if (UtilTool.getTocoId().equals(mDBRoomManage.findRoomOwner(roomId))) {
            return true;
        } else {
            return false;
        }
    }

    private void setMemberName() {
        mTvMemberName.setText(mDBRoomMember.findMemberName(roomId, UtilTool.getTocoId()) + "");
    }

    private void setGroupMember(boolean isFirst) {
        mList.clear();
        //是群主添加兩個
        if (isOwner()) {
            mList.add(new RoomMemberInfo());
        }
        mList.add(new RoomMemberInfo());
        mList.addAll(mDBRoomMember.queryAllRequest(roomId));
        if (isOwner()) {
            mTvMemberNumber.setText(getString(R.string.group_member) + "(" + (mList.size() - 2) + "/" + mDBRoomManage.findRoomNumber(roomId) + ")");
        } else {
            mTvMemberNumber.setText(getString(R.string.group_member) + "(" + (mList.size() - 1) + "/" + mDBRoomManage.findRoomNumber(roomId) + ")");
        }
        if (mList.size() > 6) {
            List<RoomMemberInfo> memberInfoListView = new ArrayList<>();
            memberInfoListView.addAll(mList.subList(0, 6));
            mList.clear();
            mList.addAll(memberInfoListView);
        }
        if (isFirst) {
            mAdapter = new GroupDetailsMemberAdapter(this, mList, roomId, mMgr, mDBRoomManage, mDBRoomMember);
            mPartnerDetialGridview.setAdapter(mAdapter);
            new GroupPresenter(this).selectGroupMember(Integer.parseInt(roomId), mDBRoomMember, true, mDBRoomManage, mMgr, new CallBack() {
                @Override
                public void send() {
                    initView();
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.refresh_group_members)));
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    private void setGroupName() {
        mTvgrouprName.setText(mDBRoomManage.findRoomName(roomId));
    }

    private void changeTop() {
        String istop = mMgr.findConversationIstop(roomId);
        if ("true".equals(istop)) {
            mOnOffTop.setSelected(true);
        } else {
            mOnOffTop.setSelected(false);
        }
    }

    private void changeFree() {
        boolean free = MySharedPreferences.getInstance().getBoolean(SETTING + roomId + UtilTool.getTocoId());
        mOnOffMessageFree.setSelected(free);
    }

    @OnClick({R.id.bark, R.id.on_off_message_free, R.id.on_off_top, R.id.rl_empty_talk, R.id.btn_brak, R.id.rl_group_qr, R.id.rl_group_name, R.id.rl_member_name, R.id.rl_group_management, R.id.rl_looking_chat
            , R.id.rl_group_image, R.id.rl_go_memberlist,R.id.rl_announcement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.on_off_message_free:
                messageFree();
                break;
            case R.id.on_off_top:
                messageTop();
                break;
            case R.id.rl_empty_talk:
                clearMessage();
                break;
            case R.id.btn_brak:
                deleteGroup();
                break;
            case R.id.rl_group_qr:
                // TODO: 2018/6/20 跳轉二維碼 
                break;
            case R.id.rl_group_name:
                if (isOwner()) {
                    goModificationName(2, mTvgrouprName.getText().toString());
                } else {
                    ToastShow.showToast2(ConversationGroupDetailsActivity.this, getString(R.string.only_owner_change_group_name));
                }
                break;
            case R.id.rl_member_name:
                goModificationName(1, mTvMemberName.getText().toString());
                break;
            case R.id.rl_group_management:
                goSelectMember();
                break;
            case R.id.rl_looking_chat:
                goRecord();
                break;
            case R.id.rl_group_image:
                changeImage();
                break;
            case R.id.rl_go_memberlist:
                goMemberList();
                break;
            case R.id.rl_announcement:
                goAnnouncement();
                break;
        }
    }

    private void goAnnouncement() {
        if (!isOwner()) {
            ToastShow.showToast2(ConversationGroupDetailsActivity.this, getString(R.string.only_owner_change_group_announcement));
        }
        Intent intent = new Intent(this, ModificationNameActivity.class);
        intent.putExtra("type", 3);
        intent.putExtra("content", mTvAnnouncement.getText().toString());
        intent.putExtra("roomId", roomId);
        intent.putExtra("tocoId", UtilTool.getTocoId());
        startActivity(intent);
    }

    private void goMemberList() {
        Intent intent = new Intent(this, GroupMemberActivity.class);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
    }

    private void changeImage() {
        if (!isOwner()) {
            ToastShow.showToast2(ConversationGroupDetailsActivity.this, getString(R.string.only_owner_change_group_image));
            return;
        }
        showDialog();
    }

    private void showDialog() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.image));
        list.add(getString(R.string.network_image));
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
                        goImage();
                        break;
                    case 2:
                        menu.dismiss();
                        Intent intent = new Intent(ConversationGroupDetailsActivity.this, SerchImageActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void goImage() {
        PictureSelector.create(this)
                .openGallery(ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                        .theme(R.style.picture_white_style)
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(true)// 是否开启点击声音 true or false
                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    //上傳頭像
    private void upImage(String path) throws UnsupportedEncodingException {
        File file = new File(path);
        final String keyCut = UtilTool.getUserId() + UtilTool.createtFileName() + "cut" + UtilTool.getPostfix2(file.getName());
        final File newFile = new File(Constants.PUBLICDIR + keyCut);
        Bitmap cutImg = BitmapFactory.decodeFile(path);
        UtilTool.comp(cutImg, newFile);
        final Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = UtilTool.getFileToByte(newFile);
        String Base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
        UtilTool.Log("編碼", Base64Image.length() + "");
        new GroupPresenter(this).updateLogoGroup(mDBRoomManage, Integer.parseInt(roomId), Base64Image, new CallBack() {
            @Override
            public void send() {
                MessageEvent messageEvent = new MessageEvent(getString(R.string.refresh_group_room));
                messageEvent.setId(roomId);
                EventBus.getDefault().post(messageEvent);
            }
        });

    }


    private void goRecord() {
        Intent intent = new Intent(this, ConversationRecordFindActivity.class);
        intent.putExtra("user", roomId);
        intent.putExtra("name", roomName);
        startActivity(intent);
    }

    private void goSelectMember() {
        Intent intent = new Intent(this, SelectGroupMemberActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void goModificationName(int type, String content) {
        Intent intent = new Intent(this, ModificationNameActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("content", content);
        intent.putExtra("roomId", roomId);
        intent.putExtra("tocoId", UtilTool.getTocoId());
        startActivity(intent);
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.quit_group))) {
            if (roomId.equals(event.getId())) {
                finish();
            }
        } else if (msg.equals(getString(R.string.refresh_group_members))) {
            initView();
            setGroupMember(false);
        } else if (msg.equals(getString(R.string.my_nickname_group))) {
            setMemberName();
        } else if (msg.equals(getString(R.string.modify_group_name))) {
            setGroupName();
        } else if (msg.equals(getString(R.string.refresh_group_room))) {
            if (roomId.equals(event.getId())) {
                initView();
                setGroupMember(false);
            }
        } else if (msg.equals(getString(R.string.kick_out_success))) {
            if (roomId.equals(event.getId())) {
                finish();
            }
        }else if(msg.equals(getString(R.string.modify_group_announcement))){
            setGroupAnnouncement();
        }
    }

    private void deleteGroup() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.determine_exit) + " " + roomName + " " + getString(R.string.what));
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
                try {
                    new GroupPresenter(ConversationGroupDetailsActivity.this).deleteGroup(Integer.parseInt(roomId), UtilTool.getTocoId(), new CallBack() {
                        @Override
                        public void send() {
                            RoomManage.getInstance().removeRoom(roomId);
                        }
                    });
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void messageFree() {
        boolean free = MySharedPreferences.getInstance().getBoolean(SETTING + roomId + UtilTool.getTocoId());
        if (free) {
            mOnOffMessageFree.setSelected(false);
            MySharedPreferences.getInstance().setBoolean(SETTING + roomId + UtilTool.getTocoId(), false);
        } else {
            mOnOffMessageFree.setSelected(true);
            MySharedPreferences.getInstance().setBoolean(SETTING + roomId + UtilTool.getTocoId(), true);
        }
    }

    private void messageTop() {
        String istop = mMgr.findConversationIstop(roomId);
        if (StringUtils.isEmpty(istop)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            ConversationInfo info = new ConversationInfo();
            info.setTime(time);
            info.setFriend(roomName);
            info.setUser(roomId);
            info.setMessage("");
            info.setCreateTime(UtilTool.createChatCreatTime());
            info.setChatType(RoomManage.ROOM_TYPE_MULTI);
            mMgr.addConversation(info);
            mOnOffTop.setSelected(true);
            mMgr.updateConversationIstop(roomId, "true");
        } else if ("true".equals(istop)) {
            mOnOffTop.setSelected(false);
            mMgr.updateConversationIstop(roomId, "false");
        } else {
            mOnOffTop.setSelected(true);
            mMgr.updateConversationIstop(roomId, "true");
        }
        EventBus.getDefault().post(new MessageEvent(getString(R.string.message_top_change)));
    }

    private void clearMessage() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.make_sure_clear_transcript));
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
                try {
                    mMgr.deleteMessage(roomId);
                    mMgr.updateConversationMessage(roomId, "");
                    Toast.makeText(ConversationGroupDetailsActivity.this, getString(R.string.empty_success), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.msg_database_update)));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    try {
                        selectList = PictureSelector.obtainMultipleResult(data);
                        upImage(selectList.get(0).getCutPath());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
