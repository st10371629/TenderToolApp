package com.tendertool.app

import android.os.Bundle
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

        //fetch data from the API
        fetchTenders()
    }

    private fun fetchTenders()
    {
        lifecycleScope.launch{
            try
            {
                val api = Retrofit.api //retrofit instance
                val tenders: List<BaseTender> = api.fetchTenders()
                adapter.updateData(tenders)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}
