package com.tendertool.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.TopBarFragment

class AnalyticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        // Attach TopBarFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // Attach nav bar listeners
        NavBar.LoadNav(this)
    }
}
