package ru.korolevss.tribune

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.korolevss.tribune.model.Token

private const val TOKEN_KEY = "TOKEN_KEY"
private const val SHARED_PREF_KEY = "SHARED_PREF"

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