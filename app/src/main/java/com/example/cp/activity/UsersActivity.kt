package com.example.cp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cp.R
import com.example.cp.adapter.UserAdapter
import com.example.cp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class UsersActivity : AppCompatActivity() {
    private lateinit var userList : ArrayList<User>
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var imgBack : ImageView
    private lateinit var imgProfile : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        userList = ArrayList<User>()
        userRecyclerView = findViewById<RecyclerView>(R.id.userRecyclerView)
        imgBack = findViewById<ImageView>(R.id.imgBack)
        imgProfile = findViewById<ImageView>(R.id.imgProfile)
        userRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        imgBack.setOnClickListener{
            onBackPressed()
        }
        imgProfile.setOnClickListener {
            val intent = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        getUsersList()

    }
    fun getUsersList(){
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if(currentUser!!.profileImage == ""){
                    imgBack.setImageResource(R.drawable.profile_image)
                }
                else
                {
                    Glide.with(this@UsersActivity).load(currentUser.profileImage).into(imgProfile)
                }
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {
                        userList.add(user)
                    }
                }
                val userAdapter = UserAdapter(this@UsersActivity, userList)
                userRecyclerView.adapter = userAdapter
            }
        })
    }
}


