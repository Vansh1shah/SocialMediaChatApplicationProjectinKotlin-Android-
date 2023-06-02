package com.example.cp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cp.activity.LoginActivity

class forgotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)



        val fgbtn= findViewById<Button>(R.id.login_button)

        fgbtn.setOnClickListener {


            supportActionBar!!.hide()

            val handler = Handler()
            handler.postDelayed(
                {
                    Toast.makeText(applicationContext, "OTP SEND SUCCESSFULLY", Toast.LENGTH_SHORT).show()
            }, 4000)

        }
    }








}