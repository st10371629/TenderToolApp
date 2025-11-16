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
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class SettingsActivity : BaseActivity() {

    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "app_settings"
    private val KEY_LANGUAGE = "app_language"

    //Biometrics
    private val KEY_BIOMETRICS_ENABLED = "BIOMETRICS_ENABLED"
    private val KEY_BIOMETRICS_USERNAME = "BIOMETRICS_USERNAME"
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var switchBiometrics: Switch

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

        // Start biometrics logics

        // Initialize Biometric components
        executor = ContextCompat.getMainExecutor(this)
        switchBiometrics = findViewById(R.id.switch_biometrics)
        setupBiometricPrompt()

        // Check current biometric setting
        val isBiometricsEnabled = prefs.getBoolean(KEY_BIOMETRICS_ENABLED, false)
        switchBiometrics.isChecked = isBiometricsEnabled

        // Set the listener
        switchBiometrics.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // User wants to turn it ON
                enrollBiometrics()
            } else {
                // User wants to turn it OFF
                disableBiometrics()
            }
        }

        // Disable switch if hardware isn't supported
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            switchBiometrics.isEnabled = false
        }
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // User cancelled or an error occurred
                    showToast("Authentication error: $errString")
                    switchBiometrics.isChecked = false // Toggle back off
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Authentication was successful, now save the setting
                    Amplify.Auth.getCurrentUser(
                        { authUser ->

                            val username = authUser.username

                            // Save the settings
                            prefs.edit()
                                .putBoolean(KEY_BIOMETRICS_ENABLED, true)
                                .putString(KEY_BIOMETRICS_USERNAME, username)
                                .commit()

                            runOnUiThread {
                                showToast("Biometric login enabled")
                            }
                        },
                        { error ->
                            Log.e("SettingsBiometric", "Could not get current user", error)

                            runOnUiThread {
                                showToast("Error enabling biometrics. Not signed in?")
                                switchBiometrics.isChecked = false
                            }
                        }
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("Authentication failed")
                    switchBiometrics.isChecked = false // Toggle back off
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enable Biometric Login")
            .setSubtitle("Confirm your identity to enable biometric login")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun enrollBiometrics() {
        // This function is called when the switch is turned ON
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricPrompt.authenticate(promptInfo) // Show the prompt

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showToast("Please enroll a fingerprint in your device settings first.")

            // Other errors
            else ->
                showToast("Biometric features are unavailable.")
        }
        // If auth fails, the callback will set the switch back to false
    }

    private fun disableBiometrics() {
        // This function is called when the switch is turned OFF
        prefs.edit()
            .putBoolean(KEY_BIOMETRICS_ENABLED, false)
            .remove(KEY_BIOMETRICS_USERNAME)
            .commit()
        showToast("Biometric login disabled")
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // Function to change app language dynamically
    private fun setAppLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
  }



