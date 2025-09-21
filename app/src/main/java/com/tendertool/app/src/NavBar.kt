package com.tendertool.app.src

import android.app.Activity
import android.content.Intent
import android.widget.LinearLayout
import com.tendertool.app.*

object NavBar
{
    fun LoadNav(activity: Activity)
    {
        val navSettings = activity.findViewById<LinearLayout>(R.id.navSettings)
        val navDiscover = activity.findViewById<LinearLayout>(R.id.navDiscover)

        navSettings.setOnClickListener {
            val settingsIntent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(settingsIntent)
        }

        navDiscover.setOnClickListener {
            val discoverIntent = Intent(activity, DiscoverActivity::class.java)
            activity.startActivity(discoverIntent)
        }
    }
}