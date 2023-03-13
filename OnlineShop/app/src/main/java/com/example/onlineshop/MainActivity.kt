package com.example.onlineshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.onlineshop.databinding.ActivityMainBinding
import com.example.onlineshop.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db.getReference("users")

        binding.logIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signInBtn.setOnClickListener {
            val firstName = binding.signinFirstName.text.toString()
            val secName = binding.signinLastName.text.toString()
            val email = binding.signinEmail.text.toString()
            val pass = binding.signinPass.text.toString()

            if (firstName.isNotEmpty() && secName.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {
//                if ("/(?=.*[0-9])(?=.*[!@#\$%^&*])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#\$%^&*]{6,}/g".toRegex()
//                        .containsMatchIn(pass)
//                    && pass.length >= 8
//                ) {
                firebaseAuth
                    .createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            users
                                .child(firebaseAuth.currentUser?.uid.toString())
                                .setValue(
                                    User(firstName, secName, 0, "default"," ",email, pass)
                                )
                            outMessage("Successful registration!")
                            startActivity(Intent(this, NavigationMenuActivity::class.java))

                        } else
                            if (it.exception.toString() == ("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")
                            )
                                outMessage("User with this data already exists!")
                            else if (it.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")
                                outMessage("Email entered incorrectly!")
                            else
                                outMessage(it.exception.toString())
                    }
//                } else
//                    outMessage("Password must contain at least 6 characters with numbers, special, lowercase and uppercase")
            } else
                outMessage("Empty fields aren't allowed!")
        }

        // sign in with Google
        auth = Firebase.auth
        auth.currentUser
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null)
                    firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d("Google signIn", "Error: Api exception")
            }
        }
        binding.signInGoogle.setOnClickListener {
            onClickSignInGoogle()
        }
        checkAuthState()
    }

    private fun outMessage(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    private fun checkAuthState() {
        if (auth.currentUser != null)
            startActivity(Intent(this, NavigationMenuActivity::class.java))
    }

    // -----------  sign in with Google
    private fun onClickSignInGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun getClient(): GoogleSignInClient {
        val signInRequest = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, signInRequest)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Google signIn", "Success!")
                    users
                        .child(firebaseAuth.currentUser?.uid.toString())
                        .setValue(
                            User("Username", " ", 0, "default"," ", firebaseAuth.currentUser?.email.toString(), "nopass")
                        )
                    checkAuthState()
                } else
                    Log.d("Google signIn", "Error!")
            }
    }

}