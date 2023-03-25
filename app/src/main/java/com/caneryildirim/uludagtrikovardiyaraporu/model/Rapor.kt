package com.caneryildirim.uludagtrikovardiyaraporu.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Rapor(val tarih:Timestamp?, val personel:String, val vardiya:String,
                 val makina:String, val calismaSuresi:String,
                 val uretimYuzdesi:String, val uretimAdedi:String, val modelNo:String,
                 val nosu:String, val parca:String, val en:String, val boy:String,
                 val sure:String, val fireAdedi:String, val kirilanIgne:String,
                 val kirilanPlatin:String,val not:String, val email:String,val docRef:String): Serializable
