package com.cloudling.imageloader

import android.content.Context

/**
 * 描述: 图片加载策略
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/26
 */
interface BaseImageLoaderStrategyKt {

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param url     图片地址
     * @param view    不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     */
    fun <Source, Target> load(
        context: Context?,
        url: Source,
        view: Target,
        vararg placeholder: Int,
    )

    /**
     * 加载图片
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param listener 加载监听
     */
    fun <Source, Target> load(
        context: Context?,
        url: Source,
        view: Target,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    )

    fun <Source> loadImageBitmap(
        context: Context?,
        url: Source,
        maxWidth: Int,
        maxHeight: Int,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
    )

    /**
     * 加载图片（圆形）
     *
     * @param context 上下文
     * @param url     图片加载地址
     * @param view    不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     */
    fun <Source, Target> circle(
        context: Context?,
        url: Source,
        view: Target,
        vararg placeholder: Int,
    )

    /**
     * 加载图片（圆形）
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param listener 加载监听
     */
    fun <Source, Target> circle(
        context: Context?,
        url: Source,
        view: Target,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    )

    /**
     * 加载图片（圆角）
     *
     * @param context 上下文
     * @param url     图片加载地址
     * @param view    不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius  圆角半径
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target,
        radius: Float,
        vararg placeholder: Int,
    )

    /**
     * 加载图片（圆角）
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius   圆角半径
     * @param listener 加载监听
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target,
        radius: Float,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    )

    /**
     * 加载图片（圆角）
     *
     * @param context     上下文
     * @param url         图片加载地址
     * @param view        不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius      左上角圆角半径，右上角圆角半径，左下角圆角半径，右下角圆角半径
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target,
        radius: FloatArray?,
        vararg placeholder: Int,
    )

    /**
     * 加载图片（圆角）
     *
     * @param context  上下文
     * @param url      图片加载地址
     * @param view     不同的图片加载框架要设置的视图可能不一样，如Glide为ImageView而Fresco则为SimpleDraweeView
     * @param radius   左上角圆角半径，右上角圆角半径，左下角圆角半径，右下角圆角半径
     * @param listener 加载监听
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target,
        radius: FloatArray?,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    )

    /**
     * 是否为支持的加载类型（不同图片加载框架支持的加载类型可能不一样，在具体的实现类中实现逻辑）
     */
    fun <Source> supportLoad(url: Source): Boolean

    /**
     * 是否为支持的加载类型（不同图片加载框架支持的加载类型可能不一样，在具体的实现类中实现逻辑）
     */
    fun <Source, Target> supportLoad(url: Source, view: Target): Boolean
}