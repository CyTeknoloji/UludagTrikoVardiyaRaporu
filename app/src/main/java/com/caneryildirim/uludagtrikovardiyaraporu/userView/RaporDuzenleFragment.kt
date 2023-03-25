package com.caneryildirim.uludagtrikovardiyaraporu.userView

import android.os.Bundle
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
import com.caneryildirim.uludagtrikovardiyaraporu.adminView.AdminActivity
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentRaporDetailBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentRaporDuzenleBinding
import com.caneryildirim.uludagtrikovardiyaraporu.model.Rapor
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.RaporDetailViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.userViewModel.RaporDuzenleViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RaporDuzenleFragment : Fragment() {
    private var _binding: FragmentRaporDuzenleBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: RaporDuzenleViewModel
    private lateinit var userActivity: UserActivity
    lateinit var rapor: Rapor
    private var modelNo=""
    private lateinit var nosu:String
    private lateinit var parca:String
    private lateinit var makina:String
    private lateinit var vardiya:String
    private var parcaList=ArrayList<String>()
    private var bedenList=ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentRaporDuzenleBinding.inflate(inflater,container,false)
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
        viewModel= ViewModelProvider(this).get(RaporDuzenleViewModel::class.java)
        viewModel.getParcaList(requireContext())
        viewModel.getBedenList(requireContext())
        viewModel.getModelList(requireContext())

        arguments?.let {
            rapor=RaporDuzenleFragmentArgs.fromBundle(it).rapor
            viewModel.getRapor(rapor)
            modelNo=rapor.modelNo
            uiSettings()
        }

        observeLiveData()

    }

    private fun uiSettings() {
        userActivity= requireActivity() as UserActivity
        userActivity.hideBottomNav(true)

        binding.editTextPersonel.text=viewModel.getPersonelName()
        binding.editTextTarih.text=dateToString(rapor.tarih!!)
        binding.editTextCalismaSuresiSaat.setText("${rapor.calismaSuresi.get(0)}${rapor.calismaSuresi.get(1)}")
        binding.editTextCalismaSuresiDakika.setText("${rapor.calismaSuresi.get(3)}${rapor.calismaSuresi.get(4)}")
        binding.editTextSureSaat.setText("${rapor.sure.get(0)}${rapor.sure.get(1)}")
        binding.editTextSureDakika.setText("${rapor.sure.get(3)}${rapor.sure.get(4)}")
        makina=rapor.makina
        binding.spinnerMakina.text=rapor.makina
        binding.editTextUretimYuzdesi.setText(rapor.uretimYuzdesi)
        binding.editTextUretimAdedi.setText(rapor.uretimAdedi)
        modelNo=rapor.modelNo
        binding.spinnerModelNo.text=rapor.modelNo
        binding.editTextEn.setText(rapor.en)
        binding.editTextBoy.setText(rapor.boy)
        binding.editTextFireAdedi.setText(rapor.fireAdedi)
        binding.editTextKirilanIgne.setText(rapor.kirilanIgne)
        binding.editTextKirilanPlatin.setText(rapor.kirilanPlatin)
        binding.editTextNot.setText(rapor.not)


        val vardiyaList=viewModel.getVardiyaList()
        val vardiyaAdapter= ArrayAdapter(requireContext(),R.layout.spinner_item,R.id.spinner_text,vardiyaList)
        binding.spinnerVardiya.adapter=vardiyaAdapter
        binding.spinnerVardiya.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                vardiya=vardiyaList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                vardiya=vardiyaList[0]
            }

        }


        binding.buttonRaporEkle.setOnClickListener {
            val tarih=rapor.tarih
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
            val email= rapor.email
            val docRef=rapor.docRef
            val rapor=Rapor(tarih,personel,vardiya,makina,calismaSuresi,uretimYuzdesi,uretimAdedi,modelNo,nosu,parca,
                en,boy,sure,fireAdedi,kirilanIgne,kirilanPlatin,not,email,docRef)
            if (calismaSuresiSaat.isEmpty() || calismaSuresiDakika.isEmpty()){
                Toast.makeText(requireContext(),"Çalışma süresini giriniz", Toast.LENGTH_SHORT).show()
            }else if (sureSaat.isEmpty() || sureDakika.isEmpty()){
                Toast.makeText(requireContext(),"Süreyi giriniz", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.raporKaydet(rapor,requireContext(),requireActivity(),requireView())
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
            parcaList.add(rapor.parca)
            it.forEach {
                parcaList.add(it.parca)
            }
            val parcaAdapter= ArrayAdapter(requireContext(),R.layout.spinner_item,R.id.spinner_text,parcaList)
            binding.spinnerParca.adapter=parcaAdapter
            binding.spinnerParca.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
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
            bedenList.add(rapor.nosu)
            it.forEach {
                bedenList.add(it.beden)
            }
            val bedenAdapter= ArrayAdapter(requireContext(),R.layout.spinner_item,R.id.spinner_text,bedenList)
            binding.spinnerNosu.adapter=bedenAdapter
            binding.spinnerNosu.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
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
                binding.spinnerModelNo.text=rapor.modelNo
                binding.spinnerModelNo.setOnClickListener {
                    viewModel.selectModelNo(layoutInflater,requireContext())
                }
            }
        })

        viewModel.textStringLive.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                binding.spinnerModelNo.text=it
                modelNo=it
            }
        })
    }

    private fun dateToString(timestamp:Timestamp):String{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = timestamp.toDate()
        val dateTime: String = format.format(date)
        return dateTime
    }

    private fun stringToDate(dtStart:String):Timestamp{
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = format.parse(dtStart)
        return Timestamp(date)
    }


}