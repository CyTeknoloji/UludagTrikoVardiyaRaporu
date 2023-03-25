package com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.icu.util.Calendar
import android.net.Uri
import android.os.Environment
import android.text.format.Time
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.model.MakinaPerformansRapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
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

class AdminRaporGosterViewModel:ViewModel() {
    val uploadLiveData= MutableLiveData<Boolean>(false)
    private val auth= Firebase.auth
    private val firestore= Firebase.firestore
    private val storage= Firebase.storage
    private val raporList=ArrayList<MakinaPerformansRapor>()
    val raporListLive= MutableLiveData<ArrayList<MakinaPerformansRapor>>()
    private lateinit var outputDirectory: File
    val dateLive=MutableLiveData<Timestamp?>()

    fun getData(view: View, context: Context,currentDate:Timestamp){
        uploadLiveData.value=true
        firestore.collection("makinaperformansraporlar").whereEqualTo("tarih",currentDate)
            .orderBy("makina", Query.Direction.DESCENDING).get().addOnSuccessListener {
                if (it!=null){
                    if (!it.isEmpty){
                        uploadLiveData.value=false
                        raporList.clear()
                        val documents=it.documents
                        for (document in documents){
                            val tarih=document.get("tarih") as Timestamp
                            val calismaSuresi=document.get("calismaSuresi") as String
                            val dirDrs=document.get("dirDrs") as String
                            val docRef=document.get("docRef") as String
                            val hareketCubuk=document.get("hareketCubuk") as String
                            val igneSen=document.get("igneSen") as String
                            val iplikBes=document.get("iplikBes") as String
                            val jakarHatasi=document.get("jakarHatasi") as String
                            val makina=document.get("makina") as String
                            val makineStop=document.get("makineStop") as String
                            val merdanECekim=document.get("merdanECekim") as String
                            val parcaSayaci=document.get("parcaSayaci") as String
                            val programlama=document.get("programlama") as String
                            val sokStopAparati=document.get("sokStopAparati") as String
                            val toplamHareket=document.get("toplamHareket") as String
                            val uretimAdedi=document.get("uretimAdedi") as String
                            val uretimSuresi=document.get("uretimSuresi") as String
                            val uretimYuzdesi=document.get("uretimYuzdesi") as String
                            val yavasHareket=document.get("yavasHareket") as String

                            val makinaPerformansRapor=MakinaPerformansRapor(tarih,makina,calismaSuresi,uretimSuresi,uretimYuzdesi,
                            hareketCubuk, iplikBes, parcaSayaci, dirDrs, igneSen, merdanECekim, programlama, makineStop, sokStopAparati,
                                jakarHatasi, toplamHareket, yavasHareket, uretimAdedi, docRef)

                            raporList.add(makinaPerformansRapor)
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
                    Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Evet", View.OnClickListener {
                            getData(view,context,currentDate)
                        }).show()
                }
            }.addOnFailureListener {
                uploadLiveData.value=false
                Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Evet", View.OnClickListener {
                        getData(view,context,currentDate)
                    }).show()
            }
    }

    fun deleteRapor(docRef:String, context: Context, view: View,currentDate: Timestamp){
        uploadLiveData.value=true
        firestore.collection("makinaperformansraporlar").document(docRef).delete().addOnSuccessListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Rapor silindi!", Toast.LENGTH_SHORT).show()
            getData(view,context,currentDate)
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Hata oluştu! Tekrar deneyiniz.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getDateFromPicker(context:Context){
        val calendar=Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val datePicker=
            DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, y, m, dayOfMonth ->
            val newM=m+1
            val dtStart="$dayOfMonth-$newM-$y"
            dateLive.value= stringToDate(dtStart)
        },year,month,day)
        datePicker.setTitle("Tarih Seçiniz")
        datePicker.setButton(DialogInterface.BUTTON_POSITIVE,"ayarla",datePicker)
        datePicker.setButton(DialogInterface.BUTTON_NEGATIVE,"iptal",datePicker)
        datePicker.show()
    }

    private fun stringToDate(dtStart:String):Timestamp{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = format.parse(dtStart)
        return Timestamp(date)
    }

    private fun downloadFile(url:String,fileName:String,context: Context){
        val downloadManager= context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val link= Uri.parse(url)
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

    fun downloadExcel(raporListForExcel:ArrayList<MakinaPerformansRapor>, activity: Activity, resources: Resources, context: Context){
        if (raporListForExcel.size>0){
            uploadLiveData.value=true
            val excel=createWorkbook(raporListForExcel)
            val calendar= Calendar.getInstance()
            val year=calendar.get(Calendar.YEAR)
            val month=calendar.get(Calendar.MONTH)
            val day=calendar.get(Calendar.DAY_OF_MONTH)
            outputDirectory=getOutputDirectory(activity, resources)
            val xlsFile=File(outputDirectory, "makinarapor-${day}-${month+1}-${year}.xls")
            val fileOutputStream= FileOutputStream(xlsFile)
            excel.write(fileOutputStream)
            val uri=Uri.fromFile(xlsFile)
            val docRef=auth.currentUser!!.uid       // uuid den buna çevirdin
            val refStorage=storage.reference.child(docRef).child("makinarapor-${day}-${month+1}-${year}.xls")
            refStorage.putFile(uri).addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener {
                    downloadFile(it.toString(),"makinarapor-${day}-${month+1}-${year}.xls",context)
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

    private fun getOutputDirectory(activity: Activity,resources:Resources): File {
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

    private fun createWorkbook(raporListForExcel: ArrayList<MakinaPerformansRapor>): HSSFWorkbook {
        val hssfWorkbook= HSSFWorkbook()
        val sheet: Sheet = hssfWorkbook.createSheet("Makina Rapor")
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

    private fun addData(sheet: Sheet, raporListForExcel: ArrayList<MakinaPerformansRapor>) {
        val rowList=ArrayList<Row>()

        //Creating rows at passed in indices
        raporListForExcel.forEachIndexed { index, rapor ->
            rowList.add(sheet.createRow(index+1))
        }

        val row0=sheet.createRow(0)
        createCell(row0,0,"Makina")
        createCell(row0,1,"Tarih")
        createCell(row0,2,"Çalışma Süresi")
        createCell(row0,3,"Üretim Süresi")
        createCell(row0,4,"Üretim Yüzdesi")
        createCell(row0,5,"Hareket Çubuk")
        createCell(row0,6,"İplik Bes")
        createCell(row0,7,"Parça Sayaci")
        createCell(row0,8,"Dir Drs")
        createCell(row0,9,"İğne Sen")
        createCell(row0,10,"Merdan E Çekim")
        createCell(row0,11,"Programlama")
        createCell(row0,12,"Makine Stop")
        createCell(row0,13,"Şok Stop Aparatı")
        createCell(row0,14,"Jakar Hatası")
        createCell(row0,15,"Toplam Hareket")
        createCell(row0,16,"Yavaş hareket")
        createCell(row0,17,"Üretim Adedi")
        rowList.forEachIndexed { index, row ->
            createCell(row,0,raporListForExcel[index].makina)
            createCell(row,1,dateToString(raporListForExcel[index].tarih))
            createCell(row,2,raporListForExcel[index].calismaSure)
            createCell(row,3,raporListForExcel[index].uretimSure)
            createCell(row,4,raporListForExcel[index].uretimYuzde)
            createCell(row,5,raporListForExcel[index].hareketCubuk)
            createCell(row,6,raporListForExcel[index].iplikBes)
            createCell(row,7,raporListForExcel[index].parcaSayaci)
            createCell(row,8,raporListForExcel[index].dirDrs)
            createCell(row,9,raporListForExcel[index].igneSen)
            createCell(row,10,raporListForExcel[index].merdanECekim)
            createCell(row,11,raporListForExcel[index].programlama)
            createCell(row,12,raporListForExcel[index].makineStop)
            createCell(row,13,raporListForExcel[index].sokStopAparati)
            createCell(row,14,raporListForExcel[index].jakarHatasi)
            createCell(row,15,raporListForExcel[index].toplamHareket)
            createCell(row,16,raporListForExcel[index].yavasHareket)
            createCell(row,17,raporListForExcel[index].uretimAdedi)
        }



    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

    override fun onCleared() {
        super.onCleared()
    }
}