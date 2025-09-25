package com.tendertool.app.src

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tendertool.app.R

class TopBarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationButton = view.findViewById<ImageButton>(R.id.notificationButton)
        notificationButton.setOnClickListener {
            // Inflate the overlay layout
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.notification_overlay, null)

            // Create PopupWindow
            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true // focusable so taps outside dismiss it
            )

            // Optional: shadow/elevation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10f
            }

            // Dim background behind the popup
            popupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.transparent)))
            popupWindow.isOutsideTouchable = true

            // Show popup anchored to the bell
            popupWindow.showAsDropDown(notificationButton, -50, 0)
        }
    }
}
