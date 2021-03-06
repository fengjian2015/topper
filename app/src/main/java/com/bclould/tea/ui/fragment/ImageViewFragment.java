package com.bclould.tea.ui.fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.network.https.GlideProgressListener;
import com.bclould.tea.network.https.GlideProgressListenerManager;
import com.bclould.tea.ui.widget.ZoomImageView;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.QRDiscernUtil;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import org.greenrobot.eventbus.EventBus;
import java.io.File;

/**
 * Created by GA on 2018/3/7.
 */
public class ImageViewFragment extends Fragment implements GlideProgressListener{
    private String imageUrl;
    private ProgressBar loadBar;
    private ZoomImageView imageGiv;
    private String mBigImgUrl;
    private TextView mArtworkMaster;
    private DBManager mMgr;
    private int mId;

    public View onCreateView(android.view.LayoutInflater inflater,
                             android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_images_view_item, container,
                false);
        init(view);
        loadImage(imageUrl);
        GlideProgressListenerManager.get().registerListener(this);
        return view;
    }

    private void init(View mView) {
        loadBar = (ProgressBar) mView.findViewById(R.id.imageView_loading_pb);
        imageGiv = (ZoomImageView) mView.findViewById(R.id.imageView_item_giv);
        mArtworkMaster = (TextView) mView.findViewById(R.id.tv_artwork_master);
        imageGiv.setImageURI(Uri.parse(imageUrl));
        if (mBigImgUrl.isEmpty()) {
            mArtworkMaster.setVisibility(View.GONE);
        } else {
            mArtworkMaster.setVisibility(View.VISIBLE);
            mArtworkMaster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilTool.Log("图片", mBigImgUrl);
                    mArtworkMaster.setText("0%");
                    loadBar.setVisibility(View.VISIBLE);
                    loadBar.bringToFront();
                    Glide.with(ImageViewFragment.this).load(mBigImgUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource != null) {
                                imageGiv.setImageDrawable(resource);
                                mArtworkMaster.setVisibility(View.GONE);
                                loadBar.setVisibility(View.GONE);
                                mMgr.updateImageType(mId + "", 1);
                                mMgr.updateImageVoice(mId+"",mBigImgUrl);
                                MessageEvent messageEvent = new MessageEvent(getString(R.string.look_original));
                                messageEvent.setId(mId + "");
                                messageEvent.setFilepath(mBigImgUrl);
                                EventBus.getDefault().post(messageEvent);
                            }
                            return false;
                        }
                    }).into(imageGiv);
                }
            });
        }
        imageGiv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        imageGiv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new QRDiscernUtil(getActivity()).discernQR(imageUrl);
                return false;
            }
        });
    }





    public void loadImage(String url) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            Glide.with(ImageViewFragment.this).load(url).apply(requestOptions).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageGiv.setImageDrawable(resource);
                    return false;
                }
            }).into(imageGiv);
        } else {
            Glide.with(ImageViewFragment.this).load(new File(url)).apply(requestOptions).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageGiv.setImageDrawable(resource);
                    return false;
                }
            }).into(imageGiv);
//            imageGiv.setImageDrawable(Drawable.createFromPath(url));
        }
    }

    RequestOptions requestOptions=new RequestOptions()
            .placeholder(R.mipmap.image_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl, String bigImgUrl, DBManager mgr, Integer id) {
        this.imageUrl = imageUrl;
        this.mBigImgUrl = bigImgUrl;
        mMgr = mgr;
        mId = id;
    }

    @Override
    public void onDestroy() {
        GlideProgressListenerManager.get().unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void start(String url) {

    }

    @Override
    public void progress(final int progress, String url) {
        if(mBigImgUrl.equals(url)){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mArtworkMaster.setText(progress+"%");
                }
            });
        }
    }

    @Override
    public void end(String url) {

    }
}

