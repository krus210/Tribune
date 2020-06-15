package ru.korolevss.tribune.postadapter

import androidx.recyclerview.widget.DiffUtil
import ru.korolevss.tribune.model.PostModel

class PostDiffUtilCallback(
    private val oldList: MutableList<PostModel>,
    private val newList: MutableList<PostModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldModel = oldList[oldItemPosition]
        val newModel = newList[newItemPosition]
        return oldModel.id == newModel.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldModel = oldList[oldItemPosition]
        val newModel = newList[newItemPosition]
        return oldModel.likedByUser== newModel.likedByUser
                && oldModel.likeActionPerforming == newModel.likeActionPerforming
                && oldModel.likes == newModel.likes
                && oldModel.dislikedByUser== newModel.dislikedByUser
                && oldModel.dislikeActionPerforming == newModel.dislikeActionPerforming
                && oldModel.dislikes == newModel.dislikes
                && oldModel.statusOfUser == newModel.statusOfUser
    }

}
