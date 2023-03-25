package com.caneryildirim.uludagtrikovardiyaraporu.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class IstatistikRapor(val makina:String,val vardiya:String,val personel:String,
                           val tarihAralik:String, val timestamp: Timestamp,
                           val email:String, val toplamCalismaSuresi:String, val toplamSure:String,
                           val uretimYuzdesiOrtalama:String, val uretimAdediToplam:String, val fireAdediToplam:String,
                           val kirilanIgneToplam:String, val kirilanPlatinToplam:String, val docRef:String):
    Serializable
