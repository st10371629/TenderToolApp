package com.tendertool.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.tendertool.app.src.NavBar
import kotlin.math.log

class SettingsActivity : BaseActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        //initialise logout button
        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            //fetches session - checks if there is a user logged in before attempting to sign out.
            Amplify.Auth.fetchAuthSession(
                {session ->
                    runOnUiThread { //run on ui thread since amplify default runs in the bg
                        if(!session.isSignedIn)
                        {
                            //in the event there is no user session active
                            Toast.makeText(this, "Sign out unsuccessful! No active session.", Toast.LENGTH_SHORT).show()
                            Log.w("AuthSettings", "No active session.")
                        }
                        else
                        {
                            try
                            {
                                //perfroms the actual sign out
                                Amplify.Auth.signOut(
                                    {
                                        Log.i("AuthSettings", "Signed out successfully.")
                                        runOnUiThread { //run on ui thread since amplify default runs in the bg
                                            Toast.makeText(this, "Sign out successful!", Toast.LENGTH_SHORT).show()
                                        }
                                        //redirect to login
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                )
                            }
                            catch (e: Exception)
                            {
                                e.printStackTrace()
                                Toast.makeText(this, "Sign out unsuccessful.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                {error ->
                    Log.e("AuthSettings", "Failed to retrieve session.", error)
                }
            )
        }

        // attach nav bar listeners
        NavBar.LoadNav(this)
    }
}