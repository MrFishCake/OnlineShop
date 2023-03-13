package com.example.onlineshop

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private lateinit var v: View

    private lateinit var users: DatabaseReference
    private lateinit var products: DatabaseReference
    private lateinit var sales: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_home, container, false)


        users = FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        products = FirebaseDatabase.getInstance().reference.child("products")
        sales = FirebaseDatabase.getInstance().reference.child("sales")

        val userAvatar = v.findViewById<View>(R.id.profile_img) as CircleImageView

        val avatar: String = arguments?.getString("avatar").toString()
        if (avatar != "default") {
            Picasso.get().load(avatar).into(userAvatar)
        }

        val latest: String = arguments?.getString("latest").toString()
        if (latest != " ") {
            setLatest(latest)
            (v.findViewById<HorizontalScrollView>(R.id.scrollview_latest)).visibility = View.VISIBLE
            (v.findViewById<View>(R.id.latest_txt)).visibility = View.VISIBLE
            (v.findViewById<View>(R.id.view_all)).visibility = View.VISIBLE
        }

        sales.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val flashSale = snapshot.value.toString()
                if (flashSale != "") {
                    setFlashSale(flashSale)
                    (v.findViewById<View>(R.id.scrollview_flashsale)).visibility = View.VISIBLE
                    (v.findViewById<View>(R.id.flash_sale_txt)).visibility = View.VISIBLE
                    (v.findViewById<View>(R.id.view_all2)).visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseDatabase.SALES read error!", error.message)
            }
        })

        userAvatar.setOnClickListener {
            val transaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.home_page, ProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return v
    }

    private fun setLatest(latest: String) {
        val list: List<String> = listOf(*latest.split(", ").toTypedArray())

        products.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (list.size >= 2) {
                    val name =
                        if (list[1].indexOf("]") != -1)
                            list[1].substring(0, list[1].indexOf("]"))
                        else
                            list[1]

                    val price = "$ " + dataSnapshot.child(name).child("price").value.toString()

                    Picasso.get().load(dataSnapshot.child(name).child("image_url").value.toString())
                        .into(v.findViewById<ShapeableImageView>(R.id.latest_1) as ShapeableImageView)
                    (v.findViewById<TextView>(R.id.latest_1_category) as TextView).text =
                        dataSnapshot.child(name).child("category").value.toString()
                    (v.findViewById<TextView>(R.id.latest_1_name) as TextView).text = name
                    (v.findViewById<TextView>(R.id.latest_1_price) as TextView).text = price
                }
                if (list.size >= 3) {
                    val name =
                        if (list[2].indexOf("]") != -1)
                            list[2].substring(0, list[2].indexOf("]"))
                        else
                            list[2]

                    val price = "$ " + dataSnapshot.child(name).child("price").value.toString()
                    val imageURL = dataSnapshot.child(name).child("image_url").value.toString()

                    Picasso.get().load(imageURL)
                        .into(v.findViewById<ShapeableImageView>(R.id.latest_2) as ShapeableImageView)
                    (v.findViewById<TextView>(R.id.latest_2_category) as TextView).text =
                        dataSnapshot.child(name).child("category").value.toString()
                    (v.findViewById<TextView>(R.id.latest_2_name) as TextView).text = name
                    (v.findViewById<TextView>(R.id.latest_2_price) as TextView).text = price
                }
                if (list.size >= 4) {
                    val name =
                        if (list[3].indexOf("]") != -1)
                            list[3].substring(0, list[3].indexOf("]"))
                        else
                            list[3]

                    val price = "$ " + dataSnapshot.child(name).child("price").value.toString()
                    val imageURL = dataSnapshot.child(name).child("image_url").value.toString()

                    Picasso.get().load(imageURL)
                        .into(v.findViewById<ShapeableImageView>(R.id.latest_3) as ShapeableImageView)
                    (v.findViewById<TextView>(R.id.latest_3_category) as TextView).text =
                        dataSnapshot.child(name).child("category").value.toString()
                    (v.findViewById<TextView>(R.id.latest_3_name) as TextView).text = name
                    (v.findViewById<TextView>(R.id.latest_3_price) as TextView).text = price
                }
                if (list.size == 5) {
                    val name = list[4].substring(0, list[4].indexOf("]"))
                    val price = "$ " + dataSnapshot.child(name).child("price").value.toString()
                    val imageURL = dataSnapshot.child(name).child("image_url").value.toString()

                    Picasso.get().load(imageURL)
                        .into(v.findViewById<ShapeableImageView>(R.id.latest_4) as ShapeableImageView)
                    (v.findViewById<TextView>(R.id.latest_4_category) as TextView).text =
                        dataSnapshot.child(name).child("category").value.toString()
                    (v.findViewById<TextView>(R.id.latest_4_name) as TextView).text = name
                    (v.findViewById<TextView>(R.id.latest_4_price) as TextView).text = price
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseDatabase.PRODUCTS read error!", error.message)
            }
        })
    }

    private fun setFlashSale(str: String) {
        val list: List<String> =
            listOf(*str.substring(7, str.length - 1).split(", ").toTypedArray())

        products.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (list.isNotEmpty()) {
                    val name = list[0]

                    val price = "$ " + dataSnapshot.child(name).child("price").value.toString()

                    Picasso.get().load(dataSnapshot.child(name).child("image_url").value.toString())
                        .into(v.findViewById<ShapeableImageView>(R.id.flashsale_1) as ShapeableImageView)
                    (v.findViewById<TextView>(R.id.flashsale_1_category) as TextView).text =
                        dataSnapshot.child(name).child("category").value.toString()
                    (v.findViewById<TextView>(R.id.flashsale_1_name) as TextView).text = name
                    (v.findViewById<TextView>(R.id.flashsale_1_price) as TextView).text = price
                }
                if (list.size == 2) {
                    val name = list[1]

                    val price = "$ " + dataSnapshot.child(name).child("price").value.toString()
                    val imageURL = dataSnapshot.child(name).child("image_url").value.toString()

                    Picasso.get().load(imageURL)
                        .into(v.findViewById<ShapeableImageView>(R.id.flashsale_2) as ShapeableImageView)
                    (v.findViewById<TextView>(R.id.flashsale_2_category) as TextView).text =
                        dataSnapshot.child(name).child("category").value.toString()
                    (v.findViewById<TextView>(R.id.flashsale_2_name) as TextView).text = name
                    (v.findViewById<TextView>(R.id.flashsale_2_price) as TextView).text = price
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseDatabase.PRODUCTS read error!", error.message)
            }
        })
    }

    companion object {
        fun getNewInstance(args: Bundle?): HomeFragment {
            val homeFragment = HomeFragment()
            homeFragment.arguments = args
            return homeFragment
        }
    }
}