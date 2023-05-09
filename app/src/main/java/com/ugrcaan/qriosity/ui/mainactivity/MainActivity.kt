package com.ugrcaan.qriosity.ui.mainactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.ViewModelProvider
import com.ugrcaan.qriosity.data.repository.BarcodeRepository
import com.ugrcaan.qriosity.databinding.ActivityMainBinding
import com.ugrcaan.qriosity.utils.ImageUtils.processImageForQrCode
import com.ugrcaan.qriosity.viewmodel.feature.FeatureUIViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var bindView : ActivityMainBinding
    private lateinit var featureUIViewModel: FeatureUIViewModel

    private val isClicked : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindView.root)

        featureUIViewModel = ViewModelProvider(this)[FeatureUIViewModel::class.java]
        val barcodeRepository = BarcodeRepository()

        bindView.fabNewQR.setOnClickListener {
            featureUIViewModel.setAnimation(isClicked, bindView.fabNewQR, bindView.fabGallery, bindView.fabCamera, this)
            featureUIViewModel.setFabSize(isClicked, bindView.fabNewQR, this, resources)
            featureUIViewModel.setVisibility(isClicked, bindView.fabNewQR, this)
        }

        bindView.fabNewQR.setOnLongClickListener {
            return@setOnLongClickListener true
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Gallery image selected
            data?.data?.let { uri ->
                // Convert gallery image URI to bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                // Process the bitmap for QR code scanning
                processImageForQrCode(bitmap)
            }
        }
    }
}