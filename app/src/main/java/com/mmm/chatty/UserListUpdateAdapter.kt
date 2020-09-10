package com.mmm.chatty

import android.content.Context
import android.content.res.Resources
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
        if (userList[position][2].substringAfterLast("=") == "null") {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.username.text = PhoneNumberUtils.formatNumber(
                    userList[position][4].substringAfterLast("="),
                    "SG"
                )
            } else {
                holder.username.text =
                    PhoneNumberUtils.formatNumber(userList[position][4].substringAfterLast("="))
            }
        } else {
            holder.username.text = userList[position][2].substringAfterLast("=")
        }

        // Profile pic loader
        Picasso.get().load(userList[position][0].substringAfterLast("photoURL=")).fit()
            .transform(
                RoundedTransformation(
                    (60 * Resources.getSystem().displayMetrics.density).toInt(),
                    0
                )
            ) // Get px from dp
            .into(holder.profilePic, object : Callback {
                override fun onSuccess() = Unit

                override fun onError(e: Exception?) {
                    // Load placeholder image
                    holder.profilePic.setImageBitmap(
                        RoundedTransformation(
                            (400 * Resources.getSystem().displayMetrics.density).toInt(),
                            0
                        )
                            .transform(
                                ContextCompat.getDrawable(context, R.drawable.ic_user_placeholder)
                                    ?.toBitmap()
                            )
                    )

                    if (e != null) {
                        Log.d(
                            "Image URL",
                            userList[position][0].substringAfterLast("photoURL=")
                        )
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