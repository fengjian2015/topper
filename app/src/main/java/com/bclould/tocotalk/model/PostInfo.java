package com.bclould.tocotalk.model;

import android.os.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by GA on 2017/10/20.
 */

public class PostInfo implements Serializable {
    private String mContent;
    private int mSpanType;
    private List<String> mImgUrlList;

    public PostInfo() {
    }

    public PostInfo(String content, List<String> imgUrlList) {
        mContent = content;
        mImgUrlList = imgUrlList;
    }

    public PostInfo(String content, int spanType, List<String> imgUrlList) {
        mContent = content;
        mSpanType = spanType;
        mImgUrlList = imgUrlList;
    }

    protected PostInfo(Parcel in) {
        mContent = in.readString();
        mSpanType = in.readInt();
        mImgUrlList = in.createStringArrayList();
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getmSpanType() {
        return mSpanType;
    }

    public void setmSpanType(int mSpanType) {
        this.mSpanType = mSpanType;
    }

    public List<String> getImgUrlList() {
        return mImgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        mImgUrlList = imgUrlList;
    }

}

