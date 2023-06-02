package com.example.cp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.cp.R
import com.example.cp.forgotActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        Log.i("FireBase User:", "onCreate: ${auth.currentUser}")

        if(auth.currentUser != null){
            val intent = Intent(this@LoginActivity, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSign = findViewById<Button>(R.id.btnSignUp)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val forgetpass = findViewById<TextView>(R.id.forgetpass)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }



        btnSign.setOnClickListener {

            val nik = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(nik)

        }

        forgetpass.setOnClickListener {

            val intent = Intent(this@LoginActivity,forgotActivity::class.java)
            startActivity(intent)
        }


        btnLogin.setOnClickListener{
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            
            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    applicationContext,
                    "E-mail and Password are required",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            etEmail.setText("")
                            etPassword.setText("")
                            val intent = Intent(this@LoginActivity, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "E-mail or Password invalid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }
        }





    }
}