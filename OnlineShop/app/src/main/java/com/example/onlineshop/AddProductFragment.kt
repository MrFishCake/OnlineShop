package com.example.onlineshop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction

class AddProductFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_add_product, container, false)

        val backButton = v.findViewById<View>(R.id.back_button) as ImageView

        backButton.setOnClickListener {
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.add_new_product, ProfileFragment.getNewInstance(arguments))
            transaction.addToBackStack(null)
            transaction.commit()
        }


        return v
    }

    companion object {
        fun getNewInstance(args: Bundle?): AddProductFragment {
            val fragment = AddProductFragment()
            fragment.arguments = args
            return fragment
        }
    }
}