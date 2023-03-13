package com.example.onlineshop.Intefaces

import android.view.View

interface ItemClickListener {
    fun OnClick(view: View, position: Int, isLongClick: Boolean)
}