package com.tendertool.app

import android.animation.ObjectAnimator
import android.content.Context // Import this
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.animation.ValueAnimator
import com.amplifyframework.core.Amplify
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.ThemeHelper
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
//    private val viewModel: TestViewModel by viewModels()

    private val PREFS_NAME = "app_settings"
    private val KEY_BIOMETRICS_ENABLED = "BIOMETRICS_ENABLED"

    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply saved theme first
        ThemeHelper.applySavedTheme(this)

        super.onCreate(savedInstanceState)

        // This tells the activity to resize when the keyboard opens
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        //fetch session
        Amplify.Auth.fetchAuthSession(
            { session ->
                runOnUiThread {
                    // 1. Get SharedPreferences to check our biometric setting
                    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val isBiometricsEnabled = prefs.getBoolean(KEY_BIOMETRICS_ENABLED, false)

                    // 2. Decide where to go
                    val shouldGoToWatchlist = session.isSignedIn && !isBiometricsEnabled

                    if (shouldGoToWatchlist) {
                        // This is the only case we go straight to the app:
                        // User is signed in AND has biometrics disabled.
                        val intent = Intent(this, WatchlistActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // This handles all other cases:
                        // 1. User is signed out (session.isSignedIn is false)
                        // 2. User is signed in BUT has biometrics enabled
                        setupLoginScreen(savedInstanceState)
                    }
                }
            },
            { error ->
                Log.e("MainActivity", "Failed to fetch auth session.", error)
                //if fetching session fails, default to login screen
                runOnUiThread {
                    // We must also call the full setup here
                    setupLoginScreen(savedInstanceState)
                }
            }
        )
    }

    private fun setupLoginScreen(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)

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
            // Animate X position
            ObjectAnimator.ofFloat(indicator, "translationX", targetTab.x).apply {
                duration = 200
                start()
            }

            // Animate width
            val targetWidth = targetTab.width
            val widthAnimator = ValueAnimator.ofInt(indicator.width, targetWidth)
            widthAnimator.addUpdateListener { animation ->
                val newWidth = animation.animatedValue as Int
                val layoutParams = indicator.layoutParams
                layoutParams.width = newWidth
                indicator.layoutParams = layoutParams
            }
            widthAnimator.duration = 200
            widthAnimator.start()
        }

        // Set default indicator position under login tab
        indicator.post {
            // Set initial width
            val layoutParams = indicator.layoutParams
            layoutParams.width = loginTab.width
            indicator.layoutParams = layoutParams

            // Set initial position
            moveIndicatorTo(loginTab)
        }

        registerTab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.formContainer, RegisterFragment())
                .commit()
            registerTab.setTextColor(Color.WHITE)
            loginTab.setTextColor(Color.parseColor("#80FFFFFF"))
            moveIndicatorTo(registerTab)
        }

        loginTab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                .replace(R.id.formContainer, LoginFragment())
                .commit()
            loginTab.setTextColor(Color.WHITE)
            registerTab.setTextColor(Color.parseColor("#80FFFFFF"))
            moveIndicatorTo(loginTab)
        }

        NavBar.LoadNav(this) //passes through the current activity, and loads nav intents
    }
}