package com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel

import android.app.Activity
import android.content.Context
import android.icu.util.Calendar
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.model.Beden
import com.caneryildirim.uludagtrikovardiyaraporu.model.MakinaPerformansRapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.ModelNo
import com.caneryildirim.uludagtrikovardiyaraporu.model.Parca
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminRaporEkleViewModel:ViewModel() {
    val uploadLiveData= MutableLiveData<Boolean>(false)
    val firestore= Firebase.firestore
    val auth= Firebase.auth


    fun raporKaydet(rapor:MakinaPerformansRapor,context: Context,activity: Activity){
        uploadLiveData.value=true
        val raporHashMap= hashMapOf<String,Any>()
        raporHashMap.put("tarih",rapor.tarih)
        raporHashMap.put("makina",rapor.makina)
        raporHashMap.put("calismaSuresi",rapor.calismaSure)
        raporHashMap.put("uretimSuresi",rapor.uretimSure)
        raporHashMap.put("uretimYuzdesi",rapor.uretimYuzde)
        raporHashMap.put("hareketCubuk",rapor.hareketCubuk)
        raporHashMap.put("iplikBes",rapor.iplikBes)
        raporHashMap.put("parcaSayaci",rapor.parcaSayaci)
        raporHashMap.put("dirDrs",rapor.dirDrs)
        raporHashMap.put("igneSen",rapor.igneSen)
        raporHashMap.put("merdanECekim",rapor.merdanECekim)
        raporHashMap.put("programlama",rapor.programlama)
        raporHashMap.put("makineStop",rapor.makineStop)
        raporHashMap.put("sokStopAparati",rapor.sokStopAparati)
        raporHashMap.put("jakarHatasi",rapor.jakarHatasi)
        raporHashMap.put("toplamHareket",rapor.toplamHareket)
        raporHashMap.put("yavasHareket",rapor.yavasHareket)
        raporHashMap.put("uretimAdedi",rapor.uretimAdedi)
        raporHashMap.put("docRef",rapor.docRef)

        firestore.collection("makinaperformansraporlar").document(rapor.docRef).set(raporHashMap).addOnSuccessListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Rapor Kaydedildi",Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }



    fun getCurrentDate(): Timestamp {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val stringDate = "$day-${month + 1}-$year"
        return stringToDate(stringDate)
    }

    private fun stringToDate(dtStart:String):Timestamp{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = format.parse(dtStart)
        return Timestamp(date)
    }

    fun getMakinaList():ArrayList<String>{
        val makinaList=ArrayList<String>()
        makinaList.add("Makina Seçiniz")
        makinaList.add("M521")
        makinaList.add("M522")
        makinaList.add("M822")
        makinaList.add("M143")
        makinaList.add("M142")
        makinaList.add("M141")
        makinaList.add("M121")
        makinaList.add("M122")
        makinaList.add("M101")
        makinaList.add("M102")
        makinaList.add("M103")
        makinaList.add("MG-621")
        makinaList.add("MG-622")
        makinaList.add("MG-623")
        makinaList.add("MG-624")
        makinaList.add("MG-625")
        makinaList.add("MG-626")
        makinaList.add("MG-627")
        makinaList.add("MG-628")
        makinaList.add("MG-629")


        return makinaList
    }



    override fun onCleared() {
        super.onCleared()
    }

}