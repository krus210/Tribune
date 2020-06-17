package ru.korolevss.tribune

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.dto.AttachmentModel
import ru.korolevss.tribune.dto.PasswordChangeRequestDto
import ru.korolevss.tribune.model.Token
import ru.korolevss.tribune.postadapter.PostViewHolder
import ru.korolevss.tribune.repository.Repository
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    private companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val GALLERY_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(my_toolbar_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        imageViewAddImageSettings.setOnClickListener {
            dispatchTakePictureIntent()
        }

        lifecycleScope.launch {
            try {
                switchDeterminateBar(true)
                val response = Repository.getMe()
                if (response.isSuccessful) {
                    val user = response.body()!!
                    textViewUserName.text = user.name
                    textViewStatus.text = user.status.toString()
                    if (!user.attachmentImage.isNullOrEmpty()) {
                        val attachmentModel = AttachmentModel(user.attachmentImage)
                        loadImage(imageViewUser, attachmentModel.url)
                    }
                } else {
                    Toast.makeText(
                        this@SettingsActivity,
                        R.string.load_user_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@SettingsActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }
        buttonChangePassword.setOnClickListener {
            val oldPassword = editTextOldPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()
            val confirmPassword = editTextConfirmNewPassword.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, R.string.empty_password, Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, R.string.passwords_not_equal, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch{
                    try {
                        switchDeterminateBar(true)
                        val passwordChangeRequestDto = PasswordChangeRequestDto(oldPassword, newPassword)
                        val response = Repository.changePassword(passwordChangeRequestDto)
                        if (response.isSuccessful) {
                            val token: Token? = response.body()
                            saveToken(token, this@SettingsActivity)
                            Toast.makeText(
                                this@SettingsActivity,
                                R.string.change_password_successful,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@SettingsActivity,
                                R.string.change_password_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@SettingsActivity,
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarSettings.isVisible = true
            switchAddPhotoFromGallerySettings.isEnabled = false
            buttonChangePassword.isEnabled = false
            imageViewAddImageSettings.isEnabled = false
        } else {
            determinateBarSettings.isVisible = false
            switchAddPhotoFromGallerySettings.isEnabled = true
            buttonChangePassword.isEnabled = true
            imageViewAddImageSettings.isEnabled = true
        }
    }

    private fun dispatchTakePictureIntent() {
        if (switchAddPhotoFromGallerySettings.isChecked) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            loadImage(imageBitmap)
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
            loadImage(imageBitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadImage(imageBitmap: Bitmap?) {
        imageBitmap?.let {
            lifecycleScope.launch {
                try {
                    switchDeterminateBar(true)
                    val imageUploadResult = Repository.upload(it)
                    if (imageUploadResult.isSuccessful) {
                        Toast.makeText(
                            this@SettingsActivity,
                            R.string.photo_upload_is_successful,
                            Toast.LENGTH_SHORT
                        ).show()
                        val attachmentModel = imageUploadResult.body()!!
                        loadImage(imageViewUser, attachmentModel.url)
                        val attachImageToUser = Repository.addImageToUser(attachmentModel)
                        if (!attachImageToUser.isSuccessful) {
                            Toast.makeText(
                                this@SettingsActivity,
                                R.string.attach_image__to_user_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SettingsActivity,
                            R.string.photo_upload_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@SettingsActivity,
                        R.string.connect_to_server_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    switchDeterminateBar(false)
                }
            }
        }
    }

    private fun loadImage(photoImg: ImageView, imageUrl: String) {
        Glide.with(photoImg)
            .load(imageUrl)
            .into(photoImg)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            true
        }
        R.id.action_my_posts -> {
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra(PostViewHolder.USERNAME, getString(R.string.me))
            startActivity(intent)
            finish()
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }
}