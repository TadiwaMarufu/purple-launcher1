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
import com.thepurpleweb.purplelauncher.gestures.GestureAction
import com.thepurpleweb.purplelauncher.gestures.GestureRepository
import com.thepurpleweb.purplelauncher.gestures.LauncherGestureDetector
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var appRepository: AppRepository
    private lateinit var dockRepository: DockRepository
    private lateinit var gestureRepository: GestureRepository
    private lateinit var gestureDetector: LauncherGestureDetector

    private lateinit var profileLabel: TextView
    private lateinit var appGrid: GridView
    private lateinit var dockGrid: GridView
    private lateinit var dockAdapter: DockAdapter

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

                throwable.printStackTrace(
                    PrintWriter(sw)
                )

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

        setContentView(
            R.layout.activity_main
        )

        profileEngine =
            ProfileEngine(
                applicationContext
            )

        appRepository =
            AppRepository(
                applicationContext
            )

        dockRepository =
            DockRepository(
                applicationContext
            )

        gestureRepository =
            GestureRepository(
                applicationContext
            )

        gestureDetector =
            LauncherGestureDetector(
                gestureRepository
            ) { action ->
                handleGestureAction(action)
            }

        profileLabel =
            findViewById(
                R.id.profile_label
            )

        appGrid =
            findViewById(
                R.id.app_grid
            )

        dockGrid =
            findViewById(
                R.id.dock_grid
            )

        dockRepository.ensureDefaultDock()

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

        dockRepository.removeMissingApps()
        dockRepository.ensureDefaultDock()

        loadHomeApps()
        loadDock()
    }

    override fun dispatchTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            gestureDetector.onTouchEvent(event)
        ) {
            return true
        }

        return super.dispatchTouchEvent(event)
    }

    private fun handleGestureAction(
        action: GestureAction
    ) {

        when (action) {

            GestureAction.OPEN_APP_DRAWER ->
                openDrawer()

            GestureAction.OPEN_SETTINGS ->
                openLauncherSettings()

            GestureAction.SWITCH_PROFILE ->
                profileEngine.cycleNext()

            GestureAction.OPEN_SEARCH ->
                openSearch()

            GestureAction.OPEN_NOTIFICATIONS ->
                openNotifications()

            GestureAction.NONE -> Unit
        }
    }

    private fun loadHomeApps() {

        val allApps =
            appRepository
                .getAllLaunchableApps()

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

        loadDock()
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

    private fun openDrawer() {

        startActivity(
            Intent(
                this,
                AppDrawerActivity::class.java
            )
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

    private fun openLauncherSettings() {
        // Settings screen will be implemented in P0.
    }

    private fun openSearch() {
        // Search screen will be implemented in P0.
    }

    private fun openNotifications() {
        // Notification presentation will be implemented later.
    }
}
