package com.tendertool.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.amplifyframework.core.Amplify
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.ThemeHelper
import kotlin.math.log
import com.tendertool.app.src.TopBarFragment
import java.util.Locale

class SettingsActivity : BaseActivity() {

    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "app_settings"
    private val KEY_LANGUAGE = "app_language"

    override fun onCreate(savedInstanceState: Bundle?) {

        // Load theme setting before view inflation
        ThemeHelper.applySavedTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        //initialise logout button
        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            //fetches session - checks if there is a user logged in before attempting to sign out.
            Amplify.Auth.fetchAuthSession(
                { session ->
                    runOnUiThread { //run on ui thread since amplify default runs in the bg
                        if (!session.isSignedIn) {
                            //in the event there is no user session active
                            Toast.makeText(
                                this,
                                "Sign out unsuccessful! No active session.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.w("AuthSettings", "No active session.")
                        } else {
                            try {
                                //perfroms the actual sign out
                                Amplify.Auth.signOut(
                                    {
                                        Log.i("AuthSettings", "Signed out successfully.")
                                        runOnUiThread { //run on ui thread since amplify default runs in the bg
                                            Toast.makeText(
                                                this,
                                                "Sign out successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        //redirect to login
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(this, "Sign out unsuccessful.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                },
                { error ->
                    Log.e("AuthSettings", "Failed to retrieve session.", error)
                }
            )
        }

        // Attach TopBarFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // attach nav bar listeners
        NavBar.LoadNav(this)

        // Theme switch and text
        val switchTheme = findViewById<Switch>(R.id.switch_theme)
        val textThemeStatus = findViewById<TextView>(R.id.text_theme_status)

        // Load current theme mode from preferences
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val isDarkMode = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
        switchTheme.isChecked = isDarkMode
        textThemeStatus.text = if (isDarkMode) getString(R.string.theme_dark) else getString(R.string.theme_light)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val lightText = getString(R.string.theme_light)
            val darkText = getString(R.string.theme_dark)
            textThemeStatus.text = if (isChecked) darkText else lightText

            // Save and apply using ThemeHelper
            ThemeHelper.saveThemePreference(this, isChecked)

            // Restart the SettingsActivity with a fresh theme
            val intent = Intent(this, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // Smooth transition effect
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }



        // Notifications switch and text
        val switchNotifications = findViewById<Switch>(R.id.switch_notifications)
        val textNotificationsStatus = findViewById<TextView>(R.id.text_notifications_status)

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val onText = getString(R.string.notifications_on)   // from strings.xml
            val offText = getString(R.string.notifications_off) // from strings.xml
            textNotificationsStatus.text = if (isChecked) onText else offText
        }

        // SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load saved language and apply
        val savedLanguage = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        setAppLanguage(savedLanguage)

        // Language Spinner
        val spinnerLanguage = findViewById<Spinner>(R.id.spinner_language)
        val languages = listOf("English", "Afrikaans", "Zulu")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter

        // Set spinner to saved language
        val selectedPosition = when (savedLanguage) {
            "af" -> 1
            "zu" -> 2
            else -> 0
        }
        spinnerLanguage.setSelection(selectedPosition)

        // Set listener for language selection
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val languageCode = when (position) {
                    1 -> "af"
                    2 -> "zu"
                    else -> "en"
                }

                // Only change if different from saved language
                if (languageCode != prefs.getString(KEY_LANGUAGE, "en")) {
                    prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
                    setAppLanguage(languageCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Function to change app language dynamically
    private fun setAppLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}


