package ru.korolevss.tribune.postadapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_card.view.*
import ru.korolevss.tribune.LikesDislikesListActivity
import ru.korolevss.tribune.R
import ru.korolevss.tribune.UserActivity
import ru.korolevss.tribune.dto.AttachmentModel
import ru.korolevss.tribune.model.PostModel

class PostViewHolder(
    private val adapter: PostAdapter, private val view: View, var list: MutableList<PostModel>
) : RecyclerView.ViewHolder(view) {

    companion object {
        const val USERNAME = "USERNAME"
        const val POST_ID = "POST_ID"
    }

    init {
        this.clickButtonListener()
    }

    fun bind(post: PostModel) {
        with(view) {
            textViewUserName.text = post.userName
            textViewStatus.text = post.statusOfUser.toString()
            textViewDate.text = post.date
            textViewTextOfPost.text = post.text
            imageViewLink.isVisible = post.attachmentLink != null
            fillCount(textViewNumberLikes, post.likes)
            fillCount(textViewNumberDislikes, post.dislikes)

            val attachModel = AttachmentModel(post.attachmentImage)
            loadImage(imageViewPost, attachModel.url)

            if (post.attachmentImageUser != null) {
                val attachModelUser = AttachmentModel(post.attachmentImageUser)
                loadImage(imageViewUser, attachModelUser.url)
            }
            when {
                post.likeActionPerforming -> {
                    imageViewLike.setImageResource(R.drawable.ic_baseline_thumb_up_white)
                }
                post.likedByUser -> {
                    imageViewLike.setImageResource(R.drawable.ic_baseline_thumb_up_light)
                    textViewNumberLikes.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )
                }
                else -> {
                    imageViewLike.setImageResource(R.drawable.ic_thumb_up_black)
                    textViewNumberLikes.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.tab_indicator_text
                        )
                    )
                }
            }
            when {
                post.dislikeActionPerforming -> {
                    imageViewDislike.setImageResource(R.drawable.ic_baseline_thumb_down_white)
                }
                post.dislikedByUser -> {
                    imageViewDislike.setImageResource(R.drawable.ic_baseline_thumb_down_light)
                    textViewNumberDislikes.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimaryDark
                        )
                    )
                }
                else -> {
                    imageViewDislike.setImageResource(R.drawable.ic_baseline_thumb_down_black)
                    textViewNumberDislikes.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.tab_indicator_text
                        )
                    )
                }
            }
        }
    }

    private fun clickButtonListener() {
        with(view) {
            imageViewLike.setOnClickListener {
                val currentPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[currentPosition]
                    if (item.likeActionPerforming) {
                        Toast.makeText(
                            context,
                            R.string.like_is_performing,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.likeBtnClickListener?.onLikeBtnClicked(item, currentPosition)
                    }
                }
            }
            imageViewDislike.setOnClickListener {
                val currentPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[currentPosition]
                    if (item.dislikeActionPerforming) {
                        Toast.makeText(
                            context,
                            R.string.dislike_is_performing,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.dislikeBtnClickListener?.onDislikeBtnClicked(item, currentPosition)
                    }
                }
            }
            imageViewUser.setOnClickListener {
                context as Activity
                if (context !is UserActivity) {
                    startUserActivity(context)
                }
            }
            textViewUserName.setOnClickListener {
                context as Activity
                if (context !is UserActivity) {
                    startUserActivity(context)
                }
            }
            imageViewLikeDislikeList.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val postId = list[adapterPosition].id
                    val intent = Intent(context, LikesDislikesListActivity::class.java)
                    intent.putExtra(POST_ID, postId)
                    context.startActivity(intent)
                }
            }
            if (imageViewLink.isVisible) {
                imageViewLink.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val link = list[adapterPosition].attachmentLink!!
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    private fun startUserActivity(context: Context) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            val usernameOfPost = list[adapterPosition].userName
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(USERNAME, usernameOfPost)
            context.startActivity(intent)
        }
    }

    private fun fillCount(view: TextView, count: Int) {
        if (count == 0) {
            view.isVisible = false
        } else {
            view.isVisible = true
            view.text = count.toString()
        }
    }
}

fun loadImage(photoImg: ImageView, imageUrl: String) {
    Glide.with(photoImg.context)
        .load(imageUrl)
        .into(photoImg)
}