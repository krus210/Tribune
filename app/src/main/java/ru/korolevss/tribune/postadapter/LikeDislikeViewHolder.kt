package ru.korolevss.tribune.postadapter

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.like_dislike_card.view.*
import ru.korolevss.tribune.R
import ru.korolevss.tribune.UserActivity
import ru.korolevss.tribune.dto.AttachmentModel
import ru.korolevss.tribune.model.LikeDislike
import ru.korolevss.tribune.model.LikeDislikeModel


class LikeDislikeViewHolder(
    private val adapter: LikeDislikeAdapter, private val view: View, var list: MutableList<LikeDislikeModel>
) : RecyclerView.ViewHolder(view) {

    init {
        this.clickButtonListener()
    }

    private fun clickButtonListener() {
        with(view) {
            imageViewUser.setOnClickListener {
                startUserActivity(context)
            }
            textViewUserName.setOnClickListener {
                startUserActivity(context)
            }
        }
    }

    fun bind(likeDislikeModel: LikeDislikeModel) {
        with (view) {
            textViewUserName.text = likeDislikeModel.username
            textViewStatus.text = likeDislikeModel.status.toString()
            textViewDate.text = likeDislikeModel.date

            if (likeDislikeModel.attachmentImage != null) {
                val attachModelUser = AttachmentModel(likeDislikeModel.attachmentImage)
                loadImage(imageViewUser, attachModelUser.url)
            }

            if (likeDislikeModel.likeDislike == LikeDislike.LIKE) {
                imageViewLike.setImageResource(R.drawable.ic_baseline_thumb_up_light)
            } else {
                imageViewLike.setImageResource(R.drawable.ic_baseline_thumb_down_light)
            }

        }
     }

    private fun startUserActivity(context: Context) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            val username = list[adapterPosition].username
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(PostViewHolder.USERNAME, username)
            context.startActivity(intent)
        }
    }
}