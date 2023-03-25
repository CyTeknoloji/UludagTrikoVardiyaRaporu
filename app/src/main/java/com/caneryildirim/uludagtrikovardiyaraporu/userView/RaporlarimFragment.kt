package com.caneryildirim.uludagtrikovardiyaraporu.userView

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerRaporAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerRaporKullaniciAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerUserAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentHesabimKullaniciBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentRaporlarimBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.model.User
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.HesabimKullaniciViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.RaporlarimViewModel
import com.google.android.material.snackbar.Snackbar


class RaporlarimFragment : Fragment(),RecyclerRaporKullaniciAdapter.OptionRecycler {
    private var _binding: FragmentRaporlarimBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: RaporlarimViewModel
    private var adapterRapor: RecyclerRaporKullaniciAdapter?=null
    private var raporList:ArrayList<Rapor>?= arrayListOf()
    private var raporListForUpload=ArrayList<Rapor>()
    private lateinit var permiisonLauncherStorage: ActivityResultLauncher<String>
    private lateinit var permissionLauncherWriteStorage:ActivityResultLauncher<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentRaporlarimBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(RaporlarimViewModel::class.java)
        viewModel.getData(requireView(),requireContext())
        observeLiveData()
        //createMenu()
        registerLauncher()

        val makinaList=viewModel.getMakinaListForAdapter()
        binding.recyclerViewRaporlarim.layoutManager= LinearLayoutManager(requireContext())
        adapterRapor= RecyclerRaporKullaniciAdapter(raporList!!,makinaList,this)
        binding.recyclerViewRaporlarim.adapter=adapterRapor

        binding.swipeRaporlarim.setColorSchemeColors(resources.getColor(R.color.purple_500))
        binding.swipeRaporlarim.setOnRefreshListener {
            binding.progressBarRaporlarim.visibility=View.GONE
            binding.recyclerViewRaporlarim.visibility=View.GONE
            binding.swipeRaporlarim.isRefreshing=false
            viewModel.getData(requireView(),requireContext())
        }

    }

    private fun observeLiveData() {
        viewModel.uploadLiveData.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarRaporlarim.visibility=View.VISIBLE
                binding.recyclerViewRaporlarim.visibility=View.GONE
            }else{
                binding.progressBarRaporlarim.visibility=View.GONE
                binding.recyclerViewRaporlarim.visibility=View.VISIBLE
            }
        })

        viewModel.raporListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                raporListForUpload.clear()
                raporListForUpload.addAll(it)
                binding.recyclerViewRaporlarim.visibility=View.VISIBLE
                adapterRapor?.updateRaporList(it)
            }else{
                raporListForUpload.clear()
                adapterRapor!!.updateRaporList(raporListForUpload)
                binding.recyclerViewRaporlarim.visibility=View.GONE
            }
        })

    }

    override fun onItemDelete(docRef: String) {
        viewModel.deleteRapor(docRef,requireContext(),requireView())
    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_tamamlanmis_anket_filter, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.filterAnket->{
                        viewModel.filter(layoutInflater,requireContext(),requireActivity() as UserActivity)
                        return true
                    }
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
                Toast.makeText(requireContext(),"İzin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }

        permissionLauncherWriteStorage=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                viewModel.downloadExcel(raporListForUpload,requireActivity(),resources,requireContext())
            }else{
                Toast.makeText(requireContext(),"İzin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

}