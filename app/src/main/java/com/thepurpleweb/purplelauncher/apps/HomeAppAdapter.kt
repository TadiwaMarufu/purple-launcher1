package com.thepurpleweb.purplelauncher.apps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.icons.IconTreatment
import com.thepurpleweb.purplelauncher.profile.ProfileEngine

class HomeAppAdapter(
    private val context: Context,
    private val apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : BaseAdapter() {

    private val profileEngine = ProfileEngine(context.applicationContext)

    override fun getCount(): Int = apps.size

    override fun getItem(position: Int): Any = apps[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.home_app_item, parent, false)

        val app = apps[position]

        val icon = view.findViewById<ImageView>(R.id.app_icon)
        val label = view.findViewById<TextView>(R.id.app_label)

        IconTreatment.apply(
            icon,
            app.icon,
            profileEngine.current.value
        )

        label.text = app.label

        view.setOnClickListener {
            onAppClick(app)
        }

        return view
    }
}
