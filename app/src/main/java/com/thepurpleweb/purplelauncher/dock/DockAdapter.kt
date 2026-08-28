package com.thepurpleweb.purplelauncher.dock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.apps.AppInfo

class DockAdapter(
    private val context: Context,
    private var apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit,
    private val onAppLongClick: (AppInfo) -> Boolean
) : BaseAdapter() {

    fun updateApps(newApps: List<AppInfo>) {
        apps = newApps
        notifyDataSetChanged()
    }

    override fun getCount(): Int =
        apps.size

    override fun getItem(position: Int): AppInfo =
        apps[position]

    override fun getItemId(position: Int): Long =
        position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val view =
            convertView ?: LayoutInflater
                .from(context)
                .inflate(
                    R.layout.dock_app_item,
                    parent,
                    false
                )

        val app = apps[position]

        view.findViewById<ImageView>(
            R.id.dock_app_icon
        ).setImageDrawable(app.icon)

        view.findViewById<TextView>(
            R.id.dock_app_label
        ).text = app.label

        view.setOnClickListener {
            onAppClick(app)
        }

        view.setOnLongClickListener {
            onAppLongClick(app)
        }

        return view
    }
}
