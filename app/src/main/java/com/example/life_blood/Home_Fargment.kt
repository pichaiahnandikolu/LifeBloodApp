package com.example.life_blood

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class Home_Fargment : Fragment() {

    private lateinit var requestBloodCard: CardView
    private lateinit var donateBloodCard: CardView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_home__fargment, container, false)

        // Initialize views
        requestBloodCard = rootView.findViewById(R.id.requestBloodCard)
        donateBloodCard = rootView.findViewById(R.id.donateBloodCard)


        // Initialize the database helper
        databaseHelper = DatabaseHelper(requireContext())


        donateBloodCard.setOnClickListener {
            if (databaseHelper.isUserLoggedIn()) {

                Toast.makeText(requireContext(), "Welcome to the logged-in activity!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), ReceiveActivity::class.java))
            } else {

                startActivity(Intent(requireContext(), DonateActivity::class.java))
            }
        }

        requestBloodCard.setOnClickListener {
            startActivity(Intent(requireContext(), ReceiveActivity::class.java))
        }

        return rootView
    }
}
