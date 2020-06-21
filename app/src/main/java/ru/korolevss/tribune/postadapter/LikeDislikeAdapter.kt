package ru.korolevss.tribune.postadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.korolevss.tribune.R
import ru.korolevss.tribune.model.LikeDislikeModel
import ru.korolevss.tribune.model.PostModel

class LikeDislikeAdapter(var list: MutableList<LikeDislikeModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun refresh(newData: List<LikeDislikeModel>) {
        this.list.clear()
        this.list.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.like_dislike_card, parent, false)
        return LikeDislikeViewHolder(this, view, list)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val likeDislikeModel = list[position]
        with(holder as LikeDislikeViewHolder) {
            bind(likeDislikeModel)
        }
    }
}