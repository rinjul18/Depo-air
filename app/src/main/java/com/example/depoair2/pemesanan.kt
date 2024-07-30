package com.example.depoair2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.depoair2.databinding.ActivityPemesananBinding // Sesuaikan dengan nama file binding yang sesuai

class pemesanan : AppCompatActivity() {
    private lateinit var binding: ActivityPemesananBinding // Sesuaikan dengan nama file binding yang sesuai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPesan.setOnClickListener {
            val intent = Intent(this, PemesananActivity::class.java) // Ganti dengan nama Activity yang benar
            startActivity(intent)
        }
    }
}
