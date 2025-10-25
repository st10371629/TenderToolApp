package com.tendertool.app.adapters

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.tendertool.app.R
import com.tendertool.app.models.*
import com.tendertool.app.src.DateUtil.toSimpleDate

class DiscoverAdapter(private var tenders: List<BaseTender>) : RecyclerView.Adapter<DiscoverAdapter.TenderViewHolder>()
{
    var onToggleWatch: ((String) -> Unit)? = null
    var onCardClick: ((String) -> Unit)? = null // Passes the tenderID as a String


    inner class TenderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val closingDateText: TextView = view.findViewById(R.id.closingDateText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val bookmarkButton: AppCompatImageButton = view.findViewById(R.id.bookmarkButton)
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
        holder.closingDateText.text = tender.closingDate.toSimpleDate()
        holder.statusText.text = tender.status

        holder.bookmarkButton.setOnClickListener {
            val tender = tenders[position]

            //haptic Feedback
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            //button scale animation
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(50)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(50)
                        .start()
                }
                .start()

            onToggleWatch?.invoke(tender.tenderID)
        }
    }

    override fun getItemCount(): Int = tenders.size

    fun updateData(newTenders: List<BaseTender>)
    {
        tenders = newTenders
        notifyDataSetChanged()
    }
}