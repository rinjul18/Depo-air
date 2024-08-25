package com.example.depoair2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.depoair2.databinding.ItemRiwayatBinding
import com.example.depoair2.models.Orders
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatAdapter(
    private val riwayatList: List<Orders>,
    private val isAdmin: Boolean,
    private val onItemClick: (Orders) -> Unit

) : RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    inner class RiwayatViewHolder(val binding: ItemRiwayatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(riwayatItem: Orders) {
            if (isAdmin){
                binding.textView3.text = "Isi ulang ${riwayatItem.jumlah} galon"
                binding.tanggal.text = riwayatItem.tanggal?.toLong()?.let { formatDate(it) }
                binding.selesai.text = riwayatItem.status
                binding.nama.text = riwayatItem.nama
                binding.alamat.text = riwayatItem.alamat
                binding.totalHarga.text = riwayatItem.jumlah?.let { totalHarga(it) }
                if (riwayatItem.status == "Diproses") {
                    binding.root.setOnClickListener {
                        onItemClick(riwayatItem)
                    }
                } else {
                    binding.root.setOnClickListener(null)
                }
            }else{
                binding.textView3.text = "Isi ulang ${riwayatItem.jumlah} galon"
                binding.tanggal.text = riwayatItem.tanggal?.toLong()?.let { formatDate(it) }
                binding.selesai.text = riwayatItem.status
                binding.nama.visibility = View.GONE
                binding.alamat.visibility = View.GONE
                binding.totalHarga.text = riwayatItem.jumlah?.let { totalHarga(it) }
                if (riwayatItem.status == "Dikirim") {
                    binding.root.setOnClickListener {
                        onItemClick(riwayatItem)
                    }
                } else {
                    binding.root.setOnClickListener(null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiwayatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        holder.bind(riwayatList[position])
    }

    override fun getItemCount(): Int {
        return riwayatList.size
    }
    private fun totalHarga(qty:Int):String {
        val pricePerItem = 6000
        val total = qty * pricePerItem
        val formattedTotal = NumberFormat.getNumberInstance(Locale("in", "ID")).format(total)
        return "Rp $formattedTotal"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
