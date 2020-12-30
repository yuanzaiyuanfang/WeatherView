package com.yzyfdf.weatherview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.yzyfdf.weatherview.util.Util

/**
 * @author : SJJ
 * @date   : 2020/12/29-09:57
 * desc    :
 */
abstract class BaseView {

    lateinit var context: Context

    val tempPaint = Paint()

    init {
        tempPaint.apply {
            color = Color.parseColor("#333333")
            textSize = Util.sp2px(14f)
            strokeCap = Paint.Cap.ROUND
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }

    abstract fun drawTop(
        canvas: Canvas,
        data: Any,
        start: Float,
        top: Float,
        width: Float,
        height: Float
    )

    abstract fun drawBottom(
        canvas: Canvas,
        data: Any,
        start: Float,
        top: Float,
        width: Float,
        height: Float
    )

}