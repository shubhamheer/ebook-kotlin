package com.example.allunipapers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.allunipapers.databinding.ActivityDashboardAdminBinding
import com.example.allunipapers.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {


    // view binding
    private lateinit var binding: ActivityDashboardUserBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()



        // handle click logout
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()


        }
    }

    private fun checkUser() {
        // get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null ){
            //not logged in user can stay in dashboard without login
           binding.subtitleTv.text = "Not Logged In"

        }
        else{
            // logged in get and show user info
            val email = firebaseUser.email
            // set text view of toolbar

            binding.subtitleTv.text = email
        }
    }
}