package com.example.cp.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cp.R
import com.example.cp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var  firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imgBack : ImageView
    private lateinit var userImage : CircleImageView
    private lateinit var etUserName: EditText
    private lateinit var btnSave: Button
    private lateinit var logout:Button
    private lateinit var progressBar: ProgressBar


    private var filePath:Uri? = null
    private var PICK_IMAGE_REQUEST:Int = 2023

    private lateinit var storage: FirebaseStorage
    private lateinit var  storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgBack = findViewById<ImageView>(R.id.imgBack)
        userImage = findViewById<CircleImageView>(R.id.userImage)
        etUserName = findViewById<EditText>(R.id.etUserName)
        btnSave = findViewById<Button>(R.id.btnSave)
        progressBar = findViewById<ProgressBar>(R.id.progressBar )

        logout = findViewById<Button>(R.id.logout)


        auth = FirebaseAuth.getInstance()





        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


        logout.setOnClickListener {

            auth.signOut()
            val intent = Intent(this@ProfileActivity,
                LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                etUserName.setText(user!!.userName)

                if(user.profileImage == ""){
                    userImage.setImageResource(R.drawable.profile_image)
                }
                else
                {
                    Glide.with(this@ProfileActivity).load(user.profileImage).into(userImage)
                }

            }
        })
        imgBack.setOnClickListener{
            onBackPressed()
        }
        userImage.setOnClickListener {
            chooseImage()
        }

        btnSave.setOnClickListener {
            uploadImage()
            progressBar.visibility = View.VISIBLE
        }

    }
    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
            filePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                userImage.setImageBitmap(bitmap)
                btnSave.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {

            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userName",etUserName.text.toString())
                    hashMap.put("profileImage",filePath.toString())
                    databaseReference.updateChildren(hashMap as Map<String, Any>)
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                    btnSave.visibility = View.GONE
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Failed" + it.message, Toast.LENGTH_SHORT)
                        .show()

                }

        }
    }
}