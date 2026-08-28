package com.thepurpleweb.purplelauncher

import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val file = File(getExternalFilesDir(null), "crash_log.txt")
                file.writeText(sw.toString())
            } catch (e: Exception) {
                // ignore, nothing more we can do
            }
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        super.onCreate(savedInstanceState)

        profileEngine = ProfileEngine(applicationContext)

        textView = TextView(this)
        textView.textSize = 20f
        textView.setPadding(40, 200, 40, 40)
        setContentView(textView)

        textView.setOnClickListener {
            profileEngine.cycleNext()
        }

        lifecycleScope.launch {
            profileEngine.current.collect { profile ->
                textView.text = "Purple Launcher\n\nCurrent profile: ${profile.displayName}\n\n(tap to cycle)"
            }
        }
    }
}
