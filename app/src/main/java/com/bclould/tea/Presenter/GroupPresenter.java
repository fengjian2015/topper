package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.GroupCreateInfo;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.GroupMemberInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GIjia on 2018/5/30.
 * 房間的請求
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public GroupPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    public void createGroup(String room_name, String toco_ids, String description, final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .createGroup(UtilTool.getToken(), room_name, toco_ids, description)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GroupCreateInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupCreateInfo groupCreateInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (groupCreateInfo.getStatus() == 1) {
                            callBack.send(groupCreateInfo.getData().getGroup_id() + "");
                        } else {
                            ToastShow.showToast2((Activity) mContext, groupCreateInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void inviteGroup(String roomid, String toco_ids, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .inviteGroup(UtilTool.getToken(), roomid, toco_ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                        } else {
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.invite_failure));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getGroup(final DBRoomMember mDBRoomMember, final DBRoomManage mDBRoomManage, final DBManager dbManager, boolean isShow, final CallBack1 callBack) {
        if (isShow) showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getGroup(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GroupInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final GroupInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            new Thread(){
                                @Override
                                public void run() {
                                    mDBRoomManage.deleteAllRoom();
                                    mDBRoomMember.deleteAllRoomMember();
                                    List<ConversationInfo> list = dbManager.queryConversationGroup();
                                    for (GroupInfo.DataBean dataBean : baseInfo.getData()) {
                                        RoomManageInfo roomManageInfo = new RoomManageInfo();
                                        roomManageInfo.setRoomName(dataBean.getName());
                                        roomManageInfo.setRoomId(dataBean.getId() + "");
                                        roomManageInfo.setOwner(dataBean.getToco_id());
                                        roomManageInfo.setRoomNumber(dataBean.getMax_people());
                                        roomManageInfo.setRoomImage(dataBean.getLogo());
                                        mDBRoomManage.addRoom(roomManageInfo);
                                        for (GroupInfo.DataBean.UsersBean usersBean : dataBean.getUsers()) {
                                            RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
                                            roomMemberInfo.setRoomId(dataBean.getId() + "");
                                            roomMemberInfo.setJid(usersBean.getToco_id());
                                            roomMemberInfo.setImage_url(usersBean.getAvatar());
                                            roomMemberInfo.setName(usersBean.getName());
                                            mDBRoomMember.addRoomMember(roomMemberInfo);
                                            dbManager.addStrangerUserInfo(usersBean.getToco_id(), usersBean.getAvatar(), usersBean.getName());
                                        }
                                    }
                                    for (ConversationInfo conversationInfo : list) {
                                        boolean isExist = false;
                                        A:
                                        for (GroupInfo.DataBean dataBean : baseInfo.getData()) {
                                            if (conversationInfo.getUser().equals(dataBean.getId() + "")) {
                                                isExist = true;
                                                break A;
                                            }
                                        }
                                        if (!isExist) {
                                            dbManager.deleteConversation(conversationInfo.getUser());
                                        }
                                    }
                                    ((Activity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.refresh_group_room)));
                                            callBack.send(baseInfo);
                                        }
                                    });
                                }
                            }.start();


                        }else {
                            callBack.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteGroup(int group_id, String toco_id, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteGroup(UtilTool.getToken(), toco_id, group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.out_group_success));
                        } else {
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void selectGroupMember(final int group_id, final DBRoomMember dbRoomMember, boolean isShow, final DBRoomManage mdbRoomManage, final DBManager manager, final CallBack callBack) {
        if (isShow) {
            showDialog();
        }
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .selectGroupMember(UtilTool.getToken(), group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GroupMemberInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupMemberInfo baseInfo) {
                        if (mContext instanceof Activity && !ActivityUtil.isActivityOnTop((Activity) mContext)) {
                            return;
                        }
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            if (baseInfo.getData() == null) {
                                RoomManage.getInstance().removeRoom(group_id + "");
                                return;
                            }
                            RoomManageInfo roomManageInfo = new RoomManageInfo();
                            roomManageInfo.setRoomName(baseInfo.getData().getName());
                            roomManageInfo.setRoomId(baseInfo.getData().getId() + "");
                            roomManageInfo.setOwner(baseInfo.getData().getToco_id());
                            roomManageInfo.setRoomImage(baseInfo.getData().getLogo());
                            roomManageInfo.setRoomNumber(baseInfo.getData().getMax_people());
                            mdbRoomManage.addRoom(roomManageInfo);
                            dbRoomMember.deleteRoom(group_id + "");
                            dbRoomMember.addRoomMember(baseInfo.getData().getUsers(), group_id + "");
                            if (manager.findConversation(group_id + "")) {
                                manager.updateConversationFriend(group_id + "", baseInfo.getData().getName());
                            }
                            callBack.send();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mContext instanceof Activity && !ActivityUtil.isActivityOnTop((Activity) mContext)) {
                            return;
                        }
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updataGroupName(int group_id, String name, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .updataGroupName(UtilTool.getToken(), name, group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.xg_succeed));
                        } else {
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updataGroupMemberName(int group_id, String name, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .updataGroupMemberName(UtilTool.getToken(), name, group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.xg_succeed));
                        } else {
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void transferGroup(int group_id, String toco_id, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .transferGroup(UtilTool.getToken(), toco_id, group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.transfer_success));
                        } else {
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void kickOutGroup(int group_id, String toco_ids, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .kickOutGroup(UtilTool.getToken(), toco_ids, group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.kick_out_success));
                        } else {
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateLogoGroup(final DBRoomManage dbRoomManage, final int group_id, String content, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .updateLogoGroup(UtilTool.getToken(), content, group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            dbRoomManage.updateUrl(group_id + "", baseInfo.getData().getUrl());
                            callBack.send();
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.up_succeed));
                        } else {
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send();
    }

    //定义接口
    public interface CallBack1 {
        void send(GroupInfo baseInfo);
        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack2 {
        void send(String group_id);
    }
}
