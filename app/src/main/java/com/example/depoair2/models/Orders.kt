package com.example.depoair2.models

import android.provider.ContactsContract.CommonDataKinds.Phone

data class Orders(
    val orderId: String? = null,
    val nama: String? = null,
    val jumlah: Int? = null,
    val status: String? = null,
    val alamat: String? = null,
    val tanggal: String? = null,
    val phone:String? = null
){
    constructor() : this(null, null, null, null, null)
}