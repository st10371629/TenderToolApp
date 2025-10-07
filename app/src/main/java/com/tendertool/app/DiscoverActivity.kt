package com.tendertool.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tendertool.app.adapters.DiscoverAdapter
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.Retrofit
import com.tendertool.app.src.ThemeHelper
import com.tendertool.app.src.TopBarFragment
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiscoverActivity : BaseActivity() {

    //private variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiscoverAdapter
    private lateinit var spinner: ProgressBar
    private var allTenders: List<BaseTender> = emptyList() // store fetched data

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        // Attach TopBarFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // attach nav bar listeners
        NavBar.LoadNav(this)

        recyclerView = findViewById(R.id.discoverRecycler)
        adapter = DiscoverAdapter(emptyList())
        adapter.onToggleWatch = {tenderID -> toggleWatch(tenderID)}

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        spinner = findViewById(R.id.loadingSpinner)

        // Filter button
        val filterButton = findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener { showFilterOverlay() }

        // Show spinner before fetching data
        spinner.visibility = View.VISIBLE

        //fetch data from the API
        fetchTenders()
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
                                Log.d("Discover", "CoreID: ${coreID}")
                                continuation.resume(coreID)
                            } else {
                                continuation.resumeWithException(
                                    IllegalStateException("CoreID not found.")
                                )
                            }
                        },
                        { error ->
                            Log.e("Discover", "Failed to retrieve attribute.", error)
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
                                    Toast.makeText(this@DiscoverActivity, "Watchlist updated.", Toast.LENGTH_SHORT).show()
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

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DiscoverActivity", "Error toggling tenders: ${e.message}")
            }
        }
    }

    private fun fetchTenders() {
        lifecycleScope.launch {
            try {
                val api = Retrofit.api
                val tenders: List<BaseTender> = api.fetchTenders()

                // Store fetched data in allTenders
                allTenders = tenders
                Log.d("DiscoverActivity", "Fetched ${tenders.size} tenders")

                // update RecyclerView
                adapter.updateData(tenders)

                // hide spinner
                spinner.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DiscoverActivity", "Error fetching tenders: ${e.message}")
                spinner.visibility = View.GONE
            }
        }
    }

    private fun showFilterOverlay() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.filter_overlay, null)
        dialog.setContentView(view)

        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val clearButton = view.findViewById<Button>(R.id.clearButton)
        val applyButton = view.findViewById<Button>(R.id.applyButton)

        cancelButton.setOnClickListener { dialog.dismiss() }

        clearButton.setOnClickListener {
            view.findViewById<RadioGroup>(R.id.closingDateGroup).clearCheck()
            view.findViewById<RadioGroup>(R.id.alphabeticalGroup).clearCheck()
            view.findViewById<CheckBox>(R.id.sourceEskom).isChecked = false
            view.findViewById<CheckBox>(R.id.sourceEtenders).isChecked = false
        }

        applyButton.setOnClickListener {
            // Read selections
            val closingDateId = view.findViewById<RadioGroup>(R.id.closingDateGroup).checkedRadioButtonId
            val alphabeticalId = view.findViewById<RadioGroup>(R.id.alphabeticalGroup).checkedRadioButtonId
            val sourceEskom = view.findViewById<CheckBox>(R.id.sourceEskom).isChecked
            val sourceEtenders = view.findViewById<CheckBox>(R.id.sourceEtenders).isChecked

            // Show spinner
            spinner.visibility = View.VISIBLE

            // Run filtering and update the recycler view
            lifecycleScope.launch {
                filterTenders(closingDateId, alphabeticalId, sourceEskom, sourceEtenders)

                // Hide spinner and dismiss dialog after filtering
                spinner.visibility = View.GONE
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun filterTenders(
        closingDateId: Int,
        alphabeticalId: Int,
        sourceEskom: Boolean,
        sourceEtenders: Boolean
    ) {
        Log.d("DiscoverActivity", "Filtering: closingDateId=$closingDateId, alphabeticalId=$alphabeticalId, sourceEskom=$sourceEskom, sourceEtenders=$sourceEtenders")

        var filtered = allTenders
        Log.d("DiscoverActivity", "Initial tenders count: ${filtered.size}")

        // Filter by closing date
        filtered = when (closingDateId) {
            R.id.closing1Week -> filtered.filter { it.isClosingWithinDays(7) }
            R.id.closing1Month -> filtered.filter { it.isClosingWithinDays(30) }
            R.id.closing3Months -> filtered.filter { it.isClosingWithinDays(90) }
            else -> filtered
        }
        Log.d("DiscoverActivity", "After closing date filter: ${filtered.size}")

        // Filter by source
        val selectedSources = mutableListOf<String>()
        if (sourceEskom) selectedSources.add("Eskom")
        if (sourceEtenders) selectedSources.add("eTenders")
        if (selectedSources.isNotEmpty()) filtered =
            filtered.filter { selectedSources.contains(it.source) }
        Log.d("DiscoverActivity", "After source filter: ${filtered.size}")

        // Sort alphabetically
        filtered = when (alphabeticalId) {
            R.id.alphabetAsc -> filtered.sortedBy { it.title }
            R.id.alphabetDesc -> filtered.sortedByDescending { it.title }
            else -> filtered
        }
        Log.d("DiscoverActivity", "After sorting: ${filtered.size}")

        // Update RecyclerView
        adapter.updateData(filtered)
    }
}
