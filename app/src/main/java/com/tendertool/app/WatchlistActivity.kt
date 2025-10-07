package com.tendertool.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.tendertool.app.adapters.DiscoverAdapter
import com.tendertool.app.adapters.WatchlistAdapter
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.Retrofit
import com.tendertool.app.src.TopBarFragment
import kotlinx.coroutines.launch
import com.amplifyframework.core.Amplify
import com.tendertool.app.src.ThemeHelper
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WatchlistActivity : AppCompatActivity() {

    //private variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WatchlistAdapter
    private lateinit var spinner: ProgressBar
    private var allTenders: List<BaseTender> = emptyList() // store fetched data
    private lateinit var totalTenders: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_watchlist)

        // Attach TopBarFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // attach nav bar listeners
        NavBar.LoadNav(this)

        totalTenders = findViewById(R.id.TotalTenders)
        recyclerView = findViewById(R.id.watchlistRecycler)
        adapter = WatchlistAdapter(emptyList())
        adapter.onToggleWatch = {tenderID -> toggleWatch(tenderID)}

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        spinner = findViewById(R.id.loadingSpinner)

        // Show spinner before fetching data
        spinner.visibility = View.VISIBLE

        //fetch data from the API
        fetchWatchlist()
    }

    private fun toggleWatch(tenderID: String){
        lifecycleScope.launch {
            try {
                //fetch CoreID using a coroutine suspend function
                val userID = suspendCoroutine<String> { continuation ->
                    Amplify.Auth.fetchUserAttributes(
                        { attributes ->
                            val coreID =
                                attributes.firstOrNull { it.key.keyString == "custom:CoreID" }?.value

                            //return necessary response
                            if (coreID != null) {
                                Log.d("Watchlist", "CoreID: ${coreID}")
                                continuation.resume(coreID)
                            } else {
                                continuation.resumeWithException(
                                    IllegalStateException("CoreID not found.")
                                )
                            }
                        },
                        { error ->
                            Log.e("Watchlist", "Failed to retrieve attribute.", error)
                            continuation.resumeWithException(error)
                        })
                }

                Amplify.Auth.fetchAuthSession(
                    { session ->
                        if (session.isSignedIn) {
                            val cognitoSession = session as AWSCognitoAuthSession
                            val idToken = cognitoSession.userPoolTokensResult.value?.idToken
                            Log.d("AuthSession", "ID Token: $idToken")

                            val bearerToken = "Bearer $idToken"
                            lifecycleScope.launch {
                                try {
                                    val api = Retrofit.api
                                    val result = api.toggleWatchlist(bearerToken, userID, tenderID)

                                    //Log and notify
                                    Log.d("DiscoverActivity", "Toggled tender: ${result.tenderID}")
                                    Toast.makeText(this@WatchlistActivity, "Watchlist updated.", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Log.e("API", "Error calling API: ${e.message}")
                                }
                            }
                        } else {
                            Log.e("AuthSession", "User not signed in")
                        }
                    },
                    { error ->
                        Log.e("AuthSession", "Failed to fetch session: ${error.message}", error)
                    }
                )
                //refresh watchlist
                fetchWatchlist()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("WatchlistActivity", "Error toggling tenders: ${e.message}")
            }
        }
    }

    private fun fetchWatchlist()
    {
        lifecycleScope.launch {
            try{
                //fetch CoreID using a coroutine suspend function
                val userID = suspendCoroutine<String> { continuation ->
                    Amplify.Auth.fetchUserAttributes(
                        { attributes ->
                            val coreID = attributes.firstOrNull {it.key.keyString == "custom:CoreID"}?.value

                            //return necessary response
                            if (coreID != null) {
                                Log.d("Watchlist", "CoreID: ${coreID}")
                                continuation.resume(coreID)
                            }
                            else {
                                continuation.resumeWithException(
                                    IllegalStateException("CoreID not found.")
                                )
                            }
                        },
                        { error ->
                            Log.e("Watchlist", "Failed to retrieve attribute.", error)
                            continuation.resumeWithException(error)
                        })
                }

                val api = Retrofit.api
                val tenders: List<BaseTender> = api.getWatchlist(userID)

                //store fetched data in allTenders
                allTenders = tenders

                //set totalTenders Text
                totalTenders.text = "TOTAL ACTIVE TENDERS: ${tenders.size}"
                Log.d("WatchlistActivity", "Fetched ${tenders.size} tenders")

                // update RecyclerView
                adapter.updateData(tenders)

                // hide spinner
                spinner.visibility = View.GONE
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.e("WatchlistActivity", "Error fetching tenders: ${e.message}")
                spinner.visibility = View.GONE
            }
        }
    }
}