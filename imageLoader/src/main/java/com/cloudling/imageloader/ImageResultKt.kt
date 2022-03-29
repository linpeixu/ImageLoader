package com.cloudling.imageloader

import android.graphics.Bitmap

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