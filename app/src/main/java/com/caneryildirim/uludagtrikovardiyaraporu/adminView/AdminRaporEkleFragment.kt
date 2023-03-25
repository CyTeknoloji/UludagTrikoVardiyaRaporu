package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.AdminRaporEkleViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentAdminRaporEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentRaporEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.MakinaPerformansRapor
import com.caneryildirim.uludagtrikovardiyaraporu.userView.UserActivity
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.RaporEkleViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class AdminRaporEkleFragment : Fragment() {
    private var _binding: FragmentAdminRaporEkleBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminRaporEkleViewModel
    private lateinit var adminActivity: AdminActivity
    private lateinit var makina:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentAdminRaporEkleBinding.inflate(inflater,container,false)
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
        viewModel= ViewModelProvider(this).get(AdminRaporEkleViewModel::class.java)
        observeLiveData()
        uiSettings()

    }

    private fun uiSettings() {
        binding.textTarih.text=dateToString(viewModel.getCurrentDate())

        val makinaList=viewModel.getMakinaList()
        val makinaAdapter= ArrayAdapter(requireContext(),R.layout.spinner_item,R.id.spinner_text,makinaList)
        binding.spinnerMakina.adapter= makinaAdapter
        binding.spinnerMakina.onItemSelectedListener =object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                makina=makinaList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                makina=makinaList[0]
            }

        }

        binding.buttonRaporuEkle.setOnClickListener {
            val tarih=viewModel.getCurrentDate()

            var calismaSureSaat=binding.editCalismaSureSaat.text.toString().trim()
            var calismaSureDakika=binding.editCalismaSureDakika.text.toString().trim()
            if (calismaSureSaat.length==1){
                calismaSureSaat="00$calismaSureSaat"
            }else if (calismaSureSaat.length==2){
                calismaSureSaat="0$calismaSureSaat"
            }
            if (calismaSureDakika.length==1){
                calismaSureDakika="0$calismaSureDakika"
            }
            val calismaSuresi="$calismaSureSaat:$calismaSureDakika"

            var uretimSureSaat=binding.editUretimSureSaat.text.toString().trim()
            var uretimSureDakika=binding.editUretimSureDakika.text.toString().trim()
            if (uretimSureSaat.length==1){
                uretimSureSaat="00$uretimSureSaat"
            }else if (uretimSureSaat.length==2){
                uretimSureSaat="0$uretimSureSaat"
            }
            if (uretimSureDakika.length==1){
                uretimSureDakika="0$uretimSureDakika"
            }
            val uretimSuresi="$uretimSureSaat:$uretimSureDakika"

            val uretimYuzde=binding.editUretimYuzde.text.toString().trim()

            var hareketCubukSaat=binding.editHareketCubuguSaat.text.toString().trim()
            var hareketCubukDakika=binding.editHareketCubuguDakika.text.toString().trim()
            if (hareketCubukSaat.length==1){
                hareketCubukSaat="00$hareketCubukSaat"
            }else if (hareketCubukSaat.length==2){
                hareketCubukSaat="0$hareketCubukSaat"
            }
            if (hareketCubukDakika.length==1){
                hareketCubukDakika="0$hareketCubukDakika"
            }
            val hareketCubugu="$hareketCubukSaat:$hareketCubukDakika"

            var iplikBesSaat=binding.editIplikBesSaat.text.toString().trim()
            var iplikBesDakika=binding.editIplikBesDakika.text.toString().trim()
            if (iplikBesSaat.length==1){
                iplikBesSaat="00$iplikBesSaat"
            }else if (iplikBesSaat.length==2){
                iplikBesSaat="0$iplikBesSaat"
            }
            if (iplikBesDakika.length==1){
                iplikBesDakika="0$iplikBesDakika"
            }
            val iplikBes="$iplikBesSaat:$iplikBesDakika"

            var parcaSayaciSaat=binding.editParcaSayaciSaat.text.toString().trim()
            var parcaSayaciDakika=binding.editParcaSayaciDakika.text.toString().trim()
            if (parcaSayaciSaat.length==1){
                parcaSayaciSaat="00$parcaSayaciSaat"
            }else if (parcaSayaciSaat.length==2){
                parcaSayaciSaat="0$parcaSayaciSaat"
            }
            if (parcaSayaciDakika.length==1){
                parcaSayaciDakika="0$parcaSayaciDakika"
            }
            val parcaSayaci="$parcaSayaciSaat:$parcaSayaciDakika"

            var dirDrsSaat=binding.editDirDrsSaat.text.toString().trim()
            var dirDrsDakika=binding.editDirDrsDakika.text.toString().trim()
            if (dirDrsSaat.length==1){
                dirDrsSaat="00$dirDrsSaat"
            }else if (dirDrsSaat.length==2){
                dirDrsSaat="0$dirDrsSaat"
            }
            if (dirDrsDakika.length==1){
                dirDrsDakika="0$dirDrsDakika"
            }
            val dirDrs="$dirDrsSaat:$dirDrsDakika"

            var igneSenSaat=binding.editIgneSenSaat.text.toString().trim()
            var igneSenDakika=binding.editIgneSenDakika.text.toString().trim()
            if (igneSenSaat.length==1){
                igneSenSaat="00$igneSenSaat"
            }else if (igneSenSaat.length==2){
                igneSenSaat="0$igneSenSaat"
            }
            if (igneSenDakika.length==1){
                igneSenDakika="0$igneSenDakika"
            }
            val igneSen="$igneSenSaat:$igneSenDakika"

            var merdanECekimSaat=binding.editMerdanECekimiSaat.text.toString().trim()
            var merdanECekimDakika=binding.editMerdanECekimiDakika.text.toString().trim()
            if (merdanECekimSaat.length==1){
                merdanECekimSaat="00$merdanECekimSaat"
            }else if (merdanECekimSaat.length==2){
                merdanECekimSaat="0$merdanECekimSaat"
            }
            if (merdanECekimDakika.length==1){
                merdanECekimDakika="0$merdanECekimDakika"
            }
            val merdanECekim="$merdanECekimSaat:$merdanECekimDakika"

            var programlamaSaat=binding.editProgramlamaSaat.text.toString().trim()
            var programlamaDakika=binding.editProgramlamaDakika.text.toString().trim()
            if (programlamaSaat.length==1){
                programlamaSaat="00$programlamaSaat"
            }else if (programlamaSaat.length==2){
                programlamaSaat="0$programlamaSaat"
            }
            if (programlamaDakika.length==1){
                programlamaDakika="0$programlamaDakika"
            }
            val programlama="$programlamaSaat:$programlamaDakika"

            var makineStopSaat=binding.editMakineStopSaat.text.toString().trim()
            var makineStopDakika=binding.editMakineStopDakika.text.toString().trim()
            if (makineStopSaat.length==1){
                makineStopSaat="00$makineStopSaat"
            }else if (makineStopSaat.length==2){
                makineStopSaat="0$makineStopSaat"
            }
            if (makineStopDakika.length==1){
                makineStopDakika="0$makineStopDakika"
            }
            val makineStop="$makineStopSaat:$makineStopDakika"

            var sokStopSaat=binding.editSokStopAparatiSaat.text.toString().trim()
            var sokStopDakika=binding.editSokStopAparatiDakika.text.toString().trim()
            if (sokStopSaat.length==1){
                sokStopSaat="00$sokStopSaat"
            }else if (sokStopSaat.length==2){
                sokStopSaat="0$sokStopSaat"
            }
            if (sokStopDakika.length==1){
                sokStopDakika="0$sokStopDakika"
            }
            val sokStop="$sokStopSaat:$sokStopDakika"

            var jakarHataSaat=binding.editJakarHatasiSaat.text.toString().trim()
            var jakarHataDakika=binding.editJakarHatasiDakika.text.toString().trim()
            if (jakarHataSaat.length==1){
                jakarHataSaat="00$jakarHataSaat"
            }else if (jakarHataSaat.length==2){
                jakarHataSaat="0$jakarHataSaat"
            }
            if (jakarHataDakika.length==1){
                jakarHataDakika="0$jakarHataDakika"
            }
            val jakarHata="$jakarHataSaat:$jakarHataDakika"

            val toplamHareket=binding.editToplamHar.text.toString().trim()
            val yavasHareket=binding.editYavasHarSay.text.toString().trim()
            val uretimAdedi=binding.editUretimAd.text.toString().trim()
            val docRef= UUID.randomUUID().toString()

            val makinaPerformansRapor=MakinaPerformansRapor(tarih,makina,calismaSuresi,uretimSuresi,uretimYuzde,
            hareketCubugu,iplikBes,parcaSayaci,dirDrs,igneSen,merdanECekim,programlama,makineStop,sokStop,jakarHata,
            toplamHareket,yavasHareket,uretimAdedi,docRef)
            viewModel.raporKaydet(makinaPerformansRapor,requireContext(),requireActivity())




        }

    }

    private fun observeLiveData() {
        viewModel.uploadLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                binding.progressBarRaporEkle.visibility=View.VISIBLE
                binding.buttonRaporuEkle.isEnabled=false
            }else{
                binding.progressBarRaporEkle.visibility=View.GONE
                binding.buttonRaporuEkle.isEnabled=true
            }
        })
    }

    private fun dateToString(timestamp: Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

}