package com.tendertool.app

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendertool.app.adapters.DiscoverAdapter
import com.tendertool.app.models.BaseTender
import com.tendertool.app.src.NavBar
import com.tendertool.app.src.Retrofit
import com.tendertool.app.src.TopBarFragment
import kotlinx.coroutines.launch

class DiscoverActivity : BaseActivity() {

    //private variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiscoverAdapter
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        // Attach TopBarFragment to the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.topBarContainer, TopBarFragment())
            .commit()

        // attach nav bar listeners
        NavBar.LoadNav(this)

        recyclerView = findViewById(R.id.discoverRecycler)
        adapter = DiscoverAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        spinner = findViewById(R.id.loadingSpinner)

        // Show spinner before fetching data
        spinner.visibility = View.VISIBLE

        //fetch data from the API
        fetchTenders()
    }

    private fun fetchTenders() {
        lifecycleScope.launch {
            try {
                val api = Retrofit.api // retrofit instance
                val tenders: List<BaseTender> = api.fetchTenders()

                // update RecyclerView
                adapter.updateData(tenders)

                // hide spinner once data is loaded
                spinner.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
                spinner.visibility = View.GONE // hide even on error
            }
        }
    }
}
