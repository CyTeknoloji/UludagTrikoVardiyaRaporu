package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerUserAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.HesabimViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.YoneticiListesiViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentHesabimBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentYoneticiListesiBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.User


class YoneticiListesiFragment : Fragment(),RecyclerUserAdapter.OptionRecycler {
    private var _binding: FragmentYoneticiListesiBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: YoneticiListesiViewModel
    private var adapterKullanici: RecyclerUserAdapter?=null
    private var userList:ArrayList<User>?= arrayListOf()
    private var userListForUpload=ArrayList<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentYoneticiListesiBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(YoneticiListesiViewModel::class.java)
        observeLiveData()
        viewModel.getYoneticiList(requireView())

        binding.recyclerYoneticiListesi.layoutManager= LinearLayoutManager(requireContext())
        adapterKullanici= RecyclerUserAdapter(userList!!,this)
        binding.recyclerYoneticiListesi.adapter=adapterKullanici

        binding.fabYoneticiListesi.setOnClickListener {
            viewModel.yoneticiEkle(layoutInflater,requireContext(),requireView())
        }
    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarYoneticiListesi.visibility=View.VISIBLE
                binding.recyclerYoneticiListesi.visibility=View.GONE
            }else{
                binding.progressBarYoneticiListesi.visibility=View.GONE
                binding.recyclerYoneticiListesi.visibility=View.VISIBLE
            }
        })

        viewModel.yoneticiListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                userListForUpload.clear()
                userListForUpload.addAll(it)
                binding.recyclerYoneticiListesi.visibility=View.VISIBLE
                adapterKullanici?.updateKullaniciList(it)
            }else{
                binding.recyclerYoneticiListesi.visibility=View.GONE
            }
        })
    }

    override fun onItemDelete(position: Int) {
        viewModel.yoneticiSil(position,requireContext(),requireView())
    }
}