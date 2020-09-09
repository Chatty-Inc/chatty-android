package com.mmm.chatty

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login_page.*

class LoginPage : AppCompatActivity() {
    private val signInRC = 1822
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.GitHubBuilder().build()
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == signInRC) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                val database = FirebaseDatabase.getInstance()
                if (user != null) {
                    database.getReference(user.uid).updateChildren(
                        mapOf(
                            "uid" to user.uid,
                            "photoURL" to user.photoUrl.toString(),
                            "phoneNo" to user.phoneNumber.toString(),
                            "email" to user.email.toString(),
                            "name" to user.displayName.toString(),
                        )
                    )

                    startActivity(
                        Intent(this@LoginPage, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    Snackbar.make(
                        login_toolbar,
                        "Login was canceled",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    // Start as root activity so that the user won't be able to navigate backwards
                    startActivity(
                        Intent(this@LoginPage, ErrorViewer::class.java)
                            .putExtra("error", response.error.toString())
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        setSupportActionBar(findViewById(R.id.login_toolbar))

        startLoginFlow.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(R.style.SignInDark)
                    .build(),
                signInRC
            )
        }
    }
}