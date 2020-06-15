package ru.korolevss.tribune.model

data class UserModel(
    val id: Long,
    val name: String,
    val attachmentImage: String?,
    val status: UserStatus,
    val token: String?,
    val readOnly: Boolean
)
enum class UserStatus {
    NORMAL,
    PROMOTER,
    HATER
}