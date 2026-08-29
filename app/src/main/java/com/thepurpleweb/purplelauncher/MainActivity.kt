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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.AppRepository
import com.thepurpleweb.purplelauncher.drawer.AppDrawerActivity
import com.thepurpleweb.purplelauncher.gestures.GestureAction
import com.thepurpleweb.purplelauncher.gestures.GestureRepository
import com.thepurpleweb.purplelauncher.gestures.LauncherGestureDetector
import com.thepurpleweb.purplelauncher.home.HomeLayoutFactory
import com.thepurpleweb.purplelauncher.intelligence.IntelligenceManager
import com.thepurpleweb.purplelauncher.notifications.NotificationCenterActivity
import com.thepurpleweb.purplelauncher.nowbar.NowBarCallManager
import com.thepurpleweb.purplelauncher.nowbar.NowBarController
import com.thepurpleweb.purplelauncher.nowbar.NowBarCoordinator
import com.thepurpleweb.purplelauncher.nowbar.NowBarItem
import com.thepurpleweb.purplelauncher.nowbar.NowBarMediaManager
import com.thepurpleweb.purplelauncher.nowbar.NowBarNotificationBridge
import com.thepurpleweb.purplelauncher.nowbar.NowBarTimerManager
import com.thepurpleweb.purplelauncher.nowbar.NowBarType
import com.thepurpleweb.purplelauncher.nowbar.NowBarView
import com.thepurpleweb.purplelauncher.performance.DevicePerformance
import com.thepurpleweb.purplelauncher.performance.VisualQuality
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
    private lateinit var gestureRepository: GestureRepository
    private lateinit var gestureDetector: LauncherGestureDetector

    private lateinit var widgetRepository: WidgetRepository
    private lateinit var intelligenceManager: IntelligenceManager

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var widgetContainer: FrameLayout

    private lateinit var profileLabel: TextView
    private lateinit var homeContentContainer: FrameLayout
    private lateinit var nowbarContainer: FrameLayout

    private lateinit var nowBarController: NowBarController
    private lateinit var nowBarView: NowBarView
    private lateinit var nowBarCoordinator: NowBarCoordinator
    private lateinit var nowBarMediaManager: NowBarMediaManager
    private lateinit var nowBarCallManager: NowBarCallManager
    private lateinit var nowBarTimerManager: NowBarTimerManager
    private lateinit var nowBarNotificationBridge: NowBarNotificationBridge

    private var currentWidgetView: AppWidgetHostView? = null
    private var pendingProvider: AppWidgetProviderInfo? = null

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
                File(getExternalFilesDir(null), "crash_log.txt").writeText(sw.toString())
            } catch (_: Exception) {}
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileEngine = ProfileEngine.getInstance(applicationContext)
        appRepository = AppRepository(applicationContext)
        gestureRepository = GestureRepository(applicationContext)
        widgetRepository = WidgetRepository(applicationContext)
        intelligenceManager = IntelligenceManager(applicationContext)

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, WidgetRepository.HOST_ID)

        profileLabel = findViewById(R.id.profile_label)
        homeContentContainer = findViewById(R.id.home_content_container)
        widgetContainer = findViewById(R.id.widget_container)
        nowbarContainer = findViewById(R.id.nowbar_container)

        setupNowBar()
        setupGestures()
        setupWidgetArea()

        profileLabel.setOnClickListener {
            startActivity(
                Intent(this, com.thepurpleweb.purplelauncher.profile.ProfilesActivity::class.java)
            )
        }

        loadCuratedAppsAsync()
        observeProfile()
        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            SearchActivity.ACTION_START_TIMER -> {
                val duration = intent.getLongExtra(SearchActivity.EXTRA_TIMER_DURATION, 0L)
                if (duration > 0 && ::nowBarTimerManager.isInitialized) {
                    nowBarTimerManager.start(duration)
                }
            }
            SearchActivity.ACTION_SET_CUSTOM_NOWBAR -> {
                val message = intent.getStringExtra(SearchActivity.EXTRA_CUSTOM_MESSAGE)
                if (!message.isNullOrEmpty() && ::nowBarCoordinator.isInitialized) {
                    nowBarCoordinator.publish(
                        NowBarItem(
                            type = NowBarType.CUSTOM,
                            title = "Note",
                            subtitle = message
                        ),
                        priority = 80
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening()

        if (::nowBarCoordinator.isInitialized) nowBarCoordinator.start()
        if (::nowBarMediaManager.isInitialized) nowBarMediaManager.start()
        if (::nowBarCallManager.isInitialized) nowBarCallManager.start()
        if (::nowBarNotificationBridge.isInitialized) nowBarNotificationBridge.start()
    }

    override fun onStop() {
        if (::nowBarNotificationBridge.isInitialized) nowBarNotificationBridge.stop()
        if (::nowBarCallManager.isInitialized) nowBarCallManager.stop()
        if (::nowBarMediaManager.isInitialized) nowBarMediaManager.stop()
        if (::nowBarCoordinator.isInitialized) nowBarCoordinator.stop()

        appWidgetHost.stopListening()
        super.onStop()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (::gestureDetector.isInitialized) {
            gestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun loadCuratedAppsAsync() {
        lifecycleScope.launch {
            val allApps = appRepository.getAllLaunchableAppsAsync()
            curatedApps = allApps.filter { it.packageName in curatedPackageHints }
                .ifEmpty { allApps.take(8) }

            renderHomeLayout(profileEngine.current.value)
        }
    }

    private fun renderHomeLayout(profile: Profile) {
        val layout = HomeLayoutFactory.forProfile(profile)
        layout.build(homeContentContainer, curatedApps) { app ->
            appRepository.launchApp(app.packageName)
        }
    }

    // ---------------------------------------------------------
    // NOW BAR
    // ---------------------------------------------------------

    private fun setupNowBar() {
        nowBarController = NowBarController()
        nowBarView = NowBarView(this)

        nowBarCoordinator = NowBarCoordinator(this, nowBarController)

        lifecycleScope.launch {
            nowBarController.state.collect { state ->
                nowBarView.setState(state.primary, determineVisualQuality())
            }
        }

        nowBarMediaManager = NowBarMediaManager(this) { mediaItem ->
            nowBarCoordinator.setMedia(mediaItem)
        }

        nowBarCallManager = NowBarCallManager(this) { callItem ->
            nowBarCoordinator.setCall(callItem)
        }

        nowBarTimerManager = NowBarTimerManager { timerItem ->
            nowBarCoordinator.setTimer(timerItem)
        }

        nowBarNotificationBridge = NowBarNotificationBridge(this, nowBarCoordinator)

        nowBarView.setActions(
            onSwitchProfile = { profileEngine.cycleNext() },
            onSearch = { startActivity(Intent(this, SearchActivity::class.java)) },
            onNotifications = { openNotifications() }
        )

        nowbarContainer.removeAllViews()
        nowbarContainer.addView(
            nowBarView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )

        updateNowBar(profileEngine.current.value)
    }

    private fun updateNowBar(profile: Profile) {
        val quality = determineVisualQuality()
        nowBarView.setProfile(profile, quality)

        val activeItem = nowBarCoordinator.current()
        nowBarController.setPrimary(activeItem ?: buildIdleItem(profile))
    }

    private fun buildIdleItem(profile: Profile): NowBarItem {
        return NowBarItem(
            type = NowBarType.IDLE,
            title = profile.displayName,
            subtitle = when (profile) {
                Profile.Fluid -> "Living environment"
                Profile.Premium -> "Refined environment"
                Profile.Calm -> "Quiet environment"
                Profile.Focus -> "Productive environment"
                Profile.Expressive -> "Creative environment"
            }
        )
    }

    private fun determineVisualQuality(): VisualQuality {
        return try {
            DevicePerformance.classify(this)
        } catch (_: Exception) {
            VisualQuality.MEDIUM
        }
    }

    // ---------------------------------------------------------
    // GESTURES
    // ---------------------------------------------------------

    private fun setupGestures() {
        gestureDetector = LauncherGestureDetector(gestureRepository) { action ->
            handleGestureAction(action)
        }
    }

    private fun handleGestureAction(action: GestureAction) {
        when (action) {
            GestureAction.NONE -> {}
            GestureAction.OPEN_APP_DRAWER -> startActivity(Intent(this, AppDrawerActivity::class.java))
            GestureAction.OPEN_SEARCH -> startActivity(Intent(this, SearchActivity::class.java))
            GestureAction.OPEN_SETTINGS -> startActivity(Intent(this, SettingsActivity::class.java))
            GestureAction.SWITCH_PROFILE -> profileEngine.cycleNext()
            GestureAction.OPEN_NOTIFICATIONS -> openNotifications()
        }
    }

    private fun openNotifications() {
        startActivity(Intent(this, NotificationCenterActivity::class.java))
    }

    // ---------------------------------------------------------
    // ADAPTIVE INTELLIGENCE
    // ---------------------------------------------------------

    private fun evaluateIntelligence(profile: Profile) {
        try {
            intelligenceManager.evaluate(profile)
        } catch (_: Exception) {}
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

        val savedId = widgetRepository.getSavedWidgetId()
        if (savedId != -1) {
            val info = appWidgetManager.getAppWidgetInfo(savedId)
            if (info != null) {
                attachWidgetView(savedId, info)
            } else {
                widgetRepository.clearWidgetId()
            }
        }
    }

    private fun showWidgetPicker() {
        val providers = appWidgetManager.installedProviders
        if (providers.isEmpty()) return

        val labels = providers.map { it.loadLabel(packageManager) }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Choose a widget")
            .setItems(labels) { _, which -> beginBind(providers[which]) }
            .show()
    }

    private fun beginBind(provider: AppWidgetProviderInfo) {
        val appWidgetId = appWidgetHost.allocateAppWidgetId()
        val canBind = appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider.provider)

        if (canBind) {
            proceedAfterBind(appWidgetId, provider)
        } else {
            pendingProvider = provider
            val bindIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
            }
            startActivityForResult(bindIntent, REQUEST_BIND_APPWIDGET)
        }
    }

    private fun proceedAfterBind(appWidgetId: Int, provider: AppWidgetProviderInfo) {
        if (provider.configure != null) {
            val configIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = provider.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            try {
                startActivityForResult(configIntent, REQUEST_CREATE_APPWIDGET_CONFIGURE)
            } catch (_: Exception) {
                attachWidgetView(appWidgetId, provider)
            }
        } else {
            attachWidgetView(appWidgetId, provider)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_BIND_APPWIDGET -> {
                val appWidgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
                val provider = pendingProvider
                pendingProvider = null

                if (resultCode == RESULT_OK && appWidgetId != -1 && provider != null) {
                    proceedAfterBind(appWidgetId, provider)
                } else if (appWidgetId != -1) {
                    appWidgetHost.deleteAppWidgetId(appWidgetId)
                }
            }
            REQUEST_CREATE_APPWIDGET_CONFIGURE -> {
                val appWidgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
                if (resultCode == RESULT_OK && appWidgetId != -1) {
                    val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
                    if (info != null) attachWidgetView(appWidgetId, info)
                } else if (appWidgetId != -1) {
                    appWidgetHost.deleteAppWidgetId(appWidgetId)
                }
            }
        }
    }

    private fun attachWidgetView(appWidgetId: Int, info: AppWidgetProviderInfo) {
        val hostView = appWidgetHost.createView(this, appWidgetId, info)
        hostView.setAppWidget(appWidgetId, info)

        widgetContainer.removeAllViews()
        widgetContainer.addView(
            hostView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        currentWidgetView = hostView
        widgetRepository.saveWidgetId(appWidgetId)
    }

    private fun confirmRemoveWidget() {
        AlertDialog.Builder(this)
            .setTitle("Remove widget?")
            .setPositiveButton("Remove") { _, _ -> removeCurrentWidget() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun removeCurrentWidget() {
        val savedId = widgetRepository.getSavedWidgetId()
        if (savedId != -1) {
            appWidgetHost.deleteAppWidgetId(savedId)
        }

        widgetContainer.removeAllViews()
        val placeholder = TextView(this).apply {
            text = "Long-press to add a widget"
            setTextColor(getColor(android.R.color.darker_gray))
            textSize = 14f
            gravity = Gravity.CENTER
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
    // PROFILE
    // ---------------------------------------------------------

    private fun observeProfile() {
        lifecycleScope.launch {
            profileEngine.current.collect { profile ->
                profileLabel.text = profile.displayName
                renderHomeLayout(profile)
                updateNowBar(profile)
                evaluateIntelligence(profile)
            }
        }
    }

    companion object {
        private const val REQUEST_BIND_APPWIDGET = 2001
        private const val REQUEST_CREATE_APPWIDGET_CONFIGURE = 2002
    }
}
