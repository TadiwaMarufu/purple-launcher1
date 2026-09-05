package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import android.view.Gravity
import android.widget.TextView
import java.util.Calendar

object CanvasModuleFactory {

    fun createContent(type: CanvasModuleType, context: Context): android.view.View {
        return when (type) {
            CanvasModuleType.CLOCK -> TextView(context).apply {
                text = DateFormat.format("h:mm", Calendar.getInstance()).toString()
                textSize = 42f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setBackgroundColor(Color.rgb(18, 18, 18))
            }
        }
    }
}
