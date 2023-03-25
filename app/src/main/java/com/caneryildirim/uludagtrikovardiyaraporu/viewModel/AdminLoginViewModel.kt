package com.caneryildirim.uludagtrikovardiyaraporu.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.model.User
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminLoginViewModel: ViewModel() {
    private val auth=Firebase.auth
    private val firestore=Firebase.firestore
    private val yoneticiList=ArrayList<User>()
    val yoneticiListLive= MutableLiveData<ArrayList<User>>()
    val uploadDataLive=MutableLiveData<Boolean>(false)
    val errorLoginLive=MutableLiveData<Boolean>(false)


    fun getAdminData(){
        uploadDataLive.value=true
        firestore.collection("yoneticiler").get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    uploadDataLive.value=false
                    yoneticiList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val email=document.get("email") as String
                        val uid=document.get("uid") as String
                        val displayName=document.get("displayName") as String
                        val ps=document.get("ps") as String
                        val user=User(email,uid,displayName,ps)
                        yoneticiList.add(user)
                    }
                    yoneticiListLive.value=yoneticiList
                }else{
                    uploadDataLive.value=false
                }
            }
        }.addOnFailureListener {
            uploadDataLive.value=false
            errorLoginLive.value=true
        }
    }

    fun userLogin(activity: Activity, context: Context, email:String, password:String){
        uploadDataLive.value=true
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            uploadDataLive.value=true
            val intent= Intent(context, AdminActivity::class.java)
            context.startActivity(intent)
            activity.finish()
        }.addOnFailureListener {
            uploadDataLive.value=false
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCleared() {
        super.onCleared()
    }
}