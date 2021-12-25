package com.example.allunipapers

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.allunipapers.databinding.ActivityDashboardAdminBinding
import com.example.allunipapers.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class DashboardAdminActivity : AppCompatActivity() {
    // view binding
    private lateinit var binding: ActivityDashboardAdminBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth


  //array list to hold categories
    private lateinit var  categoryArrayList: ArrayList<ModelCategory>

    //adapter
    private lateinit var adapterCategory: AdapterCategory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()
        // search
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               // called as and when user type anything
                try {
                    adapterCategory.filter.filter(s)

                }
                catch ( e: Exception){

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })



        // handle click logout
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()

        }
        // handle click and start add category page
        binding.addCategoryBtn.setOnClickListener{
            startActivity(Intent(this, CategoryAddActivity::class.java))
        }

        // handle click add pdf page
        binding.addpdfFab.setOnClickListener{
            startActivity(Intent(this, pdfAddActivity::class.java))
        }

    }

    private fun loadCategories() {
       // init array list
        categoryArrayList = ArrayList()
         // get all categories from firebase database ... Firebase DB> Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               // clear list before starting adding data into it
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    // get data as model
                    val model = ds.getValue(ModelCategory::class.java)
                    // add TO array list
                    categoryArrayList.add(model!!)
                }
                // setup adapter
                adapterCategory= AdapterCategory(this@DashboardAdminActivity, categoryArrayList)
                // set adapter to recycle view
                binding.categoriesRv.adapter= adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkUser() {
         // get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null ){
            //not logged in go to main screen
            startActivity(Intent( this, MainActivity::class.java))
            finish()

        }
        else{
            // logged in get and show user info
            val email = firebaseUser.email
            // set text view of toolbar

            binding.subtitleTv.text = email
        }
    }
}