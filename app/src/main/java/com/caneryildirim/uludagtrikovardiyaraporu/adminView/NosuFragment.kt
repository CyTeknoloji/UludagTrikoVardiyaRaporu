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
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.NosuViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.VaryantViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentNosuBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentVaryantBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Varyant


class NosuFragment : Fragment(),RecyclerVaryantAdapter.OptionVaryant {
    private var _binding: FragmentNosuBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: NosuViewModel
    private var adapterVaryant: RecyclerVaryantAdapter?=null
    private var varyantList:ArrayList<Varyant>?= arrayListOf()
    private var varyantListForUpload=ArrayList<Varyant>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentNosuBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(NosuViewModel::class.java)
        viewModel.getNosuList(requireView())
        observeLiveData()

        binding.recyclerNosu.layoutManager= LinearLayoutManager(requireContext())
        adapterVaryant= RecyclerVaryantAdapter(varyantList!!,this)
        binding.recyclerNosu.adapter=adapterVaryant

        binding.fabNosu.setOnClickListener {
            viewModel.addNosu(layoutInflater,requireContext(),requireView())
        }
    }

    private fun observeLiveData() {
        viewModel.uploadDataLive.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.fabNosu.isEnabled=false
                binding.progressBarNosu.visibility=View.VISIBLE
            }else{
                binding.fabNosu.isEnabled=true
                binding.progressBarNosu.visibility=View.GONE
            }
        })

        viewModel.nosuListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                val varyantList=ArrayList<Varyant>()
                it.forEach {
                    val id=it.id
                    val name=it.beden
                    val docRef=it.docRef
                    varyantList.add(Varyant(id, name, docRef))
                }
                varyantListForUpload.clear()
                varyantListForUpload.addAll(varyantList)
                binding.recyclerNosu.visibility=View.VISIBLE
                adapterVaryant?.updateData(varyantList)
            }else{
                binding.recyclerNosu.visibility=View.GONE
            }
        })
    }

    override fun deleteVaryant(docRef: String) {
        viewModel.deleteNosu(docRef,requireContext(),requireView())
    }
}