package com.example.allunipapers

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import java.util.*

class MyApplication: Application(){
    override fun onCreate() {
        super.onCreate()
    }
    companion object {
        // change the date format
        fun formatTimeStamp(timestamp:Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis= timestamp
            // ddmmyyyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()

        }
        // function to get pdf size
        fun loadPdfSize(pdfUrl:String   , pdfTitle:String,sizeTv:TextView){
            val TAG = "PDF_SIZE_TAG"

            // using url  we can get pdf data from firebase
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {storageMetaData ->
                    Log.d(TAG,"loadPdfSize: got metadata")
                    val bytes = storageMetaData.sizeBytes.toDouble()
                    Log.d(TAG,"loadPdfSize: Size Bytes $bytes")

                    // convert bytes to kb/mb
                    val kb= bytes/1024
                    val mb = kb/1024
                    if (mb >=1){
                        sizeTv.text = "${String.format("%.2f",mb)} MB"
                    }
                    else if (kb>=1) {
                        sizeTv.text = "${String.format("%.2f",kb)} KB"
                    }
                    else{
                        sizeTv.text = "${String.format("%.2f",bytes)}bytes"
                    }


                }
                .addOnFailureListener {e->
                    // failed to get meta data
                    Log.d(TAG,"loadPdfSize: Failed to get metada due to ${e.message}")

                }

        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle : String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv:TextView?

        ){

            val TAG = "PDF_THUMBNAIL_TAG"
            // using url we can get its file and metadata from firebase
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener {bytes->
                    Log.d(TAG,"loadPdfSize: Size Bytes $bytes")
                    // set to pdf view
                    pdfView.fromBytes(bytes)
                        .pages(0)// show first page only
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError{t ->
                            progressBar.visibility= View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")

                        }
                        .onPageError { page, t ->
                            progressBar.visibility= View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                        }
                        .onLoad { nbPages ->
                            Log.d(TAG, "loadPdfFromUrlSinglePage: Pages: $nbPages")
                            // pdf loaded so we can set page count , pdf thumbnail
                            progressBar.visibility = View.INVISIBLE
                            // if pages tv param is not null then set page numbers
                            if(pagesTv != null){
                                pagesTv.text=  "$nbPages"
                            }
                        }
                        .load()




                }
                .addOnFailureListener {e->
                    // failed to get meta data
                    Log.d(TAG,"loadPdfSize: Failed to get metadata due to ${e.message}")

                }

        }


        fun loadCategory(categoryId:String, categoryTv: TextView){
            // load category using category id from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // get category
                        val category = "${snapshot.child("category").value}"
                        // set category
                        categoryTv.text= category

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
                )


        }

        fun deleteBook(context: Context, bookId: String, bookTitle: String , bookUrl: String ){
            val TAG = "DELETE_BOOK_TAG"
            Log.d(TAG, "deleteBook: deleting...")

            // progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting $bookTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG,"deleteBook: Deleting from storage...")
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
            storageReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "deleteBook: Deleted from storage...")
                    Log.d(TAG, "deleteBook: Deleting from db now ...")

                    val ref = FirebaseDatabase.getInstance().getReference("Books")
                    ref.child(bookId)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context, "successfully deleted...", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "deleteBook: Deleted from db too...")


                        }
                        .addOnFailureListener{ e->
                            progressDialog.dismiss()
                            Log.d(TAG, "deleteBook: Failed to delete from db due to ${e.message}")
                            Toast.makeText(context, "Failed to delete due to...", Toast.LENGTH_SHORT).show()

                        }


                }
                .addOnFailureListener { e->
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteBook: Failed to delete from storage due to ${e.message}")
                    Log.d(TAG, "deleteBook: Failed to delete due to ${e.message}")



                }




        }

    }


}