package com.caneryildirim.uludagtrikovardiyaraporu.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.IstatistikFragmentDirections
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.IstatistikMainFragmentDirections
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.IstatistikPersonelFragmentDirections
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.RecyclerIstatistikRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.IstatistikRapor

class RecyclerIstatistikPersonelAdapter(val istatistikRaporList:ArrayList<IstatistikRapor>,val optionRecycler:OptionRecycler):RecyclerView.Adapter<RecyclerIstatistikPersonelAdapter.IstatistikPersonelHolder>() {
    interface OptionRecycler{
        fun deleteItem(docRef:String)
    }
    class IstatistikPersonelHolder(val binding: RecyclerIstatistikRowBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IstatistikPersonelHolder {
        val binding= RecyclerIstatistikRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IstatistikPersonelHolder(binding)
    }

    override fun getItemCount(): Int {
        return istatistikRaporList.size
    }

    override fun onBindViewHolder(holder: IstatistikPersonelHolder, position: Int) {
        holder.binding.textViewMakina.text="Personel:${istatistikRaporList[position].personel}"
        holder.binding.textViewTarih.text="Tarih aralığı:${istatistikRaporList[position].tarihAralik}"
        holder.itemView.setOnLongClickListener {
            val alertDialog= AlertDialog.Builder(holder.itemView.context)
            alertDialog.setMessage("İstatistik Raporunu Sil")
            alertDialog.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
                optionRecycler.deleteItem(istatistikRaporList[position].docRef)
            }
            alertDialog.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(holder.itemView.context,"Vazgeçildi", Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
            return@setOnLongClickListener true
        }

        holder.itemView.setOnClickListener {
            //val action= IstatistikPersonelFragmentDirections.actionIstatistikPersonelFragmentToIstatistikDetailFragment(istatistikRaporList[position],"personel")
            val actionNew=IstatistikMainFragmentDirections.actionIstatistikMainFragmentToIstatistikDetailFragment(istatistikRaporList[position],"personel")
            Navigation.findNavController(it).navigate(actionNew)
        }
    }

    fun updateIstatistikData(newIstatistikRaporList:List<IstatistikRapor>){
        istatistikRaporList.clear()
        istatistikRaporList.addAll(newIstatistikRaporList)
        notifyDataSetChanged()
    }
}