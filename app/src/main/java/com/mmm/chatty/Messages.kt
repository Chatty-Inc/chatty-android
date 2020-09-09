package com.mmm.chatty

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_messages.*

class Messages : AppCompatActivity(), IDialog<IMessage>, IUser {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        setSupportActionBar(findViewById(R.id.messages_list_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val senderID = "random"
        val adapter: MessagesListAdapter<IMessage> =
            MessagesListAdapter<IMessage>(senderID) { imageView: ImageView, url: String?, _: Any? ->
                Picasso.get().load(url).into(imageView)
            }
        messagesList.setAdapter(adapter)

    }

    /* IDialog and IUser function */
    override fun getId(): String {
        TODO("Not yet implemented")
    }

    /* IDialog override functions */
    override fun getDialogPhoto(): String {
        TODO("Not yet implemented")
    }

    override fun getDialogName(): String {
        TODO("Not yet implemented")
    }

    override fun getUsers(): MutableList<out IUser> {
        TODO("Not yet implemented")
    }

    override fun getLastMessage(): IMessage {
        TODO("Not yet implemented")
    }

    override fun setLastMessage(message: IMessage?) {
        TODO("Not yet implemented")
    }

    override fun getUnreadCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getAvatar(): String {
        TODO("Not yet implemented")
    }
}