package com.tendertool.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.Retrofit
import com.tendertool.app.src.ThemeHelper
import com.tendertool.app.src.TopBarFragment
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TenderDetailsActivity : BaseActivity() {

    companion object {
        const val TENDER_ID_KEY = "TENDER_ID"
    }

    private lateinit var spinner: ProgressBar
    private lateinit var contentScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tender_details)

        // Attach TopBarFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // Attach nav bar listeners
        NavBar.LoadNav(this)

        // Find views
        spinner = findViewById(R.id.detailLoadingSpinner)
        contentScrollView = findViewById(R.id.contentScrollView)

        // Get the Tender ID from the Intent
        val tenderId = intent.getStringExtra(TENDER_ID_KEY)

        if (tenderId == null) {
            Log.e("TenderDetails", "Tender ID was null. Finishing activity.")
            Toast.makeText(this, "Error: Could not load tender.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Fetch the details
        fetchDetails(tenderId)
    }

    private fun fetchDetails(tenderId: String) {
        // Show spinner, hide content
        spinner.visibility = View.VISIBLE
        contentScrollView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val api = Retrofit.api

                //val tender = api.fetchTenderDetails(tenderId)

                // data fetched successfully, populate the UI
                //populateUi(tender)

                // Hide spinner, show content
                spinner.visibility = View.GONE
                contentScrollView.visibility = View.VISIBLE

            } catch (e: Exception) {
                // Handle error
                Log.e("TenderDetails", "Error fetching tender details: ${e.message}", e)
                spinner.visibility = View.GONE
                Toast.makeText(this@TenderDetailsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateUi(tender: BaseTender) {
        // Find all the detail TextViews
        val title: TextView = findViewById(R.id.detailTenderTitle)
        val status: TextView = findViewById(R.id.detailTenderStatus)
        val tenderID: TextView = findViewById(R.id.detailTenderID)
        val source: TextView = findViewById(R.id.detailTenderSource)
        val publishedDate: TextView = findViewById(R.id.detailPublishedDate)
        val closingDate: TextView = findViewById(R.id.detailClosingDate)
        val description: TextView = findViewById(R.id.detailDescription)

        title.text = tender.title
        status.text = tender.status
        tenderID.text = tender.tenderID // Set Tender ID
        source.text = tender.source
        description.text = tender.description ?: "No description provided."

    }
}