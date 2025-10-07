package com.tendertool.app

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tendertool.app.adapters.AnalyticsAdapter
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.ThemeHelper
import com.tendertool.app.src.TopBarFragment
import java.util.Calendar

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var dailyActiveTimeText: TextView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        dailyActiveTimeText = findViewById(R.id.text_daily_active_time)
        viewPager = findViewById(R.id.viewPagerAnalytics)

        // initial check
        updateDailyActiveTime()

        // attach TopBarFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // attach nav bar listeners
        NavBar.LoadNav(this)

        //set up pager adapter
        viewPager.adapter = AnalyticsAdapter(this)
        viewPager.offscreenPageLimit = 1 //for now
    }

    override fun onResume() {
        super.onResume()
        // recheck when user comes back from settings
        updateDailyActiveTime()
    }

    private fun updateDailyActiveTime() {
        if (!hasUsageStatsPermission(this)) {
            dailyActiveTimeText.text = "Permission Needed\n----\ntap to enable"
            dailyActiveTimeText.setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            Log.d("AnalyticsActivity", "Usage access permission NOT granted")
        } else {
            val dailyActiveMinutes = getDailyActiveMinutes()
            dailyActiveTimeText.text = formatTime(dailyActiveMinutes)
            Log.d("AnalyticsActivity", "Daily active minutes: $dailyActiveMinutes")
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        val granted = mode == AppOpsManager.MODE_ALLOWED
        Log.d("AnalyticsActivity", "Permission check result: $granted")
        return granted
    }

    private fun getDailyActiveMinutes(): Int {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        var totalTime: Long = 0
        stats.forEach {
            Log.d("AnalyticsActivity", "Package: ${it.packageName}, foreground: ${it.totalTimeInForeground} ms")
            totalTime += it.totalTimeInForeground
        }

        val minutes = (totalTime / 1000 / 60).toInt() // convert ms → minutes
        Log.d("AnalyticsActivity", "Total daily usage in minutes: $minutes")
        return minutes
    }

    private fun formatTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
}
