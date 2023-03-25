package com.caneryildirim.uludagtrikovardiyaraporu.userView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.HesabimViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentHesabimBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentHesabimKullaniciBinding
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.HesabimKullaniciViewModel


class HesabimKullaniciFragment : Fragment() {
    private var _binding: FragmentHesabimKullaniciBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: HesabimKullaniciViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentHesabimKullaniciBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(HesabimKullaniciViewModel::class.java)
        observeLiveData()
        viewModel.currentAuth()



        binding.buttonSifreDegisHesabimAnketor.setOnClickListener {
            viewModel.updatePass(layoutInflater,requireContext())
        }
    }

    private fun observeLiveData() {
        viewModel.emailLive.observe(viewLifecycleOwner, Observer {
            binding.textEmailHesabimAnketor.text=it
        })

        viewModel.uploadData.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarHesabimAnketor.visibility=View.VISIBLE
                binding.buttonSifreDegisHesabimAnketor.isEnabled=false
            }else{
                binding.progressBarHesabimAnketor.visibility=View.GONE
                binding.buttonSifreDegisHesabimAnketor.isEnabled=true
            }
        })
    }


}