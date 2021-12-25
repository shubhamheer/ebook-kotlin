package com.example.allunipapers


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.allunipapers.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class PdfListAdminActivity : AppCompatActivity() {

     // view binding
    private lateinit var binding: ActivityPdfListAdminBinding
    private companion object{
        const val TAG = " PDF_LIST_ADMIN_TAG"
    }

    // category id , title
    private var categoryId = ""
    private var category = ""

    // array list to hold books
    private lateinit var pdfArrayList:ArrayList<ModelPdf>
    // adapter
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get pdf intent that we passed from adapter
        val intent= intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        // set pdf category
        binding.subtitleTv.text = category
        // load pdf and books
        loadPdfList()
       // serach


        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                // filter data
                try {
                    adapterPdfAdmin.filter!!.filter(s)

                }
                catch ( e: Exception){
                    Log.d(TAG, "onTextChanged: ${e.message}")

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        // handle click back
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
    }

    private fun loadPdfList() {
    // init array list
        pdfArrayList= ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // clear list before start adding data into it
                    pdfArrayList.clear()
                    for(ds in snapshot.children){
                        // get data
                        val model = ds.getValue(ModelPdf::class.java)
                        // add to list
                        if(model != null){
                            pdfArrayList.add(model)
                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                        }

                    }
                // setup adapter
                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity, pdfArrayList)
                    binding.booksRv.adapter = adapterPdfAdmin
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            )

    }
}