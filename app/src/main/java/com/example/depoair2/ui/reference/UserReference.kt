package com.example.depoair2.ui.reference

import android.content.Context
import android.content.SharedPreferences
import com.example.depoair2.models.Users
import com.google.gson.Gson

class UserReference(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    // Menyimpan data pengguna ke SharedPreferences
    fun simpanUser(user: Users) {
        val json = gson.toJson(user)
        editor.putString("user", json)
        editor.apply()
    }

    // Mengambil data pengguna dari SharedPreferences
    fun ambilUser(): Users? {
        val json = sharedPreferences.getString("user", null)
        return if (json != null) {
            gson.fromJson(json, Users::class.java)
        } else {
            null
        }
    }

    // Menghapus data pengguna dari SharedPreferences
    fun hapusUser() {
        editor.remove("user")
        editor.apply()
    }
}