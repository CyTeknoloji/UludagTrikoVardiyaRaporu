package com.caneryildirim.uludagtrikovardiyaraporu.userViewModel

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.icu.util.Calendar
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
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
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FilterRaporlarimBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FilterTamamlanmisRaporBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.ModelNo
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.userView.UserActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RaporlarimViewModel:ViewModel() {
    val uploadLiveData=MutableLiveData<Boolean>(false)
    private val auth=Firebase.auth
    private val firestore=Firebase.firestore
    private val raporList=ArrayList<Rapor>()
    val raporListLive=MutableLiveData<ArrayList<Rapor>>()
    private lateinit var outputDirectory: File
    private val storage=Firebase.storage

    fun getData(view:View,context:Context){
        uploadLiveData.value=true
        val calendar=Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val dtStart="$day-${month+1}-$year"
        println(dtStart)
        val currentDate=stringToDate(dtStart)
        val currentEmail=auth.currentUser!!.email.toString()
        firestore.collection("raporlar").whereEqualTo("email",currentEmail)
            .whereEqualTo("tarih",currentDate).get().addOnSuccessListener {
                if (it!=null){
                    if (!it.isEmpty){
                        uploadLiveData.value=false
                        raporList.clear()
                        val documents=it.documents
                        for (document in documents){
                            val boy=document.get("boy") as String
                            val calismaSuresi=document.get("calismaSuresi") as String
                            val email=document.get("email") as String
                            val en=document.get("en") as String
                            val fireAdedi=document.get("fireAdedi") as String
                            val kirilanIgne=document.get("kirilanIgne") as String
                            val kirilanPlatin=document.get("kirilanPlatin") as String
                            val not=document.get("not") as String
                            val makina=document.get("makina") as String
                            val modelNo=document.get("modelNo") as String
                            val nosu=document.get("nosu") as String
                            val parca=document.get("parca") as String
                            val personel=document.get("personel") as String
                            val sure=document.get("sure") as String
                            val tarih=document.get("tarih") as Timestamp
                            val docRef=document.get("docRef") as String
                            val uretimAdedi=document.get("uretimAdedi") as String
                            val uretimYuzdesi=document.get("uretimYuzdesi") as String
                            val vardiya=document.get("vardiya") as String
                            val rapor=Rapor(tarih,personel,vardiya,makina,calismaSuresi,uretimYuzdesi,uretimAdedi,modelNo,
                            nosu,parca,en,boy,sure,fireAdedi,kirilanIgne,kirilanPlatin,not,email,docRef)

                            raporList.add(rapor)
                            }
                        raporListLive.value=raporList
                        }else{
                            //emty
                            raporList.clear()
                            raporListLive.value=raporList
                            uploadLiveData.value=false
                    }
                    }else{
                        //null
                    uploadLiveData.value=false
                    Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Evet",View.OnClickListener {
                            getData(view,context)
                        }).show()
                }
            }.addOnFailureListener {
                uploadLiveData.value=false
                Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Evet",View.OnClickListener {
                        getData(view,context)
                    }).show()
            }
    }

    fun deleteRapor(docRef:String,context: Context,view: View){
        uploadLiveData.value=true
        firestore.collection("raporlar").document(docRef).delete().addOnSuccessListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Rapor silindi!",Toast.LENGTH_SHORT).show()
            getData(view,context)
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Hata oluştu! Tekrar deneyiniz.",Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadExcel(raporListForExcel:ArrayList<Rapor>,activity: Activity,resources: Resources,context: Context){
        if (raporListForExcel.size>0){
            uploadLiveData.value=true
            val excel=createWorkbook(raporListForExcel)
            val calendar=Calendar.getInstance()
            val year=calendar.get(Calendar.YEAR)
            val month=calendar.get(Calendar.MONTH)
            val day=calendar.get(Calendar.DAY_OF_MONTH)
            outputDirectory=getOutputDirectory(activity, resources)
            val xlsFile=File(outputDirectory, "rapor-${day}-${month+1}-${year}.xls")
            val fileOutputStream= FileOutputStream(xlsFile)
            excel.write(fileOutputStream)
            val uri= Uri.fromFile(xlsFile)
            val docRef=auth.currentUser!!.uid       //uuid den buna çevirdin
            val refStorage=storage.reference.child(docRef).child("rapor-${day}-${month+1}-${year}.xls")
            refStorage.putFile(uri).addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener {
                    downloadFile(it.toString(),"rapor-${day}-${month+1}-${year}.xls",context)
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

    fun filter(layoutInflater: LayoutInflater, context: Context, activity: UserActivity){
        var makina=""
        var vardiya=""
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        var startDateString="01-01-1990"
        var endDateString="$day-${month+1}-$year"
        val bindingAlert= FilterRaporlarimBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()

        val vardiyaFilterList=getVardiyaList()
        val vardiyaFilterAdapter= ArrayAdapter(context, R.layout.spinner_item, R.id.spinner_text,vardiyaFilterList)
        bindingAlert.spinnerVardiyaFilter.adapter=vardiyaFilterAdapter
        bindingAlert.spinnerVardiyaFilter.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                vardiya=vardiyaFilterList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                vardiya=vardiyaFilterList[0]
            }

        }

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
                startDateString.isEmpty()->Toast.makeText(context,"Tarih aralığı seçiniz",Toast.LENGTH_SHORT).show()
                endDateString.isEmpty()->Toast.makeText(context,"Tarih aralığı seçiniz",Toast.LENGTH_SHORT).show()
                makina=="Makina Seçiniz"->Toast.makeText(context,"Makina seçiniz",Toast.LENGTH_SHORT).show()
                else->{
                    val startDate=stringToDate(startDateString)
                    val endDate=stringToDate(endDateString)
                    resultFilter(startDate,endDate, makina, vardiya)
                    builder.cancel()
                }
            }
        }
        bindingAlert.buttonVazgecFilter.setOnClickListener {
            raporListLive.value=raporList
            builder.cancel()
        }

    }

    private fun resultFilter(startDate:Timestamp,endDate:Timestamp,makina:String,vardiya:String){
        val newraporList=ArrayList<Rapor>()
        newraporList.clear()
        when{
            vardiya=="Tümü"->{
                newraporList.addAll(
                    raporList.filter { it.tarih!! >= startDate && it.tarih <= endDate && it.makina==makina })
                raporListLive.value=newraporList
            }
            else->{
                newraporList.addAll(
                    raporList.filter { it.tarih!! >= startDate && it.tarih <= endDate && it.makina==makina && it.vardiya==vardiya })
                raporListLive.value=newraporList
            }

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

    fun getMakinaListForAdapter():ArrayList<String>{
        val makinaList=ArrayList<String>()
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

    private fun getVardiyaList():ArrayList<String>{
        val vardiyaList=ArrayList<String>()
        vardiyaList.add("Tümü")
        vardiyaList.add("07:00-15:00")
        vardiyaList.add("15:00-23:00")
        vardiyaList.add("23:00-07:00")
        return vardiyaList
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

    private fun createWorkbook(raporListForExcel: ArrayList<Rapor>): HSSFWorkbook {
        val hssfWorkbook= HSSFWorkbook()
        val sheet: Sheet = hssfWorkbook.createSheet("Rapor")
        addData(sheet,raporListForExcel)
        return hssfWorkbook
    }

    private fun createCell(sheetRow: Row, columnIndex: Int, cellValue: String?) {
        //create a cell at a passed in index
        val ourCell = sheetRow.createCell(columnIndex)
        //add the value to it
        //a cell can be empty. That's why its nullable
        ourCell?.setCellValue(cellValue)
    }

    private fun addData(sheet: Sheet, raporListForExcel: ArrayList<Rapor>) {
        val rowList=ArrayList<Row>()

        //Creating rows at passed in indices
        raporListForExcel.forEachIndexed { index, rapor ->
            rowList.add(sheet.createRow(index+1))
        }

        val row0=sheet.createRow(0)
        createCell(row0,0,"Tarih")
        createCell(row0,1,"Personel")
        createCell(row0,2,"Vardiya")
        createCell(row0,3,"Makina")
        createCell(row0,4,"Çalışma Süresi")
        createCell(row0,5,"Üretim Yüzdesi")
        createCell(row0,6,"Üretim Adedi")
        createCell(row0,7,"Model No")
        createCell(row0,8,"Nosu")
        createCell(row0,9,"Parça")
        createCell(row0,10,"En")
        createCell(row0,11,"Boy")
        createCell(row0,12,"Süre")
        createCell(row0,13,"Fire Adedi")
        createCell(row0,14,"Kırılan İğne")
        createCell(row0,15,"Kırılan Platin")
        createCell(row0,16,"Not")
        rowList.forEachIndexed { index, row ->
            createCell(row,0,dateToString(raporListForExcel[index].tarih!!))
            createCell(row,1,raporListForExcel[index].personel)
            createCell(row,2,raporListForExcel[index].vardiya)
            createCell(row,3,raporListForExcel[index].makina)
            createCell(row,4,raporListForExcel[index].calismaSuresi)
            createCell(row,5,raporListForExcel[index].uretimYuzdesi)
            createCell(row,6,raporListForExcel[index].uretimAdedi)
            createCell(row,7,raporListForExcel[index].modelNo)
            createCell(row,8,raporListForExcel[index].nosu)
            createCell(row,9,raporListForExcel[index].parca)
            createCell(row,10,raporListForExcel[index].en)
            createCell(row,11,raporListForExcel[index].boy)
            createCell(row,12,raporListForExcel[index].sure)
            createCell(row,13,raporListForExcel[index].fireAdedi)
            createCell(row,14,raporListForExcel[index].kirilanIgne)
            createCell(row,15,raporListForExcel[index].kirilanPlatin)
            createCell(row,16,raporListForExcel[index].not)

        }



    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

    private fun downloadFile(url:String,fileName:String,context: Context){
        val downloadManager= context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val link=Uri.parse(url)
        val request=DownloadManager.Request(link)
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




    override fun onCleared() {
        super.onCleared()
    }






}