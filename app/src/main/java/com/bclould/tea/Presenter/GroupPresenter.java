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
import com.bclould.tea.model.MessageTopInfo;
import com.bclould.tea.model.ReviewInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.UnclaimedRedInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.topperchat.RoomMemberManage;
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
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
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


    public synchronized void getGroup(final DBRoomMember mDBRoomMember, final DBRoomManage mDBRoomManage, final DBManager dbManager, boolean isShow, final CallBack1 callBack) {
        UtilTool.Log("房間","開始存儲");
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
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)){
                            return;
                        }
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            new Thread(){
                                @Override
                                public void run() {

                                    RoomMemberManage.getInstance().addRoomManage(baseInfo.getData());
                                    RoomMemberManage.getInstance().addRoomMember(baseInfo.getData());

                                    List<ConversationInfo> list = dbManager.queryConversationGroup();
                                    for (ConversationInfo conversationInfo : list) {
                                        boolean isExist = false;
                                        A: for (GroupInfo.DataBean dataBean : baseInfo.getData()) {
                                            if (conversationInfo.getUser().equals(dataBean.getId() + "")) {
                                                dbManager.updateConversationName(dataBean.getId()+"",dataBean.getName());
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
                                            UtilTool.Log("房間","存儲結束");
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
                        callBack.error();
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
                    public void onNext(final GroupMemberInfo baseInfo) {
                        if (mContext instanceof Activity && !ActivityUtil.isActivityOnTop((Activity) mContext)) {
                            return;
                        }
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            if (baseInfo.getData() == null) {
                                RoomManage.getInstance().removeRoom(group_id + "");
                                return;
                            }
                            new Thread(){
                                @Override
                                public void run() {
                                    RoomManageInfo roomManageInfo = new RoomManageInfo();
                                    roomManageInfo.setRoomName(baseInfo.getData().getName());
                                    roomManageInfo.setRoomId(baseInfo.getData().getId() + "");
                                    roomManageInfo.setOwner(baseInfo.getData().getToco_id());
                                    roomManageInfo.setRoomImage(baseInfo.getData().getLogo());
                                    roomManageInfo.setRoomNumber(baseInfo.getData().getMax_people());
                                    roomManageInfo.setDescription(baseInfo.getData().getDescription());
                                    roomManageInfo.setAllowModify(baseInfo.getData().getIs_allow_modify_data());
                                    roomManageInfo.setIsReview(baseInfo.getData().getIs_review());
                                    mdbRoomManage.addRoom(roomManageInfo);
                                    dbRoomMember.deleteRoom(group_id+"");
                                    dbRoomMember.addRoomMember(baseInfo.getData().getUsers(), group_id + "");
                                    if (manager.findConversation(group_id + "")) {
                                        manager.updateConversationFriend(group_id + "", baseInfo.getData().getName());
                                    }
                                    if(mContext instanceof Activity) {
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                callBack.send();
                                            }
                                        });
                                    }else{
                                        callBack.send();
                                    }
                                }
                            }.start();
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
        RetrofitUtil.getInstance(mContext,60)
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

    public void updateBullet(final int group_id, String content, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getNewFriendData(UtilTool.getToken(), group_id, content)
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

    public void setAllowModifyt(final int group_id,final int is_allow_modify_data, final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .setAllowModifyt(UtilTool.getToken(), group_id,is_allow_modify_data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GroupInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            baseInfo.setIs_allow_modify_data(is_allow_modify_data);
                            callBack.send(baseInfo);
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

    public void setIsReview(final int group_id,final int isReview, final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .setIsReview(UtilTool.getToken(), group_id,isReview)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GroupInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            baseInfo.setIs_allow_modify_data(isReview);
                            callBack.send(baseInfo);
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

    public void getReviewList(final int group_id, final CallBack3 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getReviewList(UtilTool.getToken(), group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<ReviewInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReviewInfo reviewInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (reviewInfo.getStatus() == 1) {
                            callBack.send(reviewInfo);
                        } else {
                            ToastShow.showToast2((Activity) mContext, reviewInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        callBack.error();
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void setReview(final int group_id,final int status,final String toco_id, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .setReview(UtilTool.getToken(), group_id,status,toco_id)
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
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.send_request_succeed));
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

    public void getUnclaimedRed(final int group_id ,final CallBack4 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getUnclaimedRed(UtilTool.getToken(), group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<UnclaimedRedInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnclaimedRedInfo unclaimedRedInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (unclaimedRedInfo.getStatus() == 1) {
                            callBack.send(unclaimedRedInfo);
                        }else {
                            ToastShow.showToast2((Activity) mContext, unclaimedRedInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        callBack.error();
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteGroupReview(int id, int group_id, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteGroupReview(UtilTool.getToken(), id, group_id)
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
                        }
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
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

    public void changeBackgound(String content, final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext,60)
                .getServer()
                .changeBackgound(UtilTool.getToken(), content)
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
                            callBack.send(baseInfo.getData().getUrl());
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.up_succeed));
                        }else{
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

    public void getBackgound(final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getBackgound(UtilTool.getToken())
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
                            callBack.send(baseInfo.getData().getUrl());
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

    public void deleteBackgound(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteBackgound(UtilTool.getToken())
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
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.reduction_success));
                        }else{
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

    public void setTopMessage(String roomId, int status, final boolean isShow, final CallBack callBack) {
        if(isShow)
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .setTopMessage(UtilTool.getToken(),roomId,status)
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
                            if (isShow)
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.xg_succeed));
                        } else {
                            if (isShow)
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

    public void getTopMessage( final CallBack5 callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getTopMessage(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<MessageTopInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageTopInfo messageTopInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (messageTopInfo.getStatus() == 1) {
                            callBack.send(messageTopInfo);
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

    //定义接口
    public interface CallBack3 {
        void send(ReviewInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack4 {
        void send(UnclaimedRedInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack5 {
        void send(MessageTopInfo baseInfo);
    }
}
