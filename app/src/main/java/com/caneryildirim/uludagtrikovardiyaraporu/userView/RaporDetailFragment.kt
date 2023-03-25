package com.caneryildirim.uludagtrikovardiyaraporu.userView

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.RaporDetailViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentRaporDetailBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat


class RaporDetailFragment : Fragment() {
    private var _binding: FragmentRaporDetailBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: RaporDetailViewModel
    private lateinit var userActivity: UserActivity
    private lateinit var adminActivity: AdminActivity
    private var adminInfo:Boolean?=null
    lateinit var rapor:Rapor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentRaporDetailBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        if (adminInfo!!){
            adminActivity.hideToolbar(false)
        }else{
            userActivity.hideBottomNav(false)
        }

    }

    override fun onResume() {
        super.onResume()
        if (adminInfo!!){
            adminActivity.hideToolbar(true)
        }else{
            userActivity.hideBottomNav(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(RaporDetailViewModel::class.java)
        observeLiveData()

        arguments?.let {
            rapor=RaporDetailFragmentArgs.fromBundle(it).rapor
            adminInfo=RaporDetailFragmentArgs.fromBundle(it).adminInfo

            if (adminInfo!!){
                adminActivity= requireActivity() as AdminActivity
                adminActivity.hideToolbar(true)
            }else{
                createMenu()
                userActivity= requireActivity() as UserActivity
                userActivity.hideBottomNav(true)
            }

            binding.editTextTarih.text=dateToString(rapor.tarih!!)
            binding.editTextPersonel.text=rapor.personel
            binding.editTextVardiya.text=rapor.vardiya
            binding.spinnerMakina.text=rapor.makina
            binding.editTextCalismaSuresiRaporDetay.text=rapor.calismaSuresi
            binding.editTextUretimYuzdesi.text=rapor.uretimYuzdesi
            binding.editTextUretimAdedi.text=rapor.uretimAdedi
            binding.editTextModelNo.text=rapor.modelNo
            binding.editTextNosu.text=rapor.nosu
            binding.editTextParca.text=rapor.parca
            binding.editTextEn.text=rapor.en
            binding.editTextBoy.text=rapor.boy
            binding.editTextSureRaporDetay.text=rapor.sure
            binding.editTextFireAdedi.text=rapor.fireAdedi
            binding.editTextKirilanIgne.text=rapor.kirilanIgne
            binding.editTextKirilanPlatin.text=rapor.kirilanPlatin
            binding.editTextNot.text=rapor.not
            
        }

    }

    private fun observeLiveData() {
        viewModel.uploadLiveData.observe(viewLifecycleOwner, Observer { 
            if (it){
                binding.progressBarRaporDetail.visibility=View.VISIBLE
            }else{
                binding.progressBarRaporDetail.visibility=View.GONE
            }
        })
    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }


    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_rapor_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.editRapor->{
                        val action=RaporDetailFragmentDirections.actionRaporDetailFragmentToRaporDuzenleFragment(rapor)
                        Navigation.findNavController(requireView()).navigate(action)
                        return true
                    }
                    R.id.deleteRapor->{
                        val alertDialog= AlertDialog.Builder(requireContext())
                        alertDialog.setMessage("Raporu Sil")
                        alertDialog.setPositiveButton("Evet"){ dialogInterface: DialogInterface, i: Int ->
                            viewModel.deleteRapor(rapor.docRef,requireContext(),requireActivity())
                        }
                        alertDialog.setNegativeButton("Hayır"){ dialogInterface: DialogInterface, i: Int ->
                            Toast.makeText(requireContext(),"Vazgeçildi", Toast.LENGTH_SHORT).show()
                        }
                        alertDialog.show()
                        return true
                    }
                    else->return false
                }

            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }



}