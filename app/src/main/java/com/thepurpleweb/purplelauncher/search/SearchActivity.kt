package com.thepurpleweb.purplelauncher.search

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.AppRepository
import com.thepurpleweb.purplelauncher.drawer.AppDrawerActivity
import com.thepurpleweb.purplelauncher.profile.Profile
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import com.thepurpleweb.purplelauncher.settings.SettingsActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var resultMessage: TextView
    private lateinit var resultList: android.widget.ListView

    private lateinit var appRepository: AppRepository
    private lateinit var profileEngine: ProfileEngine

    private lateinit var adapter: SearchResultAdapter

    private var allApps: List<AppInfo> = emptyList()

    private val commandParser =
        SearchCommandParser()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_search
        )

        appRepository =
            AppRepository(
                applicationContext
            )

        profileEngine =
            ProfileEngine(
                applicationContext
            )

        searchInput =
            findViewById(
                R.id.search_input
            )

        resultMessage =
            findViewById(
                R.id.search_result_message
            )

        resultList =
            findViewById(
                R.id.search_results
            )

        allApps =
            appRepository
                .getAllLaunchableApps()

        adapter =
            SearchResultAdapter(
                this,
                allApps
            ) { app ->
                appRepository.launchApp(
                    app.packageName
                )
            }

        resultList.adapter =
            adapter

        setupSearch()

        searchInput.requestFocus()

        window.setSoftInputMode(
            android.view.WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE
        )
    }

    override fun onResume() {
        super.onResume()

        allApps =
            appRepository
                .getAllLaunchableApps()

        if (::adapter.isInitialized) {
            adapter.updateResults(
                allApps
            )
        }
    }

    private fun setupSearch() {

        searchInput.addTextChangedListener(
            object :
                android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    performSearch(
                        s?.toString().orEmpty()
                    )
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) = Unit
            }
        )

        searchInput.setOnEditorActionListener {
            _,
            actionId,
            _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_SEARCH
            ) {
                executeCommand(
                    searchInput.text
                        .toString()
                        .trim()
                )

                true
            } else {
                false
            }
        }
    }

    private fun performSearch(
        rawQuery: String
    ) {

        val query =
            rawQuery
                .trim()
                .lowercase()

        if (query.isEmpty()) {

            resultMessage.text =
                "Search apps or type a command"

            adapter.updateResults(
                allApps
            )

            return
        }

        val command =
            commandParser.parse(query)

        if (
            command !=
            SearchCommand.NONE
        ) {

            resultMessage.text =
                commandDescription(command)

            adapter.updateResults(
                emptyList()
            )

            return
        }

        val results =
            allApps
                .filter { app ->
                    app.label
                        .lowercase()
                        .contains(query) ||
                    app.packageName
                        .lowercase()
                        .contains(query)
                }
                .sortedWith(
                    compareBy<AppInfo> {
                        !it.label
                            .lowercase()
                            .startsWith(query)
                    }.thenBy {
                        it.label.lowercase()
                    }
                )

        if (results.isEmpty()) {
            resultMessage.text =
                "No results for \"$rawQuery\""
        } else {
            resultMessage.text =
                "${results.size} result" +
                    if (results.size == 1) "" else "s"
        }

        adapter.updateResults(
            results
        )
    }

    private fun executeCommand(
        query: String
    ) {

        if (query.isEmpty()) {
            return
        }

        val command =
            commandParser.parse(
                query
            )

        when (command) {

            SearchCommand.OPEN_APP_DRAWER -> {
                startActivity(
                    Intent(
                        this,
                        AppDrawerActivity::class.java
                    )
                )
                finish()
            }

            SearchCommand.SWITCH_PROFILE -> {
                profileEngine.setProfile(Profile.Focus)
                finish()
            }

            SearchCommand.OPEN_SETTINGS -> {
                startActivity(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
                finish()
            }

            SearchCommand.NONE -> {

                val exactMatch =
                    allApps.firstOrNull {
                        it.label.equals(
                            query,
                            ignoreCase = true
                        )
                    }

                if (
                    exactMatch != null
                ) {
                    appRepository.launchApp(
                        exactMatch.packageName
                    )
                    finish()
                }
            }
        }
    }

    private fun commandDescription(
        command: SearchCommand
    ): String =
        when (command) {

            SearchCommand.OPEN_APP_DRAWER ->
                "Press search to open the app drawer"

            SearchCommand.SWITCH_PROFILE ->
                "Press search to switch to Focus"

            SearchCommand.OPEN_SETTINGS ->
                "Press search to open launcher settings"

            SearchCommand.NONE ->
                "Search"
        }
}
