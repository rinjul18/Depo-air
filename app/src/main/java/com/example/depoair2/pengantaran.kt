package com.example.depoair2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class pengantaran : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengantaran)

        database = FirebaseDatabase.getInstance().reference

        val pengguna: EditText = findViewById(R.id.pengguna)
        val harga: EditText = findViewById(R.id.harga)
        val tanggal: EditText = findViewById(R.id.tanggal)
        val alamat: EditText = findViewById(R.id.alamat)
        val btn_antar: Button = findViewById(R.id.btn_Antar)

        btn_antar.setOnClickListener {
            val pengguna = pengguna.text.toString()

            val harga = harga.text.toString()
            val tanggal = tanggal.text.toString()
            val alamat = alamat.text.toString()

            if (pengguna.isNotEmpty() && harga.isNotEmpty() && tanggal.isNotEmpty() && alamat.isNotEmpty()) {
                val Userdatauser = Userdatauser(pengguna, harga, tanggal, alamat)
                sendDataToFirebase(Userdatauser())
            } else {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun sendDataToFirebase(Userdatauser: Userdatauser) {
            val key = database.push().key ?: return
            database.child("pengantaran").child(key).setValue(Userdatauser)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Berhasil Jemput", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal Jemput", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

