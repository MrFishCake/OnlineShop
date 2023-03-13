package com.example.onlineshop


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var v: View
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private var selectedImg = Uri.parse("")
    private lateinit var storage: FirebaseStorage
//    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        (v.findViewById<View>(R.id.username) as TextView).text = arguments?.getString("username")
        val avatar: String = arguments?.getString("avatar").toString()
        if (avatar != "default") {
            Picasso.get().load(avatar).into(v.findViewById<View>(R.id.avatar) as CircleImageView)
        }
        (v.findViewById<View>(R.id.balance) as TextView).text = arguments?.getString("balance")

        val backButton = v.findViewById<View>(R.id.back_btn) as ImageView
        val changeAvatarButton = v.findViewById<View>(R.id.change_photo_btn) as TextView
        val uploadButton = v.findViewById<View>(R.id.upload_btn) as Button
        val logOutTxt = v.findViewById<View>(R.id.log_out_txt) as TextView

        changeAvatarButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        backButton.setOnClickListener {
            val transaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.profile_page, HomeFragment.getNewInstance(arguments))
            transaction.addToBackStack(null)
            transaction.commit()
        }

        uploadButton.setOnClickListener {
            val transaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.profile_page, AddProductFragment.getNewInstance(arguments))
            transaction.addToBackStack(null)
            transaction.commit()
        }

        logOutTxt.setOnClickListener {
            val navActivity: NavigationMenuActivity = activity as NavigationMenuActivity
            AlertDialog.Builder(navActivity).apply {
                setTitle("Confirmation")
                setMessage("Are you sure you want to log out?")

                setPositiveButton("Yep") { _, _ ->
                    auth.signOut()
                    navActivity.closeActivity()
                    requireFragmentManager().beginTransaction().remove(ProfileFragment()).commit()
                }

                setNegativeButton("Nope") { _, _ -> }
                setCancelable(true)
            }.create().show()
        }

        return v
    }

    private fun uploadData() {
        val reference = storage.reference.child("profilePics").child(Date().time.toString())
        reference.putFile(selectedImg).addOnCompleteListener {
            if (it.isSuccessful)
                reference.downloadUrl.addOnSuccessListener { task ->
                    db.reference
                        .child("users")
                        .child(auth.uid.toString())
                        .child("avatar")
                        .setValue(task.toString())
                    arguments?.putString("avatar", task.toString())
                }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                selectedImg = data.data!!
                val userAvatar = v.findViewById<View>(R.id.avatar) as CircleImageView
                userAvatar.setImageURI(selectedImg)
                uploadData()
            }
        }
    }

    companion object {
        fun getNewInstance(args: Bundle?): ProfileFragment {
            val profileFragment = ProfileFragment()
            profileFragment.arguments = args
            return profileFragment
        }
    }
}