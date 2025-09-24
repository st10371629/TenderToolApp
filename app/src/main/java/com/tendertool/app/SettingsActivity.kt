package com.tendertool.app

import android.os.Bundle
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.TopBarFragment

class SettingsActivity : BaseActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Attach TopBarFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // attach nav bar listeners
        NavBar.LoadNav(this)
    }
}