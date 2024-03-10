package com.example.afinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnAuthLaunch {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun launch(intent: Intent) {
        startActivityForResult(intent, 1)
    }

    override fun showListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CityInputFragment())
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val result = task.getResult(ApiException::class.java)
                val credential =
                    GoogleAuthProvider.getCredential(result.idToken, null)
                val auth = FirebaseAuth.getInstance()
                auth.signInWithCredential(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showListFragment()
                        } else {
                            Toast.makeText(
                                this, "Authentication failed", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(
                    this, "Error:${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

interface OnAuthLaunch {
    fun launch(intent: Intent)
    fun showListFragment()
}