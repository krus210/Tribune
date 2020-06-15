package ru.korolevss.tribune

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.model.Token
import ru.korolevss.tribune.repository.Repository
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        startActivityIfAuthorized()

        loginButton.setOnClickListener {
            val username = editTextUsernameLogin.text.toString()
            val password = editTextPasswordLogin.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty_password_login, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    try {
                        switchDeterminateBar(true)
                        val response = Repository.authenticate(username, password)
                        if (response.isSuccessful) {
                            val token: Token? = response.body()
                            saveToken(token, this@LoginActivity)
                            Toast.makeText(
                                this@LoginActivity,
                                R.string.authorization_successful,
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivityIfAuthorized()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                R.string.authorization_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        switchDeterminateBar(false)
                    }
                }
            }
        }

        textButtonDontHaveAccount.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        startActivityIfAuthorized()
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarLogin.isVisible = true
            loginButton.isEnabled = false
            textButtonDontHaveAccount.isEnabled = false
        } else {
            determinateBarLogin.isVisible = false
            loginButton.isEnabled = true
            textButtonDontHaveAccount.isEnabled = true
        }
    }

    private fun startActivityIfAuthorized() {
        val token = getToken(this)
        if (!token.isNullOrEmpty()) {
            Repository.createRetrofitWithAuth(token)
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }
    }

}