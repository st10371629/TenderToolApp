package com.tendertool.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.AuthProvider
import com.google.android.gms.common.SignInButton

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
        val loginButton = view.findViewById<AppCompatButton>(R.id.confirmButton)
        val googleSignInButton = view.findViewById<SignInButton>(R.id.googleSignInButton)

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
                            //Navigate to Settings Page
                            val intent = Intent(activity, SettingsActivity::class.java)
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

        // --- Google Sign-In ---
        googleSignInButton.setOnClickListener {
            Amplify.Auth.signInWithSocialWebUI(
                AuthProvider.google(), // Use the imported AuthProvider
                requireActivity(),
                { result ->
                    Log.i("AmplifyLogin", "Google sign in successful: $result")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                        // TODO: Navigate to DiscoverActivity
                    }
                },
                { error ->
                    Log.e("AmplifyLogin", "Google sign in failed", error)
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Sign in failed: ${error.cause?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}

