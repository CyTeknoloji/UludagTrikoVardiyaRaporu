package com.caneryildirim.uludagtrikovardiyaraporu.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.UserRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.User

class RecyclerUserAdapter(val userList:ArrayList<User>, val optionRecycler:OptionRecycler):RecyclerView.Adapter<RecyclerUserAdapter.UserHolder>() {
    interface OptionRecycler{
        fun onItemDelete(position: Int)
    }

    class UserHolder(val binding:UserRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding=UserRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.binding.textEmailKullanici.text=userList[position].displayName
        holder.itemView.setOnLongClickListener {
            val alertDialog= AlertDialog.Builder(holder.itemView.context)
            alertDialog.setMessage("Kullanıcıyı Sil")
            alertDialog.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
                optionRecycler.onItemDelete(position)
            }
            alertDialog.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(holder.itemView.context,"Vazgeçildi",Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
            return@setOnLongClickListener true
        }
    }

    fun updateKullaniciList(newKullaniciList: List<User>) {
        userList.clear()
        userList.addAll(newKullaniciList)
        notifyDataSetChanged()
    }

}