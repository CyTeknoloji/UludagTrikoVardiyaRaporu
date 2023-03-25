package com.caneryildirim.uludagtrikovardiyaraporu.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.TamamlanmisRaporlarFragmentDirections
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.RaporRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.User
import com.caneryildirim.uludagtrikovardiyaraporu.userView.RaporlarimFragmentDirections
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.acosh

class RecyclerRaporAdapter(val raporList:ArrayList<Rapor>,val optionRecycler:OptionRecycler,val admin:Boolean):RecyclerView.Adapter<RecyclerRaporAdapter.RaporHolder>() {
    interface OptionRecycler{
        fun onItemDelete(docRef: String)
    }

    class RaporHolder(val binding:RaporRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaporHolder {
        val binding=RaporRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RaporHolder(binding)
    }

    override fun getItemCount(): Int {
       return raporList.size
    }

    override fun onBindViewHolder(holder: RaporHolder, position: Int) {
        holder.binding.textViewEmail.text="Personel:${raporList[position].personel}"
        holder.binding.textViewMakina.text="Makina:${raporList[position].makina}"
        holder.binding.textViewTarih.text= "Yüklenme tarihi:${dateToString(raporList[position].tarih!!)}"
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

        holder.itemView.setOnClickListener {
            if (admin){
                val action=TamamlanmisRaporlarFragmentDirections.actionTamamlanmisRaporlarFragmentToRaporDetailFragment(raporList[position],adminInfo = true)
                Navigation.findNavController(it).navigate(action)
            }else{
                val action=RaporlarimFragmentDirections.actionRaporlarimFragmentToRaporDetailFragment(raporList[position],adminInfo = false)
                Navigation.findNavController(it).navigate(action)
            }

        }
    }

    fun updateRaporList(newRaporList: List<Rapor>) {
        raporList.clear()
        raporList.addAll(newRaporList)
        notifyDataSetChanged()
    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

}