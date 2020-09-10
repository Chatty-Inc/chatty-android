package com.mmm.chatty

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_management.*

class UserManagement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        setSupportActionBar(findViewById(R.id.userMgmtToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            if (user.displayName.toString() != "null") userName.text = user.displayName
            else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    userName.text = PhoneNumberUtils.formatNumber(user.phoneNumber, "SG")
                } else {
                    userName.text = PhoneNumberUtils.formatNumber(user.phoneNumber)
                }
            }

            if (user.email.toString() != "null") emailAddr.text = user.email
            else emailAddr.text = "Unknown"

            if (user.phoneNumber != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    phoneNo.text = PhoneNumberUtils.formatNumber(user.phoneNumber, "SG")
                } else {
                    phoneNo.text = PhoneNumberUtils.formatNumber(user.phoneNumber)
                }
            } else phoneNo.text = "Unknown"

            if (!user.isEmailVerified && user.email != null) {
                verifyEmail.visibility = View.VISIBLE
                verifyEmail.setOnClickListener {
                    user.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Snackbar.make(
                                    it,
                                    "Successfully sent verification email",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                Snackbar.make(
                                    it,
                                    "An error occurred (${task.exception?.message})",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }

            val signInMethods: MutableList<String> = mutableListOf()
            user.let {
                for (profile in it.providerData) {
                    when (profile.providerId) {
                        GoogleAuthProvider.PROVIDER_ID -> signInMethods.add("Google")
                        PhoneAuthProvider.PROVIDER_ID -> signInMethods.add("Phone")
                        EmailAuthProvider.PROVIDER_ID -> signInMethods.add("Email")
                        GithubAuthProvider.PROVIDER_ID -> signInMethods.add("GitHub")
                    }
                }
            }
            signInMethod.text = signInMethods.joinToString(", ")


            if (user.photoUrl != null) {
                val radius = (200 * Resources.getSystem().displayMetrics.density).toInt()
                val highResURL = user.photoUrl.toString().replace("s96-c", "s$radius-c")
                // Profile pic loader
                Picasso.get().load(user.photoUrl).fit()
                    .transform(
                        RoundedTransformation(
                            radius,
                            0
                        )
                    ) // Get px from dp
                    .into(bigProfilePic, object : Callback {
                        override fun onSuccess() {
                            Picasso.get()
                                .load(highResURL) // image url goes here
                                .placeholder(bigProfilePic.drawable)
                                .transform(RoundedTransformation(radius, 0))
                                .into(bigProfilePic)
                        }

                        override fun onError(e: Exception?) {
                            // Do nothing here
                        }
                    })
            } else {
                bigProfilePic.setImageBitmap(
                    RoundedTransformation(
                        (400 * Resources.getSystem().displayMetrics.density).toInt(),
                        0
                    )
                        .transform(
                            ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.ic_user_placeholder
                            )
                                ?.toBitmap()
                        )
                )
            }
        } else {
            startActivity(
                Intent(this@UserManagement, LoginPage::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}