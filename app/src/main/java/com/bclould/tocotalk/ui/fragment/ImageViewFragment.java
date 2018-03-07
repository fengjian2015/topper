package com.bclould.tocotalk.ui.fragment;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.bclould.tocotalk.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.polites.android.GestureImageView;

/**
 * Created by GA on 2018/3/7.
 */

public class ImageViewFragment extends Fragment {
    private String imageUrl;
    private ProgressBar loadBar;
    private GestureImageView imageGiv;

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
        imageGiv = (GestureImageView) mView
                .findViewById(R.id.imageView_item_giv);
        imageGiv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    public void loadImage(String url) {
        if (url.startsWith("https:/")) {
            Glide.with(this).load(url).into(new SimpleTarget<Drawable>() {

                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    if (resource != null) {
                        imageGiv.setImageDrawable(resource);
                        loadBar.setVisibility(View.GONE);
                        imageGiv.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            imageGiv.setImageBitmap(BitmapFactory.decodeFile(url));
            loadBar.setVisibility(View.GONE);
            imageGiv.setVisibility(View.VISIBLE);
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

