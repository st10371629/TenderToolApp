package com.tendertool.app

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.ThemeHelper

class MainActivity : AppCompatActivity() {
//    private val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply saved theme first
        ThemeHelper.applySavedTheme(this)

        super.onCreate(savedInstanceState)

        //fetch session
        Amplify.Auth.fetchAuthSession(
            {session ->
                runOnUiThread {
                    if (session.isSignedIn){
                        val intent = Intent(this, WatchlistActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
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
                        val indicator: View = findViewById(R.id.tabIndicator)

                        // Animate indicator to target X position
                        fun moveIndicatorTo(targetTab: TextView) {
                            ObjectAnimator.ofFloat(indicator, "translationX", targetTab.x).apply {
                                duration = 200
                                start()
                            }
                        }

                        // Set default indicator position under login tab
                        indicator.post { moveIndicatorTo(loginTab) }

                        loginTab.setOnClickListener {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.formContainer, LoginFragment())
                                .commit()
                            loginTab.setTextColor(Color.WHITE)
                            registerTab.setTextColor(Color.parseColor("#80FFFFFF"))
                            moveIndicatorTo(loginTab)
                        }

                        registerTab.setOnClickListener {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.formContainer, RegisterFragment())
                                .commit()
                            registerTab.setTextColor(Color.WHITE)
                            loginTab.setTextColor(Color.parseColor("#80FFFFFF"))
                            moveIndicatorTo(registerTab)
                        }

                        NavBar.LoadNav(this) //passes through the current activity, and loads nav intents
                    }
                }
            },
            {error ->
                Log.e("MainActivity", "Failed to fetch auth session.", error)
                //if fetching session fails, default to login screen
                runOnUiThread {
                    setContentView(R.layout.activity_login)
                }
            }
        )
    }
}
