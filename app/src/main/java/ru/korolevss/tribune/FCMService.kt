package ru.korolevss.tribune

import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class FCMService: FirebaseMessagingService(), CoroutineScope by MainScope() {
}