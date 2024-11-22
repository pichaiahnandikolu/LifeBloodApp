package com.example.life_blood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class sample_show : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_show)

        val donorData = intent.getSerializableExtra("donorData") as? ArrayList<Map<String, String>>

        donorData?.let {
            // Use donorData to populate a RecyclerView or TextView
            // For example, setting up a RecyclerView adapter
            val recyclerView = findViewById<RecyclerView>(R.id.donorsRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = DonorAdapter(donorData)
        }


    }
}