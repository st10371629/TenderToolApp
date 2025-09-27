package com.tendertool.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment

class TenderCardFragment : Fragment() {

    private var isBookmarked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tender_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookmarkButton = view.findViewById<AppCompatImageButton>(R.id.bookmarkButton)

        bookmarkButton.setOnClickListener {
            // Toggle selected state
            bookmarkButton.isSelected = !bookmarkButton.isSelected
            // Force redraw
            bookmarkButton.setImageDrawable(bookmarkButton.drawable)
        }
    }
}
