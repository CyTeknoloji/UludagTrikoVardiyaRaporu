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
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerVaryantAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.ParcaViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.VaryantViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentParcaBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentVaryantBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Varyant


class ParcaFragment : Fragment(),RecyclerVaryantAdapter.OptionVaryant {
    private var _binding: FragmentParcaBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: ParcaViewModel
    private var adapterVaryant: RecyclerVaryantAdapter?=null
    private var varyantList:ArrayList<Varyant>?= arrayListOf()
    private var varyantListForUpload=ArrayList<Varyant>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentParcaBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(ParcaViewModel::class.java)
        viewModel.getParcaList(requireView())
        observeLiveData()

        binding.recyclerParca.layoutManager= LinearLayoutManager(requireContext())
        adapterVaryant= RecyclerVaryantAdapter(varyantList!!,this)
        binding.recyclerParca.adapter=adapterVaryant

        binding.fabParca.setOnClickListener {
            viewModel.addNosu(layoutInflater,requireContext(),requireView())
        }

    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.fabParca.isEnabled=false
                binding.progressBarParca.visibility=View.VISIBLE
            }else{
                binding.fabParca.isEnabled=true
                binding.progressBarParca.visibility=View.GONE
            }
        })

        viewModel.parcaListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                val varyantList=ArrayList<Varyant>()
                it.forEach {
                    val id=it.id
                    val name=it.parca
                    val docRef=it.docRef
                    varyantList.add(Varyant(id, name, docRef))
                }
                varyantListForUpload.clear()
                varyantListForUpload.addAll(varyantList)
                binding.recyclerParca.visibility=View.VISIBLE
                adapterVaryant?.updateData(varyantList)
            }else{
                binding.recyclerParca.visibility=View.GONE
            }
        })
    }

    override fun deleteVaryant(docRef: String) {
        viewModel.deleteParca(docRef,requireContext(),requireView())
    }
}