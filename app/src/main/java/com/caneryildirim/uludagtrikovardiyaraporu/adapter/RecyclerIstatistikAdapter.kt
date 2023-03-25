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
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.IstatistikVardiyaViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.RecyclerIstatistikRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.IstatistikRapor

class RecyclerIstatistikAdapter(val istatistikRaporList:ArrayList<IstatistikRapor>,val optionRecycler:OptionRecycler):RecyclerView.Adapter<RecyclerIstatistikAdapter.IstatistikHolder>() {
    interface OptionRecycler{
        fun deleteItem(docRef:String)
    }
    class IstatistikHolder(val binding:RecyclerIstatistikRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IstatistikHolder {
        val binding=RecyclerIstatistikRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IstatistikHolder(binding)
    }

    override fun getItemCount(): Int {
      return istatistikRaporList.size
    }

    override fun onBindViewHolder(holder: IstatistikHolder, position: Int) {
        holder.binding.textViewMakina.text="Makina:${istatistikRaporList[position].makina}"
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
            //val action=IstatistikFragmentDirections.actionIstatistikFragmentToIstatistikDetailFragment(istatistikRaporList[position],"makina")
            val actionNew=IstatistikMainFragmentDirections.actionIstatistikMainFragmentToIstatistikDetailFragment(istatistikRaporList[position],"makina")
            Navigation.findNavController(it).navigate(actionNew)
        }
    }

    fun updateIstatistikData(newIstatistikRaporList:List<IstatistikRapor>){
        istatistikRaporList.clear()
        istatistikRaporList.addAll(newIstatistikRaporList)
        notifyDataSetChanged()
    }
}