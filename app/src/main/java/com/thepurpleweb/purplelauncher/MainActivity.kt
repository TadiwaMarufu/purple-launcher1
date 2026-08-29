package com.thepurpleweb.purplelauncher

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.AppRepository
import com.thepurpleweb.purplelauncher.dock.DockAdapter
import com.thepurpleweb.purplelauncher.dock.DockEditorActivity
import com.thepurpleweb.purplelauncher.dock.DockRepository
import com.thepurpleweb.purplelauncher.drawer.AppDrawerActivity
import com.thepurpleweb.purplelauncher.gestures.GestureAction
import com.thepurpleweb.purplelauncher.gestures.GestureRepository
import com.thepurpleweb.purplelauncher.gestures.LauncherGestureDetector
import com.thepurpleweb.purplelauncher.home.HomeLayoutFactory
import com.thepurpleweb.purplelauncher.intelligence.IntelligenceManager
import com.thepurpleweb.purplelauncher.notifications.NotificationCenterActivity
import com.thepurpleweb.purplelauncher.profile.Profile
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import com.thepurpleweb.purplelauncher.search.SearchActivity
import com.thepurpleweb.purplelauncher.settings.SettingsActivity
import com.thepurpleweb.purplelauncher.widgets.WidgetRepository
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

    private lateinit var widgetRepository: WidgetRepository
    private lateinit var intelligenceManager: IntelligenceManager

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var widgetContainer: FrameLayout

    private var currentWidgetView: AppWidgetHostView? = null
    private var pendingProvider: AppWidgetProviderInfo? = null

    private lateinit var profileLabel: TextView
    private lateinit var homeContentContainer: FrameLayout
    private lateinit var dockGrid: GridView
    private lateinit var dockAdapter: DockAdapter

    private var curatedApps: List<AppInfo> = emptyList()

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
            ProfileEngine.getInstance(
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

        widgetRepository =
            WidgetRepository(
                applicationContext
            )

        intelligenceManager =
            IntelligenceManager(
                applicationContext
            )

        appWidgetManager =
            AppWidgetManager.getInstance(this)

        appWidgetHost =
            AppWidgetHost(
                this,
                WidgetRepository.HOST_ID
            )

        profileLabel =
            findViewById(
                R.id.profile_label
            )

        homeContentContainer =
            findViewById(
                R.id.home_content_container
            )

        dockGrid =
            findViewById(
                R.id.dock_grid
            )

        widgetContainer =
            findViewById(
                R.id.widget_container
            )

        /*
         * Dock, gestures, widgets, and intelligence
         * initialize independently from the app list.
         */
        setupDock()
        setupGestures()
        setupWidgetArea()

        profileLabel.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    com.thepurpleweb.purplelauncher.profile.ProfilesActivity::class.java
                )
            )
        }

        loadCuratedAppsAsync()
        observeProfile()
    }

    override fun onStart() {
        super.onStart()

        appWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()

        appWidgetHost.stopListening()
    }

    override fun onResume() {
        super.onResume()

        if (::dockRepository.isInitialized) {
            refreshDock()
        }
    }

    /*
     * IMPORTANT:
     *
     * Gesture detection is performed at Activity level.
     *
     * Do not move this back to a root-view OnTouchListener.
     * The home screen contains clickable/scrollable children
     * which can consume those touch events.
     */
    override fun dispatchTouchEvent(
        ev: MotionEvent
    ): Boolean {

        if (::gestureDetector.isInitialized) {
            gestureDetector.onTouchEvent(ev)
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun loadCuratedAppsAsync() {

        lifecycleScope.launch {

            val allApps =
                appRepository
                    .getAllLaunchableAppsAsync()

            curatedApps =
                allApps
                    .filter {
                        it.packageName in curatedPackageHints
                    }
                    .ifEmpty {
                        allApps.take(8)
                    }

            renderHomeLayout(
                profileEngine.current.value
            )
        }
    }

    private fun renderHomeLayout(
        profile: Profile
    ) {

        val layout =
            HomeLayoutFactory.forProfile(
                profile
            )

        layout.build(
            homeContentContainer,
            curatedApps
        ) { app ->

            appRepository.launchApp(
                app.packageName
            )
        }
    }

    // ---------------------------------------------------------
    // DOCK
    // ---------------------------------------------------------

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

        dockGrid.adapter = dockAdapter
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

    // ---------------------------------------------------------
    // GESTURES
    // ---------------------------------------------------------

    private fun setupGestures() {

        gestureDetector =
            LauncherGestureDetector(
                gestureRepository
            ) { action ->

                handleGestureAction(
                    action
                )
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

        startActivity(
            Intent(
                this,
                NotificationCenterActivity::class.java
            )
        )
    }

    // ---------------------------------------------------------
    // ADAPTIVE INTELLIGENCE
    // ---------------------------------------------------------

    /*
     * Phase 17:
     *
     * Adaptive Intelligence is deliberately kept separate
     * from the UI.
     *
     * The engine evaluates the current profile and local
     * device context and produces recommendations.
     *
     * It does NOT directly manipulate the home screen,
     * dock, widgets, or notifications.
     *
     * Future presentation layers such as the profile-aware
     * Now Bar can consume these recommendations.
     */
    private fun evaluateIntelligence(
        profile: Profile
    ) {

        try {

            intelligenceManager.evaluate(
                profile
            )

        } catch (_: Exception) {

            /*
             * Intelligence is optional.
             *
             * A failure here must never bring down
             * the launcher home screen.
             */
        }
    }

    // ---------------------------------------------------------
    // WIDGET HOSTING
    // ---------------------------------------------------------

    private fun setupWidgetArea() {

        widgetContainer.setOnLongClickListener {

            if (currentWidgetView == null) {

                showWidgetPicker()

            } else {

                confirmRemoveWidget()
            }

            true
        }

        val savedId =
            widgetRepository.getSavedWidgetId()

        if (savedId != -1) {

            val info =
                appWidgetManager.getAppWidgetInfo(
                    savedId
                )

            if (info != null) {

                attachWidgetView(
                    savedId,
                    info
                )

            } else {

                widgetRepository.clearWidgetId()
            }
        }
    }

    private fun showWidgetPicker() {

        val providers =
            appWidgetManager.installedProviders

        if (providers.isEmpty()) {
            return
        }

        val labels =
            providers
                .map {
                    it.loadLabel(
                        packageManager
                    )
                }
                .toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Choose a widget")
            .setItems(labels) { _, which ->

                beginBind(
                    providers[which]
                )
            }
            .show()
    }

    private fun beginBind(
        provider: AppWidgetProviderInfo
    ) {

        val appWidgetId =
            appWidgetHost.allocateAppWidgetId()

        val canBind =
            appWidgetManager
                .bindAppWidgetIdIfAllowed(
                    appWidgetId,
                    provider.provider
                )

        if (canBind) {

            proceedAfterBind(
                appWidgetId,
                provider
            )

        } else {

            pendingProvider = provider

            val bindIntent =
                Intent(
                    AppWidgetManager.ACTION_APPWIDGET_BIND
                ).apply {

                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        appWidgetId
                    )

                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,
                        provider.provider
                    )
                }

            startActivityForResult(
                bindIntent,
                REQUEST_BIND_APPWIDGET
            )
        }
    }

    private fun proceedAfterBind(
        appWidgetId: Int,
        provider: AppWidgetProviderInfo
    ) {

        if (provider.configure != null) {

            val configIntent =
                Intent(
                    AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                ).apply {

                    component =
                        provider.configure

                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        appWidgetId
                    )
                }

            try {

                startActivityForResult(
                    configIntent,
                    REQUEST_CREATE_APPWIDGET_CONFIGURE
                )

            } catch (_: Exception) {

                attachWidgetView(
                    appWidgetId,
                    provider
                )
            }

        } else {

            attachWidgetView(
                appWidgetId,
                provider
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        when (requestCode) {

            REQUEST_BIND_APPWIDGET -> {

                val appWidgetId =
                    data?.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        -1
                    ) ?: -1

                val provider =
                    pendingProvider

                pendingProvider = null

                if (
                    resultCode == RESULT_OK &&
                    appWidgetId != -1 &&
                    provider != null
                ) {

                    proceedAfterBind(
                        appWidgetId,
                        provider
                    )

                } else if (
                    appWidgetId != -1
                ) {

                    appWidgetHost.deleteAppWidgetId(
                        appWidgetId
                    )
                }
            }

            REQUEST_CREATE_APPWIDGET_CONFIGURE -> {

                val appWidgetId =
                    data?.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        -1
                    ) ?: -1

                if (
                    resultCode == RESULT_OK &&
                    appWidgetId != -1
                ) {

                    val info =
                        appWidgetManager.getAppWidgetInfo(
                            appWidgetId
                        )

                    if (info != null) {

                        attachWidgetView(
                            appWidgetId,
                            info
                        )
                    }

                } else if (
                    appWidgetId != -1
                ) {

                    appWidgetHost.deleteAppWidgetId(
                        appWidgetId
                    )
                }
            }
        }
    }

    private fun attachWidgetView(
        appWidgetId: Int,
        info: AppWidgetProviderInfo
    ) {

        val hostView =
            appWidgetHost.createView(
                this,
                appWidgetId,
                info
            )

        hostView.setAppWidget(
            appWidgetId,
            info
        )

        widgetContainer.removeAllViews()

        widgetContainer.addView(
            hostView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        currentWidgetView =
            hostView

        widgetRepository.saveWidgetId(
            appWidgetId
        )
    }

    private fun confirmRemoveWidget() {

        AlertDialog.Builder(this)
            .setTitle("Remove widget?")
            .setPositiveButton("Remove") { _, _ ->

                removeCurrentWidget()
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    private fun removeCurrentWidget() {

        val savedId =
            widgetRepository.getSavedWidgetId()

        if (savedId != -1) {

            appWidgetHost.deleteAppWidgetId(
                savedId
            )
        }

        widgetContainer.removeAllViews()

        val placeholder =
            TextView(this).apply {

                text =
                    "Long-press to add a widget"

                setTextColor(
                    getColor(
                        android.R.color.darker_gray
                    )
                )

                textSize = 14f

                gravity =
                    Gravity.CENTER
            }

        widgetContainer.addView(
            placeholder,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        currentWidgetView = null

        widgetRepository.clearWidgetId()
    }

    // ---------------------------------------------------------
    // PROFILE OBSERVATION
    // ---------------------------------------------------------

    private fun observeProfile() {

        lifecycleScope.launch {

            profileEngine.current.collect { profile ->

                profileLabel.text =
                    profile.displayName

                renderHomeLayout(
                    profile
                )

                /*
                 * Phase 17 intelligence evaluation.
                 *
                 * The result is intentionally not coupled
                 * to a specific presentation component yet.
                 */
                evaluateIntelligence(
                    profile
                )
            }
        }
    }

    companion object {

        private const val REQUEST_BIND_APPWIDGET =
            2001

        private const val REQUEST_CREATE_APPWIDGET_CONFIGURE =
            2002
    }
}
