package com.thepurpleweb.purplelauncher

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Purple Launcher\nPhase 0 - pipeline works"
        textView.textSize = 20f
        setContentView(textView)
    }
}
