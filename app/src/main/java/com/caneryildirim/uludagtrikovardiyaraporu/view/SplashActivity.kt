package com.caneryildirim.uludagtrikovardiyaraporu.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.ActivitySplashBinding
import com.caneryildirim.uludagtrikovardiyaraporu.userView.UserActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    private val firestore=Firebase.firestore
    private val yoneticiMailList=ArrayList<String>()
    private val userMailList=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser= Firebase.auth.currentUser
            goToMainActivity()
        },3000)
    }

    private fun getAdminData(currentEmail:String){
        firestore.collection("yoneticiler").get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    yoneticiMailList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val email=document.get("email") as String
                        yoneticiMailList.add(email)
                    }
                    if (yoneticiMailList.contains(currentEmail)){
                        val intent=Intent(this, AdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        getUserData(currentEmail)
                    }
                }else{
                    goToMainActivity()
                }
            }else{
                goToMainActivity()
            }
        }.addOnFailureListener {
            goToMainActivity()
        }
    }
    private fun getUserData(currentEmail: String){
        firestore.collection("kullanicilar").get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    userMailList.clear()
                    val documents=it.documents
                    for (document in documents){
                        val email=document.get("email") as String
                        userMailList.add(email)
                    }
                    if (userMailList.contains(currentEmail)){
                        val intent=Intent(this, UserActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }else{
                    goToMainActivity()
                }
            }else{
                goToMainActivity()
            }
        }.addOnFailureListener {
            goToMainActivity()
        }
    }

    private fun goToMainActivity(){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}