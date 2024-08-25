package com.example.depoair2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.depoair2.databinding.ActivityAdminBinding
import com.example.depoair2.models.Orders
import com.example.depoair2.ui.adapter.RiwayatAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale


class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var database: DatabaseReference
    private lateinit var riwayatAdapter: RiwayatAdapter
    private val riwayatList = mutableListOf<Orders>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Orders")
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadPesanan("Diproses")
        binding.lihatRekap.setOnClickListener{
            val intent = Intent(this,RekapActivity::class.java)
            startActivity(intent)
        }
        val chip = binding.chipGroup
        chip.isSingleSelection = true
        chip.isSelectionRequired = true
        chip.isClickable = true
        chip.setOnCheckedChangeListener { compoundButton, checkedId ->
            if (checkedId != -1) {
                val selectedChip: Chip? = compoundButton.findViewById(checkedId)
                selectedChip?.let {
                    loadPesanan(it.text.toString())
                }
            }
        }

        riwayatAdapter = RiwayatAdapter(riwayatList, true) { order ->
            if (order.status == "Diproses") {
                showConfirmationDialog(order)
            }
        }
        binding.rvAdmin.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            adapter = riwayatAdapter
        }
        pesananHariIni("Diproses"){
            binding.tvTotalPesanan.text = it.toString()
        }
        pesananTotalGalon("Selesai"){
            val pricePerItem = 6000
            val total = it * pricePerItem
            val formattedTotal = NumberFormat.getNumberInstance(Locale("in", "ID")).format(total)
            binding.tvTotalIsiUlang.text = "Rp $formattedTotal"
        }
    }

    private fun loadPesanan(status: String) {
        database.orderByChild("status").equalTo(status)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    riwayatList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Orders::class.java)
                        if (order != null && order.status == status) {
                            val riwayatItem = Orders(
                                orderId = orderSnapshot.key,
                                jumlah = order.jumlah,
                                tanggal = order.tanggal,
                                status = order.status,
                                alamat = order.alamat,
                                nama = order.nama
                            )
                            riwayatList.add(riwayatItem)
                        }
                    }
                    riwayatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("admin", "onCancelled: ${error.message}")
                }
            })
    }

    private fun showConfirmationDialog(order: Orders) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Pesanan")
            .setMessage("Kirim Pesanan?")
            .setPositiveButton("Kirim") { _, _ ->
                updateOrderStatus(order, "Dikirim")
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun pesananHariIni(status: String, callback: (Int) -> Unit) {
        database.orderByChild("status").equalTo(status)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pesananHariIni = mutableListOf<Orders>()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Orders::class.java)
                        if (order != null && order.status == status) {
                            val pesananItem = Orders(
                                orderId = orderSnapshot.key,
                                jumlah = order.jumlah,
                                tanggal = order.tanggal,
                                status = order.status,
                                nama = order.nama
                            )
                            pesananHariIni.add(pesananItem)
                        }
                    }
                    Log.d("admin", "pesanan hari ini: ${pesananHariIni.size}")
                    callback(pesananHariIni.size)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("admin", "onCancelled: ${error.message}")
                    // Jika terjadi error, kembalikan 0 atau nilai default
                    callback(0)
                }
            })
    }

    private fun pesananTotalGalon(status: String, callback: (Int) -> Unit) {
        database.orderByChild("status").equalTo(status)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalJumlahGalon = 0
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Orders::class.java)
                        Log.d("admin", "Fetched order: $order")
                        if (order?.jumlah != null && order.status == status) {
                            totalJumlahGalon += order.jumlah

                        }
                    }
                    // Mengembalikan hasil perhitungan melalui callback
                    callback(totalJumlahGalon)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("admin", "onCancelled: ${error.message}")
                    // Jika ada error, kembalikan 0 atau nilai default
                    callback(0)
                }
            })
    }


    private fun updateOrderStatus(order: Orders, newStatus: String) {
        val orderId = order.orderId
        if (orderId != null) {
            database.child(orderId).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    loadPesanan("Diproses")
                    Toast.makeText(this, "Pesanan dikirim", Toast.LENGTH_SHORT).show()
                    order.alamat?.let { alamat -> displayJalur("STMIK BANDUNG", alamat) }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Pesanan tidak memiliki ID yang valid", Toast.LENGTH_SHORT).show()
        }
    }
    private fun displayJalur(asal: String, tujuan: String) {
        try {
            val uri = Uri.parse("https://www.google.co.in/maps/dir/$asal/$tujuan")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val uri =
                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
