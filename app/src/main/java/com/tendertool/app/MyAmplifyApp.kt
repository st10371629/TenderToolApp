package com.tendertool.app

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

class MyAmplifyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            // Add the Cognito Auth plugin to Amplify
            Amplify.addPlugin(AWSCognitoAuthPlugin())

            // Configure Amplify with the auto-generated file
            Amplify.configure(applicationContext)

            Log.i("MyAmplifyApp", "Initialized Amplify successfully")
        } catch (e: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", e)
        }
    }
}