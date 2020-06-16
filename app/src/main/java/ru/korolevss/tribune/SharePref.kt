package ru.korolevss.tribune

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.korolevss.tribune.model.Token

private const val TOKEN_KEY = "TOKEN_KEY"
private const val SHARED_PREF_KEY = "SHARED_PREF"
private const val ATTACH_MODEL_KEY = "ATTACH_MODEL_KEY"

fun saveToken(token: Token?, context: Context) {
    val sharedPref = context.getSharedPreferences(
        SHARED_PREF_KEY,
        Context.MODE_PRIVATE
    )
    sharedPref.edit {
        putString(
            TOKEN_KEY,
            token?.token
        )
    }
}

fun getToken(context: Context): String? =
    context
        .getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        .getString(TOKEN_KEY, null)

fun savedAttachModel(attachModelId: String?, context: Context) {
    val sharedPref = context.getSharedPreferences(
        SHARED_PREF_KEY,
        Context.MODE_PRIVATE
    )
    sharedPref.edit {
        putString(
            ATTACH_MODEL_KEY,
            attachModelId
        )
    }
}

fun getAttachModel(context: Context): String? =
    context
        .getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        .getString(ATTACH_MODEL_KEY, null)