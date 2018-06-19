package com.bclould.tea.ui.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.MyAssetsInfo;
import com.bclould.tea.ui.activity.OutCoinActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/10.
 */

public class CloudCoinRVAdapter extends RecyclerView.Adapter {

    private final FragmentActivity mActivity;
    private final List<MyAssetsInfo.DataBean> mMyAssetsBeanList;

    public CloudCoinRVAdapter(FragmentActivity activity, List<MyAssetsInfo.DataBean> MyAssetsBeanList) {
        mActivity = activity;
        mMyAssetsBeanList = MyAssetsBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        RecyclerView.ViewHolder holder = null;

        if (viewType == 0) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin, parent, false);

            holder = new ViewHolder1(view);

        } else if (viewType == 1) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin2, parent, false);

            holder = new ViewHolder2(view);

        } else if (viewType == 2) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin3, parent, false);

            holder = new ViewHolder3(view);

        } else if (viewType == 3) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin4, parent, false);

            holder = new ViewHolder4(view);

        } else if (viewType == 4) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin5, parent, false);

            holder = new ViewHolder5(view);

        } else if (viewType == 5) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin6, parent, false);

            holder = new ViewHolder6(view);

        } else if (viewType == 6) {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin, parent, false);

            holder = new ViewHolder7(view);

        } else {

            view = LayoutInflater.from(mActivity).inflate(R.layout.item_cloud_coin2, parent, false);

            holder = new ViewHolder8(view);

        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder1) {
            ViewHolder1 viewHolder = (ViewHolder1) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else if (holder instanceof ViewHolder2) {
            ViewHolder2 viewHolder = (ViewHolder2) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else if (holder instanceof ViewHolder3) {
            ViewHolder3 viewHolder = (ViewHolder3) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else if (holder instanceof ViewHolder4) {
            ViewHolder4 viewHolder = (ViewHolder4) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else if (holder instanceof ViewHolder5) {
            ViewHolder5 viewHolder = (ViewHolder5) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else if (holder instanceof ViewHolder6) {
            ViewHolder6 viewHolder = (ViewHolder6) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else if (holder instanceof ViewHolder7) {
            ViewHolder7 viewHolder = (ViewHolder7) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        } else {
            ViewHolder8 viewHolder = (ViewHolder8) holder;
            viewHolder.setData(mMyAssetsBeanList.get(position));
        }


    }

    @Override
    public int getItemCount() {
        return mMyAssetsBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 8 == 0) {

            return 0;

        } else if (position % 8 == 1) {

            return 1;

        } else if (position % 8 == 2) {

            return 2;

        } else if (position % 8 == 3) {

            return 3;

        } else if (position % 8 == 4) {

            return 4;

        } else if (position % 8 == 5) {

            return 5;

        } else if (position % 8 == 6) {

            return 6;

        } else if (position % 8 == 7) {

            return 7;

        } else {

            return 8;

        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private MyAssetsInfo.DataBean mLtcBean;
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;

        ViewHolder1(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);

                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + "个");

        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }

    class ViewHolder3 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder3(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }

    class ViewHolder4 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder4(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getDisplay());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }

    class ViewHolder5 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder5(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }

    class ViewHolder6 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder6(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }

    class ViewHolder7 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder7(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }

    class ViewHolder8 extends RecyclerView.ViewHolder {
        @Bind(R.id.currency_name)
        TextView mCurrencyName;
        @Bind(R.id.currency_count)
        TextView mCurrencyCount;
        private MyAssetsInfo.DataBean mLtcBean;

        ViewHolder8(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, OutCoinActivity.class);
                    intent.putExtra("id", mLtcBean.getId());
                    intent.putExtra("name", mLtcBean.getName());
                    intent.putExtra("total", mLtcBean.getTotal());
                    mActivity.startActivity(intent);
                }
            });
        }

        public void setData(MyAssetsInfo.DataBean ltcBean) {
            mLtcBean = ltcBean;
            mCurrencyName.setText(ltcBean.getName());
            mCurrencyCount.setText(ltcBean.getTotal() + mActivity.getString(R.string.individual));
        }
    }
}
