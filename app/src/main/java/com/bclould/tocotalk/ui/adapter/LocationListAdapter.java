package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.bclould.tocotalk.holder.LocationListViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;

/**
 * Created by Mr.Jude on 2015/7/18.
 */
public class LocationListAdapter extends RecyclerArrayAdapter<Geo2AddressResultObject.ReverseAddressResult.Poi> {
    private LocationListViewHolder locationListViewHolder;
    public LocationListAdapter(Context context) {
        super(context);
    }

    public void setPosition(int position){
        locationListViewHolder.setPosition(position);
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        locationListViewHolder=new LocationListViewHolder(parent);
        return locationListViewHolder;
    }
}
