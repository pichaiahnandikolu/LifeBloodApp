package com.example.life_blood

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""


        navigationView = findViewById(R.id.nav_view)


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = getColor(R.color.black)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        databaseHelper = DatabaseHelper(this)
        val userName = databaseHelper.getLoggedInUserName()
        if (databaseHelper.isUserLoggedIn()) {
            findViewById<TextView>(R.id.title).text = " Welcome Back $userName"

            val headerView = navigationView.getHeaderView(0) // Access the header view
            val headerUsername = headerView.findViewById<TextView>(R.id.userEmail)
            val headerEmail = headerView.findViewById<TextView>(R.id.userName)
            val headerImage = headerView.findViewById<ImageView>(R.id.profilePicture)
        }

    navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {

                }
                R.id.action_Home -> {

                  replaceFragment(Home_Fargment())
                }
                R.id.action_about -> {

                    replaceFragment(About_Fargment())
                }
                R.id.action_login -> {


                    if(!databaseHelper.isUserLoggedIn())
                    {
                        startActivity(Intent(this, Basic_Activity::class.java))

                    }
                    else{

                        Toast.makeText(this, "User is already Login", Toast.LENGTH_SHORT).show()

                    }

                }
                R.id.action_logout -> {
                    if(databaseHelper.isUserLoggedIn()){
                        databaseHelper.logoutUser()
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Basic_Activity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else{
                        Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show()


                    }

                }
            }
            drawerLayout.closeDrawers()
            true
        }

        if (savedInstanceState == null) {
            replaceFragment(Home_Fargment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
