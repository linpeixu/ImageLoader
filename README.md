# ImageLoader

Android快速集成图片加载（支持更换图片加载库）

先看接入步骤：
Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```kotlin
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```kotlin
    dependencies {
	        implementation 'com.gitlab.linpeixu:imageloader:kt-1.0.2'
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

```kotlin
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
```
##### 4.2 图片加载策略

```kotlin
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
```
##### 4.3 Glide图片加载策略

```kotlin
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
```
##### 4.4 Glide圆角transform（跟随ImageView的scaleType缩放）

```kotlin
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
```
##### 4.5 图片加载回调

```kotlin
/**
 * 描述: 图片加载回调
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/26
 */
interface ImageLoaderCallbackKt<T> {
    fun onSuccess(vararg result: T)
    fun onFailure(vararg result: T)
    fun onCancel(vararg result: T)
}
```

```kotlin
/**
 * 描述: 用于图片加载监听回调传值（可根据图片加载框架的加载回调的参数来设置此类具体包含的属性和方法）
 * 联系: 1966353889@qq.com
 * 日期: 2022/3/26
 */
class ImageResultKt constructor(bitmap: Bitmap?) {
    var bitmap: Bitmap? = null

    init {
        this.bitmap = bitmap
    }
}
```
##### 4.6 使用
这样，我们在需要加载图片的地方调用ImageLoader就可以了，如

```kotlin
ImageLoaderKt.Companion.get().load(context, imageUrl, view);
```
为了兼容从非kotlin版本无感切换到kotlin版本，也可以通过以下方法使用

```kotlin
ImageLoader.getInstance().load(context, imageUrl, view);
```
ImageLoaderKt也提供了多种加载图片的方法，注释都写得很清楚，就不展开说明了。
##### 4.6 更换图片加载库
如果我们需要更换图片加载库的时候只需要更改ImageLoaderKt即可，即自定义继承BaseImageLoaderStrategyKt的图片实现策略类，重写各种图片加载的方法，然后在ImageLoaderKt的构造方法调用setImageLoaderStrategy(BaseImageLoaderStrategyKt strategy)设置图片加载策略就可以了。

自定义BaseImageLoaderStrategyKt可参考上边的GlideImageLoaderStrategyKt。

## 结尾
是不是很方便快捷，希望本文可以帮助到您，也希望各位不吝赐教，提出您在使用中的宝贵意见，谢谢。
