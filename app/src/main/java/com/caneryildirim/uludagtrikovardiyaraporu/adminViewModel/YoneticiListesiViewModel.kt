package com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel

import android.app.AlertDialog
import android.content.Context
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.KullaniciEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class YoneticiListesiViewModel:ViewModel() {
    val uploadDataLive= MutableLiveData<Boolean>(false)
    val errorLive= MutableLiveData<Boolean>(false)
    private val yoneticiList=ArrayList<User>()
    val yoneticiListLive= MutableLiveData<ArrayList<User>>()
    private val auth= Firebase.auth
    private val firestore=Firebase.firestore
    private val defaultApp= FirebaseApp.getInstance()


    fun getYoneticiList(view: View){
        uploadDataLive.value=true
        firestore.collection("yoneticiler").get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    yoneticiList.clear()
                    uploadDataLive.value=false
                    for (document in it.documents){
                        val email= document.get("email") as String
                        val uid= document.get("uid") as String
                        val displayName=document.get("displayName") as String
                        val ps=document.get("ps") as String
                        val user= User(email,uid,displayName,ps)
                        yoneticiList.add(user)
                    }
                    yoneticiListLive.value=yoneticiList
                }else{
                    uploadDataLive.value=false
                }

            }
        }.addOnFailureListener {
            uploadDataLive.value=false
            Snackbar.make(view,"Veriler alınamadı.Tekrar deneyin", Snackbar.LENGTH_INDEFINITE)
                .setAction("Evet", View.OnClickListener {
                    getYoneticiList(view)
                }).show()
        }
    }

    fun yoneticiEkle(layoutInflater: LayoutInflater, context: Context,viewFromFragment: View){
        val bindingAlert= KullaniciEkleBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()
        bindingAlert.textViewBaslik.text="Yönetici Ekle"
        bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
        bindingAlert.buttonSaveNew.setOnClickListener {
            uploadDataLive.value=true
            bindingAlert.buttonSaveNew.isEnabled=false
            bindingAlert.progressBarKullaniciEkle.visibility=View.VISIBLE
            val adminName=bindingAlert.editNameNew.text.toString().trim().lowercase()
            val adminSurname=bindingAlert.editSurnameNew.text.toString().trim().lowercase()
            val pass=bindingAlert.editPassNew.text.toString().trim()
            val passRepeat=bindingAlert.editPassRepeatNew.text.toString().trim()
            if (adminName.isEmpty()){
                bindingAlert.editNameNew.setError("Yönetici adını giriniz")
                bindingAlert.editNameNew.requestFocus()
            }else if (adminSurname.isEmpty()){
                bindingAlert.editSurnameNew.setError("Yönetici soyadını giriniz")
                bindingAlert.editSurnameNew.requestFocus()
            }else if (pass.isEmpty()){
                bindingAlert.editPassNew.setError("Şifreyi belirleyin")
                bindingAlert.editPassNew.requestFocus()
            }else if (pass.length<6){
                bindingAlert.editPassNew.setError("Şifre 6 karakterden az olamaz")
                bindingAlert.editPassNew.requestFocus()
            }else if (pass!=passRepeat){
                bindingAlert.editPassRepeatNew.setError("Şifreler uyuşmuyor")
                bindingAlert.editPassRepeatNew.requestFocus()
            }else{
                val randomName= UUID.randomUUID().toString()
                val verifierApp:FirebaseApp=FirebaseApp.initializeApp(defaultApp.applicationContext,defaultApp.options,randomName)
                val verifierAuth= FirebaseAuth.getInstance(verifierApp)
                val email="$adminName$adminSurname@uludagtriko.com"
                verifierAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener {
                    if (it!=null){
                        val userNew =it.user
                        val profileUpdates = userProfileChangeRequest {
                            displayName = "$adminName $adminSurname"
                        }
                        userNew!!.updateProfile(profileUpdates)
                            .addOnSuccessListener {
                                val uidNew=userNew.uid
                                val emailNew=userNew.email.toString()
                                val displayNameNew=userNew.displayName
                                val userHashMap= hashMapOf<String,Any>()
                                userHashMap.put("email",emailNew)
                                userHashMap.put("displayName",displayNameNew!!)
                                userHashMap.put("uid",uidNew)
                                userHashMap.put("ps",pass)
                                firestore.collection("yoneticiler").document(uidNew).set(userHashMap).addOnSuccessListener {
                                    Toast.makeText(context,"Yönetici Eklendi",Toast.LENGTH_SHORT).show()
                                    bindingAlert.buttonSaveNew.isEnabled=true
                                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                                    uploadDataLive.value=false
                                    builder.cancel()
                                    getYoneticiList(viewFromFragment)
                                    verifierAuth.signOut()
                                }.addOnFailureListener {
                                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
                                    uploadDataLive.value=false
                                    bindingAlert.buttonSaveNew.isEnabled=true
                                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                                }
                            }.addOnFailureListener {
                                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
                                uploadDataLive.value=false
                                bindingAlert.buttonSaveNew.isEnabled=true
                                bindingAlert.progressBarKullaniciEkle.visibility=View.GONE
                            }
                    }

                }.addOnFailureListener {
                    Toast.makeText(context,"Kullanıcı oluşturulamadı, Tekrar deneyin.",Toast.LENGTH_SHORT).show()
                    uploadDataLive.value=false
                    bindingAlert.buttonSaveNew.isEnabled=true
                    bindingAlert.progressBarKullaniciEkle.visibility=View.GONE


                }
            }

        }
    }

    fun yoneticiSil(position:Int,context: Context,view: View){
        uploadDataLive.value=true
        if (yoneticiList[position].email==auth.currentUser!!.email){
            Toast.makeText(context,"Geçerli kullanıcı hesabı buradan silinemez",Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
        }else if (yoneticiList[position].email=="uludagtrikourtm@uludagtriko.com"){
            Toast.makeText(context,"Admin hesabı silinemez",Toast.LENGTH_SHORT).show()
            uploadDataLive.value=false
        }else{
            val randomName= UUID.randomUUID().toString()
            val verifierApp:FirebaseApp=FirebaseApp.initializeApp(defaultApp.applicationContext,defaultApp.options,randomName)
            val verifierAuth= FirebaseAuth.getInstance(verifierApp)
            val email=yoneticiList[position].email
            val ps=yoneticiList[position].ps
            verifierAuth.signInWithEmailAndPassword(email,ps).addOnSuccessListener {
                it.user?.delete()?.addOnSuccessListener {
                    firestore.collection("yoneticiler").document(yoneticiList[position].uid).delete().addOnSuccessListener {
                        Toast.makeText(context,"Silindi",Toast.LENGTH_SHORT).show()
                        uploadDataLive.value=false
                        getYoneticiList(view)
                    }.addOnFailureListener {
                        Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
                        uploadDataLive.value=false
                    }
                }?.addOnFailureListener {
                    Toast.makeText(context,"Yönetici silinemedi.Tekrar deneyin",Toast.LENGTH_SHORT).show()
                    uploadDataLive.value=false
                }
            }.addOnFailureListener {
                Toast.makeText(context,"Yönetici silinemedi.Tekrar deneyin",Toast.LENGTH_SHORT).show()
                uploadDataLive.value=false
            }

        }
    }


    override fun onCleared() {
        super.onCleared()
    }
}