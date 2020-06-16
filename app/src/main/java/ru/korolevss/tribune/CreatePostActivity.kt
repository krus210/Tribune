package ru.korolevss.tribune

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.launch
import ru.korolevss.tribune.repository.Repository
import java.io.IOException


class CreatePostActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val GALLERY_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        setSupportActionBar(my_toolbar_create_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        imageViewAddImage.setOnClickListener {
            dispatchTakePictureIntent()
        }

        buttonCreatePost.setOnClickListener {
            val textContent = editTextEnterContent.text.toString()
            val link = editTextEnterLink.text.toString()
            when {
                textContent.isEmpty() -> {
                    Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show()
                }
                imageViewAddImage.isEnabled -> {
                    Toast.makeText(this, R.string.dont_add_photo, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    lifecycleScope.launch {
                        try {
                            switchDeterminateBar(true)
                            val attachmentImage = getAttachModel(this@CreatePostActivity)!!
                            val response = Repository.createPost(textContent, attachmentImage, link)
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@CreatePostActivity,
                                    R.string.create_post_is_successful,
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@CreatePostActivity,
                                    R.string.create_post_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: IOException) {
                            Toast.makeText(
                                this@CreatePostActivity,
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarCreatePost.isVisible = true
            buttonCreatePost.isEnabled = false
            switchAddPhotoFromGallery.isEnabled = false
        } else {
            determinateBarCreatePost.isVisible = false
            buttonCreatePost.isEnabled = true
            switchAddPhotoFromGallery.isEnabled = true
        }
    }

    private fun dispatchTakePictureIntent() {
        if (switchAddPhotoFromGallery.isChecked) {
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
            switchDeterminateBar(true)
            lifecycleScope.launch {
                try {
                    switchDeterminateBar(true)
                    val imageUploadResult = Repository.upload(it)
                    if (imageUploadResult.isSuccessful) {
                        Toast.makeText(
                            this@CreatePostActivity,
                            R.string.photo_upload_is_successful,
                            Toast.LENGTH_SHORT
                        ).show()
                        imageViewAddImage.isEnabled = false
                        imageViewAddImage.foreground =
                            getDrawable(R.drawable.ic_baseline_add_a_photo_light)
                        val attachmentModel = imageUploadResult.body()!!
                        savedAttachModel(attachmentModel.id, this@CreatePostActivity)
                    } else {
                        Toast.makeText(
                            this@CreatePostActivity,
                            R.string.photo_upload_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@CreatePostActivity,
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