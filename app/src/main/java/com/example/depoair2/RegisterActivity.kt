package com.example.depoair2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.depoair2.databinding.ActivityRegisterBinding
import com.example.depoair2.models.Users
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("User")

        binding.loginbtn.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {

            val nama = binding.ednm.text.toString()
            val password = binding.edpw.text.toString()
            val phone = binding.edphone.text.toString()
            val role = "pelanggan"

            database = FirebaseDatabase.getInstance().getReference("Users")
            val user = Users( nama, phone, password, role )
            database.child(phone).setValue(user).addOnSuccessListener {

                binding.ednm.text.clear()
                binding.edEmail.text.clear()
                binding.edphone.text.clear()
                binding.edpw.text.clear()

                Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }


        }

    }
}