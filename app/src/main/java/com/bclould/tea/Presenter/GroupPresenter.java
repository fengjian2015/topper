package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.GroupCreateInfo;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.GroupMemberInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

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


    public void createGroup(String room_name,String toco_ids ,String description, final CallBack2 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .createGroup(UtilTool.getToken(), room_name,toco_ids,description)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GroupCreateInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GroupCreateInfo groupCreateInfo) {
                            if(!ActivityUtil.isActivityOnTop((Activity) mContext))return;
                            hideDialog();
                            if (groupCreateInfo.getStatus()== 1) {
                                callBack.send(groupCreateInfo.getData().getGroup_id()+"");
                            } else {
                                ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.create_failure));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    public void getGroup(final CallBack1 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                        public void onNext(GroupInfo baseInfo) {
                            if(!ActivityUtil.isActivityOnTop((Activity) mContext))return;
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                callBack.send(baseInfo);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    public void deleteGroup(int group_id, String toco_id, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .deleteGroup(UtilTool.getToken(),toco_id,group_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if(!ActivityUtil.isActivityOnTop((Activity) mContext))return;
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                callBack.send();
                                ToastShow.showToast2((Activity) mContext,mContext.getString(R.string.out_group_success));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(!ActivityUtil.isActivityOnTop((Activity) mContext))return;
                            hideDialog();
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    public void selectGroupMember(final int group_id, final DBRoomMember dbRoomMember) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .selectGroupMember(UtilTool.getToken(),group_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GroupMemberInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GroupMemberInfo baseInfo) {
                            if(mContext instanceof Activity&&!ActivityUtil.isActivityOnTop((Activity) mContext)){
                                return;
                            }
                            if (baseInfo.getStatus() == 1) {
                                dbRoomMember.addRoomMember(baseInfo.getData(),group_id+"");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(mContext instanceof Activity&&!ActivityUtil.isActivityOnTop((Activity) mContext)){
                                return;
                            }
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    //定义接口
    public interface CallBack {
        void send();
    }
    //定义接口
    public interface CallBack1 {
        void send(GroupInfo baseInfo);
    }
    //定义接口
    public interface CallBack2 {
        void send(String group_id);
    }
}
