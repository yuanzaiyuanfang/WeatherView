package com.yzyfdf.weathersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
        val extra = listOf(
            WeatherBean("周一", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周二", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周三", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周四", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周五", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周六", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周日", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周一", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周二", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周三", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周四", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周五", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周六", "晴", "多云", R.mipmap.p20, R.mipmap.p55),
            WeatherBean("周日", "晴", "多云", R.mipmap.p20, R.mipmap.p55)
        )
        val dataList: MutableList<List<Int>> = mutableListOf(
            listOf(12, 9, 8, 11, 14, 13, 15, 11, 14, 13, 11, 14, 13, 15),
            listOf(5, 7, 6, 7, 5, 7, 6, 7, 6, 8, 6, 7, 6, 8)
        )

        weather_view.apply {
            setData(*dataList.toTypedArray())
            setExtraDatas(extra)
            setTextGravity(listOf(false, true))
            refresh()
        }
    }
}
