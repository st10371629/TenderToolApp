package com.tendertool.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tendertool.app.adapters.DiscoverAdapter
import com.tendertool.app.models.BaseTender
import com.tendertool.app.models.FilterDto
import com.tendertool.app.models.PaginatedResponse
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.Retrofit
import com.tendertool.app.src.ThemeHelper
import com.tendertool.app.src.TopBarFragment
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiscoverActivity : BaseActivity() {

    // pagination
    private val PAGE_SIZE = 10
    private var currentPage = 1
    private var totalPages = 0

    // stores the 10 tenders received
    private var lastFetchedTenders: List<BaseTender> = emptyList()

    // filter state
    private var currentSortOption: String = "Descending"
    private var currentServerFilters = FilterDto(
        sort = "Descending",
        // all other fields are null/empty bc filtering is handled locally.
        dateFilter = null,
        alphaSort = null,
        sources = emptyList()
    )

    // local filter state
    private var currentClosingDateId: Int = 0
    private var currentAlphabeticalId: Int = 0
    private var currentSourceEskom: Boolean = false
    private var currentSourceEtenders: Boolean = false


    // UI stuff
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiscoverAdapter
    private lateinit var spinner: ProgressBar
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var pageIndicator: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        // Attach Fragments
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()
        NavBar.LoadNav(this)

        recyclerView = findViewById(R.id.discoverRecycler)
        adapter = DiscoverAdapter(emptyList())
        adapter.onToggleWatch = {tenderID -> toggleWatch(tenderID)}
        adapter.onCardClick = { tenderID ->
            val intent = Intent(this, TenderDetailsActivity::class.java).apply {
                putExtra(TenderDetailsActivity.TENDER_ID_KEY, tenderID)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        spinner = findViewById(R.id.loadingSpinner)

        // pagination controls setup
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        pageIndicator = findViewById(R.id.pageIndicator)

        prevButton.setOnClickListener { navigatePage(-1) }
        nextButton.setOnClickListener { navigatePage(1) }

        // filter button
        val filterButton = findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener { showFilterOverlay() }

        spinner.visibility = View.VISIBLE

        // Initial data fetch: fetches Page 1 with 10 items
        fetchTendersWithFilters()
    }

    private fun toggleWatch(tenderID: String){
        // authentication and API logic to toggle watchlist item
        lifecycleScope.launch {
            try {
                val userID = suspendCoroutine<String> { continuation ->
                    Amplify.Auth.fetchUserAttributes({ attributes ->
                        val coreID = attributes.firstOrNull { it.key.keyString == "custom:CoreID" }?.value
                        if (coreID != null) continuation.resume(coreID) else continuation.resumeWithException(IllegalStateException("CoreID not found."))
                    }, { error -> continuation.resumeWithException(error) })
                }

                Amplify.Auth.fetchAuthSession({ session ->
                    if (session.isSignedIn) {
                        val cognitoSession = session as AWSCognitoAuthSession
                        val idToken = cognitoSession.userPoolTokensResult.value?.idToken
                        val bearerToken = "Bearer $idToken"
                        lifecycleScope.launch {
                            try {
                                val api = Retrofit.api
                                api.toggleWatchlist(bearerToken, userID, tenderID)
                                Toast.makeText(this@DiscoverActivity, "Watchlist updated.", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.e("API", "Error calling API: ${e.message}")
                            }
                        }
                    }
                }, { error -> Log.e("AuthSession", "Failed to fetch session: ${error.message}", error) })

            } catch (e: Exception) {
                Log.e("DiscoverActivity", "Error toggling tenders: ${e.message}")
            }
        }
    }

    // fetches ONLY 10 items for the current page
    private fun fetchTendersWithFilters() {
        lifecycleScope.launch {
            spinner.visibility = View.VISIBLE

            // pass dto
            val filterDto = currentServerFilters.copy(
                sort = currentSortOption
            )

            Log.d("DiscoverActivity", "Fetching Page $currentPage, Size $PAGE_SIZE with MINIMAL DTO: $filterDto")

            try {
                val api = Retrofit.api

                val response: PaginatedResponse = api.fetchFilteredTenders(
                    page = currentPage,
                    pageSize = PAGE_SIZE,
                    filterDto = filterDto
                )

                // 1. store the 10 fetched items
                lastFetchedTenders = response.data

                // 2. update total pages
                totalPages = response.totalPages

                // 3. apply local filters to the set of 10 tenders
                applyLocalFilteringAndPagination()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DiscoverActivity", "Error fetching tenders: ${e.message}")
                adapter.updateData(emptyList())
                totalPages = 0
                updatePaginationControls(emptyList())
            } finally {
                spinner.visibility = View.GONE
            }
        }
    }

    private fun applyLocalFilteringAndPagination() {
        // Run client-side filtering/sorting based on current local state
        val filteredSubset = filterTendersLocally(
            lastFetchedTenders,
            currentClosingDateId,
            currentAlphabeticalId,
            currentSourceEskom,
            currentSourceEtenders
        )

        // Update the RecyclerView and controls
        adapter.updateData(filteredSubset)
        updatePaginationControls(filteredSubset)
    }

    private fun navigatePage(direction: Int) {
        val newPage = currentPage + direction

        if (newPage >= 1 && newPage <= totalPages) {
            currentPage = newPage
            // Re-fetch data from the server for the new page
            fetchTendersWithFilters()
            recyclerView.scrollToPosition(0)
        }
    }

    private fun updatePaginationControls(currentDisplayedList: List<BaseTender>) {
        prevButton.isEnabled = currentPage > 1
        nextButton.isEnabled = currentPage < totalPages

        pageIndicator.text = "Page $currentPage of $totalPages"

        val visibility = if (totalPages > 0) View.VISIBLE else View.GONE
        prevButton.visibility = visibility
        nextButton.visibility = visibility
        pageIndicator.visibility = visibility

        Log.d("DiscoverActivity", "Local filter applied. Displaying ${currentDisplayedList.size} of 10 fetched tenders.")
    }

    // filter overlay logic
    private fun showFilterOverlay() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.filter_overlay, null)
        dialog.setContentView(view)

        val applyButton = view.findViewById<Button>(R.id.applyButton)
        val clearButton = view.findViewById<Button>(R.id.clearButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener { dialog.dismiss() }


        clearButton.setOnClickListener {
            // reset UI controls state
            view.findViewById<RadioGroup>(R.id.closingDateGroup).clearCheck()
            view.findViewById<RadioGroup>(R.id.alphabeticalGroup).clearCheck()
            view.findViewById<CheckBox>(R.id.sourceEskom).isChecked = false
            view.findViewById<CheckBox>(R.id.sourceEtenders).isChecked = false

            // reset local filter state and apply immediately
            currentClosingDateId = 0
            currentAlphabeticalId = 0
            currentSourceEskom = false
            currentSourceEtenders = false

            applyLocalFilteringAndPagination()
            dialog.dismiss()
        }

        applyButton.setOnClickListener {
            // Read and SAVE the new LOCAL filter state
            currentClosingDateId = view.findViewById<RadioGroup>(R.id.closingDateGroup).checkedRadioButtonId
            currentAlphabeticalId = view.findViewById<RadioGroup>(R.id.alphabeticalGroup).checkedRadioButtonId
            currentSourceEskom = view.findViewById<CheckBox>(R.id.sourceEskom).isChecked
            currentSourceEtenders = view.findViewById<CheckBox>(R.id.sourceEtenders).isChecked

            spinner.visibility = View.VISIBLE

            // Apply filters to the current 10 items without re-fetching from the server
            applyLocalFilteringAndPagination()

            spinner.visibility = View.GONE
            dialog.dismiss()
        }

        dialog.show()
    }

    // filter logic
    private fun filterTendersLocally(
        tendersToFilter: List<BaseTender>,
        closingDateId: Int,
        alphabeticalId: Int,
        sourceEskom: Boolean,
        sourceEtenders: Boolean
    ): List<BaseTender> {
        Log.d("DiscoverActivity", "Applying LOCAL Filtering to 10 items.")

        var filtered = tendersToFilter

        // Filter by closing date
        filtered = when (closingDateId) {
            R.id.closing1Week -> filtered.filter { it.isClosingWithinDays(7) }
            R.id.closing1Month -> filtered.filter { it.isClosingWithinDays(30) }
            R.id.closing3Months -> filtered.filter { it.isClosingWithinDays(90) }
            else -> filtered
        }

        // Filter by source
        val selectedSources = mutableListOf<String>()
        if (sourceEskom) selectedSources.add("Eskom")
        if (sourceEtenders) selectedSources.add("eTenders")
        if (selectedSources.isNotEmpty()) filtered =
            filtered.filter { selectedSources.contains(it.source) }

        // Sort alphabetically
        filtered = when (alphabeticalId) {
            R.id.alphabetAsc -> filtered.sortedBy { it.title }
            R.id.alphabetDesc -> filtered.sortedByDescending { it.title }
            // Keep existing server sort if no alphabetical sort is selected
            else -> filtered
        }

        return filtered
    }
}