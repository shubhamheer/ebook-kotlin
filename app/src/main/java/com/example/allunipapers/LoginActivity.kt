package com.example.allunipapers

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.allunipapers.databinding.ActivityLoginBinding
import com.example.allunipapers.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivityLoginBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // progress dialog

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()


        // init progress bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait ")
        progressDialog.setCanceledOnTouchOutside(false)

        // handle click not have any account
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))

        }


        // handle click begin to login

        binding.loginBtn.setOnClickListener{

//            input , validate, login, check for admin or user
            validateData()


        }

    }


    private  var email = ""
    private  var password  = ""


    private fun validateData() {
        // input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // invalid email pattern
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show()


        } else if (password.isEmpty()) {
            // invalid email pattern
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show()

        }
        else {
            loginUser()
        }


    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In...")
        progressDialog.show()


        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // login success
                checkUser()

            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message} ", Toast.LENGTH_SHORT).show()

            }

    }

    private fun checkUser() {
        // if admin or user
        progressDialog.setMessage("Checking user...")
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{


                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    // get user type if admin or user
                    val userType = snapshot.child("userType").value
                    if ( userType == "user"){
                        // simple suer so open user dashboard
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    }
                     else if (userType == "admin"){
                         // admin user  so open admin dashboard
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }


                    }
                    override fun onCancelled(error: DatabaseError) {


            }
            })


    }
}