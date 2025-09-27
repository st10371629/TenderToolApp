package com.tendertool.app.src

import android.app.Activity
import android.content.Intent
import android.widget.LinearLayout
import com.tendertool.app.*

object NavBar {
    fun LoadNav(activity: Activity) {
        val navSettings = activity.findViewById<LinearLayout>(R.id.navSettings)
        val navDiscover = activity.findViewById<LinearLayout>(R.id.navDiscover)
        val navAnalytics = activity.findViewById<LinearLayout>(R.id.navAnalytics)
        val navWatchlist = activity.findViewById<LinearLayout>(R.id.navWatchlist)

        navSettings.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }

        navDiscover.setOnClickListener {
            val intent = Intent(activity, DiscoverActivity::class.java)
            activity.startActivity(intent)
        }

        navAnalytics.setOnClickListener {
            val intent = Intent(activity, AnalyticsActivity::class.java)
            activity.startActivity(intent)
        }

        navWatchlist.setOnClickListener {
            val intent = Intent(activity, WatchlistActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
