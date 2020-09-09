package com.mmm.chatty

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), IDialog<IMessage>, IUser {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dialogsListAdapter: DialogsListAdapter<*> =
            DialogsListAdapter<IDialog<*>> { imageView: ImageView, url: String?, _: Any? ->
                //If you using another library - write here your way to load image
                Picasso.get().load(url).into(imageView)
            }

        dialogsList.setAdapter(dialogsListAdapter)
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