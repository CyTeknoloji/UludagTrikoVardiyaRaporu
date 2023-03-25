package com.caneryildirim.uludagtrikovardiyaraporu.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.model.User
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.userView.UserActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserLoginViewModel:ViewModel() {
    private val auth= Firebase.auth
    private val firestore= Firebase.firestore
    private val userList=ArrayList<User>()
    val userListLive= MutableLiveData<ArrayList<User>>()
    val uploadDataLive= MutableLiveData<Boolean>(false)
    val errorLoginLive= MutableLiveData<Boolean>(false)

    fun getVardiyaList():ArrayList<String>{
        val vardiyaList=ArrayList<String>()
        vardiyaList.add("Vardiya Seçiniz")
        vardiyaList.add("07:00-15:00")
        vardiyaList.add("15:00-23:00")
        vardiyaList.add("23:00-07:00")
        return vardiyaList
    }

    fun getUserData(){
        uploadDataLive.value=true
        firestore.collection("kullanicilar").get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    uploadDataLive.value=false
                    userList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val email=document.get("email") as String
                        val uid=document.get("uid") as String
                        val displayName=document.get("displayName") as String
                        val ps=document.get("ps") as String
                        val user=User(email,uid,displayName,ps)
                        userList.add(user)
                    }
                    userListLive.value=userList
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
            val intent= Intent(context, UserActivity::class.java)
            context.startActivity(intent)
            activity.finish()
        }.addOnFailureListener {
            uploadDataLive.value=false
            Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCleared() {
        super.onCleared()
    }
}