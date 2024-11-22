package com.example.life_blood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Basic_Activity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)


        databaseHelper = DatabaseHelper(this)


        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)


        val loginButton = findViewById<Button>(R.id.loginButton)
        val SkipButton = findViewById<Button>(R.id.skipButton)

        loginButton.setOnClickListener{
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (databaseHelper.validateLogin(username, password)) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this,MainActivity ::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
        SkipButton.setOnClickListener {
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)
            finish()

        }

    }
}
