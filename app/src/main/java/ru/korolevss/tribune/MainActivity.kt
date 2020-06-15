package ru.korolevss.tribune

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.model.Token
import ru.korolevss.tribune.repository.Repository
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton.setOnClickListener {
            val name = editTextUsername.text.toString()
            val password1 = editTextPassword.text.toString()
            val password2 = editTextConfirm.text.toString()
            if (name.isEmpty() || password1.isEmpty()) {
                Toast.makeText(this, R.string.empty_password_login, Toast.LENGTH_SHORT).show()
            } else if (password1 != password2) {
                Toast.makeText(this, R.string.passwords_not_equal, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch{
                    try {
                        switchDeterminateBar(true)
                        val response = Repository.register(name, password1)
                        if (response.isSuccessful) {
                            val token: Token? = response.body()
                            saveToken(token, this@MainActivity)
                            Toast.makeText(
                                this@MainActivity,
                                R.string.registration_successful,
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.registration_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        switchDeterminateBar(false)
                    }
                }
            }
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarMain.isVisible = true
            registerButton.isEnabled = false
        } else {
            determinateBarMain.isVisible = false
            registerButton.isEnabled = true
        }
    }
}
