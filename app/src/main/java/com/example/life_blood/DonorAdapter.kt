package com.example.life_blood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonorAdapter(private val donors: ArrayList<Map<String, String>>?) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    inner class DonorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.donorName)
        val cityTextView: TextView = view.findViewById(R.id.donorCity)
        val phoneTextView: TextView = view.findViewById(R.id.donorPhone)
        val emailTextView: TextView = view.findViewById(R.id.donorEmail)
        val bloodTextView: TextView = view.findViewById(R.id.donorblood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context) .inflate(R.layout.card_view, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donors!![position]
        holder.nameTextView.text = donor["name"]
        holder.cityTextView.text = donor["city"]
        holder.phoneTextView.text = donor["phone"]
        holder.emailTextView.text = donor["email"]
        holder.bloodTextView.text = donor["bloodGroup"]

    }

    override fun getItemCount(): Int {
        return donors?.size ?: 0
    }
}
