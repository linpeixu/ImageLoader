# ImageLoader

Android快速集成图片加载（支持更换图片加载库）

先看接入步骤：
Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```java
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```java
    dependencies {
	        implementation 'com.github.linpeixu:ImageLoader:1.0.1'
            //或者implementation 'com.gitlab.linpeixu:imageloader:1.0.1'
	}
```

## 前言
看这篇文章的或多或少都是平时开发有涉及到图片加载的，你可能是新手技术人员，也可能摸爬滚打多年的老司机，不管你是想知道怎么加载图片，还是想参考下如何快速集成，本文尽量都分享下自己的心得，如果能帮助到你的话，也算是自己的一份沉淀。话不多说，直接进入正文。

#### 1.为什么讲图片加载的集成呢？
其实我们都知道，图片的加载无非就是将图片（图片源可以有多种，如网络图片，本地图片等）成功地显示在我们的图片容器上（如ImageView），本地资源图片很简单，可能直接setBackground就可以达到效果，但如果是网络图片呢，写个线程将图片下载下来？然后本地存储下来？再加载出来？那么，是不是要涉及到下载，存储甚至还要考虑缓存等问题，线程的维护等等一些繁琐又容易出错的问题，如果需要显示图片的地方非常多呢？

有没有办法可以帮我们省去做重复性的工作呢？答案当然是有，就是代码的封装，把核心的逻辑封装成工具方便调用，也就是把图片下载到存储的过程封装起来，甚至考虑将图片的加载也包含进去，就不用每次都手动去将下载好的图片设置到图片容器上。

#### 2.确定实现思路
对于新手或有钻研精神的你，可以选择自己造锅踩坑，也可以跟随绝大多数人的步伐，使用开源的图片加载库（这里向开源致敬），android的图片加载库有很多，如Gilde、Picasso，Fresco等，经历了大众的考验，性能和稳定性不用我们考虑，可以专心实现我们的业务。以下，我们拿Gilde作为我们的主要使用对象。

#### 3.使用场景
业务的复杂往往要求图片的使用场景不局限于将图片加载出来就可以，有时候还需要诸如圆角（上下左右，或仅右上左下圆角，或不同方向圆角大小不一样），模糊，加载缩略图等等。上边提到的开源图片加载库可以很好地帮我们实现，具体的使用大家可以自行搜索使用教程，还是比较简单明了的。

#### 4.高级进阶
这里是本文的重中之重，前面讲的其实都是为了引出对图片加载的思考。拿Gilde来说，看下我们加载一张图片需要怎么做

```java
 Glide.with(context).load(imageUrl).dontAnimate().into(imageView);
```
是不是很简单，Glide已经为我们提供了方便的调用api，在我们的应用中需要加载图片的时候into一下就可以了，接下来整个图片的从下载到存储到缓存到显示，甚至显示占位图，都交给Glide处理就好了。

正在你高兴的时候，哪一天设计出了新的图片显示效果，可能Gilde已满足不了了，当然这里不是说Glide不行，至少在我用起来还是满足现在的所有需求的，我只是说明了需求的多样性需要我们的技术也跟上步伐，或者技术大佬出于各种原因，要求我们用另外一种图片加载库。

换就换呗。不，你可别。换当然可以换，但你想想，如果我们的应用很多地方都用了之前的Glide库加载图片，那岂不是换成其它的就要改很多地方，我们能忍受这种复制粘贴删除这种累心的没技术含量的工作？先不说其他，首先这种替换的做法是不优雅的。

能不能有一种方法，能快速响应类似这种需求的变化，或者说如果下次又需要更换图片加载库的时候怎么办。终于到我们本篇文章的主题了。以下我分享下个人的写法
##### 4.1 图片加载工具类

```java
/**
 * 作者：lpx on 2019/10/29 14:07
 * Email : 1966353889@qq.com
 * Describe:图片加载工具类（单例）
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
```
##### 4.2 图片加载策略

```java
/**
 * 作者：lpx on 2019/10/29 14:07
 * Email : 1966353889@qq.com
 * Describe:图片加载策略
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
```
##### 4.3 Glide图片加载策略

```java
/**
 * 作者：lpx on 2019/10/29 14:07
 * Email : 1966353889@qq.com
 * Describe:Glide图片加载策略
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

```
##### 4.4 Glide圆角transform（跟随ImageView的scaleType缩放）

```java
/**
 * 作者：lpx on 2019/10/29 14:07
 * Email : 1966353889@qq.com
 * Describe:Glide圆角transform（跟随ImageView的scaleType缩放）
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

```
##### 4.5 图片加载回调

```java
/**
 * 作者：lpx on 2019/10/29 14:07
 * Email : 196635389@qq.com
 * Describe:图片加载回调
 */
public interface ImageLoaderCallback<T> {
    void onSuccess(T... result);

    void onFailure(T... result);

    void onCancel(T... result);
}
```

```java
/**
 * 作者：lpx on 2019/10/29 10:50
 * Email : 196635389@qq.com
 * Describe:用于图片加载监听回调传值（可根据图片加载框架的加载回调的参数来设置此类具体包含的属性和方法）
 */
public class ImageResult {
}

```
##### 4.6 使用
这样，我们在需要加载图片的地方调用ImageLoader就可以了，如

```java
ImageLoader.getInstance().load(context,imageUrl,view);
```
ImageLoader也提供了多种加载图片的方法，注释都写得很清楚，就不展开说明了。
##### 4.6 更换图片加载库
如果我们需要更换图片加载库的时候只需要更改ImageLoader即可，即自定义继承BaseImageLoaderStrategy的图片实现策略类，重写各种图片加载的方法，然后在ImageLoader的构造方法调用setImageLoaderStrategy(BaseImageLoaderStrategy strategy)设置图片加载策略就可以了。

自定义BaseImageLoaderStrategy可参考上边的GlideImageLoaderStrategy。

## 结尾
是不是很方便快捷，希望本文可以帮助到您，也希望各位不吝赐教，提出您在使用中的宝贵意见，谢谢。
