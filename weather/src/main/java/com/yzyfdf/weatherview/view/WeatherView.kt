package com.yzyfdf.weatherview.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.yzyfdf.weatherview.R
import com.yzyfdf.weatherview.util.Util

/**
 * @author : SJJ
 * @date   : 2020/12/24-11:19
 * desc    :
 */
class WeatherView @JvmOverloads constructor(
    @NonNull context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val DEFAULT_TEXT_SIZE = 14f
    private val DEFAULT_TEXT_COLOR = Color.parseColor("#333333")
    private val DEFAULT_LINE_WIDTH = 8f
    private val DEFAULT_LINE_COLOR = Color.parseColor("#CCCCCC")


    private var tempTextEnable = true
    private var tempTextColor = DEFAULT_TEXT_COLOR
    private var tempTextHeight = 50f//文字高度
    private var tempTextSize = Util.sp2px(DEFAULT_TEXT_SIZE)
    private var tempTextBl = 0f//文字baseline
    private val tempTextPaint = Paint()

    private var lineColor = DEFAULT_LINE_COLOR
    private var lineWidth = DEFAULT_LINE_WIDTH
    private val linePaint = Paint()

    private var hasCircle = true//圆点中间是否有白点
    private val circleColor = Color.WHITE
    private val circleRadius = Util.dp2px(2.5f)//圆半径
    private val circlePaint = Paint()

    private var tempLayoutHeight = 800f//折线部分高度
    private var itemWidth = 120f//每天的宽度
    private var itemSize = 0//天数
    private var tempHeight = 0f//每一度温度占的高度
    private var tempMin = 5//整个图中最低
    private var tempRange = 10//整个图中最大范围


    private var topLayoutHeight = 0f//顶部高度
    private var bottomLayoutHeight = 0f//顶部高度
    private var customView: BaseView? = null

    private val path = Path()

    private val dataList: MutableList<List<Int>> = mutableListOf(
        listOf(12, 9, 8, 11, 14, 13, 15, 7),
        listOf(5, 7, 6, 7, 5, 7, 6, 4)
    )//全部数据
    private val extraList: MutableList<Any> = mutableListOf()//附加数据
    private val cpDatas: MutableList<List<PointF>> = mutableListOf()//控制点
    private var textBottoms: List<Boolean> = arrayListOf(true, true)//温度标识在曲线下方


    init {
        attrs?.apply { initStyle(context, this) }

        tempTextPaint.apply {
            color = tempTextColor
            textSize = tempTextSize
            strokeCap = Paint.Cap.ROUND
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        linePaint.apply {
            color = lineColor
            strokeWidth = lineWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        circlePaint.apply {
            color = circleColor
            style = Paint.Style.FILL
        }
    }

    private fun initStyle(context: Context, attrs: AttributeSet) {
        val osa = context.obtainStyledAttributes(
            attrs,
            R.styleable.WeatherView
        )

        //温度数字
        tempTextEnable = osa.getBoolean(R.styleable.WeatherView_wv_temp_enable, true)
        tempTextSize = osa.getDimensionPixelSize(
            R.styleable.WeatherView_wv_temp_size,
            tempTextSize.toInt()
        ).toFloat()
        tempTextColor = osa.getColor(R.styleable.WeatherView_wv_temp_color, DEFAULT_TEXT_COLOR)

        //曲线
        lineWidth = osa.getDimension(
            R.styleable.WeatherView_wv_line_width,
            DEFAULT_LINE_WIDTH
        )
        lineColor = osa.getColor(R.styleable.WeatherView_wv_line_color, DEFAULT_LINE_COLOR)
        hasCircle = osa.getBoolean(R.styleable.WeatherView_wv_line_has_circle, hasCircle)

        //高度
        itemWidth = osa.getDimensionPixelSize(
            R.styleable.WeatherView_wv_item_width,
            itemWidth.toInt()
        ).toFloat()
        tempLayoutHeight = osa.getDimensionPixelSize(
            R.styleable.WeatherView_wv_layout_temp_height,
            tempLayoutHeight.toInt()
        ).toFloat()
        topLayoutHeight = osa.getDimensionPixelSize(
            R.styleable.WeatherView_wv_layout_top_height,
            topLayoutHeight.toInt()
        ).toFloat()
        bottomLayoutHeight = osa.getDimensionPixelSize(
            R.styleable.WeatherView_wv_layout_bottom_height,
            bottomLayoutHeight.toInt()
        ).toFloat()


        customView = osa.getString(R.styleable.WeatherView_wv_custom_view)
            ?.let { Class.forName(it).newInstance() as? BaseView }
        customView?.context = context

        osa.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(
                itemWidth.toInt() * (dataList.firstOrNull()?.size ?: 0),
                MeasureSpec.EXACTLY
            ), MeasureSpec.makeMeasureSpec(
                (tempLayoutHeight + topLayoutHeight + bottomLayoutHeight).toInt(),
                MeasureSpec.EXACTLY
            )
        )

        tempTextPaint.fontMetrics.apply {
            val fh = bottom - top
            tempTextHeight = fh * 1.3f
            tempTextBl = tempTextHeight - (tempTextHeight - fh) / 2 - bottom
        }

        tempHeight = (tempLayoutHeight - 2 * tempTextHeight) / tempRange//每一度温度占的高度,要去掉上下文字


        setControlPoints()
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            dataList.forEachIndexed { index, list ->
                drawTemp(this, list, index)
            }

            dataList.firstOrNull()?.forEachIndexed { index, i ->
                drawExtra(this, index)
            }
        }
    }

    private fun drawExtra(canvas: Canvas, index: Int) {
        if (index >= extraList.size) {
            return//防下标越界
        }
        customView?.apply {
            if (topLayoutHeight > 0) {
                drawTop(
                    canvas,
                    extraList[index],
                    itemWidth * index,
                    0f,
                    itemWidth,
                    topLayoutHeight
                )
            }
            if (bottomLayoutHeight > 0) {
                drawBottom(
                    canvas,
                    extraList[index],
                    itemWidth * index,
                    topLayoutHeight + tempLayoutHeight,
                    itemWidth,
                    bottomLayoutHeight
                )
            }
        }
    }

    /**
     * 画天气
     */
    private fun drawTemp(canvas: Canvas, list: List<Int>, lineIndex: Int) {
        val offset = if (textBottoms[lineIndex]) 0f else tempTextHeight//文字偏移

        drawLine(canvas, list, cpDatas[lineIndex])

        //画圆点和温度数字
        list.forEachIndexed { index, temp ->
            val x = getPointX(index)
            val y = getPointY(temp)

            if (!hasCircle) circlePaint.color = linePaint.color
            canvas.drawCircle(x, y, circleRadius, circlePaint)
            canvas.drawCircle(x, y, circleRadius, linePaint)
            if (tempTextEnable) {
                canvas.drawText("${temp}°", x, tempTextBl + y - offset, tempTextPaint)
            }
        }
    }

    /**
     * 画曲线
     */
    private fun drawLine(canvas: Canvas, list: List<Int>, cpList: List<PointF>) {
        repeat(list.size) { index ->
            when (index) {
                0 -> {
                    path.reset()
                    path.moveTo(getPointX(index), getPointY(list[index]))
                    path.quadTo(
                        cpList[index].x,
                        cpList[index].y,
                        getPointX(index + 1),
                        getPointY(list[index + 1])
                    )
                }
                list.size - 2 -> {
                    path.quadTo(
                        cpList[cpList.size - 1].x,
                        cpList[cpList.size - 1].y,
                        getPointX(index + 1),
                        getPointY(list[index + 1])
                    )
                }
                list.size - 1 -> {
                    //最后一个点不用画线了
                }
                else -> {
                    path.cubicTo(
                        cpList[2 * index - 1].x,
                        cpList[2 * index - 1].y,
                        cpList[2 * index].x,
                        cpList[2 * index].y,
                        getPointX(index + 1),
                        getPointY(list[index + 1])
                    )
                }
            }
        }
        canvas.drawPath(path, linePaint)
    }

    /**
     * x坐标
     */
    private fun getPointX(index: Int): Float {
        return itemWidth * index + itemWidth / 2f
    }

    /**
     * y坐标
     */
    private fun getPointY(temp: Int): Float {
        return topLayoutHeight + tempTextHeight + tempHeight * (tempMin + tempRange - temp)
    }

    /**
     * 计算控制点
     */
    private fun setControlPoints() {
        cpDatas.clear()
        cpDatas.addAll(
            dataList.map { list ->
                val pList = mutableListOf<PointF>()
                for (index in 1 until list.size - 1) {
                    val p1x = getPointX(index - 1)
                    val p1y = getPointY(list[index - 1])
                    val p2x = getPointX(index)
                    val p2y = getPointY(list[index])
                    val p3x = getPointX(index + 1)
                    val p3y = getPointY(list[index + 1])

                    val p4x = (p1x + p2x) / 2f
                    val p4y = (p1y + p2y) / 2f
                    val p5x = (p2x + p3x) / 2f
                    val p5y = (p2y + p3y) / 2f

                    val p6x = (p4x + p5x) / 2f
                    val p6y = (p4y + p5y) / 2f

                    val p7x = p2x - p6x + p4x
                    val p7y = p2y - p6y + p4y
                    val p8x = p2x - p6x + p5x
                    val p8y = p2y - p6y + p5y

                    pList.add(PointF(p7x, p7y))
                    pList.add(PointF(p8x, p8y))
                }
                pList
            }
        )
    }

    fun setData(vararg lists: List<Int>) {
        val size = lists.map { it.size }.distinct()
        size.size.takeIf {
            it == 1
        }?.run {
            size[0].takeIf {
                it >= 3
            }?.run {
                val max = lists.mapNotNull { it.max() }.max() ?: 30
                val min = lists.mapNotNull { it.min() }.min() ?: 10
                tempMin = min
                tempRange = max - min
                itemSize = lists[0].size
                dataList.clear()
                dataList.addAll(lists.toList())
            } ?: kotlin.run {
                Toast.makeText(context, "曲线至少3个点", Toast.LENGTH_SHORT).show()
            }
        } ?: kotlin.run {
            Toast.makeText(context, "曲线数据量不一致", Toast.LENGTH_SHORT).show()
        }
    }

    fun setExtraDatas(list: List<Any>) {
        extraList.clear()
        extraList.addAll(list)
    }

    fun setTextGravity(list: List<Boolean>) {
        textBottoms = list
    }

    fun refresh() {
        invalidate()
    }

}