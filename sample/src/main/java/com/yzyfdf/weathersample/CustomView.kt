package com.yzyfdf.weathersample

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.SparseArray
import androidx.core.content.ContextCompat
import com.yzyfdf.weatherview.R
import com.yzyfdf.weatherview.util.Util
import com.yzyfdf.weatherview.view.BaseView

/**
 * @author : SJJ
 * @date   : 2020/12/29-10:12
 * desc    :
 */
class CustomView : BaseView() {

    private val rectSrc = Rect()
    private val rectDst = RectF()
    private val bitmapArray = SparseArray<Bitmap>()

    override fun drawTop(
        canvas: Canvas,
        data: Any,
        start: Float,
        top: Float,
        width: Float,
        height: Float
    ) {
        val weatherBean = data as WeatherBean
        canvas.drawText(weatherBean.week, start + width / 2, top + Util.dp2px(20f), tempPaint)
        canvas.drawText(weatherBean.weather1, start + width / 2, top + Util.dp2px(80f), tempPaint)
        drawIcon(canvas, weatherBean.icon1, start + width / 2, top)
    }

    override fun drawBottom(
        canvas: Canvas,
        data: Any,
        start: Float,
        top: Float,
        width: Float,
        height: Float
    ) {
        val weatherBean = data as WeatherBean
        canvas.drawText(weatherBean.weather2, start + width / 2, top + Util.dp2px(20f), tempPaint)
        drawIcon(canvas, weatherBean.icon2, start + width / 2, top)
    }

    private fun drawIcon(canvas: Canvas, icon: Int, x: Float, y: Float) {
        if (bitmapArray.indexOfKey(icon) < 0) {
            bitmapArray.put(
                icon,
                (ContextCompat.getDrawable(context, icon) as BitmapDrawable).bitmap
            )
        }
        val bitmap = bitmapArray[icon]

        rectSrc.apply {
            this.left = 0
            this.top = 0
            this.right = bitmap.width
            this.bottom = bitmap.height
        }
        rectDst.apply {
            this.left = x - Util.dp2px(12f)
            this.top = y + Util.dp2px(36f)
            this.right = x + Util.dp2px(12f)
            this.bottom = y + Util.dp2px(60f)
        }
        canvas.drawBitmap(bitmap, rectSrc, rectDst, null)
    }
}