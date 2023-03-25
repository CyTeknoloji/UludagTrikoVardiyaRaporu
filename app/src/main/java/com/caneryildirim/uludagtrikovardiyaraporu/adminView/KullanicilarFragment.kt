package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.KullanicilarViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.TamamlanisRaporlarViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentKullanicilarBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentTamamlanmisRaporlarBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class KullanicilarFragment : Fragment() {
    private var _binding: FragmentKullanicilarBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: KullanicilarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentKullanicilarBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(KullanicilarViewModel::class.java)
        tabLayoutSet()
    }

    private fun tabLayoutSet() {
        val tabList= listOf<String>("Yöneticiler","Kullanıcılar")
        binding.viewPager2Kullanicilar.adapter=FragmentAdapter(this)
        TabLayoutMediator(binding.tabLayoutKullanicilar,binding.viewPager2Kullanicilar){ tab: TabLayout.Tab, i: Int ->
            tab.text=tabList[i]
        }.attach()
    }

    class FragmentAdapter(fragment:Fragment): FragmentStateAdapter(fragment){
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0->YoneticiListesiFragment()
                1->KullaniciListesiFragment()
                else->YoneticiListesiFragment()
            }
        }

    }


}