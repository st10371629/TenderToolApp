package com.tendertool.app

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tendertool.app.src.NavBar

class MainActivity : AppCompatActivity() {
//    private val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // your big XML with fragmentContainer

        // load LoginFragment first when app opens
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, LoginFragment())
                .commit()
        }

        // find tabs
        val loginTab: TextView = findViewById(R.id.loginTab)
        val registerTab: TextView = findViewById(R.id.registerTab)

        // when Login tab is clicked
        loginTab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, LoginFragment())
                .commit()
            loginTab.setTextColor(Color.WHITE)
            registerTab.setTextColor(Color.parseColor("#80FFFFFF"))
        }

        // when Register tab is clicked
        registerTab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, RegisterFragment())
                .commit()
            registerTab.setTextColor(Color.WHITE)
            loginTab.setTextColor(Color.parseColor("#80FFFFFF"))
        }

        NavBar.LoadNav(this) //passes through the current activity, and loads nav intents

//        viewModel.testApi()

    }
}
