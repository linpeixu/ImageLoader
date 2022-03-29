package com.cloudling.imageloader

import android.content.Context
import android.view.View
import android.widget.ImageView
import java.lang.NullPointerException

/**
 * 描述: 图片加载工具类（单例）
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/26
 */
class ImageLoaderKt private constructor() {
    val version = "1.0.1"
    val describe = "ImageLoader 1.0.1 by kotlin"
    private var mStrategy: BaseImageLoaderStrategyKt? = null

    init {
        /*初始化时设置图片加载策略为Glide*/
        setImageLoaderStrategy(GlideImageLoaderStrategyKt())
    }

    companion object {
        private val mInstance: ImageLoaderKt by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ImageLoaderKt()
        }

        fun get(): ImageLoaderKt {
            return mInstance
        }
    }

    private fun setImageLoaderStrategy(strategy: BaseImageLoaderStrategyKt) {
        mStrategy = strategy
    }

    /**
     * 加载图片
     *
     * @param context     上下文
     * @param url         图片地址（泛型）
     * @param view        图片宿主
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> load(
        context: Context?,
        url: Source,
        view: Target?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.load(context, url, view, *placeholder)
        }
    }

    /**
     * 加载图片
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param listener    加载监听
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> load(
        context: Context?,
        url: Source,
        view: Target?,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.load(context, url, view, listener, *placeholder)
        }
    }

    /**
     * 加载图片
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> circle(
        context: Context?,
        url: Source,
        view: Target?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.circle(context, url, view, *placeholder)
        }
    }

    /**
     * 加载图片
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param listener    加载监听
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> circle(
        context: Context?,
        url: Source,
        view: Target?,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.circle(context, url, view, listener, *placeholder)
        }
    }

    /**
     * 加载图片（圆角）
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param radius      圆角半径
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target?,
        radius: Float,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.round(context, url, view, radius, *placeholder)
        }
    }

    /**
     * 加载图片（圆角）
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param radius      圆角半径
     * @param listener    加载监听
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target?,
        radius: Float,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.round(context, url, view, radius, listener, *placeholder)
        }
    }

    /**
     * 加载图片（圆角）
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param radius      左上角圆角半径，右上角圆角半径，左下角圆角半径，右下角圆角半径
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target?,
        radius: FloatArray?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.round(context, url, view, radius, *placeholder)
        }
    }

    /**
     * 加载图片（圆角）
     *
     * @param context     上下文
     * @param url         图片地址
     * @param view        图片宿主
     * @param radius      左上角圆角半径，右上角圆角半径，左下角圆角半径，右下角圆角半径
     * @param listener    加载监听
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    fun <Source, Target> round(
        context: Context?,
        url: Source,
        view: Target?,
        radius: FloatArray?,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            mStrategy!!.round(context, url, view, radius, listener, *placeholder)
        }
    }

    fun clear(context: Context?, view: View) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            if (mStrategy is GlideImageLoaderStrategyKt) {
                (mStrategy as GlideImageLoaderStrategyKt).clear(context, view)
            }
        }
    }

    fun pauseRequests(context: Context?) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            if (mStrategy is GlideImageLoaderStrategyKt) {
                (mStrategy as GlideImageLoaderStrategyKt).pauseRequests(context)
            }
        }
    }

    fun pauseAllRequests(context: Context?) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            if (mStrategy is GlideImageLoaderStrategyKt) {
                (mStrategy as GlideImageLoaderStrategyKt).pauseAllRequests(context)
            }
        }
    }

    fun resumeRequests(context: Context?) {
        if (mStrategy == null) {
            throw NullPointerException("you should invoke setImageLoaderStrategy first")
        } else {
            if (mStrategy is GlideImageLoaderStrategyKt) {
                (mStrategy as GlideImageLoaderStrategyKt).resumeRequests(context)
            }
        }
    }
}