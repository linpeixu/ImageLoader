package com.cloudling.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import java.io.File

/**
 * 描述: Glide图片加载策略
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/26
 */
class GlideImageLoaderStrategyKt : BaseImageLoaderStrategyKt {
    override fun <Source, TargetView> load(
        context: Context?,
        url: Source,
        view: TargetView,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            if (placeholder.size in 1..2) {
                val options = if (placeholder.size == 2) {
                    RequestOptions().placeholder(placeholder[0]).error(placeholder[1])
                } else {
                    RequestOptions().placeholder(placeholder[0])
                }
                Glide.with(context!!).load(url).apply(options).dontAnimate().into(view as ImageView)
            } else {
                Glide.with(context!!).load(url).dontAnimate().into(view as ImageView)
            }
        }
    }

    override fun <Source, TargetView> load(
        context: Context?,
        url: Source,
        view: TargetView,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            if (placeholder.size in 1..2) {
                val options = if (placeholder.size == 2) {
                    RequestOptions().placeholder(placeholder[0]).error(placeholder[1])
                } else {
                    RequestOptions().placeholder(placeholder[0])
                }
                Glide.with(context!!).load(url).apply(options)
                    .addListener(if (listener == null) null else object :
                        RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            listener.onFailure()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean {
                            listener.onSuccess()
                            return false
                        }
                    }).dontAnimate().into(view as ImageView)
            } else {
                Glide.with(context!!).load(url).addListener(if (listener == null) null else object :
                    RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onFailure()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onSuccess()
                        return false
                    }
                }).dontAnimate().into(view as ImageView)
            }
        }
    }

    override fun <Source> loadImageBitmap(
        context: Context?,
        url: Source,
        maxWidth: Int,
        maxHeight: Int,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
    ) {
        if (supportLoad(url)) {
            Glide.with(context!!)
                .asBitmap()
                .override(maxWidth, maxHeight)
                .load(url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?,
                    ) {
                        listener?.onSuccess(ImageResultKt(resource))
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        listener?.onFailure(ImageResultKt(null))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                })
        }
    }

    override fun <Source, TargetView> circle(
        context: Context?,
        url: Source,
        view: TargetView,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            /*占位图进行圆形处理*/
            var load: RequestBuilder<Drawable?>? = null
            var error: RequestBuilder<Drawable?>? = null
            if (placeholder.size in 1..2) {
                load = Glide.with(context!!)
                    .load(placeholder[0]).dontAnimate()
                    .apply(RequestOptions.circleCropTransform())
                if (placeholder.size == 2) error = Glide.with(context)
                    .load(placeholder[1]).dontAnimate()
                    .apply(RequestOptions.circleCropTransform())
            }
            Glide.with(context!!).load(url).apply(RequestOptions.circleCropTransform())
                .thumbnail(load).error(error).dontAnimate().into(view as ImageView)
        }
    }

    override fun <Source, TargetView> circle(
        context: Context?,
        url: Source,
        view: TargetView,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            /*占位图进行圆形处理*/
            var load: RequestBuilder<Drawable?>? = null
            var error: RequestBuilder<Drawable?>? = null
            if (placeholder.size in 1..2) {
                load = Glide.with(context!!)
                    .load(placeholder[0]).dontAnimate()
                    .apply(RequestOptions.circleCropTransform())
                if (placeholder.size == 2) error = Glide.with(context)
                    .load(placeholder[1]).dontAnimate()
                    .apply(RequestOptions.circleCropTransform())
            }
            Glide.with(context!!).load(url).apply(RequestOptions.circleCropTransform())
                .thumbnail(load).error(error).dontAnimate()
                .addListener(if (listener == null) null else object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onFailure()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onSuccess()
                        return false
                    }
                }).into(view as ImageView)
        }
    }

    override fun <Source, TargetView> round(
        context: Context?,
        url: Source,
        view: TargetView,
        radius: Float,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            /*占位图进行圆角处理*/
            var load: RequestBuilder<Drawable?>? = null
            var error: RequestBuilder<Drawable?>? = null
            if (placeholder.size in 1..2) {
                load = Glide.with(context!!)
                    .load(placeholder[0]).dontAnimate()
                    .apply(RequestOptions().transform(GlideRoundTransformKt(radius,
                        (view as ImageView).scaleType)))
                if (placeholder.size == 2) error = Glide.with(context)
                    .load(placeholder[1]).dontAnimate()
                    .apply(RequestOptions().transform(GlideRoundTransformKt(radius,
                        view.scaleType)))
            }
            val options = RequestOptions().transform(GlideRoundTransformKt(radius,
                (view as ImageView).scaleType))
            Glide.with(context!!).load(url).apply(options).thumbnail(load).error(error)
                .dontAnimate().into(view)
        }
    }

    override fun <Source, TargetView> round(
        context: Context?,
        url: Source,
        view: TargetView,
        radius: Float,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            /*占位图进行圆角处理*/
            var load: RequestBuilder<Drawable?>? = null
            var error: RequestBuilder<Drawable?>? = null
            if (placeholder.size in 1..2) {
                load = Glide.with(context!!)
                    .load(placeholder[0]).dontAnimate()
                    .apply(RequestOptions().transform(GlideRoundTransformKt(radius,
                        (view as ImageView).scaleType)))
                if (placeholder.size == 2) error = Glide.with(context)
                    .load(placeholder[1]).dontAnimate()
                    .apply(RequestOptions().transform(GlideRoundTransformKt(radius,
                        view.scaleType)))
            }
            val options = RequestOptions().transform(GlideRoundTransformKt(radius,
                (view as ImageView).scaleType))
            Glide.with(context!!).load(url).apply(options).thumbnail(load).error(error)
                .addListener(if (listener == null) null else object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onFailure()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onSuccess()
                        return false
                    }
                }).dontAnimate().into(view)
        }
    }

    override fun <Source, TargetView> round(
        context: Context?,
        url: Source,
        view: TargetView,
        radius: FloatArray?,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            var leftTop = 0f
            var rightTop = 0f
            var leftBottom = 0f
            var rightBottom = 0f
            if (radius != null && radius.size == 4) {
                leftTop = radius[0]
                rightTop = radius[1]
                leftBottom = radius[2]
                rightBottom = radius[3]
            }
            /*占位图进行圆角处理*/
            var load: RequestBuilder<Drawable?>? = null
            var error: RequestBuilder<Drawable?>? = null
            if (placeholder.size in 1..2) {
                load = Glide.with(context!!)
                    .load(placeholder[0]).dontAnimate()
                    .apply(
                        RequestOptions().transform(
                            GlideRoundTransformKt(
                                leftTop,
                                rightTop,
                                leftBottom,
                                rightBottom,
                                (view as ImageView).scaleType
                            )
                        )
                    )
                if (placeholder.size == 2) error = Glide.with(context)
                    .load(placeholder[1]).dontAnimate()
                    .apply(
                        RequestOptions().transform(
                            GlideRoundTransformKt(
                                leftTop,
                                rightTop,
                                leftBottom,
                                rightBottom,
                                view.scaleType
                            )
                        )
                    )
            }
            val options = RequestOptions().transform(
                GlideRoundTransformKt(
                    leftTop,
                    rightTop,
                    leftBottom,
                    rightBottom,
                    (view as ImageView).scaleType
                )
            )
            Glide.with(context!!).load(url).apply(options).thumbnail(load).error(error)
                .dontAnimate().into(view)
        }
    }

    override fun <Source, TargetView> round(
        context: Context?,
        url: Source,
        view: TargetView,
        radius: FloatArray?,
        listener: ImageLoaderCallbackKt<ImageResultKt>?,
        vararg placeholder: Int,
    ) {
        if (supportLoad(url, view)) {
            var leftTop = 0f
            var rightTop = 0f
            var leftBottom = 0f
            var rightBottom = 0f
            if (radius != null && radius.size == 4) {
                leftTop = radius[0]
                rightTop = radius[1]
                leftBottom = radius[2]
                rightBottom = radius[3]
            }
            /*占位图进行圆角处理*/
            var load: RequestBuilder<Drawable?>? = null
            var error: RequestBuilder<Drawable?>? = null
            if (placeholder.size in 1..2) {
                load = Glide.with(context!!)
                    .load(placeholder[0]).dontAnimate()
                    .apply(
                        RequestOptions().transform(
                            GlideRoundTransformKt(
                                leftTop,
                                rightTop,
                                leftBottom,
                                rightBottom,
                                (view as ImageView).scaleType
                            )
                        )
                    )
                if (placeholder.size == 2) error = Glide.with(context)
                    .load(placeholder[1]).dontAnimate()
                    .apply(
                        RequestOptions().transform(
                            GlideRoundTransformKt(
                                leftTop,
                                rightTop,
                                leftBottom,
                                rightBottom,
                                view.scaleType
                            )
                        )
                    )
            }
            val options = RequestOptions().transform(
                GlideRoundTransformKt(
                    leftTop,
                    rightTop,
                    leftBottom,
                    rightBottom,
                    (view as ImageView).scaleType
                )
            )
            Glide.with(context!!).load(url).apply(options).thumbnail(load).error(error)
                .addListener(if (listener == null) null else object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onFailure()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        listener.onSuccess()
                        return false
                    }
                }).dontAnimate().into(view)
        }
    }

    override fun <Source, Target> supportLoad(url: Source, view: Target): Boolean {
        return view is ImageView && (url is Bitmap || url is Drawable || url is String || url is Uri || url is File || url is Int || url is ByteArray)
    }

    override fun <Source> supportLoad(url: Source): Boolean {
        return url is Bitmap || url is Drawable || url is String || url is Uri || url is File || url is Int || url is ByteArray
    }

    fun clear(context: Context?, view: View) {
        Glide.with(context!!).clear(view)
    }

    fun pauseRequests(context: Context?) {
        Glide.with(context!!).pauseRequests()
    }

    fun pauseAllRequests(context: Context?) {
        Glide.with(context!!).pauseAllRequests()
    }

    fun resumeRequests(context: Context?) {
        Glide.with(context!!).resumeRequests()
    }
}