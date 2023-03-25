package com.caneryildirim.uludagtrikovardiyaraporu.userView

import android.os.Bundle
import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentRaporEkleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Beden
import com.caneryildirim.uludagtrikovardiyaraporu.model.ModelNo
import com.caneryildirim.uludagtrikovardiyaraporu.model.Parca
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.RaporEkleViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.util.Singleton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RaporEkleFragment : Fragment() {
    private var _binding: FragmentRaporEkleBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: RaporEkleViewModel
    private lateinit var makina:String
    private var modelNo="Modelno seçiniz"
    private lateinit var nosu:String
    private lateinit var parca:String
    private lateinit var userActivity: UserActivity
    private var parcaList=ArrayList<String>()
    private var bedenList=ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentRaporEkleBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        userActivity.hideBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(RaporEkleViewModel::class.java)

        arguments?.let {
            makina=RaporEkleFragmentArgs.fromBundle(it).makina
        }

        viewModel.getParcaList(requireContext())
        viewModel.getBedenList(requireContext())
        viewModel.getModelList(requireContext())
        observeLiveData()
        uiSettings()
    }

    private fun uiSettings() {
        userActivity= requireActivity() as UserActivity
        userActivity.hideBottomNav(true)

        binding.spinnerMakina.text=makina

        binding.editTextTarih.text=dateToString(viewModel.getCurrentDate())

        binding.editTextPersonel.text=viewModel.getPersonelName()

        binding.spinnerVardiya.text=Singleton.vardiyaFromStart

        binding.buttonRaporEkle.setOnClickListener {
            val vardiya=Singleton.vardiyaFromStart
            val tarih=viewModel.getCurrentDate()
            val personel=viewModel.getPersonelName()
            var calismaSuresiSaat=binding.editTextCalismaSuresiSaat.text.toString().trim()
            var calismaSuresiDakika=binding.editTextCalismaSuresiDakika.text.toString().trim()
            if (calismaSuresiSaat.length==1){
                calismaSuresiSaat="0$calismaSuresiSaat"
            }
            if (calismaSuresiDakika.length==1){
                calismaSuresiDakika="0$calismaSuresiDakika"
            }
            val calismaSuresi="$calismaSuresiSaat:$calismaSuresiDakika"
            val uretimYuzdesi=binding.editTextUretimYuzdesi.text.toString().trim()
            val uretimAdedi=binding.editTextUretimAdedi.text.toString().trim()
            val en=binding.editTextEn.text.toString().trim()
            val boy=binding.editTextBoy.text.toString().trim()
            var sureSaat=binding.editTextSureSaat.text.toString().trim()
            var sureDakika=binding.editTextSureDakika.text.toString().trim()
            if (sureSaat.length==1){
                sureSaat="0$sureSaat"
            }
            if (sureDakika.length==1){
                sureDakika="0$sureDakika"
            }
            val sure="$sureSaat:$sureDakika"
            val fireAdedi=binding.editTextFireAdedi.text.toString().trim()
            val kirilanIgne=binding.editTextKirilanIgne.text.toString().trim()
            val kirilanPlatin=binding.editTextKirilanPlatin.text.toString().trim()
            val not=binding.editTextNot.text.toString().trim()
            val email=Firebase.auth.currentUser!!.email.toString()
            val docRef=UUID.randomUUID().toString()
            val rapor=Rapor(tarih,personel,vardiya,makina,calismaSuresi,uretimYuzdesi,uretimAdedi,modelNo,nosu,parca,
            en,boy,sure,fireAdedi,kirilanIgne,kirilanPlatin,not,email,docRef)
            if (calismaSuresiSaat.isEmpty() || calismaSuresiDakika.isEmpty()){
                Toast.makeText(requireContext(),"Çalışma süresini giriniz",Toast.LENGTH_SHORT).show()
            }else if (sureSaat.isEmpty() || sureDakika.isEmpty()){
                Toast.makeText(requireContext(),"Süreyi giriniz",Toast.LENGTH_SHORT).show()
            }else{
                viewModel.raporKaydet(rapor,requireContext(),requireActivity())
            }

        }
    }

    private fun observeLiveData() {
        viewModel.uploadLiveData.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.progressBarRaporEkle.visibility=View.VISIBLE
                binding.buttonRaporEkle.isEnabled=false
            }else{
                binding.progressBarRaporEkle.visibility=View.GONE
                binding.buttonRaporEkle.isEnabled=true
            }
        })

        viewModel.parcaListLive.observe(viewLifecycleOwner, Observer {
            parcaList.clear()
            parcaList.add("Parça seçiniz")
            it.forEach {
                parcaList.add(it.parca)
            }
            val parcaAdapter=ArrayAdapter(requireContext(),R.layout.spinner_item,R.id.spinner_text,parcaList)
            binding.spinnerParca.adapter=parcaAdapter
            binding.spinnerParca.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    parca=parcaList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parca=parcaList[0]
                }

            }
        })

        viewModel.bedenListLive.observe(viewLifecycleOwner, Observer {
            bedenList.clear()
            bedenList.add("No seçiniz")
            it.forEach {
                bedenList.add(it.beden)
            }
            val bedenAdapter=ArrayAdapter(requireContext(),R.layout.spinner_item,R.id.spinner_text,bedenList)
            binding.spinnerNosu.adapter=bedenAdapter
            binding.spinnerNosu.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    nosu=bedenList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    nosu=bedenList[0]
                }

            }
        })

        viewModel.modelListLive.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                binding.spinnerModelNo.text="Modelno seçiniz"
                binding.spinnerModelNo.setOnClickListener {
                    viewModel.selectModelNo(layoutInflater,requireContext())
                }
            }
        })

        viewModel.textStringLive.observe(viewLifecycleOwner, Observer {
            binding.spinnerModelNo.text=it
            modelNo=it
        })

    }

    private fun dateToString(timestamp:Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }


}