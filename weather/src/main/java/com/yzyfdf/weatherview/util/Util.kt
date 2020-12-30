package com.yzyfdf.weatherview.util

import android.content.Context
import android.content.res.Resources

/**
 * @author : SJJ
 * @date   : 2020/12/24-11:28
 * desc    :
 */
object Util {

    fun dp2px(dpValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return dpValue * scale
    }

    fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return spValue * fontScale
    }
}