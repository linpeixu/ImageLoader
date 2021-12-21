package com.cloudling.imageloader;

import android.content.Context;
import android.view.View;

/**
 * 描述: 图片加载策略
 * 联系: 1966353889@qq.com
 * 日期: 2019/10/29
 */
public interface BaseImageLoaderStrategy<V extends View, L> {

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param url     图片地址
     * @param view    不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     */
    <LoadAddress> void load(Context context, LoadAddress url, V view, int... placeholder);

    /**
     * 加载图片
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param listener 加载监听
     */
    <LoadAddress> void load(Context context, LoadAddress url, V view, ImageLoaderCallback<L> listener, int... placeholder);

    /**
     * 加载图片（圆形）
     *
     * @param context 上下文
     * @param url     图片加载地址
     * @param view    不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     */
    <LoadAddress> void circle(Context context, LoadAddress url, V view, int... placeholder);

    /**
     * 加载图片（圆形）
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param listener 加载监听
     */
    <LoadAddress> void circle(Context context, LoadAddress url, V view, ImageLoaderCallback<L> listener, int... placeholder);

    /**
     * 加载图片（圆角）
     *
     * @param context 上下文
     * @param url     图片加载地址
     * @param view    不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius  圆角半径
     */
    <LoadAddress> void round(Context context, LoadAddress url, V view, float radius, int... placeholder);

    /**
     * 加载图片（圆角）
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius   圆角半径
     * @param listener 加载监听
     */
    <LoadAddress> void round(Context context, LoadAddress url, V view, float radius, ImageLoaderCallback<L> listener, int... placeholder);

    /**
     * 加载图片（圆角）
     *
     * @param context     上下文
     * @param url         图片加载地址
     * @param view        不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius      左上角圆角半径，右上角圆角半径，左下角圆角半径，右下角圆角半径
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    <LoadAddress> void round(Context context, LoadAddress url, V view, float[] radius, int... placeholder);

    /**
     * 加载图片（圆角）
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius   左上角圆角半径，右上角圆角半径，左下角圆角半径，右下角圆角半径
     * @param listener 加载监听
     */
    <LoadAddress> void round(Context context, LoadAddress url, V view, float[] radius, ImageLoaderCallback<L> listener, int... placeholder);

    /**
     * 是否为支持的加载类型（不同图片加载框架支持的加载类型可能不一样，在具体的实现类中实现逻辑）
     */
    <LoadAddress> boolean supportLoad(LoadAddress url);
}
