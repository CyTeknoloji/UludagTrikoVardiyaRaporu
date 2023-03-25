package com.caneryildirim.uludagtrikovardiyaraporu.userViewModel

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RaporDetailViewModel:ViewModel() {
    val uploadLiveData=MutableLiveData<Boolean>(false)
    private val firestore=Firebase.firestore

    fun deleteRapor(docRef:String, context: Context, activity: Activity){
        uploadLiveData.value=true
        firestore.collection("raporlar").document(docRef).delete().addOnSuccessListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Rapor silindi!", Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }.addOnFailureListener {
            uploadLiveData.value=false
            Toast.makeText(context,"Hata oluştu! Tekrar deneyiniz.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCleared() {
        super.onCleared()
    }
}