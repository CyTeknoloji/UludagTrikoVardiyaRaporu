package com.caneryildirim.uludagtrikovardiyaraporu.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Istatistik(val tarih: Timestamp?,val makina:String, val vardiya:String,val personel:String, val calismaSuresi:String,
                      val uretimYuzdesi:String, val uretimAdedi:String,
                      val sure:String, val fireAdedi:String, val kirilanIgne:String,
                      val kirilanPlatin:String): Serializable
