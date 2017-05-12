package com.example.jingbin.cloudreader.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jingbin.cloudreader.R;
import com.example.jingbin.cloudreader.base.baseadapter.BaseRecyclerViewAdapter;
import com.example.jingbin.cloudreader.base.baseadapter.BaseRecyclerViewHolder;
import com.example.jingbin.cloudreader.bean.AndroidBean;
import com.example.jingbin.cloudreader.databinding.ItemEverydayOneBinding;
import com.example.jingbin.cloudreader.databinding.ItemEverydayThreeBinding;
import com.example.jingbin.cloudreader.databinding.ItemEverydayTitleBinding;
import com.example.jingbin.cloudreader.databinding.ItemEverydayTwoBinding;
import com.example.jingbin.cloudreader.http.rx.RxBus;
import com.example.jingbin.cloudreader.http.rx.RxCodeConstants;
import com.example.jingbin.cloudreader.utils.CommonUtils;
import com.example.jingbin.cloudreader.utils.ImgLoadUtil;
import com.example.jingbin.cloudreader.utils.PerfectClickListener;
import com.example.jingbin.cloudreader.view.webview.WebViewActivity;

import java.util.List;

/**
 * 每日推荐适配器
 * 4 种布局
 */

public class EverydayAdapter extends BaseRecyclerViewAdapter<List<AndroidBean>> {

    private static final int TYPE_TITLE = 1; // title
    private static final int TYPE_ONE = 2;// 一张图
    private static final int TYPE_TWO = 3;// 二张图
    private static final int TYPE_THREE = 4;// 三张图

    /**
     * 根据服务器返回标题的类型
     * 0.标题
     * 1.一张图片
     * 2.两张图片
     * 3.三张图片
     */
    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(getData().get(position).get(0).getType_title())) {
            return TYPE_TITLE;
        } else if (getData().get(position).size() == 1) {
            return TYPE_ONE;
        } else if (getData().get(position).size() == 2) {
            return TYPE_TWO;
        } else if (getData().get(position).size() == 3) {
            return TYPE_THREE;
        }
        return super.getItemViewType(position);
    }

    /**
     * 返回不同的 ViewHolder
     */
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TITLE:// 标题
                return new TitleHolder(parent, R.layout.item_everyday_title);
            case TYPE_ONE:// 一张图片
                return new OneHolder(parent, R.layout.item_everyday_one);
            case TYPE_TWO:// 两张图片
                return new TwoHolder(parent, R.layout.item_everyday_two);
            default:// 三张图片
                return new ThreeHolder(parent, R.layout.item_everyday_three);
        }
    }

    /**
     * 标题 Holder
     * index 设置 RxBus 点击标题上更多的跳转
     */
    private class TitleHolder extends BaseRecyclerViewHolder<List<AndroidBean>, ItemEverydayTitleBinding> {

        TitleHolder(ViewGroup parent, int title) {
            super(parent, title);
        }

        // BaseRecyclerViewHolder binding
        @Override
        public void onBindViewHolder(List<AndroidBean> object, final int position) {
            int index = 0;
            String title = object.get(0).getType_title();
            binding.tvTitleType.setText(title);
            if ("Android".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_android));
                index = 0;
            } else if ("福利".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_meizi));
                index = 1;
            } else if ("IOS".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_ios));
                index = 2;
            } else if ("休息视频".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_movie));
                index = 2;
            } else if ("拓展资源".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_source));
                index = 2;
            } else if ("瞎推荐".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_xia));
                index = 2;
            } else if ("前端".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_qian));
                index = 2;
            } else if ("App".equals(title)) {
                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_app));
                index = 2;
            }

            if (position != 0) {
                binding.viewLine.setVisibility(View.VISIBLE);
            } else {
                binding.viewLine.setVisibility(View.GONE);
            }

            // 点击更多的跳转
            final int finalIndex = index;
            binding.llTitleMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RxBus.getDefault().post(RxCodeConstants.JUMP_TYPE, finalIndex);
                }
            });
        }
    }

    /**
     * 一张图片
     * 只需要实现  onBindViewHolder，其他 base 中封装了
     */
    private class OneHolder extends BaseRecyclerViewHolder<List<AndroidBean>, ItemEverydayOneBinding> {

        OneHolder(ViewGroup parent, int title) {
            super(parent, title);// 调用 super 获得 binding
        }

        @Override
        public void onBindViewHolder(final List<AndroidBean> object, int position) {
            if ("福利".equals(object.get(0).getType())) {
                binding.tvOnePhotoTitle.setVisibility(View.GONE);
                binding.ivOnePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                displayOneImg(object);

            } else {
                binding.tvOnePhotoTitle.setVisibility(View.VISIBLE);
                setDes(object, 0, binding.tvOnePhotoTitle);
                displayRandomImg(1, 0, binding.ivOnePhoto, object);
            }

            // 通过根布局设置 item 的点击事件
            setOnClick(binding.llOnePhoto, object.get(0));
        }

        // 使用 Glide 加载一张图片
        private void displayOneImg(List<AndroidBean> object) {
            Glide.with(binding.ivOnePhoto.getContext())
                    .load(object.get(0).getUrl())
                    .crossFade(1500)  // 其实是 0 -》 1 的透明度的变化
                    .placeholder(R.drawable.img_two_bi_one)
                    .error(R.drawable.img_two_bi_one)
                    .into(binding.ivOnePhoto);
        }
    }

    /**
     * 两张片 Holder
     */
    private class TwoHolder extends BaseRecyclerViewHolder<List<AndroidBean>, ItemEverydayTwoBinding> {

        TwoHolder(ViewGroup parent, int title) {
            super(parent, title);
        }

        @Override
        public void onBindViewHolder(List<AndroidBean> object, int position) {
            displayRandomImg(2, 0, binding.ivTwoOneOne, object);
            displayRandomImg(2, 1, binding.ivTwoOneTwo, object);
            setDes(object, 0, binding.tvTwoOneOneTitle);
            setDes(object, 1, binding.tvTwoOneTwoTitle);
            setOnClick(binding.llTwoOneOne, object.get(0));
            setOnClick(binding.llTwoOneTwo, object.get(1));
        }
    }

    /**
     * 三张图片 Holder
     */
    private class ThreeHolder extends BaseRecyclerViewHolder<List<AndroidBean>, ItemEverydayThreeBinding> {

        ThreeHolder(ViewGroup parent, int title) {
            super(parent, title);
        }

        @Override
        public void onBindViewHolder(List<AndroidBean> object, int position) {
            displayRandomImg(3, 0, binding.ivThreeOneOne, object);
            displayRandomImg(3, 1, binding.ivThreeOneTwo, object);
            displayRandomImg(3, 2, binding.ivThreeOneThree, object);
            setOnClick(binding.llThreeOneOne, object.get(0));
            setOnClick(binding.llThreeOneTwo, object.get(1));
            setOnClick(binding.llThreeOneThree, object.get(2));
            setDes(object, 0, binding.tvThreeOneOneTitle);
            setDes(object, 1, binding.tvThreeOneTwoTitle);
            setDes(object, 2, binding.tvThreeOneThreeTitle);
        }
    }

    /**
     * 设置描述
     */
    private void setDes(List<AndroidBean> object, int position, TextView textView) {
        textView.setText(object.get(position).getDesc());
    }

    /**
     * 展示随机图片
     */
    private void displayRandomImg(int imgNumber, int position, ImageView imageView, List<AndroidBean> object) {
        ImgLoadUtil.displayRandom(imgNumber, object.get(position).getImage_url(), imageView);
    }


    /**
     * 设置点击事件 WebView
     */
    private void setOnClick(final LinearLayout linearLayout, final AndroidBean bean) {

        // 单击 跳转 WebView
        linearLayout.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                WebViewActivity.loadUrl(v.getContext(), bean.getUrl(), "加载中...");
            }
        });

        /**
         * 长按点击事件，弹窗，在跳 WebView
         */
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = View.inflate(v.getContext(), R.layout.title_douban_top, null);
                TextView titleTop = (TextView) view.findViewById(R.id.title_top);
                titleTop.setTextSize(14);
                String title = TextUtils.isEmpty(bean.getType()) ? bean.getDesc() : bean.getType() + "：  " + bean.getDesc();
                titleTop.setText(title);
                builder.setCustomTitle(view);
                builder.setPositiveButton("查看详情", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WebViewActivity.loadUrl(linearLayout.getContext(), bean.getUrl(), "加载中...");
                    }
                });
                builder.show();
                return false;
            }
        });

    }
}
