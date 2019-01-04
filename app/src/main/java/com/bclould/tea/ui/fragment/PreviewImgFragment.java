package com.bclould.tea.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.ui.widget.ZoomImageView;
import com.bclould.tea.utils.QRDiscernUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/14.
 */
public class PreviewImgFragment extends Fragment {
    @Bind(R.id.imageView_loading_pb)
    ProgressBar mImageViewLoadingPb;
    @Bind(R.id.imageView_item_giv)
    ZoomImageView mImageViewItemGiv;
    @Bind(R.id.tv_artwork_master)
    TextView mTvArtworkMaster;
    @Bind(R.id.image_view_rl)
    RelativeLayout mImageViewRl;

    private  String mImgUrl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_images_view_item, container,
                false);
        ButterKnife.bind(this, view);
        mImgUrl=getArguments().getString("mImgUrl");
        loadImage(mImgUrl);
        mTvArtworkMaster.setVisibility(View.GONE);
        return view;
    }

    public void loadImage(final String imgUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .error(R.mipmap.ic_empty_photo)
                .centerCrop()
                .placeholder(R.mipmap.ic_empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (imgUrl.startsWith("https://") || imgUrl.startsWith("http://")) {
            mImageViewLoadingPb.setVisibility(View.VISIBLE);
            mImageViewLoadingPb.bringToFront();
            Glide.with(PreviewImgFragment.this).load(imgUrl).apply(requestOptions).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    if (resource != null) {
                        mImageViewItemGiv.setImageDrawable(resource);
                        mImageViewLoadingPb.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Glide.with(PreviewImgFragment.this).load(new File(imgUrl)).apply(requestOptions).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    if (resource != null) {
                        mImageViewItemGiv.setImageDrawable(resource);
                        mImageViewLoadingPb.setVisibility(View.GONE);
                    }
                }
            });
//            imageGiv.setImageDrawable(Drawable.createFromPath(url));
        }
        mImageViewItemGiv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new QRDiscernUtil(getActivity()).discernQR(imgUrl);
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
