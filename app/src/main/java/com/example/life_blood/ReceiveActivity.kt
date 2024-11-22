package com.example.life_blood
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class ReceiveActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 100
    private lateinit var btnFetchLocation: ImageView
    private lateinit var btnFindDonors: Button
    private lateinit var tvCity: TextView
    private val dbHelper = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)
        btnFetchLocation=findViewById(R.id.locationIcon)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        btnFindDonors = findViewById(R.id.find)
        tvCity = findViewById(R.id.location)


        btnFetchLocation.setOnClickListener {
            Toast.makeText(this, "Fetching location...", Toast.LENGTH_SHORT).show()
            getLastLocation()
        }

        val btnfind = findViewById<Button>(R.id.find)



        btnfind.setOnClickListener {
            findData()
        }

    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]

                            val city = address.locality ?: "City not found"

                            tvCity.text = city



                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error fetching address: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            askPermission()
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Please provide the required permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun findData() {
        val city = tvCity.text.toString()
        val bloodGroup = getSelectedBloodType()

        if (city.isNotBlank() && bloodGroup.isNotBlank()) {
            val donors = dbHelper.getDonorsByCityAndBloodGroup(city, bloodGroup)
            if (donors.isNotEmpty()) {
                val donorDetails = ArrayList(donors.map { donor ->
                    mapOf(
                        "name" to donor.name,
                        "age" to donor.age,
                        "gender" to donor.gender,
                        "phone" to donor.phone,
                        "email" to donor.email,
                        "city" to donor.city,
                        "bloodGroup" to donor.bloodGroup
                    )
                })

                val intent = Intent(this, sample_show::class.java)
                intent.putExtra("donorData", donorDetails)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No donors found for $bloodGroup in $city.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter both city and blood group.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getSelectedBloodType(): String {
        val selectedBloodGroup = when {
            findViewById<RadioButton>(R.id.radio_a_positive).isChecked -> "A+"
            findViewById<RadioButton>(R.id.radio_a_negative).isChecked -> "A-"
            findViewById<RadioButton>(R.id.radio_b_positive).isChecked -> "B+"
            findViewById<RadioButton>(R.id.radio_b_negative).isChecked -> "B-"
            findViewById<RadioButton>(R.id.radio_ab_positive).isChecked -> "AB+"
            findViewById<RadioButton>(R.id.radio_ab_negative).isChecked -> "AB-"
            findViewById<RadioButton>(R.id.radio_o_positive).isChecked -> "O+"
            findViewById<RadioButton>(R.id.radio_o_negative).isChecked -> "O-"
            else -> ""
        }
        Log.d("BloodType", "Selected Blood Group: $selectedBloodGroup")
        return selectedBloodGroup
    }
}