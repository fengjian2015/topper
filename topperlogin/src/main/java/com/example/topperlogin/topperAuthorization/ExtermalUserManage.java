package com.example.topperlogin.topperAuthorization;

import com.example.topperlogin.model.ExternalUserInfo;

/**
 * Created by GIjia on 2018/12/21.
 */

public class ExtermalUserManage {
    private static ExternalUserInfo mExternalUserInfo;
    public static void setExternalUserInfo(ExternalUserInfo externalUserInfo){
        mExternalUserInfo=externalUserInfo;
    }

    public static ExternalUserInfo getExternalUserInfo(){
        return mExternalUserInfo;
    }
}
