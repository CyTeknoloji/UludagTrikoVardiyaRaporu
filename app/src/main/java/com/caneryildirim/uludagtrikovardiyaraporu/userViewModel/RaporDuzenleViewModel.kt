package com.caneryildirim.uludagtrikovardiyaraporu.userViewModel

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.DialogSpinnerBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Beden
import com.caneryildirim.uludagtrikovardiyaraporu.model.ModelNo
import com.caneryildirim.uludagtrikovardiyaraporu.model.Parca
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.userView.RaporDuzenleFragmentDirections
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RaporDuzenleViewModel:ViewModel() {
    val uploadLiveData= MutableLiveData<Boolean>(false)
    private lateinit var rapor: Rapor
    private val auth=Firebase.auth
    private val firestore=Firebase.firestore
    val parcaListLive=MutableLiveData<ArrayList<Parca>>()
    val bedenListLive=MutableLiveData<ArrayList<Beden>>()
    val modelListLive=MutableLiveData<ArrayList<ModelNo>>()
    val textStringLive=MutableLiveData<String>("")
    val dateLive=MutableLiveData<Timestamp?>()

    fun  getRapor(raporFromArgument: Rapor){
        rapor=raporFromArgument
    }

    fun getPersonelName():String{
        val user=auth.currentUser
        val displayName=user!!.displayName.toString()
        return displayName
    }

    fun getParcaList(context: Context){
        val parcaList=ArrayList<Parca>()
        uploadLiveData.value=true
        firestore.collection("parca").orderBy("id", Query.Direction.ASCENDING).get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    uploadLiveData.value=false
                    parcaList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val docRef=document.get("docRef") as String
                        val parca=document.get("parca") as String
                        val id=document.get("id") as Number?
                        val parcaObj= Parca(docRef, parca, id!!.toInt())
                        parcaList.add(parcaObj)
                    }
                    parcaListLive.value=parcaList
                }else{
                    parcaList.clear()
                    parcaListLive.value=parcaList
                    uploadLiveData.value=false
                    Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin", Toast.LENGTH_SHORT).show()
                }
            }else{
                uploadLiveData.value=false
                Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin", Toast.LENGTH_SHORT).show()
        }
    }

    fun getBedenList(context: Context){
        val bedenList=ArrayList<Beden>()
        uploadLiveData.value=true
        firestore.collection("beden").orderBy("id",Query.Direction.ASCENDING).get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    uploadLiveData.value=false
                    bedenList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val docRef=document.get("docRef") as String
                        val beden=document.get("beden") as String
                        val id=document.get("id") as Number?
                        val bedenObj=Beden(docRef, beden, id!!.toInt())
                        bedenList.add(bedenObj)
                    }
                    bedenListLive.value=bedenList
                }else{
                    bedenList.clear()
                    bedenListLive.value=bedenList
                    uploadLiveData.value=false
                    Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
                }
            }else{
                uploadLiveData.value=false
                Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
        }
    }

    fun getModelList(context: Context){
        val modelList=ArrayList<ModelNo>()
        uploadLiveData.value=true
        firestore.collection("modelno").orderBy("id",Query.Direction.ASCENDING).get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    modelList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val docRef=document.get("docRef") as String
                        val modelNo=document.get("modelNo") as String
                        val modelAdi=document.get("modelAdi") as String
                        val id=document.get("id") as Number?
                        val modelObj=ModelNo(docRef, modelNo, modelAdi, id!!.toInt())
                        modelList.add(modelObj)
                    }
                    uploadLiveData.value=false
                    modelListLive.value=modelList
                }else{
                    modelList.clear()
                    modelListLive.value=modelList
                    uploadLiveData.value=false
                    Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
                }
            }else{
                uploadLiveData.value=false
                Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Veriler alınamadı.Tekrar deneyin",Toast.LENGTH_SHORT).show()
        }
    }

    fun raporKaydet(rapor: Rapor,context: Context,activity: Activity,view: View){
        when{
            rapor.modelNo=="Modelno seçiniz"->Toast.makeText(context,"Modelno seçiniz",Toast.LENGTH_SHORT).show()
            rapor.parca=="Parça seçiniz"->Toast.makeText(context,"Parça seçiniz",Toast.LENGTH_SHORT).show()
            rapor.nosu=="No seçiniz"->Toast.makeText(context,"No seçiniz",Toast.LENGTH_SHORT).show()
            rapor.tarih==null->Toast.makeText(context,"Tarihi giriniz",Toast.LENGTH_SHORT).show()
            rapor.makina=="Makina Seçiniz"->Toast.makeText(context,"Makina seçiniz",Toast.LENGTH_SHORT).show()
            rapor.personel.isEmpty()->Toast.makeText(context,"Personeli giriniz",Toast.LENGTH_SHORT).show()
            rapor.vardiya=="Vardiya Seçiniz"->Toast.makeText(context,"Vardiya Seçiniz",Toast.LENGTH_SHORT).show()
            rapor.calismaSuresi.isEmpty()->Toast.makeText(context,"Çalışma süresi giriniz",Toast.LENGTH_SHORT).show()
            rapor.uretimYuzdesi.isEmpty()->Toast.makeText(context,"Üretim yüzdesi giriniz",Toast.LENGTH_SHORT).show()
            rapor.uretimAdedi.isEmpty()->Toast.makeText(context,"Üretim adedi giriniz",Toast.LENGTH_SHORT).show()
            rapor.modelNo.isEmpty()->Toast.makeText(context,"Model no giriniz",Toast.LENGTH_SHORT).show()
            rapor.nosu.isEmpty()->Toast.makeText(context,"Noyu giriniz",Toast.LENGTH_SHORT).show()
            rapor.parca.isEmpty()->Toast.makeText(context,"Parçayı giriniz",Toast.LENGTH_SHORT).show()
            rapor.en.isEmpty()->Toast.makeText(context,"Eni giriniz",Toast.LENGTH_SHORT).show()
            rapor.boy.isEmpty()->Toast.makeText(context,"Boyu giriniz",Toast.LENGTH_SHORT).show()
            rapor.sure.isEmpty()->Toast.makeText(context,"Süreyi giriniz",Toast.LENGTH_SHORT).show()
            rapor.fireAdedi.isEmpty()->Toast.makeText(context,"Fire adedi giriniz",Toast.LENGTH_SHORT).show()
            rapor.kirilanIgne.isEmpty()->Toast.makeText(context,"Kırılan iğne giriniz",Toast.LENGTH_SHORT).show()
            rapor.kirilanPlatin.isEmpty()->Toast.makeText(context,"Kırılan platin giriniz",Toast.LENGTH_SHORT).show()
            else->kaydet(rapor,context, activity,view)
        }
    }

    private fun kaydet(rapor: Rapor,context: Context,activity: Activity,view: View){
        uploadLiveData.value=true
        val raporHashMap= hashMapOf<String,Any>()
        raporHashMap.put("tarih",rapor.tarih!!)
        raporHashMap.put("personel",rapor.personel)
        raporHashMap.put("vardiya",rapor.vardiya)
        raporHashMap.put("makina",rapor.makina)
        raporHashMap.put("calismaSuresi",rapor.calismaSuresi)
        raporHashMap.put("uretimYuzdesi",rapor.uretimYuzdesi)
        raporHashMap.put("uretimAdedi",rapor.uretimAdedi)
        raporHashMap.put("modelNo",rapor.modelNo)
        raporHashMap.put("nosu",rapor.nosu)
        raporHashMap.put("parca",rapor.parca)
        raporHashMap.put("en",rapor.en)
        raporHashMap.put("boy",rapor.boy)
        raporHashMap.put("sure",rapor.sure)
        raporHashMap.put("fireAdedi",rapor.fireAdedi)
        raporHashMap.put("kirilanIgne",rapor.kirilanIgne)
        raporHashMap.put("kirilanPlatin",rapor.kirilanPlatin)
        raporHashMap.put("not",rapor.not)
        raporHashMap.put("email",rapor.email)
        raporHashMap.put("docRef",rapor.docRef)

        firestore.collection("raporlar").document(rapor.docRef).set(raporHashMap).addOnSuccessListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Rapor Kaydedildi",Toast.LENGTH_SHORT).show()
            //activity.onBackPressed()
            val action=RaporDuzenleFragmentDirections.actionRaporDuzenleFragmentToRaporDetailFragment(rapor,false)
            Navigation.findNavController(view).navigate(action)
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }

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

    fun getVardiyaList():ArrayList<String>{
        val vardiyaList=ArrayList<String>()
        vardiyaList.add(rapor.vardiya)
        vardiyaList.add("07:00-15:00")
        vardiyaList.add("15:00-23:00")
        vardiyaList.add("23:00-07:00")
        return vardiyaList
    }

    fun selectModelNo(layoutInflater: LayoutInflater, context: Context){
        val bindingAlert= DialogSpinnerBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val dialog= Dialog(context)
        dialog.setContentView(view)
        dialog.window?.setLayout(1000,1200)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val modelList=modelListLive.value
        val arrayList=ArrayList<String>()
        modelList?.forEach {
            arrayList.add("${it.modelNo} (${it.modelAdi})")
        }
        val adapter= ArrayAdapter<String>(context, R.layout.simple_list_item_1,arrayList)
        bindingAlert.listView.adapter=adapter
        bindingAlert.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        bindingAlert.listView.setOnItemClickListener { parent, viewListener, position, id ->
            textStringLive.value=adapter.getItem(position)!!
            dialog.dismiss()
        }

    }



}