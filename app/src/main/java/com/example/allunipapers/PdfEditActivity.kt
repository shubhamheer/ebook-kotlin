package com.example.allunipapers

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.allunipapers.databinding.ActivityPdfEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditActivity : AppCompatActivity() {
    // view binding
    private lateinit var binding:ActivityPdfEditBinding

    // book id get from intent started from adapter pdf admin
    private var bookId =""

    // progress dialog

    private lateinit var progressDialog: ProgressDialog

    // array list to hold category titles
    private  lateinit  var categoryTitleArrayList:ArrayList<String>

    // array list to hold category id
    private  lateinit var categoryIdArrayList:ArrayList<String>

    private companion object{
        private const val TAG = "PDF_EDIT_TAG"
    }




    override fun onCreate(savedInstanceState: Bundle?) {

        // init progress bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait ")
        progressDialog.setCanceledOnTouchOutside(false)



        super.onCreate(savedInstanceState)
        binding= ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get book id to edit info
        bookId = intent.getStringExtra("bookId")!!

        loadCategories()
        loadBookInfo()
        // go back
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }


        // pick category
        binding.categoryTv.setOnClickListener{
            categoryDialog()
        }


        // handle click to begin update

        binding.submitBtn.setOnClickListener{
            validateData()

        }



    }

    private fun loadBookInfo() {
         Log.d(TAG,"loadBookInfo: Loading Book Info...")
        val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                         // get book info
                        selectedCategoryId = snapshot.child("categoryId").value.toString()
                        val description  = snapshot.child("description").value.toString()
                        val title = snapshot.child("title").value.toString()


                        // set to views
                        binding.titleEt.setText(title)
                        binding.descriptionEt.setText(description)


                        // load book category info using categoryId
                            Log.d(TAG,"onDataChange: Loading book category info...")
                        val refBookCategory = FirebaseDatabase.getInstance().getReference("Categories")
                        refBookCategory.child(selectedCategoryId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    // get category
                                    val category = snapshot.child("category").value
                                    // set to text view
                                    binding.categoryTv.text= category.toString()


                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                })
    }


    private var title = ""
    private var description = ""
    private fun validateData() {
        // get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        // validate data
        if (title.isEmpty()){
            Toast.makeText(this, "Enter Title ", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Enter description ", Toast.LENGTH_SHORT).show()
        }
        else if (selectedCategoryId.isEmpty()){
            Toast.makeText(this, "Pick Category ", Toast.LENGTH_SHORT).show()
        }
        else{
            updatePdf()
        }

    }

    private fun updatePdf() {
        Log.d(TAG, "updatePdf: Starting updating pdf info...")
        // show progress
        progressDialog.setMessage("Updating book info")
        progressDialog.show()

//        setup data to upload to db in firebase
        val hashMap = HashMap<String, Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"

        // start updating
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updatePdf: Updated Successfully...")
                Toast.makeText(this, "Updated Successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Log.d(TAG, "updatePdf: Failed to update pdf due to ${e.message}")
                Toast.makeText(this, "Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }


    private var selectedCategoryId = ""
    private var selectedCategoryTitle =""


    private fun categoryDialog() {

        // show dialog to pick the category of pdf / book  we already got the categories

        // make string of array last of the string
        var categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for (i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]

        }

        // alert dialog
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog, position->
                // handle click save clicked category id and title
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                // set to text view
                binding.categoryTv.text = selectedCategoryTitle

            }
            .show()

    }

    private fun loadCategories() {
        Log.d(TAG, "loadCategories: loading categories...")
        categoryTitleArrayList = ArrayList()
        categoryIdArrayList   = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list before adding data
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()
                for (ds in snapshot.children){
                    // get data
                        val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"
                    Log.d(TAG, "onDataChange: Category ID $id")
                    Log.d(TAG, "onDataChange: Category $category")




                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}