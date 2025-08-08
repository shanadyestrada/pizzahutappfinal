package com.example.pizzahutappfinal

import android.content.Context
import android.widget.Toast

object AppUtil {

    fun showToast(context : Context, mesagge : String) {
        Toast.makeText(context,mesagge, Toast.LENGTH_LONG).show()
    }

}