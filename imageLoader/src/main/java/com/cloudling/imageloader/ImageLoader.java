package com.cloudling.imageloader;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * 描述: 图片加载工具类（单例）
 * 联系: 1966353889@qq.com
 * 日期: 2019/10/29
 */
public final class ImageLoader {
    /*由于instance = new Singleton()，这并非是一个原子操作，事实上在 JVM 中这句话大概做了下面 3 件事情。
    1.给 instance 分配内存
    2.调用 Singleton 的构造函数来初始化成员变量
    3.将instance对象指向分配的内存空间（执行完这步instance就为非null了）
    但是在 JVM 的即时编译器中存在指令重排序的优化。也就是说上面的第二步和第三步的顺序是不能保证的，最终的执行顺序可能是 1-2-3 也可能是 1-3-2。如果是后者，则在 3 执行完毕、2 未执行之前，被线程二抢占了，这时 instance 已经是非 null 了（但却没有初始化），所以线程二会直接返回 instance，然后使用，然后顺理成章地报错。
    我们只需要将 instance 变量声明成 volatile 就可以了。*/
    private static volatile ImageLoader mInstance;
    private BaseImageLoaderStrategy mStrategy;

    private ImageLoader() {
        /*初始化时设置图片加载策略为Glide*/
        setImageLoaderStrategy(new GlideImageLoaderStrategy());
    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader();
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置图片加载策略（根据使用的图片加载框架实现对应的BaseImageLoaderStrategy并在这里设置）
     */
    private void setImageLoaderStrategy(BaseImageLoaderStrategy strategy) {
        mStrategy = strategy;
    }

    /**
     * 加载图片
     *
     * @param context     上下文
     * @param url         图片地址（泛型）
     * @param view        图片宿主
     * @param placeholder 占位图（加载时或加载错误占位图），可选参数，第一个元素代表加载时占位图，第二个元素代表加载错误占位图
     */
    public <LoadAddress, V extends View> void load(Context context, LoadAddress url, V view, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.load(context, url, view, placeholder);
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
    public <LoadAddress, V extends View, L> void load(Context context, LoadAddress url, V view, ImageLoaderCallback<L> listener, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.load(context, url, view, listener, placeholder);
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
    public <V extends View, LoadAddress> void circle(Context context, LoadAddress url, V view, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.circle(context, url, view, placeholder);
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
    public <LoadAddress, V extends View, L> void circle(Context context, LoadAddress url, V view, ImageLoaderCallback<L> listener, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.circle(context, url, view, listener, placeholder);
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
    public <LoadAddress, V extends View> void round(Context context, LoadAddress url, V view, float radius, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.round(context, url, view, radius, placeholder);
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
    public <LoadAddress, V extends View, L> void round(Context context, LoadAddress url, V view, float radius, ImageLoaderCallback<L> listener, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.round(context, url, view, radius, listener, placeholder);
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
    public <LoadAddress, V extends View> void round(Context context, LoadAddress url, V view, float[] radius, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.round(context, url, view, radius, placeholder);
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
    public <LoadAddress, V extends View, L> void round(Context context, LoadAddress url, V view, float[] radius, ImageLoaderCallback<L> listener, int... placeholder) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            mStrategy.round(context, url, view, radius, listener, placeholder);
        }
    }

    public void clear(Context context, @NonNull View view) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            if (mStrategy instanceof GlideImageLoaderStrategy) {
                ((GlideImageLoaderStrategy) mStrategy).clear(context, view);
            }
        }
    }

    public void pauseRequests(Context context) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            if (mStrategy instanceof GlideImageLoaderStrategy) {
                ((GlideImageLoaderStrategy) mStrategy).pauseRequests(context);
            }
        }
    }

    public void pauseAllRequests(Context context) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            if (mStrategy instanceof GlideImageLoaderStrategy) {
                ((GlideImageLoaderStrategy) mStrategy).pauseAllRequests(context);
            }
        }
    }

    public void resumeRequests(Context context) {
        if (mStrategy == null) {
            throw new NullPointerException("you should invoke setImageLoaderStrategy first");
        } else {
            if (mStrategy instanceof GlideImageLoaderStrategy) {
                ((GlideImageLoaderStrategy) mStrategy).resumeRequests(context);
            }
        }
    }
}
