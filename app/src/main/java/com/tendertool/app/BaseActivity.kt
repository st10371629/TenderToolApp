package com.tendertool.app

import android.content.Intent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    fun setupNavBar() {
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)
        val navDiscover = findViewById<LinearLayout>(R.id.navDiscover)

        navSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        navDiscover.setOnClickListener {
            startActivity(Intent(this, DiscoverActivity::class.java))
        }
    }
}
