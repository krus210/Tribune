package ru.korolevss.tribune.model

data class PostModel(
    val id: Long,
    val userName: String,
    val date: String,
    val text: String,
    val attachmentImage: String,
    val attachmentLink: String?,
    var likes: Int,
    var dislikes: Int,
    var likedByUser: Boolean,
    var dislikedByUser: Boolean,
    val isPostOfUser: Boolean,
    var statusOfUser: UserStatus,
    val attachmentImageUser: String?
) {
    var likeActionPerforming = false
    var dislikeActionPerforming = false

    fun updatePost(updatedModel: PostModel) {
        if (id != updatedModel.id) throw IllegalAccessException("Ids are different")
        likes = updatedModel.likes
        dislikes = updatedModel.dislikes
        likedByUser = updatedModel.likedByUser
        dislikedByUser = updatedModel.dislikedByUser
        statusOfUser = updatedModel.statusOfUser
    }
}