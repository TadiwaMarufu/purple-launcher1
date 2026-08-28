package com.thepurpleweb.purplelauncher.dock

import android.content.Context
import com.thepurpleweb.purplelauncher.apps.AppRepository
import org.json.JSONArray

class DockRepository(context: Context) {

    companion object {
        private const val PREFS = "purple_dock"
        private const val KEY_ITEMS = "dock_items"
        private const val KEY_SLOT_COUNT = "dock_slot_count"

        const val DEFAULT_SLOT_COUNT = 5
        const val MIN_SLOT_COUNT = 3
        const val MAX_SLOT_COUNT = 7
    }

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    private val appRepository =
        AppRepository(context.applicationContext)

    fun getSlotCount(): Int {
        return preferences.getInt(
            KEY_SLOT_COUNT,
            DEFAULT_SLOT_COUNT
        ).coerceIn(
            MIN_SLOT_COUNT,
            MAX_SLOT_COUNT
        )
    }

    fun setSlotCount(count: Int) {
        preferences.edit()
            .putInt(
                KEY_SLOT_COUNT,
                count.coerceIn(
                    MIN_SLOT_COUNT,
                    MAX_SLOT_COUNT
                )
            )
            .apply()

        trimItemsToSlotCount()
    }

    fun getPackageNames(): List<String> {
        val raw = preferences.getString(
            KEY_ITEMS,
            null
        ) ?: return emptyList()

        return try {
            val json = JSONArray(raw)
            buildList {
                for (index in 0 until json.length()) {
                    val packageName = json.optString(index)

                    if (packageName.isNotBlank()) {
                        add(packageName)
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun savePackageNames(packageNames: List<String>) {
        val cleaned = packageNames
            .filter { it.isNotBlank() }
            .distinct()
            .take(getSlotCount())

        val json = JSONArray()

        cleaned.forEach {
            json.put(it)
        }

        preferences.edit()
            .putString(
                KEY_ITEMS,
                json.toString()
            )
            .apply()
    }

    fun addApp(packageName: String): Boolean {
        val current = getPackageNames()

        if (packageName in current) {
            return false
        }

        if (current.size >= getSlotCount()) {
            return false
        }

        savePackageNames(
            current + packageName
        )

        return true
    }

    fun removeApp(packageName: String): Boolean {
        val current = getPackageNames()

        if (packageName !in current) {
            return false
        }

        savePackageNames(
            current.filterNot {
                it == packageName
            }
        )

        return true
    }

    fun moveApp(
        fromPosition: Int,
        toPosition: Int
    ): Boolean {

        val current = getPackageNames()
            .toMutableList()

        if (
            fromPosition !in current.indices ||
            toPosition !in current.indices
        ) {
            return false
        }

        val item = current.removeAt(fromPosition)

        current.add(
            toPosition,
            item
        )

        savePackageNames(current)

        return true
    }

    fun getDockApps(): List<com.thepurpleweb.purplelauncher.apps.AppInfo> {
        val installedApps =
            appRepository.getAllLaunchableApps()

        val byPackage =
            installedApps.associateBy {
                it.packageName
            }

        return getPackageNames()
            .mapNotNull {
                byPackage[it]
            }
    }

    fun removeMissingApps() {
        val installedPackages =
            appRepository
                .getAllLaunchableApps()
                .map { it.packageName }
                .toSet()

        savePackageNames(
            getPackageNames().filter {
                it in installedPackages
            }
        )
    }

    private fun trimItemsToSlotCount() {
        savePackageNames(
            getPackageNames()
        )
    }
}
