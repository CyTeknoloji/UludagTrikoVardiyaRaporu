package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerIstatistikAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adapter.RecyclerIstatistikVardiyaAdapter
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.IstatistikVardiyaViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.IstatistikViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentIstatistikBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentIstatistikVardiyaBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.IstatistikRapor
import com.google.android.material.snackbar.Snackbar


class IstatistikVardiyaFragment : Fragment(),RecyclerIstatistikVardiyaAdapter.OptionRecycler {
    private var _binding: FragmentIstatistikVardiyaBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: IstatistikVardiyaViewModel
    private var adapterIstatistikRapor: RecyclerIstatistikVardiyaAdapter?=null
    private var istatistikRaporList:ArrayList<IstatistikRapor>?= arrayListOf()
    private var istatistikRaporListForUpload=ArrayList<IstatistikRapor>()
    private lateinit var permiisonLauncherStorage: ActivityResultLauncher<String>
    private lateinit var permissionLauncherWriteStorage:ActivityResultLauncher<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentIstatistikVardiyaBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(IstatistikVardiyaViewModel::class.java)
        viewModel.getIstatistikData(requireView(),requireContext())
        observeLiveData()
        createMenu()
        registerLauncher()

        binding.recyclerViewIstatistikVardiya.layoutManager= LinearLayoutManager(requireContext())
        adapterIstatistikRapor= RecyclerIstatistikVardiyaAdapter(istatistikRaporList!!,this)
        binding.recyclerViewIstatistikVardiya.adapter=adapterIstatistikRapor

        binding.fabIstatistikVardiya.setOnClickListener {
            viewModel.filterVardiya(requireView(),layoutInflater,requireContext(),requireActivity() as AdminActivity)
        }

        binding.swipeIstatistikVardiya.setColorSchemeColors(resources.getColor(R.color.purple_500))
        binding.swipeIstatistikVardiya.setOnRefreshListener {
            binding.progressBarIstatistikVardiya.visibility=View.GONE
            binding.recyclerViewIstatistikVardiya.visibility=View.GONE
            binding.swipeIstatistikVardiya.isRefreshing=false
            viewModel.getIstatistikData(requireView(),requireContext())
        }
    }

    private fun observeLiveData() {
        viewModel.uploadLiveData.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarIstatistikVardiya.visibility=View.VISIBLE
                binding.fabIstatistikVardiya.isEnabled=false
            }else{
                binding.progressBarIstatistikVardiya.visibility=View.GONE
                binding.fabIstatistikVardiya.isEnabled=true
            }
        })

        viewModel.istatistikRaporListLive.observe(viewLifecycleOwner, Observer {
            if (it.size>0){
                istatistikRaporListForUpload.clear()
                istatistikRaporListForUpload.addAll(it)
                binding.recyclerViewIstatistikVardiya.visibility=View.VISIBLE
                adapterIstatistikRapor?.updateIstatistikData(it)
            }else{
                istatistikRaporListForUpload.clear()
                adapterIstatistikRapor!!.updateIstatistikData(istatistikRaporListForUpload)
                binding.recyclerViewIstatistikVardiya.visibility=View.GONE
            }
        })
    }

    override fun deleteItem(docRef: String) {
        viewModel.deleteIstatistik(docRef,requireContext(),requireView())
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
                                viewModel.downloadExcel(istatistikRaporListForUpload,requireActivity(),resources,requireContext())
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
                    viewModel.downloadExcel(istatistikRaporListForUpload,requireActivity(),resources,requireContext())
                }
            }else{
                Toast.makeText(requireContext(),"İzin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }

        permissionLauncherWriteStorage=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                viewModel.downloadExcel(istatistikRaporListForUpload,requireActivity(),resources,requireContext())
            }else{
                Toast.makeText(requireContext(),"İzin verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }


}