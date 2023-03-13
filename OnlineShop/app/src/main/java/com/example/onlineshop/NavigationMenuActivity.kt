package com.example.onlineshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.onlineshop.databinding.ActivityNavigationMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class NavigationMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationMenuBinding

    private lateinit var users: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = Bundle()
        users = FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        users.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bundle.putString("avatar", dataSnapshot.child("avatar").value.toString())

                bundle.putString("username", dataSnapshot.child("firstName").value.toString() + " "+
                        dataSnapshot.child("secondName").value.toString())

                bundle.putString("balance", "$ " + dataSnapshot.child("balance").value.toString())

                bundle.putString("latest", dataSnapshot.child("latest").value.toString())

                replaceFragment(HomeFragment.getNewInstance(args = bundle))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseDatabase read error!", error.message)
            }
        })

        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment.getNewInstance(args = bundle))
                }
                R.id.favorite -> replaceFragment(FavoriteFragment())
                R.id.basket -> replaceFragment(BasketFragment())
                R.id.message -> replaceFragment(MessageFragment())
                R.id.user -> {
                    replaceFragment(ProfileFragment.getNewInstance(args = bundle))
                }
                else -> {}
            }
            true
        }
    }

    fun closeActivity() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
        this.finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmation")
            setMessage("Are you sure you want to exit the program?")

            setPositiveButton("Yep") { _, _ ->
                super.onBackPressed()
                moveTaskToBack(true)
                super.onDestroy()
                System.runFinalizersOnExit(true)
                exitProcess(0)
            }

            setNegativeButton("Nope"){_, _ ->}
            setCancelable(true)
        }.create().show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
