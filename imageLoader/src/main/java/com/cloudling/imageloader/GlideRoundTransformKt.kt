package com.cloudling.imageloader

import android.graphics.Bitmap
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

/**
 * 描述: Glide圆角transform（跟随ImageView的scaleType缩放）
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/26
 */
class GlideRoundTransformKt : BitmapTransformation {
    private var scaleType: ScaleType? = null
    private var leftTop = 0f
    private var rightTop = 0f
    private var leftBottom = 0f
    private var rightBottom = 0f

    constructor (radius: Float, scaleType: ScaleType?) {
        rightBottom = radius
        leftBottom = radius
        rightTop = radius
        leftTop = radius
        this.scaleType = scaleType
    }

    constructor(
        leftTop: Float,
        rightTop: Float,
        leftBottom: Float,
        rightBottom: Float,
        scaleType: ScaleType?,
    ) {
        this.leftTop = leftTop
        this.rightTop = rightTop
        this.leftBottom = leftBottom
        this.rightBottom = rightBottom
        this.scaleType = scaleType
    }

    constructor(
        leftTop: Float,
        rightTop: Float,
        leftBottom: Float,
        rightBottom: Float,
    ) {
        this.leftTop = leftTop
        this.rightTop = rightTop
        this.leftBottom = leftBottom
        this.rightBottom = rightBottom
    }

    fun radius(radius: Float): GlideRoundTransformKt {
        rightBottom = radius
        leftBottom = radius
        rightTop = radius
        leftTop = radius
        return this
    }

    fun scaleType(scaleType: ScaleType?): GlideRoundTransformKt {
        this.scaleType = scaleType
        return this
    }

    /**
     * 左上角设置圆角
     *
     * @param leftTop 是否在左上角设置圆角
     */
    fun leftTop(leftTop: Float): GlideRoundTransformKt {
        this.leftTop = leftTop
        return this
    }

    /**
     * 右上角设置圆角
     *
     * @param rightTop 是否在右上角设置圆角
     */
    fun rightTop(rightTop: Float): GlideRoundTransformKt {
        this.rightTop = rightTop
        return this
    }

    /**
     * 左下角设置圆角
     *
     * @param leftBottom 是否在左下角设置圆角
     */
    fun leftBottom(leftBottom: Float): GlideRoundTransformKt {
        this.leftBottom = leftBottom
        return this
    }

    /**
     * 右下角设置圆角
     *
     * @param rightBottom 是否在右下角设置圆角
     */
    fun rightBottom(rightBottom: Float): GlideRoundTransformKt {
        this.rightBottom = rightBottom
        return this
    }

    fun newBuilder(): GlideRoundTransformKt {
        return GlideRoundTransformKt(10F, ScaleType.CENTER_CROP)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int,
    ): Bitmap {
        val bitmap: Bitmap
        if (scaleType != null) {
            bitmap = when (scaleType) {
                ScaleType.CENTER_CROP -> TransformationUtils.centerCrop(pool,
                    toTransform,
                    outWidth,
                    outHeight)
                ScaleType.FIT_CENTER -> TransformationUtils.fitCenter(pool,
                    toTransform,
                    outWidth,
                    outHeight)
                ScaleType.CENTER_INSIDE -> TransformationUtils.centerInside(pool,
                    toTransform,
                    outWidth,
                    outHeight)
                else -> toTransform
            }
        } else {
            bitmap = toTransform
        }
        return TransformationUtils.roundedCorners(
            pool,
            bitmap,
            leftTop,
            rightTop,
            rightBottom,
            leftBottom
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }
}