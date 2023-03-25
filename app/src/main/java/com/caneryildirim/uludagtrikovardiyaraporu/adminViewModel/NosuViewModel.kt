package com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.ModelnoEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Beden
import com.caneryildirim.uludagtrikovardiyaraporu.model.ModelNo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class NosuViewModel:ViewModel() {
    val uploadDataLive= MutableLiveData<Boolean>(false)
    private val firestore= Firebase.firestore
    val nosuListLive= MutableLiveData<ArrayList<Beden>>()

    fun getNosuList(view: View){
        val nosuList=ArrayList<Beden>()
        uploadDataLive.value=true
        firestore.collection("beden").orderBy("id", Query.Direction.ASCENDING).get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    nosuList.clear()
                    uploadDataLive.value=false
                    for (document in it.documents){
                        val docRef= document.get("docRef") as String
                        val id= document.get("id") as Number?
                        val beden=document.get("beden") as String
                        val nosuObj= Beden(docRef,beden,id!!.toInt())
                        nosuList.add(nosuObj)
                    }
                    nosuListLive.value=nosuList
                }else{
                    uploadDataLive.value=false
                }
            }
        }.addOnFailureListener {
            uploadDataLive.value=false
            Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                .setAction("Evet", View.OnClickListener {
                    getNosuList(view)
                }).show()
        }
    }

    fun deleteNosu(docRef:String, context: Context, view: View){
        uploadDataLive.value=true
        firestore.collection("beden").document(docRef).delete().addOnSuccessListener {
            Toast.makeText(context,"Silindi", Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
            getNosuList(view)
        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
        }
    }

    fun addNosu(layoutInflater: LayoutInflater, context: Context, viewFromFragment: View){
        val bindingAlert= ModelnoEkleBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()
        bindingAlert.textViewBaslikModelNo.text="Beden Ekle"
        bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
        bindingAlert.editModelAdi.visibility=View.GONE
        bindingAlert.editModelNo.hint="Beden:"
        bindingAlert.buttonSaveNew.setOnClickListener {
            val id=bindingAlert.editId.text.toString().toIntOrNull()
            val beden=bindingAlert.editModelNo.text.toString().trim()
            val docRef= UUID.randomUUID().toString()
            if (id==null){
                bindingAlert.editId.setError("Id giriniz")
                bindingAlert.editId.requestFocus()
            }else if (beden.isEmpty()){
                bindingAlert.editModelNo.setError("Beden adı giriniz")
                bindingAlert.editModelNo.requestFocus()
            }else{
                bindingAlert.buttonSaveNew.isEnabled=false
                bindingAlert.progressBarKullaniciEkle.visibility=View.VISIBLE
                uploadDataLive.value=true
                val hashMap= hashMapOf<String,Any>()
                hashMap.put("id",id)
                hashMap.put("docRef",docRef)
                hashMap.put("beden",beden)
                firestore.collection("beden").document(docRef).set(hashMap).addOnSuccessListener {
                    Toast.makeText(context,"Beden Eklendi",Toast.LENGTH_SHORT).show()
                    uploadDataLive.value=false
                    bindingAlert.buttonSaveNew.isEnabled=true
                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                    builder.cancel()
                    getNosuList(viewFromFragment)
                }.addOnFailureListener {
                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                    bindingAlert.buttonSaveNew.isEnabled=true
                    uploadDataLive.value=false
                    Toast.makeText(context,"Beden eklenemedi.Tekrar deneyiniz",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}