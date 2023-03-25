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
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.KullaniciListesiViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentHesabimBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentKullaniciListesiBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.User


class KullaniciListesiFragment : Fragment(),RecyclerUserAdapter.OptionRecycler {
    private var _binding: FragmentKullaniciListesiBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: KullaniciListesiViewModel
    private var adapterKullanici: RecyclerUserAdapter?=null
    private var kullaniciList:ArrayList<User>?= arrayListOf()
    private var kullaniciListForUpload=ArrayList<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentKullaniciListesiBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(KullaniciListesiViewModel::class.java)
        observeLiveData()
        viewModel.getUserList(requireView())

        binding.recyclerAnketorListesi.layoutManager= LinearLayoutManager(requireContext())
        adapterKullanici= RecyclerUserAdapter(kullaniciList!!,this)
        binding.recyclerAnketorListesi.adapter=adapterKullanici

        binding.fabAnketorListesi.setOnClickListener {
            viewModel.addUser(layoutInflater,requireContext(),requireView())
        }
    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarAnketorListesi.visibility=View.VISIBLE
                binding.recyclerAnketorListesi.visibility=View.GONE
            }else{
                binding.progressBarAnketorListesi.visibility=View.GONE
                binding.recyclerAnketorListesi.visibility=View.VISIBLE
            }
        })

        viewModel.userListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                kullaniciListForUpload.clear()
                kullaniciListForUpload.addAll(it)
                binding.recyclerAnketorListesi.visibility=View.VISIBLE
                adapterKullanici?.updateKullaniciList(it)
            }else{
                binding.recyclerAnketorListesi.visibility=View.GONE
            }
        })
    }


    override fun onItemDelete(position: Int) {
        viewModel.userDelete(position,requireContext(),requireView())
    }
}