package com.caneryildirim.uludagtrikovardiyaraporu.userViewModel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.SifreDegistirBinding
import com.caneryildirim.uludagtrikovardiyaraporu.view.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HesabimKullaniciViewModel:ViewModel() {
    val uploadData= MutableLiveData<Boolean>(false)
    private val auth= Firebase.auth
    val emailLive= MutableLiveData<String>()
    private val firestore=Firebase.firestore

    fun currentAuth(){
        val email=auth.currentUser!!.displayName
        emailLive.value=email!!
    }

    fun signOut(activity: Activity, context: Context){
        auth.signOut()
        val intent= Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        activity.finish()
    }

    fun updatePass(layoutInflater: LayoutInflater, context: Context){
        val bindingAlert= SifreDegistirBinding.inflate(layoutInflater)
        val view=bindingAlert.root
        val alert= AlertDialog.Builder(context)
        alert.setView(view)
        val builder=alert.create()
        builder.show()
        bindingAlert.buttonSaveNew.setOnClickListener {
            val email=auth.currentUser!!.email
            val oldPass=bindingAlert.editTextPassDegis.text.toString().trim()
            val newPass=bindingAlert.editPassNewDegis.text.toString().trim()
            val newRepeatPass=bindingAlert.editPassRepeatDegis.text.toString().trim()
            if (newPass.isEmpty()){
                bindingAlert.editPassNewDegis.setError("Yeni şifreyi girin")
                bindingAlert.editPassNewDegis.requestFocus()
            }else if (newPass.length<6){
                bindingAlert.editPassNewDegis.setError("Yeni şifre 6 karakterden az olamaz")
                bindingAlert.editPassNewDegis.requestFocus()
            }else if (newPass!=newRepeatPass){
                bindingAlert.editPassRepeatDegis.setError("Şifreler uyuşmuyor")
                bindingAlert.editPassRepeatDegis.requestFocus()
            }else{
                uploadData.value=true
                bindingAlert.buttonSaveNew.isEnabled=false
                auth.signInWithEmailAndPassword(email!!,oldPass).addOnSuccessListener {
                    it.user!!.updatePassword(newPass).addOnSuccessListener {
                        firestore.collection("kullanicilar").document(auth.currentUser!!.uid)
                            .update("ps",newPass).addOnSuccessListener {
                                uploadData.value=false
                                bindingAlert.buttonSaveNew.isEnabled=true
                                Toast.makeText(context,"Şifre değiştirildi",Toast.LENGTH_SHORT).show()
                                builder.cancel()
                            }.addOnFailureListener {
                                uploadData.value=false
                                bindingAlert.buttonSaveNew.isEnabled=true
                                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
                                builder.cancel()
                            }
                    }.addOnFailureListener {
                        uploadData.value=false
                        bindingAlert.buttonSaveNew.isEnabled=true
                        Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    uploadData.value=false
                    bindingAlert.buttonSaveNew.isEnabled=true
                    Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }


        }
    }




    override fun onCleared() {
        super.onCleared()
    }
}