package com.example.depoair2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.depoair2.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class LoginActivity : AppCompatActivity() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var context: Context
    private lateinit var pref: SharedPreferences
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        pref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        binding.txtRegis.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnlogin.setOnClickListener {
            val username: String = binding.edtPhone.text.toString()
            val password: String = binding.edtPassword.text.toString()
            if (username.isEmpty()) {
                binding.edtPhone.error = "Data Tidak Boleh Kosong"
                binding.edtPhone.requestFocus()
            } else if (password.isEmpty ()) {
                binding.edtPassword.error = "Data Tidak Boleh Kosong"
                binding.edtPassword.requestFocus()
            } else {
                val query: Query = database.child("Users").orderByChild("phone").equalTo(username)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (item in snapshot.children) {
                                val user = item.getValue<UserData>()
                                if (user != null) {
                                    if (user.password == password) {
                                        pref.edit().apply {
                                            putString("username", username)
                                            putBoolean("prefStatus", true)
                                            putString("prefLevel", user.level)
                                            apply()
                                        }
                                        val intent = if (user.level == "User") {
                                            Intent(context, beranda::class.java)
                                        } else {
                                            Intent(context, beranda::class.java)
                                        }
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(context, "Password Salah", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
//    override fun onStart() {
//        super.onStart()
//        if (pref.getBoolean("prefStatus", false)) {
//            val intent = if (pref.getString("prefLevel", "") == "User") {
//                Intent(context, RegisterActivity::class.java)
//            } else {
//                Intent(context, MainActivity::class.java)
//            }
//            startActivity(intent)
//            finish()
//        }
//    }
}