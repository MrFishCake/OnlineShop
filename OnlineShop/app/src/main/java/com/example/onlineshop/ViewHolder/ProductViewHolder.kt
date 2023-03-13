package com.example.onlineshop.ViewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.Intefaces.ItemClickListener
import com.example.onlineshop.R

class ProductViewHolder(itemView: View) :  View.OnClickListener, RecyclerView.ViewHolder(
    itemView
) {
    var txtProductName: TextView
    var txtProductCategory: TextView
    var txtProductPrice: TextView
    var imageView: ImageView
    lateinit var listener: ItemClickListener

    init {
        imageView = itemView.findViewById(R.id.product_image)
        txtProductName = itemView.findViewById(R.id.product_name)
        txtProductCategory = itemView.findViewById(R.id.product_category)
        txtProductPrice = itemView.findViewById(R.id.product_price)
    }

    fun setItemClickListener(listener_: ItemClickListener) {
        listener = listener_
    }

    override fun onClick(view: View?) {
        view?.let { listener.OnClick(it, adapterPosition, false) }
    }
}