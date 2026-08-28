package com.thepurpleweb.purplelauncher

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thepurpleweb.purplelauncher.apps.AppRepository
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter
import com.thepurpleweb.purplelauncher.dock.DockAdapter
import com.thepurpleweb.purplelauncher.dock.DockEditorActivity
import com.thepurpleweb.purplelauncher.dock.DockRepository
import com.thepurpleweb.purplelauncher.drawer.AppDrawerActivity
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var appRepository: AppRepository
    private lateinit var dockRepository: DockRepository

    private lateinit var profileLabel: TextView
    private lateinit var appGrid: GridView
    private lateinit var dockGrid: GridView

    private lateinit var dockAdapter: DockAdapter

    private var downY = 0f
    private var downX = 0f

    private val curatedPackageHints = listOf(
        "com.android.dialer",
        "com.google.android.dialer",
        "com.android.mms",
        "com.google.android.apps.messaging",
        "com.android.camera",
        "com.google.android.GoogleCamera",
        "com.android.settings",
        "com.android.chrome",
        "org.mozilla.firefox",
        "com.android.vending"
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))

                File(
                    getExternalFilesDir(null),
                    "crash_log.txt"
                ).writeText(sw.toString())

            } catch (_: Exception) {
            }

            android.os.Process.killProcess(
                android.os.Process.myPid()
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileEngine =
            ProfileEngine(applicationContext)

        appRepository =
            AppRepository(applicationContext)

        dockRepository =
            DockRepository(applicationContext)

        profileLabel =
            findViewById(R.id.profile_label)

        appGrid =
            findViewById(R.id.app_grid)

        dockGrid =
            findViewById(R.id.dock_grid)

        profileLabel.setOnClickListener {
            profileEngine.cycleNext()
        }

        setupDock()
        loadHomeApps()

        lifecycleScope.launch {
            profileEngine.current.collect { profile ->
                profileLabel.text =
                    "${profile.displayName}  ·  swipe up for apps"
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appRepository.invalidateCache()

        dockRepository.removeMissingApps()

        loadHomeApps()
        loadDock()
    }

    private fun loadHomeApps() {

        val allApps =
            appRepository.getAllLaunchableApps()

        val curatedApps =
            allApps
                .filter {
                    it.packageName in curatedPackageHints
                }
                .ifEmpty {
                    allApps.take(8)
                }

        appGrid.adapter =
            HomeAppAdapter(
                this,
                curatedApps
            ) { app ->
                appRepository.launchApp(
                    app.packageName
                )
            }
    }

    private fun setupDock() {

        dockAdapter =
            DockAdapter(
                this,
                emptyList(),

                onAppClick = { app ->
                    appRepository.launchApp(
                        app.packageName
                    )
                },

                onAppLongClick = {
                    openDockEditor()
                    true
                }
            )

        dockGrid.adapter =
            dockAdapter
    }

    private fun loadDock() {

        val slotCount =
            dockRepository.getSlotCount()

        dockGrid.numColumns =
            slotCount

        dockAdapter.updateApps(
            dockRepository.getDockApps()
        )
    }

    private fun openDockEditor() {

        startActivity(
            Intent(
                this,
                DockEditorActivity::class.java
            )
        )
    }

    private fun openDrawer() {

        startActivity(
            Intent(
                this,
                AppDrawerActivity::class.java
            )
        )
    }

    override fun dispatchTouchEvent(
        event: MotionEvent
    ): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }

            MotionEvent.ACTION_UP -> {

                val deltaX =
                    event.x - downX

                val deltaY =
                    event.y - downY

                val verticalSwipe =
                    abs(deltaY) > 120 &&
                    abs(deltaY) > abs(deltaX)

                if (
                    verticalSwipe &&
                    deltaY < 0
                ) {
                    openDrawer()
                    return true
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }
}
