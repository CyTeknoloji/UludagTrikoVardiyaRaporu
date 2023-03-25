package com.caneryildirim.uludagtrikovardiyaraporu.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.RecycylerRaporKullaniciRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.MakinaPerformansRapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class RecyclerAdminRaporAdapter(val raporList:ArrayList<MakinaPerformansRapor>, val optionRecycler:OptionRecycler):RecyclerView.Adapter<RecyclerAdminRaporAdapter.AdminRaporHolder>() {
    interface OptionRecycler{
        fun onItemDelete(docRef:String)
    }
    class AdminRaporHolder(val binding: RecycylerRaporKullaniciRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRaporHolder {
        val binding=RecycylerRaporKullaniciRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdminRaporHolder(binding)
    }

    override fun getItemCount(): Int {
        return raporList.size
    }

    override fun onBindViewHolder(holder: AdminRaporHolder, position: Int) {
        holder.binding.textViewTarih.text=dateToString(raporList[position].tarih)
        holder.binding.textViewMakina.text=raporList[position].makina

        holder.itemView.setOnLongClickListener {
            val alertDialog= AlertDialog.Builder(holder.itemView.context)
            alertDialog.setMessage("Raporu Sil")
            alertDialog.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
                optionRecycler.onItemDelete(raporList[position].docRef)
            }
            alertDialog.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(holder.itemView.context,"Vazgeçildi", Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
            return@setOnLongClickListener true
        }

    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

    fun updateRaporList(newRaporList:List<MakinaPerformansRapor>){
        raporList.clear()
        raporList.addAll(newRaporList)
        notifyDataSetChanged()
    }
}