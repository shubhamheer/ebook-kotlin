package com.example.allunipapers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.allunipapers.databinding.RowPdfAdminBinding
import com.example.allunipapers.databinding.RowPdfUserBinding

class AdapterPdfUser  : RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser>{

    // view holder class row pdf user .xml
    // context


    private var context: Context
    // array list to hold pdf

    private var pdfArrayList:ArrayList<ModelPdf>


    // view binding
    private lateinit var binding: RowPdfUserBinding

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.pdfArrayList = pdfArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        // bind inflate layout for row pdf user
        binding= RowPdfUserBinding.inflate(LayoutInflater.from(context), parent , false)
        return HolderPdfUser(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {
        // get data set data and etc
        // get data
        val model = pdfArrayList[position]
        val bookId = model.id
        val categoryId = model.categoryId
        val title= model.title
        val description= model.description
        val pdfUrl = model.url
        val uid = model.uid
        val url = model.url
        val timestamp= model.timestamp



        // conver time stamp to ddmmyyyy
        val Date = MyApplication.formatTimeStamp(timestamp)
        // set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = Date

        // we dont need page number here pass null for page number // load pdf thumbnail
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)

        // load further details  like category, pdf from url , pdf size
        // load category
        MyApplication.loadCategory(categoryId, holder.categoryTv)


        // load pdf size
        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)




    }

    override fun getItemCount(): Int {
        return pdfArrayList.size // items count
    }



    inner class HolderPdfUser(itemView: View): RecyclerView.ViewHolder(itemView){
        // ui views for row pdf admin
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv


    }

}