package com.caneryildirim.uludagtrikovardiyaraporu.adapter

import android.app.DatePickerDialog
import android.content.res.Resources
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.RecycylerRaporKullaniciRowBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.userView.RaporlarimFragmentDirections
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerRaporKullaniciAdapter(val raporList:ArrayList<Rapor>,val makinaList:ArrayList<String>, val optionRecycler:OptionRecycler ):RecyclerView.Adapter<RecyclerRaporKullaniciAdapter.RaporKullaniciHolder>() {
    class RaporKullaniciHolder(val binding:RecycylerRaporKullaniciRowBinding):RecyclerView.ViewHolder(binding.root) {

    }
    val currentDate=getDate()

    interface OptionRecycler{
        fun onItemDelete(docRef: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaporKullaniciHolder {
        val binding=RecycylerRaporKullaniciRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RaporKullaniciHolder(binding)
    }

    override fun getItemCount(): Int {
        return makinaList.size
    }

    override fun onBindViewHolder(holder: RaporKullaniciHolder, position: Int) {
        var info=false
        lateinit var raporForDetail: Rapor
        holder.binding.textViewMakina.text=makinaList[position]
        holder.binding.textViewTarih.text=currentDate
        holder.binding.cardviewRaporKullanici.setCardBackgroundColor(holder.itemView.context.getColor(R.color.white))
        raporList.forEachIndexed { index, rapor ->
            if (rapor.makina==makinaList[position]){
                raporForDetail=rapor
                info=true
                holder.binding.cardviewRaporKullanici.setCardBackgroundColor(holder.itemView.context.getColor(R.color.recyclergreen))
            }
        }

        holder.itemView.setOnClickListener {
            if (info){
                val action=RaporlarimFragmentDirections.actionRaporlarimFragmentToRaporDetailFragment(raporForDetail,false)
                Navigation.findNavController(it).navigate(action)
            }else{
                val action= RaporlarimFragmentDirections.actionRaporlarimFragmentToRaporEkleFragment(makinaList[position])
                Navigation.findNavController(it).navigate(action)
            }
        }

    }


    private fun getDate():String{
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val dStart="$day-${month+1}-$year"
        val dateTimestamp=stringToDate(dStart)
        val date=dateToString(dateTimestamp)
        return date

    }
    private fun stringToDate(dtStart:String): Timestamp {
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = format.parse(dtStart)
        return Timestamp(date)
    }
    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

    fun updateRaporList(newRaporList:List<Rapor>){
        raporList.clear()
        raporList.addAll(newRaporList)
        notifyDataSetChanged()
    }

}