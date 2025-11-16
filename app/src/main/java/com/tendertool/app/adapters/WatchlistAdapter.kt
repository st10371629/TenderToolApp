package com.tendertool.app.adapters

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tendertool.app.R
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.DateUtil.toSimpleDate

class WatchlistAdapter(
    private val onToggleWatch: (String) -> Unit
) : ListAdapter<BaseTender, WatchlistAdapter.TenderViewHolder>(TenderDiffCallback()) {

    inner class TenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val closingDateText: TextView = view.findViewById(R.id.closingDateText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val bookmarkButton: AppCompatImageButton = view.findViewById(R.id.bookmarkButton)

        init {
            bookmarkButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tender = getItem(position)

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

                    onToggleWatch(tender.tenderID)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_tender_card, parent, false)
        return TenderViewHolder(view)
    }

    override fun onBindViewHolder(holder: TenderViewHolder, position: Int) {
        val tender = getItem(position)
        holder.titleText.text = tender.title
        holder.locationText.text = tender.source
        holder.closingDateText.text = tender.closingDate.toSimpleDate()
        holder.statusText.text = tender.status
    }
}

class TenderDiffCallback : DiffUtil.ItemCallback<BaseTender>() {
    override fun areItemsTheSame(oldItem: BaseTender, newItem: BaseTender): Boolean {
        return oldItem.tenderID == newItem.tenderID
    }

    override fun areContentsTheSame(oldItem: BaseTender, newItem: BaseTender): Boolean {
        // We compare fields manually because BaseTender is not a data class
        return oldItem.title == newItem.title &&
                oldItem.status == newItem.status &&
                oldItem.closingDate == newItem.closingDate &&
                oldItem.source == newItem.source
    }
}