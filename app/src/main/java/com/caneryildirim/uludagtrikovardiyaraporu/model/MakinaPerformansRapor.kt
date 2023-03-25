package com.caneryildirim.uludagtrikovardiyaraporu.model

import com.google.firebase.Timestamp

data class MakinaPerformansRapor(val tarih: Timestamp, val makina:String, val calismaSure:String,
                                 val uretimSure:String,
                                 val uretimYuzde:String, val hareketCubuk:String,
                                 val iplikBes:String, val parcaSayaci:String, val dirDrs:String,
                                 val igneSen:String, val merdanECekim:String, val programlama:String,
                                 val makineStop:String, val sokStopAparati:String, val jakarHatasi:String,
                                 val toplamHareket:String, val yavasHareket:String, val uretimAdedi:String,
                                 val docRef:String

)
