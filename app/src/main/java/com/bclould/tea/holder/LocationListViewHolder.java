package com.bclould.tea.holder;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.UtilTool;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class LocationListViewHolder extends BaseViewHolder<Geo2AddressResultObject.ReverseAddressResult.Poi> {
    private TextView mLocationName;
    private TextView mLocationAddr;
    private CheckBox mLocationCheckbox;
    private RelativeLayout mRlSelectLocation;


    public LocationListViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_select_location_view);
        mLocationName = $(R.id.location_name);
        mLocationAddr = $(R.id.location_addr);
        mLocationCheckbox = $(R.id.location_checkbox);
        mRlSelectLocation = $(R.id.rl_select_location);
    }

    public void setPosition(int position){
        UtilTool.Log("fengjian---","傳入內部的位置："+position);
        mLocationCheckbox.setChecked(true);
    }

    @Override
    public void setData(final Geo2AddressResultObject.ReverseAddressResult.Poi tencentPoi) {
        mLocationName.setText("" + tencentPoi.title);
        mLocationAddr.setText("" + tencentPoi.address);
        if("true".equals(tencentPoi.id)){
            mLocationCheckbox.setChecked(true);
        }else{
            mLocationCheckbox.setChecked(false);
        }
        UtilTool.Log("fengjian----",tencentPoi.category+"\n"+tencentPoi.id);
    }
}
