package com.caneryildirim.uludagtrikovardiyaraporu.userView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        uiSettings()
    }

    private fun uiSettings() {
        //bottom nav ile nav component birleştirme
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentAnketor) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavAnketor, navHostFragment.navController)

        //Toolbar
        //binding.toolbarYonetici.title = "Anadolu Anket Araştırma"
        binding.toolbarAnketor.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarAnketor)
        binding.toolbarAnketor.setNavigationOnClickListener {
            onBackPressed()
        }

        //Toolbar başlıklarını senkronize etmek için
        val appBarConfiguration= AppBarConfiguration(setOf(
            R.id.raporlarimFragment,R.id.hesabimKullaniciFragment))
        setupActionBarWithNavController(navHostFragment.navController,appBarConfiguration)
    }

    fun hideBottomNav(value:Boolean){
        if (value){
            binding.bottomNavAnketor.visibility= View.GONE
        }else{
            binding.bottomNavAnketor.visibility=View.VISIBLE
        }
    }
}