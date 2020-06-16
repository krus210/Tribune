package ru.korolevss.tribune.dto

data class PostRequestDto (
    val text: String,
    val attachmentImage: String,
    val attachmentLink: String?
)