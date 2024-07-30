package com.example.depoair2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.depoair2.databinding.ActivityDataPelanggan2Binding
import com.example.depoair2.models.Orders
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PemesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataPelanggan2Binding
    private lateinit var database: DatabaseReference
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout menggunakan binding
        binding = ActivityDataPelanggan2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Inisialisasi database Firebase
        database = FirebaseDatabase.getInstance().getReference("Orders")

        // Set onClickListener untuk tombol pesan
        binding.btnPesan.setOnClickListener {
            val username = pref.getString("username", null)
            val jumlah = binding.jumlah.text.toString()
            val status = "diproses"

            if (username != null && jumlah.isNotEmpty()) {
                // Gunakan no_hp sebagai kunci unik

                val data = Orders(username, jumlah.toInt(), status)

                // Simpan data ke Firebase
                database.push().setValue(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                        // Kosongkan field setelah penyimpanan berhasil
                        binding.nama.text.clear()
                        binding.jumlah.text.clear()
                        binding.alamat.text.clear()
                        binding.noHp.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@PemesananActivity,PembayaranActivity::class.java)
            startActivity(intent)
        }
    }
}
