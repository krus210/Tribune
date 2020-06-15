package ru.korolevss.tribune.postadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.korolevss.tribune.R
import ru.korolevss.tribune.model.PostModel

class PostAdapter(var list: MutableList<PostModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM_POST = 0
        private const val ITEM_FOOTER = 1
    }

    var likeBtnClickListener: OnLikeBtnClickListener? = null
    var dislikeBtnClickListener: OnDislikeBtnClickListener? = null

    interface OnLikeBtnClickListener {
        fun onLikeBtnClicked(item: PostModel, position: Int)
    }

    interface OnDislikeBtnClickListener {
        fun onDislikeBtnClicked(item: PostModel, position: Int)
    }

    fun newRecentPosts(newData: List<PostModel>) {
        this.list.clear()
        this.list.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_card, parent, false)
        return when (viewType) {
            ITEM_FOOTER -> FooterViewHolder(
                this,
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_load_more, parent, false)
            )
            else -> PostViewHolder(this, view, list)
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            list.size -> ITEM_FOOTER
            else -> TYPE_ITEM_POST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != list.size) {
            val post = list[position]
            with(holder as PostViewHolder) {
                bind(post)
            }
        }
    }
}