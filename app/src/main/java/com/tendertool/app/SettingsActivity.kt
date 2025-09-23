package com.tendertool.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tendertool.app.src.NavBar

class SettingsActivity : BaseActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // attach nav bar listeners
        NavBar.LoadNav(this)
    }
}