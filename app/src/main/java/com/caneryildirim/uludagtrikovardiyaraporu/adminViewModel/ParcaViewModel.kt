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
import com.caneryildirim.uludagtrikovardiyaraporu.model.Parca
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class ParcaViewModel:ViewModel() {
    val uploadDataLive= MutableLiveData<Boolean>(false)
    private val firestore= Firebase.firestore
    val parcaListLive= MutableLiveData<ArrayList<Parca>>()

    fun getParcaList(view: View){
        val parcaList=ArrayList<Parca>()
        uploadDataLive.value=true
        firestore.collection("parca").orderBy("id", Query.Direction.ASCENDING).get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    parcaList.clear()
                    uploadDataLive.value=false
                    for (document in it.documents){
                        val docRef= document.get("docRef") as String
                        val id= document.get("id") as Number?
                        val parca=document.get("parca") as String
                        val parcaObj= Parca(docRef,parca,id!!.toInt())
                        parcaList.add(parcaObj)
                    }
                    parcaListLive.value=parcaList
                }else{
                    uploadDataLive.value=false
                }
            }
        }.addOnFailureListener {
            uploadDataLive.value=false
            Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                .setAction("Evet", View.OnClickListener {
                    getParcaList(view)
                }).show()
        }
    }

    fun deleteParca(docRef:String, context: Context, view: View){
        uploadDataLive.value=true
        firestore.collection("parca").document(docRef).delete().addOnSuccessListener {
            Toast.makeText(context,"Silindi", Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
            getParcaList(view)
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
        bindingAlert.textViewBaslikModelNo.text="Parça Ekle"
        bindingAlert.progressBarKullaniciEkle.visibility= View.GONE
        bindingAlert.editModelAdi.visibility= View.GONE
        bindingAlert.editModelNo.hint="Parça:"
        bindingAlert.buttonSaveNew.setOnClickListener {
            val id=bindingAlert.editId.text.toString().toIntOrNull()
            val parca=bindingAlert.editModelNo.text.toString().trim()
            val docRef= UUID.randomUUID().toString()
            if (id==null){
                bindingAlert.editId.setError("Id giriniz")
                bindingAlert.editId.requestFocus()
            }else if (parca.isEmpty()){
                bindingAlert.editModelNo.setError("Parça giriniz")
                bindingAlert.editModelNo.requestFocus()
            }else{
                bindingAlert.buttonSaveNew.isEnabled=false
                bindingAlert.progressBarKullaniciEkle.visibility= View.VISIBLE
                uploadDataLive.value=true
                val hashMap= hashMapOf<String,Any>()
                hashMap.put("id",id)
                hashMap.put("docRef",docRef)
                hashMap.put("parca",parca)
                firestore.collection("parca").document(docRef).set(hashMap).addOnSuccessListener {
                    Toast.makeText(context,"Parça Eklendi", Toast.LENGTH_SHORT).show()
                    uploadDataLive.value=false
                    bindingAlert.buttonSaveNew.isEnabled=true
                    bindingAlert.progressBarKullaniciEkle.visibility= View.GONE
                    builder.cancel()
                    getParcaList(viewFromFragment)
                }.addOnFailureListener {
                    bindingAlert.progressBarKullaniciEkle.visibility= View.GONE
                    bindingAlert.buttonSaveNew.isEnabled=true
                    uploadDataLive.value=false
                    Toast.makeText(context,"Parça eklenemedi.Tekrar deneyiniz", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}