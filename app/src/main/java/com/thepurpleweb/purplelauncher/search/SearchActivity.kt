package com.thepurpleweb.purplelauncher.search

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thepurpleweb.purplelauncher.MainActivity
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.AppRepository
import com.thepurpleweb.purplelauncher.drawer.AppDrawerActivity
import com.thepurpleweb.purplelauncher.profile.Profile
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import com.thepurpleweb.purplelauncher.settings.SettingsActivity
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var resultMessage: TextView
    private lateinit var resultList: android.widget.ListView

    private lateinit var appRepository: AppRepository
    private lateinit var profileEngine: ProfileEngine
    private lateinit var adapter: SearchResultAdapter

    private var allApps: List<AppInfo> = emptyList()
    private val commandParser = SearchCommandParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        appRepository = AppRepository(applicationContext)
        profileEngine = ProfileEngine.getInstance(applicationContext)

        searchInput = findViewById(R.id.search_input)
        resultMessage = findViewById(R.id.search_result_message)
        resultList = findViewById(R.id.search_results)

        adapter = SearchResultAdapter(this, emptyList()) { app ->
            appRepository.launchApp(app.packageName)
        }
        resultList.adapter = adapter

        setupSearch()
        searchInput.requestFocus()

        window.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        )

        loadAppsAsync()
    }

    override fun onResume() {
        super.onResume()
        loadAppsAsync()
    }

    private fun loadAppsAsync() {
        lifecycleScope.launch {
            allApps = appRepository.getAllLaunchableAppsAsync()
            if (::adapter.isInitialized) {
                adapter.updateResults(allApps)
            }
        }
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: android.text.Editable?) = Unit
        })

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executeCommand(searchInput.text.toString().trim())
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(rawQuery: String) {
        val query = rawQuery.trim().lowercase()

        if (query.isEmpty()) {
            resultMessage.text = "Search apps or type a command"
            adapter.updateResults(allApps)
            return
        }

        val command = commandParser.parse(query)

        if (command !is SearchCommand.None) {
            resultMessage.text = commandDescription(command)
            adapter.updateResults(emptyList())
            return
        }

        val results = allApps
            .filter { app ->
                app.label.lowercase().contains(query) || app.packageName.lowercase().contains(query)
            }
            .sortedWith(
                compareBy<AppInfo> { !it.label.lowercase().startsWith(query) }
                    .thenBy { it.label.lowercase() }
            )

        if (results.isEmpty()) {
            resultMessage.text = "No results for \"$rawQuery\""
        } else {
            resultMessage.text = "${results.size} result" + if (results.size == 1) "" else "s"
        }

        adapter.updateResults(results)
    }

    private fun executeCommand(query: String) {
        if (query.isEmpty()) return

        val command = commandParser.parse(query)

        when (command) {
            is SearchCommand.OpenAppDrawer -> {
                startActivity(Intent(this, AppDrawerActivity::class.java))
                finish()
            }
            is SearchCommand.SwitchProfile -> {
                profileEngine.setProfile(Profile.Focus)
                finish()
            }
            is SearchCommand.OpenSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
            }
            is SearchCommand.StartTimer -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    action = ACTION_START_TIMER
                    putExtra(EXTRA_TIMER_DURATION, command.durationMs)
                    putExtra(EXTRA_TIMER_LABEL, command.label)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                finish()
            }
            is SearchCommand.SetCustomNowBar -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    action = ACTION_SET_CUSTOM_NOWBAR
                    putExtra(EXTRA_CUSTOM_MESSAGE, command.message)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                finish()
            }
            is SearchCommand.None -> {
                val exactMatch = allApps.firstOrNull { it.label.equals(query, ignoreCase = true) }
                if (exactMatch != null) {
                    appRepository.launchApp(exactMatch.packageName)
                    finish()
                }
            }
        }
    }

    private fun commandDescription(command: SearchCommand): String = when (command) {
        is SearchCommand.OpenAppDrawer -> "Press search to open the app drawer"
        is SearchCommand.SwitchProfile -> "Press search to switch to Focus"
        is SearchCommand.OpenSettings -> "Press search to open launcher settings"
        is SearchCommand.StartTimer -> "Press search to start timer (${command.durationMs / 1000}s)"
        is SearchCommand.SetCustomNowBar -> "Press search to set Now Bar status: \"${command.message}\""
        is SearchCommand.None -> "Search"
    }

    companion object {
        const val ACTION_START_TIMER = "com.thepurpleweb.purplelauncher.action.START_TIMER"
        const val EXTRA_TIMER_DURATION = "extra_timer_duration"
        const val EXTRA_TIMER_LABEL = "extra_timer_label"

        const val ACTION_SET_CUSTOM_NOWBAR = "com.thepurpleweb.purplelauncher.action.SET_CUSTOM_NOWBAR"
        const val EXTRA_CUSTOM_MESSAGE = "extra_custom_message"
    }
}
