package com.tendertool.app.src

import android.app.Activity
import android.content.Intent
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.tendertool.app.*

object NavBar {
    fun LoadNav(activity: Activity) {
        val navBar = activity.findViewById<LinearLayout>(R.id.customNavBar)
        val navSettings = activity.findViewById<LinearLayout>(R.id.navSettings)
        val navDiscover = activity.findViewById<LinearLayout>(R.id.navDiscover)
        val navAnalytics = activity.findViewById<LinearLayout>(R.id.navAnalytics)
        val navWatchlist = activity.findViewById<LinearLayout>(R.id.navWatchlist)

        // If this page has no nav bar, exit early
        if (navSettings == null || navDiscover == null || navAnalytics == null || navWatchlist == null) {
            return
        }

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
