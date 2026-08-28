package com.thepurpleweb.purplelauncher

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.thepurpleweb.purplelauncher.search.SearchActivity
import com.thepurpleweb.purplelauncher.settings.SettingsActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var appRepository: AppRepository
    private lateinit var dockRepository: DockRepository
    private lateinit var gestureRepository: GestureRepository

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

        gestureRepository =
            GestureRepository(applicationContext)

        profileLabel =
            findViewById(R.id.profile_label)

        appGrid =
            findViewById(R.id.app_grid)

        dockGrid =
            findViewById(R.id.dock_grid)

        setupHome()
        setupDock()
        setupGestures()
        observeProfile()
    }

    override fun onResume() {
        super.onResume()

        if (::dockRepository.isInitialized) {
            refreshDock()
        }
    }

    private fun setupHome() {

        profileLabel.setOnClickListener {
            profileEngine.cycleNext()
        }

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

        dockRepository.ensureDefaultDock()
        dockRepository.removeMissingApps()

        dockAdapter =
            DockAdapter(
                this,
                dockRepository.getDockApps(),
                onAppClick = { app ->
                    appRepository.launchApp(
                        app.packageName
                    )
                },
                onAppLongClick = {
                    startActivity(
                        Intent(
                            this,
                            DockEditorActivity::class.java
                        )
                    )

                    true
                }
            )

        dockGrid.adapter =
            dockAdapter
    }

    private fun refreshDock() {

        if (!::dockAdapter.isInitialized) {
            return
        }

        dockRepository.removeMissingApps()

        dockAdapter.updateApps(
            dockRepository.getDockApps()
        )
    }

    private fun setupGestures() {

        val rootView =
            findViewById<View>(
                R.id.home_root
            )

        val detector =
            LauncherGestureDetector(
                gestureRepository
            ) { action ->
                handleGestureAction(
                    action
                )
            }

        rootView.setOnTouchListener { _, event ->

            detector.onTouchEvent(
                event
            )

            true
        }
    }

    private fun handleGestureAction(
        action: GestureAction
    ) {

        when (action) {

            GestureAction.NONE -> {
                // No action.
            }

            GestureAction.OPEN_APP_DRAWER -> {
                startActivity(
                    Intent(
                        this,
                        AppDrawerActivity::class.java
                    )
                )
            }

            GestureAction.OPEN_SEARCH -> {
                startActivity(
                    Intent(
                        this,
                        SearchActivity::class.java
                    )
                )
            }

            GestureAction.OPEN_SETTINGS -> {
                startActivity(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
            }

            GestureAction.SWITCH_PROFILE -> {
                profileEngine.cycleNext()
            }

            GestureAction.OPEN_NOTIFICATIONS -> {
                openNotifications()
            }
        }
    }

    private fun openNotifications() {

        try {
            startActivity(
                Intent(
                    "android.settings.NOTIFICATION_LISTENER_SETTINGS"
                )
            )
        } catch (_: Exception) {
            // Optional Android capability.
        }
    }

    private fun observeProfile() {

        lifecycleScope.launch {

            profileEngine.current.collect { profile ->

                profileLabel.text =
                    profile.displayName
            }
        }
    }
}
