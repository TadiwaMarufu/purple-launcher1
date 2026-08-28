package com.thepurpleweb.purplelauncher.apps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.thepurpleweb.purplelauncher.R

class AppDrawerAdapter(
    private val context: Context,
    private var apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : BaseAdapter() {

    fun updateApps(newApps: List<AppInfo>) {
        apps = newApps
        notifyDataSetChanged()
    }

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): AppInfo = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.drawer_app_item, parent, false)

        val app = apps[position]

        view.findViewById<ImageView>(R.id.drawer_app_icon)
            .setImageDrawable(app.icon)

        view.findViewById<TextView>(R.id.drawer_app_label)
            .text = app.label

        view.setOnClickListener {
            onAppClick(app)
        }

        return view
    }
}
