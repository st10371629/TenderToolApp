package com.tendertool.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tendertool.app.R
import com.tendertool.app.models.*

class DiscoverAdapter(private var tenders: List<BaseTender>) : RecyclerView.Adapter<DiscoverAdapter.TenderViewHolder>()
{
    inner class TenderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val closingDateText: TextView = view.findViewById(R.id.closingDateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenderViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_tender_card, parent, false)
        return TenderViewHolder(view)
    }

    override fun onBindViewHolder(holder: TenderViewHolder, position: Int)
    {
        val tender = tenders[position]
        holder.titleText.text = tender.title
        holder.locationText.text = tender.source
        holder.closingDateText.text = tender.closingDate
    }

    override fun getItemCount(): Int = tenders.size

    fun updateData(newTenders: List<BaseTender>)
    {
        tenders = newTenders
        notifyDataSetChanged()
    }
}