package com.tendertool.app

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.core.Amplify
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.tendertool.app.R
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.Analyticals
import com.tendertool.app.src.Retrofit
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AnalyticsFragment : Fragment(R.layout.fragment_analytics_card) {
    private lateinit var pieChart: PieChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart = view.findViewById(R.id.pieChartClosingSoon)
        pieChart.centerText = "Loading..."
        setupPieChart()

        // Fetch watchlist async and update chart
        lifecycleScope.launch {
            try {
                Log.d("AnalyticsFragment", "Fetching watchlist...")
                val watchlist = fetchWatchlist()
                Log.d("AnalyticsFragment", "Fetched watchlist: size = ${watchlist.size}")
                updateChartWith(watchlist)
                Log.d("AnalyticsFragment", "Chart updated with watchlist")
            } catch (e: Exception) {
                Log.e("AnalyticsFragment", "Error in analytics flow: ${e.message}")
            }
        }
    }

    private suspend fun fetchWatchlist() : List<BaseTender> = suspendCoroutine { continuation ->
        Amplify.Auth.fetchUserAttributes(
            { attributes ->
                val coreID = attributes.firstOrNull { it.key.keyString == "custom:CoreID" }?.value

                if (coreID != null) {
                    Log.d("AnalyticsFragment", "CoreID: $coreID")

                    // Now call your API
                    lifecycleScope.launch {
                        try {
                            val api = Retrofit.api
                            val tenders = api.getWatchlist(coreID)
                            continuation.resume(tenders)
                        } catch (e: Exception) {
                            Log.e("AnalyticsFragment", "Error fetching tenders: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }

                } else {
                    continuation.resumeWithException(
                        IllegalStateException("CoreID not found in Cognito attributes.")
                    )
                }
            },
            { error ->
                Log.e("AnalyticsFragment", "Failed to retrieve user attributes.", error)
                continuation.resumeWithException(error)
            }
        )
    }

    private fun setupPieChart() {
        pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            setEntryLabelTextSize(12f)
            setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
            setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.grey))
            setDrawEntryLabels(true)
            setDrawSlicesUnderHole(false)

            setCenterTextSize(14f)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                isEnabled = true
            }

            animateY(700)
        }
    }

    private fun updateChartWith(watchlist: List<BaseTender>) {
        val result = Analyticals.calculateClosingSoon(watchlist, daysWindow = 7L)

        val total = result.closingSoon + result.notClosingSoon
        val entries = mutableListOf<PieEntry>()

        if (total == 0) {
            // no data case
            entries.add(PieEntry(1f, "No tenders found"))
        } else {
            entries.add(PieEntry(result.closingSoon.toFloat(), "Closing Soon"))
            entries.add(PieEntry(result.notClosingSoon.toFloat(), "Closing Later"))
        }

        val ds = PieDataSet(entries, "")
        ds.sliceSpace = 2f
        ds.selectionShift = 6f

        // color palette: closing soon (red), later (purple)
        val colors = mutableListOf<Int>()
        colors.add(ContextCompat.getColor(requireContext(),
            if (total > 0) R.color.accent_red else R.color.grey))
        colors.add(ContextCompat.getColor(requireContext(),
            if (total > 0) R.color.primary_dark_blue else R.color.grey))
        ds.colors = colors

        val pieData = PieData(ds)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        pieData.setValueTextSize(12f)
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
        pieData.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        pieChart.data = pieData
        pieChart.centerText = if (total == 0) "No watchlist data" else "Watchlist\n${total} tenders"
        if (total == 0) ds.colors = listOf(ContextCompat.getColor(requireContext(), R.color.grey))
        pieChart.invalidate()
    }
}