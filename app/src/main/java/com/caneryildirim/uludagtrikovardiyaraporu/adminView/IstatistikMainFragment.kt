package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentIstatistikMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class IstatistikMainFragment : Fragment() {
    private var _binding:FragmentIstatistikMainBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentIstatistikMainBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayoutSet()
    }

    private fun tabLayoutSet() {
        val tabList= listOf<String>("Makina","Vardiya","Personel")
        binding.viewPager2Istatistik.adapter= FragmentAdapterIstatistik(this)
        TabLayoutMediator(binding.tabLayoutIstatistik,binding.viewPager2Istatistik){ tab: TabLayout.Tab, i: Int ->
            tab.text=tabList[i]
        }.attach()
    }

    class FragmentAdapterIstatistik(fragment:Fragment): FragmentStateAdapter(fragment){
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0->IstatistikFragment()
                1->IstatistikVardiyaFragment()
                2->IstatistikPersonelFragment()
                else->YoneticiListesiFragment()
            }
        }

    }


}