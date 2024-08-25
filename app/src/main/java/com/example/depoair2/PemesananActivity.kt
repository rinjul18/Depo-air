package com.example.depoair2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.depoair2.databinding.ActivityDataPelanggan2Binding
import com.example.depoair2.models.Orders
import com.example.depoair2.ui.reference.UserReference
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


class PemesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataPelanggan2Binding
    private lateinit var database: DatabaseReference
    private lateinit var pref: SharedPreferences
    private lateinit var user: UserReference
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataPelanggan2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        user = UserReference(this)
        database = FirebaseDatabase.getInstance().getReference("Orders")
        binding.tvquantity.text = quantity.toString()

        binding.btnKurang.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvquantity.text = quantity.toString()
                updateTotal()
            }
        }

        binding.btnTambah.setOnClickListener {
            if (quantity < 5) {
                quantity++
                binding.tvquantity.text = quantity.toString()
                updateTotal()
            } else {
                Toast.makeText(this, "Kuantitas tidak bisa lebih dari 5", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPesan.setOnClickListener {
            val nama = user.ambilUser()?.nama
            val jumlah = binding.tvquantity.text.toString()
            val status = "Diproses"
            val alamat = binding.alamat.text.toString()
            val phone = user.ambilUser()?.phone

            val timestamp = System.currentTimeMillis() // Mengambil timestamp saat ini

            if (nama?.isNotEmpty() == true && jumlah.isNotEmpty()) {
                val newOrderRef = database.push()
                val orderId = newOrderRef.key

                val data = Orders(
                    orderId = orderId,
                    nama = nama,
                    jumlah = jumlah.toInt(),
                    status = status,
                    phone = phone,
                    alamat = alamat,
                    tanggal = timestamp.toDouble()
                )

                // Menyimpan data dengan ID
                newOrderRef.setValue(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil order", Toast.LENGTH_SHORT).show()
                        // Clear form fields
                        val intent = Intent(this@PemesananActivity, PembayaranActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Gagal menyimpan data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Mohon isi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        val pricePerItem = 6000
        val total = quantity * pricePerItem
        val formattedTotal = NumberFormat.getNumberInstance(Locale("in", "ID")).format(total)
        binding.textView8.text = "Rp $formattedTotal"
    }
}
