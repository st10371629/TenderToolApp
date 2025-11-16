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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tendertool.app.adapters.WatchlistAdapter
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.TenderRepository
import com.tendertool.app.src.ThemeHelper
import com.tendertool.app.src.TopBarFragment
import kotlinx.coroutines.launch
import com.tendertool.app.MyAmplifyApp

class WatchlistActivity : AppCompatActivity() {

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WatchlistAdapter
    private lateinit var spinner: ProgressBar
    private lateinit var totalTenders: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // Data
    private lateinit var repository: TenderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_watchlist)

        // Get the repository from our Application class
        repository = (application as MyAmplifyApp).tenderRepository

        // Setup all views
        setupViews()

        // Observe the local database
        observeWatchlist()

        // Start the initial data fetch
        refreshWatchlistData()
    }

    private fun setupViews() {
        // Top Bar & Nav Bar
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()
        NavBar.LoadNav(this)

        // Find Views
        totalTenders = findViewById(R.id.TotalTenders)
        recyclerView = findViewById(R.id.watchlistRecycler)
        spinner = findViewById(R.id.loadingSpinner)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Setup RecyclerView & Adapter
        adapter = WatchlistAdapter { tenderID ->
            toggleWatch(tenderID)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Setup Swipe-to-Refresh
        swipeRefreshLayout.setOnRefreshListener {
            refreshWatchlistData()
        }
    }

    /**
     * Observes the local database (Flow) for any changes
     * and submits the new list to the adapter.
     */
    private fun observeWatchlist() {
        lifecycleScope.launch {
            repository.watchlistTenders.collect { tendersFromDb ->
                // This 'collect' block runs every time the data in Room changes
                Log.d("WatchlistActivity", "Local database updated. Count: ${tendersFromDb.size}")

                // Submit the new list to ListAdapter
                adapter.submitList(tendersFromDb)

                // Update total tenders text
                totalTenders.text = "TOTAL ACTIVE TENDERS: ${tendersFromDb.size}"
            }
        }
    }

    /**
     * Called by Swipe-to-Refresh or onCreate to fetch data from the API.
     */
    private fun refreshWatchlistData() {
        // Show loading indicator
        // Only show the big spinner if the list is empty
        if (adapter.itemCount == 0) {
            spinner.visibility = View.VISIBLE
        } else {
            // Show the pull-to-refresh spinner
            swipeRefreshLayout.isRefreshing = true
        }

        lifecycleScope.launch {
            try {
                // This single call handles API fetch and saving to Room.
                repository.refreshWatchlist()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("WatchlistActivity", "Error refreshing tenders: ${e.message}")
                Toast.makeText(this@WatchlistActivity, "Failed to load watchlist", Toast.LENGTH_SHORT).show()
            } finally {
                // Hide all loading indicators
                spinner.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    /**
     * Called when the bookmark icon is clicked.
     */
    private fun toggleWatch(tenderID: String) {
        lifecycleScope.launch {
            try {
                // This single call handles the API toggle and refreshes the data.
                repository.toggleWatchlist(tenderID)

                Toast.makeText(this@WatchlistActivity, "Watchlist updated.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("WatchTry", "Error toggling tenders: ${e.message}")
                Toast.makeText(this@WatchlistActivity, "Error updating watchlist", Toast.LENGTH_SHORT).show()
            }
        }
    }
}