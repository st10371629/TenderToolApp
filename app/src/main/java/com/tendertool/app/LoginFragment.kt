package com.tendertool.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.amplifyframework.core.Amplify
import android.widget.ImageButton
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import android.content.Context
import android.content.SharedPreferences

class LoginFragment : Fragment() {

    // Biometric dialog
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    // Add SharedPreferences and keys
    private val PREFS_NAME = "app_settings"
    private val KEY_BIOMETRICS_ENABLED = "BIOMETRICS_ENABLED"
    private val KEY_BIOMETRICS_USERNAME = "BIOMETRICS_USERNAME"
    private lateinit var prefs: SharedPreferences
    private var fingerprintButton: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.partial_login_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Find your UI elements from the layout
        val usernameInput = view.findViewById<EditText>(R.id.usernameInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = view.findViewById<ImageView>(R.id.passwordToggle)
        val loginButton = view.findViewById<AppCompatButton>(R.id.confirmButton)
        fingerprintButton = view.findViewById(R.id.fingerprintLoginButton)

        executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Show error message
                    showToast("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    checkCognitoSession()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    showToast("Authentication failed")
                }
            })


        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirm with Fingerprint")
            .setSubtitle("Touch fingerprint sensor")
            .setNegativeButtonText("Use Password Instead")
            .build()



        // Password visibility toggle
        var isPasswordVisible = false
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Save cursor position
            val selectionStart = passwordInput.selectionStart
            val selectionEnd = passwordInput.selectionEnd

            // Toggle input type
            passwordInput.inputType = if (isPasswordVisible)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            // Re-apply the font so it doesn't change
            passwordInput.typeface = passwordInput.typeface

            // Restore cursor position
            passwordInput.setSelection(selectionStart, selectionEnd)

            // Change eye icon
            passwordToggle.setImageResource(if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye_on)
        }

        // Set a click listener on the login button
        loginButton.setOnClickListener {
            // Get the text from the input fields
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            // Perform basic validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter a username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call the Amplify Auth.signIn method
            Amplify.Auth.signIn(username, password,
                { result ->
                    if (result.isSignedIn) {
                        Log.i("AmplifyLogin", "Sign in successful")
                        // Switch to the main thread to update UI
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                            //Navigate to Watchlist Page
                            val intent = Intent(activity, WatchlistActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Log.i("AmplifyLogin", "Sign in not complete, next step is: ${result.nextStep}")
                    }
                },
                { error ->
                    Log.e("AmplifyLogin", "Sign in failed", error)
                    // Switch to the main thread to show an error message
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Sign in failed: ${error.cause?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }


        fingerprintButton?.setOnClickListener {
            checkAndAuthenticate()
        }
    }

    override fun onResume() {
        super.onResume()
        // checks if prefs is initialized
        if (::prefs.isInitialized) {
            checkDeviceCapability()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fingerprintButton = null // Clean up the view reference
    }

    private fun checkCognitoSession() {
        val savedUsername = prefs.getString(KEY_BIOMETRICS_USERNAME, null)
        if (savedUsername == null) {
            showToast("Biometric error. Please log in with password.")
            return
        }

        showToast("Biometric success. Checking session...")

        Amplify.Auth.fetchAuthSession(
            { session ->
                if (session.isSignedIn) {
                    // Session is valid
                    Amplify.Auth.getCurrentUser(
                        { currentUser ->
                            if (currentUser.username == savedUsername) {
                                // SUCCESS!
                                Log.i("AmplifyLogin", "Biometric login successful for $savedUsername")
                                activity?.runOnUiThread {
                                    val intent = Intent(activity, WatchlistActivity::class.java)
                                    startActivity(intent)
                                    activity?.finish() // Finish login activity
                                }
                            } else {
                                // Biometrics are for a different user who logged out
                                activity?.runOnUiThread {
                                    showToast("Biometrics enabled for a different user. Please use password.")
                                }
                            }
                        },
                        { error ->
                            activity?.runOnUiThread {
                                showToast("Could not verify user. Please use password.")
                            }
                        }
                    )
                } else {
                    // Biometric scan was OK, but the Cognito session is expired.
                    activity?.runOnUiThread {
                        showToast("Your session has expired. Please log in with your password.")
                    }
                }
            },
            { error ->
                Log.e("AmplifyLogin", "Failed to fetch session", error)
                activity?.runOnUiThread {
                    showToast("Could not verify session. Please use password.")
                }
            }
        )
    }

    private fun checkAndAuthenticate() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                showToast("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                showToast("Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showToast("Please enroll a fingerprint in your device settings.")
        }
    }

    private fun checkDeviceCapability() {
        val isEnabledInSettings = prefs.getBoolean(KEY_BIOMETRICS_ENABLED, false)
        val biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS && isEnabledInSettings) {
            fingerprintButton?.visibility = View.VISIBLE
        } else {
            fingerprintButton?.visibility = View.GONE
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}