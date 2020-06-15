package ru.korolevss.tribune.dto

import ru.korolevss.tribune.repository.Repository.BASE_URL

class AttachmentModel(val id: String) {
    val url
        get() = "$BASE_URL/api/v1/static/$id.jpeg"
}
