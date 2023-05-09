package com.ugrcaan.qriosity.viewmodel

import androidx.lifecycle.ViewModel
import com.ugrcaan.qriosity.data.repository.BarcodeRepository


class MainActivityViewModel : ViewModel() {
    private val barcodeRepository = BarcodeRepository()

    fun onCameraButtonClick(callback: (String) -> Unit) {
        barcodeRepository.scanBarcode { result ->
            if (result == null) {
                callback("No QR code found in the image")
            } else {
                // Handle the QR code result
                callback(result)
            }
        }
    }
}