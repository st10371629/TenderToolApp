package com.tendertool.app

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DiscoverActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        // attach nav bar listeners
        setupNavBar()

        val cardContainer = findViewById<LinearLayout>(R.id.cardContainer)

        for (i in 1..3) {
            val card = TextView(this).apply {
                text = "Tender Card #$i"
                textSize = 18f
                setTextColor(Color.BLACK)
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.LTGRAY)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 20)
                }
            }
            cardContainer.addView(card)
        }
    }
}
