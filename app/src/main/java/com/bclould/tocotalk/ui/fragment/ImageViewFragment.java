package com.bclould.tocotalk.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.polites.android.GestureImageView;

/**
 * Created by GA on 2018/3/7.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ImageViewFragment extends Fragment {
    private String imageUrl;
    private ProgressBar loadBar;
    private GestureImageView imageGiv;
    private String mBigImgUrl;
    private TextView mArtworkMaster;

    public View onCreateView(android.view.LayoutInflater inflater,
                             android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_images_view_item, container,
                false);
        init(view);
        loadImage(imageUrl);
        return view;
    }

    private void init(View mView) {
        loadBar = (ProgressBar) mView.findViewById(R.id.imageView_loading_pb);
        imageGiv = (GestureImageView) mView.findViewById(R.id.imageView_item_giv);
        mArtworkMaster = (TextView) mView.findViewById(R.id.tv_artwork_master);
        if (mBigImgUrl.isEmpty()) {
            mArtworkMaster.setVisibility(View.GONE);
        } else {
            mArtworkMaster.setVisibility(View.VISIBLE);
            mArtworkMaster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilTool.Log("图片", mBigImgUrl);
                    loadBar.setVisibility(View.VISIBLE);
                    Glide.with(ImageViewFragment.this).load(mBigImgUrl).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (resource != null) {
                                UtilTool.Log("图片", "加载完成");
                                imageGiv.setImageDrawable(resource);
                                loadBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        }
        imageGiv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    public void loadImage(String url) {
        imageGiv.setImageDrawable(Drawable.createFromPath(url));
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl, String bigImgUrl) {
        this.imageUrl = imageUrl;
        this.mBigImgUrl = bigImgUrl;
    }
}

