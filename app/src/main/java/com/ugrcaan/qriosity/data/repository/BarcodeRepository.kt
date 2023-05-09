package com.ugrcaan.qriosity.data.repository

import android.app.Activity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ugrcaan.qriosity.PortraitScannerActivity

class BarcodeRepository {

    private val scanContract = ScanContract()

    fun scanBarcode(callback: (String?) -> Unit) {
        val barcodeLauncher = activity.registerForActivityResult(scanContract) { result: ScanIntentResult ->
            if (result.contents == null) {
                callback(null)
            } else {
                callback(result.contents)
            }
        }

        val options = ScanOptions()
        options.setPrompt("Scan a barcode")
        options.captureActivity = PortraitScannerActivity::class.java
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }
}