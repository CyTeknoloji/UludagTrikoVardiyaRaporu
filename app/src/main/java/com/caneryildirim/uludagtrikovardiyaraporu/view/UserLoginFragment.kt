package com.caneryildirim.uludagtrikovardiyaraporu.view

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentUserLoginBinding
import com.caneryildirim.uludagtrikovardiyaraporu.util.Singleton
import com.caneryildirim.uludagtrikovardiyaraporu.viewModel.UserLoginViewModel
import com.google.android.material.snackbar.Snackbar


class UserLoginFragment : Fragment() {
    private var _binding:FragmentUserLoginBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:UserLoginViewModel
    private var userEmailList=ArrayList<String>()
    private lateinit var vardiya:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentUserLoginBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(UserLoginViewModel::class.java)
        viewModel.getUserData()
        observeLiveData()
        setObject()
    }

    private fun setObject() {
        val vardiyaList=viewModel.getVardiyaList()
        val vardiyaAdapter= ArrayAdapter(requireContext(),
            R.layout.spinner_item,
            R.id.spinner_text,vardiyaList)
        binding.spinnerVardiya.adapter=vardiyaAdapter
        binding.spinnerVardiya.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                vardiya=vardiyaList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                vardiya=vardiyaList[0]
            }

        }


        binding.buttonAnketor.setOnClickListener {
            val userName=binding.editNameLogin.text.toString().trim().lowercase()
            val userSurname=binding.editSurnameLogin.text.toString().trim().lowercase()
            val password=binding.editTextTextPassword.text.toString().trim()
            if (userName.isEmpty()){
                binding.editNameLogin.setError("Adınızı giriniz!")
                binding.editNameLogin.requestFocus()
            }else if (userSurname.isEmpty()){
                binding.editSurnameLogin.setError("Soyadınızı giriniz!")
                binding.editSurnameLogin.requestFocus()
            }else if (password.isEmpty()){
                binding.editTextTextPassword.setError("Şifrenizi giriniz!")
                binding.editTextTextPassword.requestFocus()
            }else if (password.length<6){
                binding.editTextTextPassword.setError("Şifreniz 6 karakterden az olamaz!")
                binding.editTextTextPassword.requestFocus()
            }else if (vardiya=="Vardiya Seçiniz"){
                Toast.makeText(requireContext(),"Vardiya seçiniz",Toast.LENGTH_LONG).show()
            }else{
            val email="$userName$userSurname@uludagtriko.com"
            if (userEmailList.contains(email)){
                Singleton.vardiyaFromStart=vardiya
                viewModel.userLogin(requireActivity(),requireContext(),email,password)
            }else{
                Toast.makeText(requireContext(),"Girilen Email'e ait bir kullanıcı hesabı yok!",
                    Toast.LENGTH_SHORT).show()
            }
        }
        }
    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarLogin.visibility=View.VISIBLE
                binding.buttonAnketor.isEnabled=false
                binding.editNameLogin.isEnabled=false
                binding.editSurnameLogin.isEnabled=false
                binding.editTextTextPassword.isEnabled=false
            }else{
                binding.progressBarLogin.visibility=View.GONE
                binding.buttonAnketor.isEnabled=true
                binding.editNameLogin.isEnabled=true
                binding.editSurnameLogin.isEnabled=true
                binding.editTextTextPassword.isEnabled=true
            }
        })

        viewModel.errorLoginLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.buttonAnketor.isEnabled=false
                binding.editNameLogin.isEnabled=false
                binding.editSurnameLogin.isEnabled=false
                binding.editTextTextPassword.isEnabled=false
                Snackbar.make(requireView(),"Veriler alınamadı,tekrar deneyin!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Evet",View.OnClickListener {
                        viewModel.getUserData()
                    }).show()
            }else{
                binding.buttonAnketor.isEnabled=true
                binding.editNameLogin.isEnabled=true
                binding.editSurnameLogin.isEnabled=true
                binding.editTextTextPassword.isEnabled=true
            }
        })

        viewModel.userListLive.observe(viewLifecycleOwner, Observer {
            userEmailList.clear()
            it.forEach {
                userEmailList.add(it.email)
            }
        })
    }
}