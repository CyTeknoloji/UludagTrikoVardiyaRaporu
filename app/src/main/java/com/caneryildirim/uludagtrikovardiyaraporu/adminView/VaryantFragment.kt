package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.VaryantViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentVaryantBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class VaryantFragment : Fragment() {
    private var _binding:FragmentVaryantBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:VaryantViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentVaryantBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(VaryantViewModel::class.java)
        tabLayoutSet()
    }


    private fun tabLayoutSet() {
        val tabList= listOf<String>("ModelNo","Parça","Nosu")
        binding.viewPager2Varyant.adapter= FragmentAdapterVaryant(this)
        TabLayoutMediator(binding.tabLayoutVaryant,binding.viewPager2Varyant){ tab: TabLayout.Tab, i: Int ->
            tab.text=tabList[i]
        }.attach()
    }

    class FragmentAdapterVaryant(fragment:Fragment): FragmentStateAdapter(fragment){
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0->ModelNoFragment()
                1->ParcaFragment()
                2->NosuFragment()
                else->YoneticiListesiFragment()
            }
        }

    }
}