package com.tendertool.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_overlay)

        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val applyButton = findViewById<Button>(R.id.applyButton)

        // Cancel closes the overlay
        cancelButton.setOnClickListener { finish() }

        // Clear resets all selections
        clearButton.setOnClickListener {
            // Reset radio groups and checkboxes here
        }

        // Apply returns selections (you can pass via Intent back)
        applyButton.setOnClickListener {
            // Collect selected filters
            // Use setResult() to send back data if needed
            finish()
        }
    }
}
