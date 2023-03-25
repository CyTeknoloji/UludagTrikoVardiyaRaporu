package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.HesabimViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.TamamlanisRaporlarViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentHesabimBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentTamamlanmisRaporlarBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HesabimFragment : Fragment() {
    private var _binding: FragmentHesabimBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: HesabimViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentHesabimBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(HesabimViewModel::class.java)
        observeLiveData()
        viewModel.currentAuth()

        val auth=Firebase.auth
        val email=auth.currentUser!!.email
        if (email=="uludagtrikourtm@uludagtriko.com"){
            binding.buttonAdminRaporEkle.visibility=View.VISIBLE
            binding.buttonAdminRaporGoruntule.visibility=View.VISIBLE
        }else{
            binding.buttonAdminRaporEkle.visibility=View.GONE
            binding.buttonAdminRaporGoruntule.visibility=View.GONE
        }

        binding.buttonSifreDegisHesabim.setOnClickListener {
            viewModel.updatePass(layoutInflater,requireContext())
        }

        binding.buttonAdminRaporEkle.setOnClickListener {
            val action=HesabimFragmentDirections.actionHesabimFragmentToAdminRaporEkleFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.buttonAdminRaporGoruntule.setOnClickListener {
            val action=HesabimFragmentDirections.actionHesabimFragmentToAdminRaporGosterFragment()
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun observeLiveData() {
        viewModel.emailLive.observe(viewLifecycleOwner, Observer {
            binding.textEmailHesabim.text=it
        })

        viewModel.uploadData.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarHesabim.visibility=View.VISIBLE
                binding.buttonSifreDegisHesabim.isEnabled=false
            }else{
                binding.progressBarHesabim.visibility=View.GONE
                binding.buttonSifreDegisHesabim.isEnabled=true
            }
        })
    }


}