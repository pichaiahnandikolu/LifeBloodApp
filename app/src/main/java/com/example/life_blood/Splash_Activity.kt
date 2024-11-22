package com.example.life_blood

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity

class Splash_Activity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        databaseHelper = DatabaseHelper(this)
        startSplashAnimation()

    }
        private fun startSplashAnimation() {
            val bloodDrop = findViewById<View>(R.id.bloodDrop)
            val box = findViewById<View>(R.id.box)

            bloodDrop.bringToFront()

            val viewTreeObserver = box.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val boxY = box.y

                    val dropAnimator = ObjectAnimator.ofFloat(
                        bloodDrop,
                        "translationY",
                        0f,
                        boxY - bloodDrop.height
                    )
                    dropAnimator.duration = 3000  // 3 seconds for the drop animation
                    dropAnimator.interpolator = BounceInterpolator()  // Adds a bounce effect
                    dropAnimator.start()


                    Handler().postDelayed({

                        bloodDrop.visibility = View.GONE


                        box.setBackgroundResource(R.drawable.warehouse)


                        val moveOffScreen =
                            ObjectAnimator.ofFloat(box, "translationX", box.x, 2000f)
                        moveOffScreen.duration = 1000

                        moveOffScreen.start()

                    }, 6000)


                    Handler().postDelayed({
                        checkUserLoginStatus()


                    }, 4000)

                    box.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
        private fun checkUserLoginStatus() {
            if (databaseHelper.isUserLoggedIn()) {
                navigateToMainActivity()
            } else {
                navigateToLoginActivity()
            }
        }

        private fun navigateToMainActivity() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        private fun navigateToLoginActivity() {
            val intent = Intent(this, Basic_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

