package com.thepurpleweb.purplelauncher.drawer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.apps.AppCategory
import com.thepurpleweb.purplelauncher.apps.AppDrawerAdapter
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.AppRepository

class AppDrawerActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository
    private lateinit var appGrid: GridView
    private lateinit var searchInput: EditText
    private lateinit var categoryContainer: LinearLayout

    private lateinit var adapter: AppDrawerAdapter

    private var selectedCategory = AppCategory.ALL
    private var allApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_drawer)

        repository = AppRepository(applicationContext)

        appGrid = findViewById(R.id.drawer_app_grid)
        searchInput = findViewById(R.id.drawer_search)
        categoryContainer = findViewById(R.id.category_container)

        adapter = AppDrawerAdapter(
            this,
            emptyList()
        ) { app ->
            repository.launchApp(app.packageName)
        }

        appGrid.adapter = adapter

        buildCategories()
        loadApps()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()

        loadApps(forceRefresh = true)
    }

    private fun loadApps(forceRefresh: Boolean = false) {
        allApps = repository.getAllLaunchableApps(forceRefresh)
        applyFilter()
    }

    private fun buildCategories() {
        categoryContainer.removeAllViews()

        AppCategory.entries.forEach { category ->
            val view = TextView(this).apply {
                text = category.title
                textSize = 15f
                setTextColor(getColor(android.R.color.white))
                setPadding(28, 16, 28, 16)

                setOnClickListener {
                    selectedCategory = category
                    applyFilter()
                    updateCategorySelection()
                }
            }

            categoryContainer.addView(view)
        }

        updateCategorySelection()
    }

    private fun updateCategorySelection() {
        for (index in 0 until categoryContainer.childCount) {
            val child = categoryContainer.getChildAt(index) as TextView
            val category = AppCategory.entries[index]

            child.alpha = if (category == selectedCategory) 1f else 0.55f
        }
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {

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
                applyFilter()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun applyFilter() {
        val query = searchInput.text
            ?.toString()
            ?.trim()
            ?.lowercase()
            .orEmpty()

        var filtered = if (selectedCategory == AppCategory.ALL) {
            allApps
        } else {
            repository.getAppsByCategory(selectedCategory)
        }

        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.label.lowercase().contains(query) ||
                    it.packageName.lowercase().contains(query)
            }
        }

        adapter.updateApps(filtered)
    }
}
