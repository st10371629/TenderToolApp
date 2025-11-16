package com.tendertool.app

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.tendertool.app.db.AppDatabase
import com.tendertool.app.src.TenderRepository
import com.tendertool.app.src.Retrofit as RetrofitClient
import com.tendertool.app.src.Notifications

class MyAmplifyApp : Application() {

    // Lazily create the database when it's first needed
    private val database by lazy { AppDatabase.getDatabase(this) }

    private val apiService by lazy {
        RetrofitClient.api
    }

    // Lazily create the repository
    val tenderRepository by lazy {
        TenderRepository(database.tenderDao(), apiService)
    }

    override fun onCreate() {
        super.onCreate()

        //create our Notiification channel
        Notifications.createChannel(this)

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