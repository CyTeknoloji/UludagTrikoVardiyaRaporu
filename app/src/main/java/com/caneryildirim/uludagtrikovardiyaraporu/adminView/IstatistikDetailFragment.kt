package com.caneryildirim.uludagtrikovardiyaraporu.adminView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.caneryildirim.uludagtrikovardiyaraporu.R
import com.caneryildirim.uludagtrikovardiyaraporu.adminViewModel.IstatistikDetailViewModel
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentIstatistikBinding
import com.caneryildirim.uludagtrikovardiyaraporu.databinding.FragmentIstatistikDetailBinding


class IstatistikDetailFragment : Fragment() {
    private var _binding: FragmentIstatistikDetailBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: IstatistikDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentIstatistikDetailBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(IstatistikDetailViewModel::class.java)

        arguments?.let {
            val istatistikRapor=IstatistikDetailFragmentArgs.fromBundle(it).istatistikRapor
            val type=IstatistikDetailFragmentArgs.fromBundle(it).istatistikType
            binding.editTextTarih.text=istatistikRapor.tarihAralik
            when(type){
                "makina"->{
                    binding.textView2.text="Makina"
                    binding.editTextMakina.text=istatistikRapor.makina
                }
                "vardiya"->{
                    binding.textView2.text="Vardiya"
                    binding.editTextMakina.text=istatistikRapor.vardiya
                }
                "personel"->{
                    binding.textView2.text="Personel"
                    binding.editTextMakina.text=istatistikRapor.personel
                }
            }
            binding.editTextCalismaSuresiIstatistik.text=istatistikRapor.toplamCalismaSuresi
            binding.editTextFireAdedi.text=istatistikRapor.fireAdediToplam
            binding.editTextSureIstatistik.text=istatistikRapor.toplamSure
            binding.editTextUretimYuzdesi.text=istatistikRapor.uretimYuzdesiOrtalama
            binding.editTextUretimAdedi.text=istatistikRapor.uretimAdediToplam
            binding.editTextKirilanPlatin.text=istatistikRapor.kirilanPlatinToplam
            binding.editTextKirilanIgne.text=istatistikRapor.kirilanIgneToplam

            binding.progressBarRaporDetail.visibility=View.GONE
        }
    }

}