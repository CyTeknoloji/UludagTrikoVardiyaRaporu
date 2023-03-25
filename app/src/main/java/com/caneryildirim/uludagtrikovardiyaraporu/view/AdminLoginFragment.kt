package com.caneryildirim.uludagtrikovardiyaraporu.view

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentAdminLoginBinding
import com.caneryildirim.uludagtrikovardiyaraporu.viewModel.AdminLoginViewModel
import com.google.android.material.snackbar.Snackbar


class AdminLoginFragment : Fragment() {
    private var _binding:FragmentAdminLoginBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:AdminLoginViewModel
    private var yoneticiEmailList=ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentAdminLoginBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(AdminLoginViewModel::class.java)
        viewModel.getAdminData()
        observeLiveData()
        setObject()
    }

    private fun setObject() {
        binding.buttonAdmin.setOnClickListener {
            val adminName=binding.editNameLoginAdmin.text.toString().trim().lowercase()
            val adminSurname=binding.editSurnameLoginAdmin.text.toString().trim().lowercase()
            val password=binding.editTextTextPasswordAdmin.text.toString().trim().lowercase()
            if (adminName.isEmpty()){
                binding.editNameLoginAdmin.setError("Adınızı giriniz!")
                binding.editNameLoginAdmin.requestFocus()
            }else if (adminSurname.isEmpty()){
                binding.editSurnameLoginAdmin.setError("Soyadınızı giriniz!")
                binding.editSurnameLoginAdmin.requestFocus()
            }else if (password.isEmpty()){
                binding.editTextTextPasswordAdmin.setError("Şifrenizi giriniz!")
                binding.editTextTextPasswordAdmin.requestFocus()
            }else if (password.length<6){
                binding.editTextTextPasswordAdmin.setError("Şifreniz 6 karakterden az olamaz!")
                binding.editTextTextPasswordAdmin.requestFocus()
            }else{
                val email="$adminName$adminSurname@uludagtriko.com"
                if (yoneticiEmailList.contains(email)){
                    viewModel.userLogin(requireActivity(),requireContext(),email,password)
                }else{
                    Toast.makeText(requireContext(),"Girilen Email'e ait bir yönetici hesabı yok!",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarLoginAdmin.visibility=View.VISIBLE
                binding.buttonAdmin.isEnabled=false
                binding.editNameLoginAdmin.isEnabled=false
                binding.editSurnameLoginAdmin.isEnabled=false
                binding.editTextTextPasswordAdmin.isEnabled=false
            }else{
                binding.progressBarLoginAdmin.visibility=View.GONE
                binding.buttonAdmin.isEnabled=true
                binding.editNameLoginAdmin.isEnabled=true
                binding.editSurnameLoginAdmin.isEnabled=true
                binding.editTextTextPasswordAdmin.isEnabled=true
            }
        })

        viewModel.errorLoginLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.buttonAdmin.isEnabled=false
                binding.editNameLoginAdmin.isEnabled=false
                binding.editSurnameLoginAdmin.isEnabled=false
                binding.editTextTextPasswordAdmin.isEnabled=false
                Snackbar.make(requireView(),"Veriler alınamadı,tekrar deneyin!",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Evet",View.OnClickListener {
                        viewModel.getAdminData()
                    }).show()
            }else{
                binding.buttonAdmin.isEnabled=true
                binding.editNameLoginAdmin.isEnabled=true
                binding.editSurnameLoginAdmin.isEnabled=true
                binding.editTextTextPasswordAdmin.isEnabled=true
            }
        })

        viewModel.yoneticiListLive.observe(viewLifecycleOwner, Observer {
            yoneticiEmailList.clear()
            it.forEach {
                yoneticiEmailList.add(it.email)
            }
        })

    }

}