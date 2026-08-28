package com.thepurpleweb.purplelauncher.dock

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.AppRepository

class DockEditorActivity : AppCompatActivity() {

    private lateinit var repository: AppRepository
    private lateinit var dockRepository: DockRepository

    private lateinit var slotCountLabel: TextView
    private lateinit var appList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dock_editor)

        repository =
            AppRepository(applicationContext)

        dockRepository =
            DockRepository(applicationContext)

        slotCountLabel =
            findViewById(R.id.slot_count_label)

        appList =
            findViewById(R.id.dock_editor_list)

        findViewById<Button>(
            R.id.remove_all_button
        ).setOnClickListener {
            dockRepository.savePackageNames(emptyList())
            refresh()
        }

        findViewById<Button>(
            R.id.decrease_slots_button
        ).setOnClickListener {
            dockRepository.setSlotCount(
                dockRepository.getSlotCount() - 1
            )
            refresh()
        }

        findViewById<Button>(
            R.id.increase_slots_button
        ).setOnClickListener {
            dockRepository.setSlotCount(
                dockRepository.getSlotCount() + 1
            )
            refresh()
        }

        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {

        val apps =
            repository
                .getAllLaunchableApps(true)

        val dockPackages =
            dockRepository
                .getPackageNames()
                .toSet()

        slotCountLabel.text =
            "Dock slots: ${dockRepository.getSlotCount()}"

        val rows =
            apps.map { app ->
                EditorRow(
                    app = app,
                    selected =
                        app.packageName in dockPackages
                )
            }

        appList.adapter =
            DockEditorAdapter(
                this,
                rows
            ) { row ->
                toggleApp(row.app)
            }
    }

    private fun toggleApp(app: AppInfo) {

        val current =
            dockRepository.getPackageNames()

        if (app.packageName in current) {
            dockRepository.removeApp(
                app.packageName
            )
        } else {
            dockRepository.addApp(
                app.packageName
            )
        }

        refresh()
    }
}

private data class EditorRow(
    val app: AppInfo,
    val selected: Boolean
)

private class DockEditorAdapter(
    private val activity: DockEditorActivity,
    private val rows: List<EditorRow>,
    private val onClick: (EditorRow) -> Unit
) : ArrayAdapter<EditorRow>(
    activity,
    R.layout.dock_editor_item,
    rows
) {

    override fun getView(
        position: Int,
        convertView: android.view.View?,
        parent: android.view.ViewGroup
    ): android.view.View {

        val view =
            convertView ?: activity.layoutInflater.inflate(
                R.layout.dock_editor_item,
                parent,
                false
            )

        val row = rows[position]

        view.findViewById<android.widget.ImageView>(
            R.id.editor_app_icon
        ).setImageDrawable(row.app.icon)

        view.findViewById<TextView>(
            R.id.editor_app_label
        ).text = row.app.label

        view.findViewById<android.widget.CheckBox>(
            R.id.editor_app_selected
        ).apply {
            isChecked = row.selected
            setOnClickListener {
                onClick(row)
            }
        }

        view.setOnClickListener {
            onClick(row)
        }

        return view
    }
}
