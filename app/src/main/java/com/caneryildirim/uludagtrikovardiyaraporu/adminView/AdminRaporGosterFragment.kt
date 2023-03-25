package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerAdminRaporAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerRaporAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.AdminRaporEkleViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.AdminRaporGosterViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentAdminRaporGosterBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.MakinaPerformansRapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat


class AdminRaporGosterFragment : Fragment(),RecyclerAdminRaporAdapter.OptionRecycler {
    private var _binding:FragmentAdminRaporGosterBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel:AdminRaporGosterViewModel
    private lateinit var adminActivity: AdminActivity
    private var adapterRapor: RecyclerAdminRaporAdapter?=null
    private var raporList:ArrayList<MakinaPerformansRapor>?= arrayListOf()
    private var raporListForUpload=ArrayList<MakinaPerformansRapor>()
    private lateinit var permiisonLauncherStorage: ActivityResultLauncher<String>
    private lateinit var permissionLauncherWriteStorage:ActivityResultLauncher<String>
    private lateinit var currentDate:Timestamp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentAdminRaporGosterBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        adminActivity.hideToolbar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adminActivity= requireActivity() as AdminActivity
        adminActivity.hideToolbar(true)
        viewModel= ViewModelProvider(this).get(AdminRaporGosterViewModel::class.java)
        observeLiveData()
        createMenu()
        registerLauncher()

        binding.fabYoneticiListesi.setOnClickListener {
            viewModel.getDateFromPicker(requireContext())
        }

        binding.recyclerAdminRaporGoster.layoutManager= LinearLayoutManager(requireContext())
        adapterRapor= RecyclerAdminRaporAdapter(raporList!!,this)
        binding.recyclerAdminRaporGoster.adapter=adapterRapor

    }

    private fun observeLiveData() {
        viewModel.dateLive.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                currentDate=it
                viewModel.getData(requireView(),requireContext(),currentDate)
            }
        })

        viewModel.uploadLiveData.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarAdminRaporGoster.visibility=View.VISIBLE
                binding.recyclerAdminRaporGoster.visibility=View.GONE
            }else{
                binding.progressBarAdminRaporGoster.visibility=View.GONE
                binding.recyclerAdminRaporGoster.visibility=View.VISIBLE
            }
        })

        viewModel.raporListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                raporListForUpload.clear()
                raporListForUpload.addAll(it)
                binding.recyclerAdminRaporGoster.visibility=View.VISIBLE
                adapterRapor?.updateRaporList(it)
            }else{
                raporListForUpload.clear()
                adapterRapor!!.updateRaporList(raporListForUpload)
                binding.recyclerAdminRaporGoster.visibility=View.GONE
            }
        })
    }

    override fun onItemDelete(docRef: String) {
        viewModel.deleteRapor(docRef,requireContext(),requireView(),currentDate)
    }

    fun registerLauncher(){
        permiisonLauncherStorage=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        Snackbar.make(binding.root,"Excel indirmek için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                            permissionLauncherWriteStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }).show()
                    }else{
                        permissionLauncherWriteStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }else{
                    viewModel.downloadExcel(raporListForUpload,requireActivity(),resources,requireContext())
                }
            }else{
                Toast.makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_SHORT).show()
            }
        }

        permissionLauncherWriteStorage=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                viewModel.downloadExcel(raporListForUpload,requireActivity(),resources,requireContext())
            }else{
                Toast.makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_istatistik, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.downloadExcel->{
                        if (ContextCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                                Snackbar.make(binding.root,"Excel indirmek için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                                    permiisonLauncherStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }).show()
                            }else{
                                permiisonLauncherStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }else{
                            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                    Snackbar.make(binding.root,"Excel indirmek için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                                        permissionLauncherWriteStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    }).show()
                                }else{
                                    permissionLauncherWriteStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            }else{
                                viewModel.downloadExcel(raporListForUpload,requireActivity(),resources,requireContext())
                            }
                        }
                        return  true
                    }
                    else->return false
                }

            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }




}