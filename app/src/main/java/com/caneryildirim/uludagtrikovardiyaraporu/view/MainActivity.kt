 package com.caneryildirim.uludagtrikovardiyaraporu.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.ActivityMainBinding
import com.caneryildirim.uludagtrikovardiyaraporu.userView.UserActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

 class MainActivity : AppCompatActivity() {
     private lateinit var binding:ActivityMainBinding
     private val fragmentList=ArrayList<Fragment>()
     private val fragmentNameList=ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        //Toolbar
        binding.toolbarMain.title = "Uludağ Triko Vardiya Raporu"
        binding.toolbarMain.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(binding.toolbarMain)

        fragmentList.add(UserLoginFragment())
        fragmentList.add(AdminLoginFragment())
        fragmentNameList.add("Kullanıcı Girişi")
        fragmentNameList.add("Yönetici Girişi")

        val adapter=viewPagerMainAdapter(this)
        binding.viewPagerMain.adapter=adapter

        TabLayoutMediator(binding.tabLayoutMain,binding.viewPagerMain){ tab: TabLayout.Tab, i: Int ->
            tab.setText(fragmentNameList[i])
        }.attach()



    }


     inner class viewPagerMainAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity){
         override fun getItemCount(): Int {
             return fragmentList.size
         }

         override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
         }

     }
}