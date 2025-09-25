package com.tendertool.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.partial_register_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find your UI elements from the layout
        val usernameInput = view.findViewById<EditText>(R.id.usernameInput)
        val emailInput = view.findViewById<EditText>(R.id.EmailInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val passwordConfirmInput = view.findViewById<EditText>(R.id.passwordConfirmInput)
        val confirmButton = view.findViewById<AppCompatButton>(R.id.confirmButton)

        // Set a click listener on the register button
        confirmButton.setOnClickListener {
            // Get the text from the input fields
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = passwordConfirmInput.text.toString()

            // Perform basic validation
            if (password != confirmPassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call the Amplify Auth.signUp method
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build()

            Amplify.Auth.signUp(username, password, options,
                { result ->
                    Log.i("AmplifyRegister", "Sign up successful: $result")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Registration successful! Please check your email for a confirmation code.", Toast.LENGTH_LONG).show()
                        // TODO: Navigate to your confirmation code fragment from here.
                    }
                },
                { error ->
                    Log.e("AmplifyRegister", "Sign up failed", error)
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Registration failed: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}
