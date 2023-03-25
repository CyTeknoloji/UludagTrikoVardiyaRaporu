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
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerVaryantAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.ModelNoViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.VaryantViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentModelNoBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentVaryantBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.User
import com.caneryildirim.uludagtrikovardiyaraporu.model.Varyant


class ModelNoFragment : Fragment(),RecyclerVaryantAdapter.OptionVaryant {
    private var _binding: FragmentModelNoBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: ModelNoViewModel
    private var adapterVaryant: RecyclerVaryantAdapter?=null
    private var varyantList:ArrayList<Varyant>?= arrayListOf()
    private var varyantListForUpload=ArrayList<Varyant>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentModelNoBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(ModelNoViewModel::class.java)
        viewModel.getModelNoList(requireView())
        observeLiveData()

        binding.recyclerModelNo.layoutManager= LinearLayoutManager(requireContext())
        adapterVaryant= RecyclerVaryantAdapter(varyantList!!,this)
        binding.recyclerModelNo.adapter=adapterVaryant

        binding.fabModelNo.setOnClickListener {
            viewModel.addModelNo(layoutInflater,requireContext(),requireView())
        }

    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.fabModelNo.isEnabled=false
                binding.progressBarModelNo.visibility=View.VISIBLE
            }else{
                binding.fabModelNo.isEnabled=true
                binding.progressBarModelNo.visibility=View.GONE
            }
        })

        viewModel.modelNoListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                val varyantList=ArrayList<Varyant>()
                it.forEach {
                    val id=it.id
                    val name="${it.modelNo} (${it.modelAdi})"
                    val docRef=it.docRef
                    varyantList.add(Varyant(id, name, docRef))
                }
                varyantListForUpload.clear()
                varyantListForUpload.addAll(varyantList)
                binding.recyclerModelNo.visibility=View.VISIBLE
                adapterVaryant?.updateData(varyantList)
            }else{
                binding.recyclerModelNo.visibility=View.GONE
            }
        })
    }

    override fun deleteVaryant(docRef: String) {
        viewModel.deleteModelNo(docRef,requireContext(),requireView())
    }


}