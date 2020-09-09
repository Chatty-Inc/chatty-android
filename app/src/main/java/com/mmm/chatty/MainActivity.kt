package com.mmm.chatty

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private val RC_SIGN_IN = 1822
    private var user: FirebaseUser? = null
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.GitHubBuilder().build()
    )

    private fun startAuthFlow() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.SignInDark)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                val database = FirebaseDatabase.getInstance()
                if (user != null) {
                    ref = database.getReference(user!!.uid)
                    ref.updateChildren(
                        mapOf(
                            "uid" to user!!.uid.toString(),
                            "name" to user!!.displayName.toString(),
                            "email" to user!!.email.toString(),
                            "photoURL" to user!!.photoUrl.toString(),
                        )
                    )
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    // Try again until user signs in
                    startAuthFlow()
                } else {
                    startActivity(
                        Intent(this@MainActivity, ErrorViewer::class.java)
                            .putExtra("error", response.error.toString())
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.chatListToolbar))

        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // already signed in

        } else {
            // not signed in - Attempt to sign user in
            // Firebase Auth UI
            startAuthFlow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.sign_out -> {
                if (user != null) AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // user is now signed out
                        // Relaunch sign in UI
                        startAuthFlow()
                    }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}