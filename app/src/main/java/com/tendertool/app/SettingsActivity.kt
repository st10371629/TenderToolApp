package com.tendertool.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : BaseActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // attach nav bar listeners
        setupNavBar()
    }
}