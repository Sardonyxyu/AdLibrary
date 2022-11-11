package com.yingyongduoduo.ad.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yingyongduoduo.ad.R;
import com.yingyongduoduo.ad.interfaces.GlideRoundTransform;

public class GlideUtil {

    public static void loadCP(Context context, String url, ImageView view) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ad_prefix_ic_error) //预加载图片
                .error(R.mipmap.ad_prefix_ic_error) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new GlideRoundTransform(0)); //圆角

        Glide.with(context).load(url).apply(options).into(view);
    }
    public static void loadBanner(Context context, String url, ImageView view) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ad_prefix_ic_error) //预加载图片
                .error(R.mipmap.ad_prefix_ic_error) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new GlideRoundTransform(5)); //圆角

        Glide.with(context).load(url).apply(options).into(view);
    }
}
