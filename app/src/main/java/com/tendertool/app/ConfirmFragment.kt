package com.tendertool.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.amplifyframework.core.Amplify

class ConfirmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.partial_confirm_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameInput = view.findViewById<EditText>(R.id.usernameInputConfirm)
        val confirmationCodeInput = view.findViewById<EditText>(R.id.confirmationCodeInput)
        val confirmButton = view.findViewById<AppCompatButton>(R.id.confirmSignUpButton)

        confirmButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val code = confirmationCodeInput.text.toString()

            if (username.isEmpty() || code.isEmpty()) {
                Toast.makeText(context, "Please enter a username and code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call the Amplify Auth.confirmSignUp method
            Amplify.Auth.confirmSignUp(
                username,
                code,
                { result ->
                    Log.i("AmplifyConfirm", "Confirmation successful: $result")
                    // This is a background thread. Switch to the main thread for UI changes.
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Confirmation successful! You can now log in.", Toast.LENGTH_LONG).show()
                        // TODO: Navigate to your Login Fragment from here.
                        // Example with Navigation Component:
                        // findNavController().navigate(R.id.action_confirmFragment_to_loginFragment)
                    }
                },
                { error ->
                    Log.e("AmplifyConfirm", "Confirmation failed", error)
                    // Switch to the main thread for UI changes.
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Confirmation failed: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}