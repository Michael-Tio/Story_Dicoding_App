package com.michael.mystoryapplication.ui.create

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.michael.mystoryapplication.R
import com.michael.mystoryapplication.createCustomTempFile
import com.michael.mystoryapplication.databinding.ActivityCreateStoryBinding
import com.michael.mystoryapplication.reduceFileImage
import com.michael.mystoryapplication.ui.home.ListStoryActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class CreateStoryActivity : AppCompatActivity() {

    private var _binding: ActivityCreateStoryBinding? = null
    private val binding get() = _binding!!

    private var getImgFile: File? = null

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_story)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.buttonCamera.setOnClickListener{
            startTakePhoto()
        }
        binding.buttonGallery.setOnClickListener{
            startGallery()
        }

        binding.buttonAdd.setOnClickListener{
            val desc = binding.edAddDescription.text.toString()

            if (getImgFile != null){
                if (desc.isNotEmpty()){
                    val description = desc.toRequestBody("text/plain".toMediaType())
                    val reducedImage = reduceFileImage(getImgFile!!)
                    val requestImageFile = reducedImage.asRequestBody("image/jpeg".toMediaType())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        getImgFile!!.name,
                        requestImageFile
                    )

                    val createViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[CreateViewModel::class.java]
                    createViewModel.addStory(ListStoryActivity.USER_TOKEN!!, description, imageMultipart)
                    showLoading(true)

                    createViewModel.infoError.observe(this){
                        if (it){
                            showLoading(false)
                            Toast.makeText(this, createViewModel.errorMessage.toString(), Toast.LENGTH_SHORT).show()
                        }
                        else{
                            showLoading(false)

                            val intentList = Intent(this@CreateStoryActivity, ListStoryActivity::class.java)
                            startActivity(intentList)

                            finish()
                        }
                    }
                }
                else{
                    Toast.makeText(this, getString(R.string.description_warning), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, getString(R.string.photo_warning), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var currentPhotoPath: String
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@CreateStoryActivity,
                getString(R.string.authority),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_pic))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getImgFile = file
                binding.ivCreatePhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                getImgFile = uriToFile(uri, this@CreateStoryActivity)
                binding.ivCreatePhoto.setImageURI(uri)
            }
        }
    }

    private fun showLoading(state: Boolean) { binding.progressBarCreate.visibility = if (state) View.VISIBLE else View.GONE }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}