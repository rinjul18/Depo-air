package com.example.depoair2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.depoair2.databinding.ActivityRiwayatBinding
import com.example.depoair2.models.Orders
import com.example.depoair2.ui.adapter.RiwayatAdapter
import com.example.depoair2.ui.reference.UserReference
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RiwayatPemesananActivity : AppCompatActivity() {
    private lateinit var user:UserReference
    private lateinit var binding: ActivityRiwayatBinding
    private lateinit var database: DatabaseReference
    private lateinit var riwayatAdapter: RiwayatAdapter
    private val riwayatList = mutableListOf<Orders>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = UserReference(this)

        val phone = user.ambilUser()?.phone
        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Orders")

        // Inisialisasi RecyclerView dan Adapter
        riwayatAdapter = RiwayatAdapter(riwayatList, false) { order ->
            if (order.status == "Dikirim") {
                showConfirmationDialog(order)
            }
        }
        binding.rvRiwayat.apply {
            layoutManager = LinearLayoutManager(this@RiwayatPemesananActivity)
            adapter = riwayatAdapter
        }
        if (phone != null) {
            loadRiwayatByNameAndStatus(phone,"Diproses")
        }
        val chip = binding.chipGroup
        chip.isSingleSelection = true
        chip.isSelectionRequired = true
        chip.isClickable = true
        chip.setOnCheckedChangeListener { compoundButton, checkedId ->
            if (checkedId != -1) {
                val selectedChip: Chip? = compoundButton.findViewById(checkedId)
                selectedChip?.let {
                    if (phone != null) {
                        loadRiwayatByNameAndStatus(phone, it.text.toString())
                    }
                }
            }
        }

    }

    private fun updateOrderStatus(order: Orders, newStatus: String) {
        val orderId = order.orderId // Menggunakan orderId untuk memperbarui status
        if (orderId != null) {
            database.child(orderId).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    user.ambilUser()?.phone?.let { it1 -> loadRiwayatByNameAndStatus(it1,"Dikirim") }
                    Toast.makeText(this, "Pesanan diterima", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Pesanan tidak memiliki ID yang valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRiwayatByNameAndStatus(phone: String, status: String) {
        database.orderByChild("phone").equalTo(phone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    riwayatList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Orders::class.java)
                        if (order != null && order.status == status) {
                            val riwayatItem = Orders(
                                orderId = orderSnapshot.key, // Menyimpan orderId dari snapshot
                                jumlah = order.jumlah,
                                tanggal = order.tanggal,
                                status = order.status,
                                nama = order.nama
                            )
                            riwayatList.add(riwayatItem)
                        }
                    }
                    riwayatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RiwayatPemesanan", "Error fetching data: ${error.message}")
                }
            })
    }

    private fun showConfirmationDialog(order: Orders) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Pesanan")
            .setMessage("Pesanan sudah diterima?")
            .setPositiveButton("Diterima") { _, _ ->
                updateOrderStatus(order, "Selesai")
            }
            .setNegativeButton("Batal", null) // Menutup dialog jika "Batal" ditekan
            .show()
    }
}
