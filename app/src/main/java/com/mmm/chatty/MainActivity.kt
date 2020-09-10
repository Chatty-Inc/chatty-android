package com.mmm.chatty

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private lateinit var rootRef: DatabaseReference
    var data: ArrayList<List<String>> = ArrayList()
    private var user: FirebaseUser? = null

    private fun updateUserlist() {
        swipeRefreshLayout.isRefreshing = true
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var useAdd = false
                if (data.isEmpty()) {
                    useAdd = true
                }
                var i =
                    0 // .add won't work here as it'll just keep on adding and not delete old data
                dataSnapshot.children.forEach {
                    val dataList = it.value.toString().drop(1).dropLast(1)
                        .split(", (?=([^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()).toMutableList()
                    dataList.add(it.key.toString()) // toString is safer than !!
                    if (useAdd) {
                        data.add(dataList)
                    } else {
                        data[i] = dataList
                    }
                    i += 1
                }
                userList.adapter?.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Snackbar.make(
                    userList,
                    "Failed to update data. Please try again later.",
                    Snackbar.LENGTH_LONG
                ).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }
        rootRef.addListenerForSingleValueEvent(postListener)
    }

    private fun initRecyclerView() {
        updateUserlist()
        // Creates a vertical Layout Manager
        userList.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = UserListUpdateAdapter(data, this@MainActivity)
            this.addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateUserlist()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.chatListToolbar))

        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // already signed in
            val database = FirebaseDatabase.getInstance()
            rootRef = database.reference
            if (user != null) {
                ref = database.getReference(user!!.uid)
            }

            initRecyclerView()
        } else {
            // not signed in - start sign in Activity as the root activity
            startActivity(
                Intent(this@MainActivity, LoginPage::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
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
                        // Clear user list
                        data.clear()
                        userList.adapter?.notifyDataSetChanged()
                        // Relaunch sign in UI
                        startActivity(
                            Intent(this@MainActivity, LoginPage::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}