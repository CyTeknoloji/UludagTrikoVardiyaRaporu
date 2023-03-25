package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.ActivityAdminBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        uiSettings()
    }

    private fun uiSettings() {
        //bottom nav ile nav component birleştirme
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostYonetici) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavYonetici, navHostFragment.navController)

        //Toolbar
        //binding.toolbarYonetici.title = "Anadolu Anket Araştırma"
        binding.toolbarYonetici.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarYonetici)
        binding.toolbarYonetici.setNavigationOnClickListener {
            onBackPressed()
        }


        //Toolbar başlıklarını senkronize etmek için
        val appBarConfiguration= AppBarConfiguration(setOf(R.id.tamamlanmisRaporlarFragment,R.id.kullanicilarFragment,R.id.hesabimFragment))
        setupActionBarWithNavController(navHostFragment.navController,appBarConfiguration)
    }

    fun hideToolbar(value:Boolean){
        if (value){
            binding.bottomNavYonetici.visibility= View.GONE
        }else{
            binding.bottomNavYonetici.visibility= View.VISIBLE
        }

    }


}