package com.tendertool.app.adapters

import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.content.Context
import aws.smithy.kotlin.runtime.telemetry.trace.SpanStatus
import com.tendertool.app.R
import com.tendertool.app.models.BaseTender

class TenderDetailsAdapter(private val context: Context, private val rootView: View)
{
    private val title: TextView = rootView.findViewById(R.id.detailTenderTitle)
    private val status: TextView = rootView.findViewById(R.id.detailTenderStatus)
    private val tenderID: TextView = rootView.findViewById(R.id.detailTenderID)
    private val source: TextView = rootView.findViewById(R.id.detailTenderSource)
    private val publishedDate: TextView = rootView.findViewById(R.id.detailPublishedDate)
    private val closingDate: TextView = rootView.findViewById(R.id.detailClosingDate)
    private val description: TextView = rootView.findViewById(R.id.detailDescription)

    fun bind(tender: BaseTender)
    {
        title.text = tender.title
        tenderID.text = tender.tenderID
        source.text = tender.source
        description.text = tender.description ?: "No description provided."

        //format dates
        publishedDate.text = tender.publishedDate.take(10)
        closingDate.text = tender.closingDate.take(10)

        //status colouring
        status.text = tender.status
        status.setBackgroundColor((getStatusColor(tender.status)))
    }

    private fun getStatusColor(status: String): Int {
        return when (status.lowercase())
        {
            "open" -> Color.parseColor("#4CAF50")
            "closed" -> Color.parseColor("#F44336")
            else -> Color.parseColor("#9E9E9E")
        }
    }
}