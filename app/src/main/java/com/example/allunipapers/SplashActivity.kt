package com.example.allunipapers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.allunipapers.databinding.ActivityLoginBinding
import com.example.allunipapers.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivitySplashBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)


        setContentView(R.layout.activity_splash)

        // init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()


         Handler().postDelayed(Runnable{
              checkUser()

         },1000)
    }

    private fun checkUser() {
        // keep user logged in 1 check if  logged in , 2 and then check the type of the user
         // get current user if logged in or logged out
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            // user not logged in goto main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
        else{
            // user logged in and check user type same  as done in login screen
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object: ValueEventListener {


                    override fun onDataChange(snapshot: DataSnapshot) {

                        // get user type if admin or user
                        val userType = snapshot.child("userType").value
                        if ( userType == "user"){
                            // simple suer so open user dashboard
                            startActivity(Intent(this@SplashActivity, DashboardUserActivity::class.java))
                            finish()
                        }
                        else if (userType == "admin"){
                            // admin user  so open admin dashboard
                            startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                            finish()
                        }


                    }
                    override fun onCancelled(error: DatabaseError) {


                    }
                })

        }

    }
}



