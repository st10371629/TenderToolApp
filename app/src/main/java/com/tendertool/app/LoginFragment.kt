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

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.partial_login_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find your UI elements from the layout
        val usernameInput = view.findViewById<EditText>(R.id.usernameInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = view.findViewById<ImageView>(R.id.passwordToggle)
        val loginButton = view.findViewById<AppCompatButton>(R.id.confirmButton)

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
                        // This case handles things like MFA or Biometrics...
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

        //google sso
        val googleSignInImage = view.findViewById<ImageView>(R.id.googleSignInImage)
        googleSignInImage.setOnClickListener {
            val sheet = GoogleSignInSheet()
            sheet.show(parentFragmentManager, "GoogleSignInSheet") }
    }
}