package com.cloudling.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * 描述: Glide圆角transform（跟随ImageView的scaleType缩放）
 * 联系: 1966353889@qq.com
 * 日期: 2019/10/29
 */
public class GlideRoundTransform extends BitmapTransformation {
    private ImageView.ScaleType scaleType;
    /**
     * 左上角，右上角，左下角，右下角四个方向圆角半径
     */
    private float leftTop, rightTop, leftBottom, rightBottom;

    public GlideRoundTransform(float radius, ImageView.ScaleType scaleType) {
        leftTop = rightTop = leftBottom = rightBottom = radius;
        this.scaleType = scaleType;
    }

    public GlideRoundTransform(float leftTop, float rightTop, float leftBottom, float rightBottom, ImageView.ScaleType scaleType) {
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.leftBottom = leftBottom;
        this.rightBottom = rightBottom;
        this.scaleType = scaleType;
    }

    public GlideRoundTransform(float leftTop, float rightTop, float leftBottom, float rightBottom) {
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.leftBottom = leftBottom;
        this.rightBottom = rightBottom;
    }

    public GlideRoundTransform radius(float radius) {
        leftTop = rightTop = leftBottom = rightBottom = radius;
        return this;
    }

    public GlideRoundTransform scaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    /**
     * 左上角设置圆角
     *
     * @param leftTop 是否在左上角设置圆角
     */
    public GlideRoundTransform leftTop(float leftTop) {
        this.leftTop = leftTop;
        return this;
    }

    /**
     * 右上角设置圆角
     *
     * @param rightTop 是否在右上角设置圆角
     */
    public GlideRoundTransform rightTop(float rightTop) {
        this.rightTop = rightTop;
        return this;
    }

    /**
     * 左下角设置圆角
     *
     * @param leftBottom 是否在左下角设置圆角
     */
    public GlideRoundTransform leftBottom(float leftBottom) {
        this.leftBottom = leftBottom;
        return this;
    }

    /**
     * 右下角设置圆角
     *
     * @param rightBottom 是否在右下角设置圆角
     */
    public GlideRoundTransform rightBottom(float rightBottom) {
        this.rightBottom = rightBottom;
        return this;
    }

    public static GlideRoundTransform newBuilder() {
        return new GlideRoundTransform(10, ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform,
                               int outWidth, int outHeight) {
        if (scaleType != null) {
            switch (scaleType) {
                case CENTER_CROP:
                    toTransform = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
                    break;
                case FIT_CENTER:
                    toTransform = TransformationUtils.fitCenter(pool, toTransform, outWidth, outHeight);
                    break;
                case CENTER_INSIDE:
                    toTransform = TransformationUtils.centerInside(pool, toTransform, outWidth, outHeight);
                    break;
            }
        }
        return TransformationUtils.roundedCorners(pool, toTransform, leftTop, rightTop, rightBottom, leftBottom);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }

}