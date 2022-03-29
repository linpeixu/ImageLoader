package com.cloudling.imageloader;

/**
 * 描述: 图片加载工具类（单例）
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/29
 */
public class ImageLoader {
    public static ImageLoaderKt getInstance() {
        return ImageLoaderKt.Companion.get();
    }
}
