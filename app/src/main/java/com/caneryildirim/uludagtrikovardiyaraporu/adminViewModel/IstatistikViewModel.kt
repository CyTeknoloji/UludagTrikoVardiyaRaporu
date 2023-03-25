package com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.icu.util.Calendar
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FilterIstatistikBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FilterTamamlanmisRaporBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Istatistik
import com.caneryildirim.uludagtrikovardiyaraporu.model.IstatistikRapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileOutputStream
import java.sql.Time
import java.text.AttributedString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class IstatistikViewModel: ViewModel() {
    val uploadLiveData=MutableLiveData<Boolean>(false)
    private val auth= Firebase.auth
    private val firestore= Firebase.firestore
    private val storage= Firebase.storage
    private val istatistikList=ArrayList<Istatistik>()
    private val istatistikRaporList=ArrayList<IstatistikRapor>()
    val istatistikRaporListLive= MutableLiveData<ArrayList<IstatistikRapor>>()
    private lateinit var outputDirectory: File



    fun getIstatistikData(view: View,context: Context){
        uploadLiveData.value=true
        firestore.collection("istatistikmakina").whereEqualTo("email",auth.currentUser!!.email)
            .orderBy("timestamp",Query.Direction.DESCENDING).get().addOnSuccessListener {
                if (it!=null){
                    if (!it.isEmpty){
                        uploadLiveData.value=false
                        istatistikRaporList.clear()
                        val documents=it.documents
                        for (document in documents){
                            val makina=document.get("makina") as String
                            val vardiya=document.get("vardiya") as String
                            val personel=document.get("personel") as String
                            val tarihAralik=document.get("tarihAralik") as String
                            val timestamp=document.get("timestamp") as Timestamp
                            val email=document.get("email") as String
                            val toplamCalismaSuresi=document.get("toplamCalismaSuresi") as String
                            val toplamSure=document.get("toplamSure") as String
                            val uretimYuzdesiOrtalama=document.get("uretimYuzdesiOrtalama") as String
                            val uretimAdediToplam=document.get("uretimAdediToplam") as String
                            val fireAdediToplam=document.get("fireAdediToplam") as String
                            val kirilanIgneToplam=document.get("kirilanIgneToplam") as String
                            val kirilanPlatinToplam=document.get("kirilanPlatinToplam") as String
                            val docRef=document.get("docRef") as String
                            val istatistikRapor=IstatistikRapor(makina,vardiya,personel, tarihAralik, timestamp, email, toplamCalismaSuresi,
                                toplamSure, uretimYuzdesiOrtalama, uretimAdediToplam, fireAdediToplam, kirilanIgneToplam,
                                kirilanPlatinToplam, docRef)
                            istatistikRaporList.add(istatistikRapor)
                        }
                        istatistikRaporListLive.value=istatistikRaporList
                    }else{
                        //emtp
                        uploadLiveData.value=false
                        istatistikRaporList.clear()
                        istatistikRaporListLive.value=istatistikRaporList
                    }
                }else{
                    //null
                    uploadLiveData.value=false
                    Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Evet", View.OnClickListener {
                            getIstatistikData(view,context)
                        }).show()
                }
            }.addOnFailureListener {
                uploadLiveData.value=false
                Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Evet", View.OnClickListener {
                        getIstatistikData(view,context)
                    }).show()
            }
    }

    fun deleteIstatistik(docRef:String,context: Context,view: View){
        uploadLiveData.value=true
        firestore.collection("istatistikmakina").document(docRef).delete().addOnSuccessListener {
            uploadLiveData.value=false
            Toast.makeText(context,"İstatistik raporu silindi!", Toast.LENGTH_SHORT).show()
            getIstatistikData(view,context)
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Hata oluştu! Tekrar deneyiniz.", Toast.LENGTH_SHORT).show()
        }
    }

    fun filter(viewReq: View,layoutInflater: LayoutInflater, context: Context, activity: AdminActivity){
        var makina=""
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        var startDateString="01-01-1990"
        var endDateString="$day-${month+1}-$year"
        val bindingAlert= FilterIstatistikBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()


        val makinaFilterList=getMakinaList()
        val makinaFilterAdapter= ArrayAdapter(context, R.layout.spinner_item, R.id.spinner_text,makinaFilterList)
        bindingAlert.spinnerMakinaFilter.adapter=makinaFilterAdapter
        bindingAlert.spinnerMakinaFilter.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                makina=makinaFilterList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                makina=makinaFilterList[0]
            }

        }

        bindingAlert.editTextTarihFilter.setOnClickListener {
            val builderPicker = MaterialDatePicker.Builder.dateRangePicker()
            val now = Calendar.getInstance()
            builderPicker.setSelection(Pair(now.timeInMillis,now.timeInMillis))
            val picker = builderPicker.build()
            picker.show(activity.supportFragmentManager, picker.toString())
            picker.addOnNegativeButtonClickListener {
                startDateString="01-01-1990"
                endDateString="$day-${month+1}-$year"
                bindingAlert.editTextTarihFilter.setText("")
            }
            picker.addOnPositiveButtonClickListener {
                startDateString=longToDate(it.first)
                endDateString=longToDate((it.second))
                bindingAlert.editTextTarihFilter.setText("$startDateString/$endDateString")
            }
        }

        bindingAlert.buttonUygulaFilter.setOnClickListener {
            when{
                startDateString.isEmpty()-> Toast.makeText(context,"Tarih aralığı seçiniz", Toast.LENGTH_SHORT).show()
                endDateString.isEmpty()-> Toast.makeText(context,"Tarih aralığı seçiniz", Toast.LENGTH_SHORT).show()
                makina=="Makina Seçiniz"->Toast.makeText(context,"Makina seçiniz", Toast.LENGTH_SHORT).show()
                else->{
                    val startDate=stringToDate(startDateString)
                    val endDate=stringToDate(endDateString)
                    val tarihAralik="$startDateString/$endDateString"
                    getRaporData(viewReq,startDate,endDate,makina,context,tarihAralik)
                    builder.cancel()
                }
            }
        }
        bindingAlert.buttonVazgecFilter.setOnClickListener {
            builder.cancel()
        }

    }

    fun getRaporData(view: View,startDate: Timestamp, endDate: Timestamp, makina:String,context: Context,tarihAralik:String){
        uploadLiveData.value=true
        firestore.collection("raporlar").whereEqualTo("makina",makina)
                .orderBy("tarih",Query.Direction.DESCENDING)
                .get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    istatistikList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val tarih=document.get("tarih") as Timestamp
                        val makinaRapor=document.get("makina") as String
                        val vardiya=document.get("vardiya") as String
                        val personel=document.get("personel") as String
                        val calismaSuresi=document.get("calismaSuresi") as String
                        val uretimYuzdesi=document.get("uretimYuzdesi") as String
                        val uretimAdedi=document.get("uretimAdedi") as String
                        val sure=document.get("sure") as String
                        val fireAdedi=document.get("fireAdedi") as String
                        val kirilanIgne=document.get("kirilanIgne") as String
                        val kirilanPlatin=document.get("kirilanPlatin") as String
                        val istatistik=Istatistik(tarih, makinaRapor, vardiya, personel, calismaSuresi,
                            uretimYuzdesi, uretimAdedi, sure, fireAdedi, kirilanIgne, kirilanPlatin)
                        istatistikList.add(istatistik)
                    }
                    resultFilter(view,context,startDate, endDate, makina, istatistikList,tarihAralik)
                }else{
                    //emty
                    uploadLiveData.value=false
                    Toast.makeText(context,"Seçtiğiniz Makinaya ait yüklenmiş rapor yok",Toast.LENGTH_LONG).show()
                    println("emty")
                }
            }else{
                //null
                uploadLiveData.value=false
                Toast.makeText(context,"Seçtiğiniz Makinaya ait yüklenmiş rapor yok",Toast.LENGTH_LONG).show()
                println("null")
            }
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Seçtiğiniz Makinaya ait yüklenmiş rapor yok",Toast.LENGTH_LONG).show()
                println(it.localizedMessage)
        }

    }

    private fun resultFilter(view: View,context: Context,startDate: Timestamp, endDate: Timestamp, makina:String,istatistikList:ArrayList<Istatistik>,tarihAralik: String){
        val newraporList=ArrayList<Istatistik>()
        newraporList.clear()
        newraporList.addAll(
            istatistikList.filter { it.tarih!! >= startDate && it.tarih <= endDate})
        val raporAdet=newraporList.size
        var toplamCalismaSuresiDakika=0
        var uretimYuzdesiToplam=0
        var uretimAdediToplam=0
        var toplamSureDakika=0
        var fireAdediToplam=0
        var kirilanIgneToplam=0
        var kirilanPlatinToplam=0
        var personel=""
        var vardiya=""

        istatistikList.forEachIndexed { index, istatistik ->
            personel=istatistik.personel
            vardiya=istatistik.vardiya
            uretimAdediToplam += istatistik.uretimAdedi.toInt()
            fireAdediToplam += istatistik.fireAdedi.toInt()
            kirilanIgneToplam += istatistik.kirilanIgne.toInt()
            kirilanPlatinToplam += istatistik.kirilanPlatin.toInt()
            uretimYuzdesiToplam += istatistik.uretimYuzdesi.toInt()
            val calismaSuresi=stringToTime(istatistik.calismaSuresi)
            toplamCalismaSuresiDakika += calismaSuresi.seconds.toInt()/60
            val sure=stringToTime(istatistik.sure)
            toplamSureDakika += sure.seconds.toInt()/60

        }

        val docRef=UUID.randomUUID().toString()
        val email=auth.currentUser!!.email.toString()
        var toplamCalismaSuresiSaatString=(toplamCalismaSuresiDakika/60).toString()
        var toplamCalismaSuresiDakikaString=(toplamCalismaSuresiDakika % 60).toString()
        if (toplamCalismaSuresiSaatString.length==1){
            toplamCalismaSuresiSaatString="0$toplamCalismaSuresiSaatString"
        }
        if (toplamCalismaSuresiDakikaString.length==1){
            toplamCalismaSuresiDakikaString="0$toplamCalismaSuresiDakikaString"
        }
        val toplamCalismaSuresi="$toplamCalismaSuresiSaatString:$toplamCalismaSuresiDakikaString"
        var toplamSureSaatString=(toplamSureDakika/60).toString()
        var toplamSureDakikaString=(toplamSureDakika % 60).toString()
        if (toplamSureSaatString.length==1){
            toplamSureSaatString="0$toplamSureSaatString"
        }
        if (toplamSureDakikaString.length==1){
            toplamSureDakikaString="0$toplamSureDakikaString"
        }
        val toplamSure="$toplamSureSaatString:$toplamSureDakikaString"
        val uretimYuzdesiOrtalama=(uretimYuzdesiToplam/raporAdet).toString()


        val istatistikRaporHashMap= hashMapOf<String,Any>()
        istatistikRaporHashMap.put("makina",makina)
        istatistikRaporHashMap.put("vardiya",vardiya)
        istatistikRaporHashMap.put("personel",personel)
        istatistikRaporHashMap.put("tarihAralik",tarihAralik)
        istatistikRaporHashMap.put("timestamp",Timestamp.now())
        istatistikRaporHashMap.put("email",email)
        istatistikRaporHashMap.put("toplamCalismaSuresi",toplamCalismaSuresi)
        istatistikRaporHashMap.put("toplamSure",toplamSure)
        istatistikRaporHashMap.put("uretimYuzdesiOrtalama",uretimYuzdesiOrtalama)
        istatistikRaporHashMap.put("uretimAdediToplam",uretimAdediToplam.toString())
        istatistikRaporHashMap.put("fireAdediToplam",fireAdediToplam.toString())
        istatistikRaporHashMap.put("kirilanIgneToplam",kirilanIgneToplam.toString())
        istatistikRaporHashMap.put("kirilanPlatinToplam",kirilanPlatinToplam.toString())
        istatistikRaporHashMap.put("docRef",docRef)

        firestore.collection("istatistikmakina").document(docRef).set(istatistikRaporHashMap)
            .addOnSuccessListener {
                Toast.makeText(context,"İstatistik Raporu Yüklendi",Toast.LENGTH_SHORT).show()
                getIstatistikData(view,context)
            }.addOnFailureListener {
                uploadLiveData.value=false
                Toast.makeText(context,"İstatistik oluşturulamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
            }
    }

    private fun downloadFile(url:String,fileName:String,context: Context){
        val downloadManager= context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val link=Uri.parse(url)
        val request= DownloadManager.Request(link)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setMimeType("download/xls")
            .setAllowedOverRoaming(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,File.separator+fileName)
        downloadManager.enqueue(request)
        Toast.makeText(context,"Rapor indirildi",Toast.LENGTH_LONG).show()
        uploadLiveData.value=false
    }

    fun downloadExcel(istatistikListForExcel:ArrayList<IstatistikRapor>, activity: Activity, resources: Resources, context: Context){
        if (istatistikListForExcel.size>0){
            uploadLiveData.value=true
            val excel=createWorkbook(istatistikListForExcel)
            val calendar=Calendar.getInstance()
            val year=calendar.get(Calendar.YEAR)
            val month=calendar.get(Calendar.MONTH)
            val day=calendar.get(Calendar.DAY_OF_MONTH)
            outputDirectory=getOutputDirectory(activity, resources)
            val xlsFile= File(outputDirectory, "istatistik-${day}-${month+1}-${year}.xls")
            val fileOutputStream= FileOutputStream(xlsFile)
            excel.write(fileOutputStream)
            val uri= Uri.fromFile(xlsFile)
            val docRef=auth.currentUser!!.uid
            val refStorage=storage.reference.child(docRef).child("istatistik-${day}-${month+1}-${year}.xls")
            refStorage.putFile(uri).addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener {
                    downloadFile(it.toString(),"istatistikmakina-${day}-${month+1}-${year}.xls",context)
                }.addOnFailureListener {
                    uploadLiveData.value=false
                    Toast.makeText(context,"Hata oluştu.Tekrar deneyiniz",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                uploadLiveData.value=false
                Toast.makeText(context,"Hata oluştu.Tekrar deneyiniz",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(context,"İndirilecek rapor yok!",Toast.LENGTH_SHORT).show()
        }

    }

    private fun getOutputDirectory(activity: Activity, resources: Resources): File {
        val mediaDir=activity.externalMediaDirs.firstOrNull()?.let {
            File(it,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        if (mediaDir!=null && mediaDir.exists()){
            return mediaDir
        }else{

            return activity.filesDir
        }
    }

    private fun createWorkbook(istatistikListForExcel: ArrayList<IstatistikRapor>): HSSFWorkbook {
        val hssfWorkbook= HSSFWorkbook()
        val sheet: Sheet = hssfWorkbook.createSheet("İstatistik")
        addData(sheet,istatistikListForExcel)
        return hssfWorkbook
    }

    private fun createCell(sheetRow: Row, columnIndex: Int, cellValue: String?) {
        //create a cell at a passed in index
        val ourCell = sheetRow.createCell(columnIndex)
        //add the value to it
        //a cell can be empty. That's why its nullable
        ourCell?.setCellValue(cellValue)
    }

    private fun addData(sheet: Sheet, istatistikListForExcel: ArrayList<IstatistikRapor>) {
        val rowList=ArrayList<Row>()

        //Creating rows at passed in indices
        istatistikListForExcel.forEachIndexed { index, rapor ->
            rowList.add(sheet.createRow(index+1))
        }

        val row0=sheet.createRow(0)
        createCell(row0,0,"Tarih Aralığı")
        createCell(row0,1,"Makina")
        createCell(row0,2,"Çalışma Süresi")
        createCell(row0,3,"Süre")
        createCell(row0,4,"Üretim Yüzdesi")
        createCell(row0,5,"Üretim Adedi")
        createCell(row0,6,"Fire Adedi")
        createCell(row0,7,"Kırılan İğne")
        createCell(row0,8,"Kırılan Platin")

        rowList.forEachIndexed { index, row ->
            createCell(row,0,istatistikListForExcel[index].tarihAralik)
            createCell(row,1,istatistikListForExcel[index].makina)
            createCell(row,2,istatistikListForExcel[index].toplamCalismaSuresi)
            createCell(row,3,istatistikListForExcel[index].toplamSure)
            createCell(row,4,istatistikListForExcel[index].uretimYuzdesiOrtalama)
            createCell(row,5,istatistikListForExcel[index].uretimAdediToplam)
            createCell(row,6,istatistikListForExcel[index].fireAdediToplam)
            createCell(row,7,istatistikListForExcel[index].kirilanIgneToplam)
            createCell(row,8,istatistikListForExcel[index].kirilanPlatinToplam)

        }



    }


    private fun longToDate(long: Long):String{
        val dateString = SimpleDateFormat("dd-MM-yyyy").format(Date(long))
        return dateString
    }

    private fun stringToDate(dtStart:String):Timestamp{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = format.parse(dtStart)
        return Timestamp(date)
    }

    private fun stringToTime(dtStart: String):Timestamp{
        val format = SimpleDateFormat("HH:mm")
        val date: Date = format.parse(dtStart)
        return Timestamp(date)
    }

    private fun getMakinaList():ArrayList<String>{
        val makinaList=ArrayList<String>()
        makinaList.add("Makina Seçiniz")
        makinaList.add("M521")
        makinaList.add("M522")
        makinaList.add("M822")
        makinaList.add("M621")
        makinaList.add("M622")
        makinaList.add("M623")
        makinaList.add("M624")
        makinaList.add("M625")
        makinaList.add("M626")
        makinaList.add("M627")
        makinaList.add("M628")
        makinaList.add("M629")
        makinaList.add("M141")
        makinaList.add("M142")
        makinaList.add("M143")
        makinaList.add("M101")
        makinaList.add("M102")
        makinaList.add("M103")
        makinaList.add("M121")
        makinaList.add("M122")
        return makinaList
    }

    override fun onCleared() {
        super.onCleared()
    }

}