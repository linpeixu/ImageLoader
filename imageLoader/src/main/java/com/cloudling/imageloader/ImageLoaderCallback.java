package com.cloudling.imageloader;

/**
 * 描述: 图片加载回调
 * 联系: 1966353889@qq.com
 * 日期: 2019/10/29
 */
public interface ImageLoaderCallback<T> {
    void onSuccess(T... result);

    void onFailure(T... result);

    void onCancel(T... result);
}