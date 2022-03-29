package com.cloudling.imageloader

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