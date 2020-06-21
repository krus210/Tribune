package ru.korolevss.tribune.model

data class LikeDislikeModel(
    val date: String,
    val userId: Long,
    val username: String,
    val status: UserStatus,
    val likeDislike: LikeDislike,
    val attachmentImage: String?
)

enum class LikeDislike {
    LIKE,
    DISLIKE
}