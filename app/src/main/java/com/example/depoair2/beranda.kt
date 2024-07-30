package com.example.depoair2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class beranda : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var welcomeText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var pemesananImage: ImageView
    private lateinit var deskripsiImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        // Initialize views
        logo = findViewById(R.id.logo)
        welcomeText = findViewById(R.id.welcome_text)
        subtitleText = findViewById(R.id.subtitle_text)
        pemesananImage = findViewById(R.id.pemesanan)
        deskripsiImage = findViewById(R.id.deskripsi)

        // Set click listeners for the ImageViews
        pemesananImage.setOnClickListener {
            val intent = Intent(this@beranda, pemesanan::class.java)
            startActivity(intent)
        }

        deskripsiImage.setOnClickListener {
            val intent = Intent(this@beranda, DeskripsiActivity::class.java)
            startActivity(intent)
        }

        // Initialize and set Toolbar as ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "WATER DEPOT"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_beranda -> {
                startActivity(Intent(this, beranda::class.java))
                true
            }
            R.id.nav_riwayat -> {
                startActivity(Intent(this, riwayat::class.java))
                true
            }
            R.id.nav_akun -> {
                startActivity(Intent(this, akun::class.java))
                true
            }
            R.id.nav_Keluar -> {
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
