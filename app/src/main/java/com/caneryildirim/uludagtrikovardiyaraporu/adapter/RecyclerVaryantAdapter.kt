package com.caneryildirim.uludagtrikovardiyaraporu.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.VaryantViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.VaryantRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Varyant

class RecyclerVaryantAdapter(val varyantList:ArrayList<Varyant>,val optionVaryant:OptionVaryant):RecyclerView.Adapter<RecyclerVaryantAdapter.VaryantHolder>() {
    interface OptionVaryant {
        fun deleteVaryant(docRef:String)
    }
    class VaryantHolder(val binding:VaryantRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaryantHolder {
        val binding=VaryantRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VaryantHolder(binding)
    }

    override fun getItemCount(): Int {
        return varyantList.size
    }

    override fun onBindViewHolder(holder: VaryantHolder, position: Int) {
        holder.binding.textViewIdVaryant.text="id:${varyantList[position].id.toString()}"
        holder.binding.textViewNameVaryant.text=varyantList[position].name
        holder.itemView.setOnLongClickListener {
            val alertDialog= AlertDialog.Builder(holder.itemView.context)
            alertDialog.setMessage("Varyantı Sil")
            alertDialog.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
                optionVaryant.deleteVaryant(varyantList[position].docRef)
            }
            alertDialog.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(holder.itemView.context,"Vazgeçildi", Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
            return@setOnLongClickListener true
        }
    }

    fun updateData(newVaryantList:List<Varyant>){
        varyantList.clear()
        varyantList.addAll(newVaryantList)
        notifyDataSetChanged()
    }
}