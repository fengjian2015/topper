package com.bclould.tocotalk.holder;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.UtilTool;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;

import org.xutils.common.util.LogUtil;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class LocationListViewHolder extends BaseViewHolder<Geo2AddressResultObject.ReverseAddressResult.Poi> {
    private TextView mLocationName;
    private TextView mLocationAddr;
    private CheckBox mLocationCheckbox;
    private RelativeLayout mRlSelectLocation;
    private int position;


    public LocationListViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_select_location_view);
        mLocationName = $(R.id.location_name);
        mLocationAddr = $(R.id.location_addr);
        mLocationCheckbox = $(R.id.location_checkbox);
        mRlSelectLocation = $(R.id.rl_select_location);
    }

    public void setPosition(int position){
        this.position=position;

        UtilTool.Log("fengjian---","傳入內部的位置："+position);
    }

    @Override
    public void setData(final Geo2AddressResultObject.ReverseAddressResult.Poi tencentPoi) {
        mLocationName.setText("" + tencentPoi.title);
        mLocationAddr.setText("" + tencentPoi.address);
        UtilTool.Log("fengjian---","對比位置："+getPosition()+"    " +getOldPosition()+"    "+position);
        if(getPosition()==position){
            mLocationCheckbox.setChecked(true);
        }else{
            mLocationCheckbox.setChecked(false);
        }
    }
}
