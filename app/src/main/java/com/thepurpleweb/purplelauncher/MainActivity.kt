package com.thepurpleweb.purplelauncher

import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thepurpleweb.purplelauncher.apps.AppRepository
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var appRepository: AppRepository
    private lateinit var profileLabel: TextView
    private lateinit var appGrid: GridView

    // Curated set for Calm home screen — refine later based on usage
    private val curatedPackageHints = listOf(
        "com.android.dialer", "com.google.android.dialer",
        "com.android.mms", "com.google.android.apps.messaging",
        "com.android.camera", "com.google.android.GoogleCamera",
        "com.android.settings",
        "com.android.chrome", "org.mozilla.firefox",
        "com.android.vending"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                File(getExternalFilesDir(null), "crash_log.txt").writeText(sw.toString())
            } catch (e: Exception) { }
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileEngine = ProfileEngine(applicationContext)
        appRepository = AppRepository(applicationContext)

        profileLabel = findViewById(R.id.profile_label)
        appGrid = findViewById(R.id.app_grid)

        profileLabel.setOnClickListener {
            profileEngine.cycleNext()
        }

        val allApps = appRepository.getAllLaunchableApps()
        val curatedApps = allApps.filter { it.packageName in curatedPackageHints }
            .ifEmpty { allApps.take(8) } // fallback if none of the hints match this device

        appGrid.adapter = HomeAppAdapter(this, curatedApps) { app ->
            appRepository.launchApp(app.packageName)
        }

        lifecycleScope.launch {
            profileEngine.current.collect { profile ->
                profileLabel.text = "${profile.displayName}  ·  tap to cycle"
            }
        }
    }
}
