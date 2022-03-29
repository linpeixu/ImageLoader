package com.cloudling.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

/**
 * 描述: Glide图片加载策略
 * 联系: 1966353889@qq.com
 * 日期: 2019/10/29
 */
public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy<ImageView, ImageResult> {

    @Override
    public <LoadAddress> void load(Context context, LoadAddress url, ImageView view, int... placeholder) {
        if (supportLoad(url)) {
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                RequestOptions options = new RequestOptions().placeholder(placeholder[0]);
                if (placeholder.length == 2) options.error(placeholder[1]);
                Glide.with(context).load(url).apply(options).dontAnimate().into(view);
            } else {
                Glide.with(context).load(url).dontAnimate().into(view);
            }

        }
    }

    @Override
    public <LoadAddress> void load(Context context, LoadAddress url, ImageView view, final ImageLoaderCallback<ImageResult> listener, int... placeholder) {
        if (supportLoad(url)) {
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                RequestOptions options = new RequestOptions().placeholder(placeholder[0]);
                if (placeholder.length == 2) options.error(placeholder[1]);
                Glide.with(context).load(url).apply(options).addListener(listener == null ? null : new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        listener.onFailure();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onSuccess();
                        return false;
                    }
                }).dontAnimate().into(view);
            } else {
                Glide.with(context).load(url).addListener(listener == null ? null : new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        listener.onFailure();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onSuccess();
                        return false;
                    }
                }).dontAnimate().into(view);
            }

        }
    }

    @Override
    public <LoadAddress> void loadImageBitmap(Context context, LoadAddress url, int maxWidth, int maxHeight, ImageLoaderCallback<ImageResult> listener) {
        Glide.with(context)
                .asBitmap()
                .override(maxWidth, maxHeight)
                .load(url)
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (listener != null) {
                            listener.onSuccess(new ImageResult(resource));
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (listener != null) {
                            listener.onFailure(new ImageResult(null));
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                });
    }

    @Override
    public <LoadAddress> void circle(Context context, LoadAddress url, ImageView view, int... placeholder) {
        if (supportLoad(url)) {
            /*占位图进行圆形处理*/
            RequestBuilder<Drawable> load = null, error = null;
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                load = Glide.with(context)
                        .load(placeholder[0]).dontAnimate()
                        .apply(RequestOptions.circleCropTransform());
                if (placeholder.length == 2) error = Glide.with(context)
                        .load(placeholder[1]).dontAnimate()
                        .apply(RequestOptions.circleCropTransform());
            }
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(load).error(error).dontAnimate().into(view);
        }
    }

    @Override
    public <LoadAddress> void circle(Context context, LoadAddress url, ImageView view, final ImageLoaderCallback<ImageResult> listener, int... placeholder) {
        if (supportLoad(url)) {
            /*占位图进行圆形处理*/
            RequestBuilder<Drawable> load = null, error = null;
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                load = Glide.with(context)
                        .load(placeholder[0]).dontAnimate()
                        .apply(RequestOptions.circleCropTransform());
                if (placeholder.length == 2) error = Glide.with(context)
                        .load(placeholder[1]).dontAnimate()
                        .apply(RequestOptions.circleCropTransform());
            }
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(load).error(error).dontAnimate().addListener(listener == null ? null : new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    listener.onFailure();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    listener.onSuccess();
                    return false;
                }
            }).into(view);
        }
    }

    @Override
    public <LoadAddress> void round(Context context, LoadAddress url, ImageView view, float radius, int... placeholder) {
        if (supportLoad(url)) {
            /*占位图进行圆角处理*/
            RequestBuilder<Drawable> load = null, error = null;
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                load = Glide.with(context)
                        .load(placeholder[0]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(radius, view.getScaleType())));
                if (placeholder.length == 2) error = Glide.with(context)
                        .load(placeholder[1]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(radius, view.getScaleType())));
            }
            RequestOptions options = new RequestOptions().transform(new GlideRoundTransform(radius, view.getScaleType()));
            Glide.with(context).load(url).apply(options).thumbnail(load).error(error).dontAnimate().into(view);
        }
    }

    @Override
    public <LoadAddress> void round(Context context, LoadAddress url, ImageView view, float radius, final ImageLoaderCallback<ImageResult> listener, int... placeholder) {
        if (supportLoad(url)) {
            /*占位图进行圆角处理*/
            RequestBuilder<Drawable> load = null, error = null;
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                load = Glide.with(context)
                        .load(placeholder[0]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(radius, view.getScaleType())));
                if (placeholder.length == 2) error = Glide.with(context)
                        .load(placeholder[1]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(radius, view.getScaleType())));
            }
            RequestOptions options = new RequestOptions().transform(new GlideRoundTransform(radius, view.getScaleType()));
            Glide.with(context).load(url).apply(options).thumbnail(load).error(error).addListener(listener == null ? null : new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    listener.onFailure();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    listener.onSuccess();
                    return false;
                }
            }).dontAnimate().into(view);
        }
    }

    @Override
    public <LoadAddress> void round(Context context, LoadAddress url, ImageView view, float[] radius, int... placeholder) {
        if (supportLoad(url)) {
            float leftTop = 0, rightTop = 0, leftBottom = 0, rightBottom = 0;
            if (radius != null && radius.length == 4) {
                leftTop = radius[0];
                rightTop = radius[1];
                leftBottom = radius[2];
                rightBottom = radius[3];
            }
            /*占位图进行圆角处理*/
            RequestBuilder<Drawable> load = null, error = null;
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                load = Glide.with(context)
                        .load(placeholder[0]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(leftTop, rightTop, leftBottom, rightBottom, view.getScaleType())));
                if (placeholder.length == 2) error = Glide.with(context)
                        .load(placeholder[1]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(leftTop, rightTop, leftBottom, rightBottom, view.getScaleType())));
            }
            RequestOptions options = new RequestOptions().transform(new GlideRoundTransform(leftTop, rightTop, leftBottom, rightBottom, view.getScaleType()));
            Glide.with(context).load(url).apply(options).thumbnail(load).error(error).dontAnimate().into(view);
        }
    }

    @Override
    public <LoadAddress> void round(Context context, LoadAddress url, ImageView view, float[] radius, final ImageLoaderCallback<ImageResult> listener, int... placeholder) {
        if (supportLoad(url)) {
            float leftTop = 0, rightTop = 0, leftBottom = 0, rightBottom = 0;
            if (radius != null && radius.length == 4) {
                leftTop = radius[0];
                rightTop = radius[1];
                leftBottom = radius[2];
                rightBottom = radius[3];
            }
            /*占位图进行圆角处理*/
            RequestBuilder<Drawable> load = null, error = null;
            if (placeholder != null && placeholder.length > 0 && placeholder.length < 3) {
                load = Glide.with(context)
                        .load(placeholder[0]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(leftTop, rightTop, leftBottom, rightBottom, view.getScaleType())));
                if (placeholder.length == 2) error = Glide.with(context)
                        .load(placeholder[1]).dontAnimate()
                        .apply(new RequestOptions().transform(new GlideRoundTransform(leftTop, rightTop, leftBottom, rightBottom, view.getScaleType())));
            }
            RequestOptions options = new RequestOptions().transform(new GlideRoundTransform(leftTop, rightTop, leftBottom, rightBottom, view.getScaleType()));
            Glide.with(context).load(url).apply(options).thumbnail(load).error(error).addListener(listener == null ? null : new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    listener.onFailure();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    listener.onSuccess();
                    return false;
                }
            }).dontAnimate().into(view);
        }
    }

    @Override
    public <LoadAddress> boolean supportLoad(LoadAddress url) {
        return url instanceof Bitmap || url instanceof Drawable || url instanceof String || url instanceof Uri || url instanceof File || url instanceof Integer || url instanceof byte[];
    }

    public void clear(Context context, @NonNull View view) {
        Glide.with(context).clear(view);
    }

    public void pauseRequests(Context context) {
        Glide.with(context).pauseRequests();
    }

    public void pauseAllRequests(Context context) {
        Glide.with(context).pauseAllRequests();
    }

    public void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }
}
