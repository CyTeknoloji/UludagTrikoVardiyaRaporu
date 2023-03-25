package com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.KullaniciEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.ModelnoEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.ModelNo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class ModelNoViewModel:ViewModel() {
    val uploadDataLive=MutableLiveData<Boolean>(false)
    private val firestore=Firebase.firestore
    val modelNoListLive=MutableLiveData<ArrayList<ModelNo>>()

    fun getModelNoList(view:View){
        val modelNoList=ArrayList<ModelNo>()
        uploadDataLive.value=true
        firestore.collection("modelno").orderBy("id",Query.Direction.ASCENDING).get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    modelNoList.clear()
                    uploadDataLive.value=false
                    for (document in it.documents){
                        val docRef= document.get("docRef") as String
                        val id= document.get("id") as Number?
                        val modelNo=document.get("modelNo") as String
                        val modelAdi=document.get("modelAdi") as String
                        val modelNoObj= ModelNo(docRef,modelNo,modelAdi,id!!.toInt())
                        modelNoList.add(modelNoObj)
                    }
                    modelNoListLive.value=modelNoList
                }else{
                    uploadDataLive.value=false
                }
            }
        }.addOnFailureListener {
            uploadDataLive.value=false
            Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                .setAction("Evet", View.OnClickListener {
                    getModelNoList(view)
                }).show()
        }
    }

    fun deleteModelNo(docRef:String,context: Context,view: View){
        uploadDataLive.value=true
        firestore.collection("modelno").document(docRef).delete().addOnSuccessListener {
            Toast.makeText(context,"Silindi", Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
            getModelNoList(view)
        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
        }
    }

    fun addModelNo(layoutInflater: LayoutInflater,context: Context,viewFromFragment: View){
        val bindingAlert= ModelnoEkleBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()
        bindingAlert.textViewBaslikModelNo.text="ModelNo Ekle"
        bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
        bindingAlert.buttonSaveNew.setOnClickListener {
            val id=bindingAlert.editId.text.toString().toIntOrNull()
            val modelNo=bindingAlert.editModelNo.text.toString().trim()
            val modelAdi=bindingAlert.editModelAdi.text.toString()
            val docRef=UUID.randomUUID().toString()
            if (id==null){
                bindingAlert.editId.setError("Id giriniz")
                bindingAlert.editId.requestFocus()
            }else if (modelNo.isEmpty()){
                bindingAlert.editModelNo.setError("ModelNo giriniz")
                bindingAlert.editModelNo.requestFocus()
            }else if (modelAdi.isEmpty()){
                bindingAlert.editModelAdi.setError("Model Adı giriniz")
                bindingAlert.editModelAdi.requestFocus()
            }else{
                bindingAlert.buttonSaveNew.isEnabled=false
                bindingAlert.progressBarKullaniciEkle.visibility=View.VISIBLE
                uploadDataLive.value=true
                val hashMap= hashMapOf<String,Any>()
                hashMap.put("id",id)
                hashMap.put("docRef",docRef)
                hashMap.put("modelNo",modelNo)
                hashMap.put("modelAdi",modelAdi)
                firestore.collection("modelno").document(docRef).set(hashMap).addOnSuccessListener {
                    Toast.makeText(context,"ModelNo Eklendi",Toast.LENGTH_SHORT).show()
                    uploadDataLive.value=false
                    bindingAlert.buttonSaveNew.isEnabled=true
                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                    builder.cancel()
                    getModelNoList(viewFromFragment)
                }.addOnFailureListener {
                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                    bindingAlert.buttonSaveNew.isEnabled=true
                    uploadDataLive.value=false
                    Toast.makeText(context,"ModelNo eklenemedi.Tekrar deneyiniz",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}