package com.cloudling.imageloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * 描述: ImageLoader用法示例
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/29
 */
public class LoadExample {
    public void load(Context context, String url, ImageView view) {
        /*两种用法都可以*/
        /*直接通过kotlin使用*/
        ImageLoaderKt.Companion.get().load(context, url, view);
        /*兼容从非kotlin版本无感切换到kotlin版本*/
        ImageLoader.getInstance().load(context, url, view);
    }
}
