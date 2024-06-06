package com.dicoding.syarifh.storyapp.view.addstory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.dicoding.syarifh.storyapp.data.response.CreateResponse
import com.dicoding.syarifh.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.syarifh.storyapp.retrofit.ApiConfig
import com.dicoding.syarifh.storyapp.retrofit.ApiService
import com.dicoding.syarifh.storyapp.view.ViewModelFactory
import com.dicoding.syarifh.storyapp.view.main.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.previewImage.setImageBitmap(imageBitmap)
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                this.selectedImageUri = result.data?.data!!
                binding.previewImage.setImageURI(selectedImageUri)
            }
        }

        binding.galery.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(pickPhotoIntent)
        }

        binding.upload.setOnClickListener {
            if (selectedImageUri == null) {
                showToast("Image is empty")
            } else {
                viewModel.getSession().observe(this, Observer { user ->
                    val token = user.token
                    uploadStory(token)
                })
            }
        }
    }

    private fun createCustomTempFile(context: Context): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun uploadStory(token: String) {
        selectedImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.textStory.text.toString()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService(this@AddStoryActivity)
                    val response = apiService.upload("Bearer $token", multipartBody, requestBody).awaitResponse()
                    if (response.isSuccessful) {
                        val successResponse = response.body()
                        showToast(successResponse?.message ?: "Success")
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, CreateResponse::class.java)
                        showToast(errorResponse?.message + " Failed")
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                    showToast("An error occurred: ${e.message}")
                } finally {
                    showLoading(false)
                }
            }
        }
    }
}