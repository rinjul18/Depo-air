package com.example.depoair2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.depoair2.databinding.ActivityRekapBinding
import com.example.depoair2.models.Orders
import com.example.depoair2.ui.adapter.RiwayatAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RekapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRekapBinding
    private lateinit var rekapAdapter: RiwayatAdapter
    private val rekapList = mutableListOf<Orders>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = FirebaseDatabase.getInstance().getReference("Orders")
        binding = ActivityRekapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rekapAdapter = RiwayatAdapter(rekapList, true) {
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.detailRekap.apply {
            layoutManager = LinearLayoutManager(this@RekapActivity)
            adapter = rekapAdapter
        }
        binding.datePicker.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Pilih jangka Rekap")
                .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first ?: 0L
            val endDate = selection.second ?: 0L
            val textStartDate = formatDate(startDate)
            val textEndDate = formatDate(endDate)
            binding.tvPeriode.text = "$textStartDate sampai $textEndDate"
            queryDataFromFirebase(startDate, endDate)
        }

        dateRangePicker.show(supportFragmentManager, "dateRangePicker")
    }

    private fun queryDataFromFirebase(startDate: Long, endDate: Long) {
        val ref = database.orderByChild("tanggal")
            .startAt(startDate.toDouble())
            .endAt(endDate.toDouble())

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rekapList.clear()  // Clear the list before adding new data
                var totalQuantity = 0
                var totalIncome = 0.0
                val pricePerItem = 6000
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Orders::class.java)
                    if (order != null && order.status == "Selesai") {
                        rekapList.add(order)
                        // Hitung total kuantitas
                        totalQuantity += order.jumlah ?: 0

                        // Hitung total penghasilan
                        totalIncome += (order.jumlah ?: 0) * pricePerItem
                        val formattedIncome = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(totalIncome)
                        binding.totalGalon.text = totalQuantity.toString()
                        binding.totalPenjualan.text = formattedIncome
                        binding.noData.visibility = View.GONE
                    }else{
                        binding.noData.visibility = View.VISIBLE
                    }
                }
                Log.d("list", "onDataChange: $rekapList")
                rekapAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseQuery", "Error: ${error.message}")
            }
        })
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
