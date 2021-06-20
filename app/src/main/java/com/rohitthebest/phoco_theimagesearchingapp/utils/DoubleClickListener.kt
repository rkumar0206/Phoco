package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.view.View

abstract class DoubleClickListener : View.OnClickListener {

    private var lastClickTime = 0L

    override fun onClick(v: View?) {

        val clickTime = System.currentTimeMillis()

        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {

            onDoubleClick(v)
            lastClickTime = 0
        }

        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)

    private companion object {

        private const val DOUBLE_CLICK_TIME_DELTA = 300L //milliseconds
    }
}