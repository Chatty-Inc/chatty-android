package com.mmm.chatty

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_list.view.*

class UserListUpdateAdapter(
    private val userList: ArrayList<List<String>>,
    private val context: Context
) : RecyclerView.Adapter<ViewHolder>() {
    // Click listener extension
    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T { // Adapter for onClickListener
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

    // Gets the number of rows
    override fun getItemCount(): Int {
        Log.i("data", userList.toString())
        return userList.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list, parent, false))
            .listen { _, _ -> // row
            }
    }

    // Binds each row of data in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = userList[position][2].substringAfterLast("=")

        Picasso.get().load(userList[position][0].substringAfterLast("photoURL=")).fit()
            .transform(
                RoundedTransformation(
                    (60 * Resources.getSystem().displayMetrics.density).toInt(),
                    0
                )
            ) // Get px from dp
            .into(holder.profilePic, object : Callback {
                override fun onSuccess() {
                    Log.d("Picasso", "Successfully loaded image")
                }

                override fun onError(e: Exception?) {
                    if (e != null) {
                        Log.d("Image URL", userList[position][0].substringAfterLast("photoURL="))
                        Log.e("Picasso error", e.message.toString())
                    }
                }
            })
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val profilePic: ImageView = view.profilePic
    val username: TextView = view.userName
    val latestMsg: TextView = view.latestMsg
}