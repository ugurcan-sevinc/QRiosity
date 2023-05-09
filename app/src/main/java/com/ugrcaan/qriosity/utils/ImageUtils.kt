package com.ugrcaan.qriosity.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.ugrcaan.qriosity.ScannerUtility
import com.ugrcaan.qriosity.model.QrCodeResult
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun processImageForQrCode(bitmap: Bitmap): QrCodeResult {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)

        val result = ScannerUtility.decodeQrCode(decodedBitmap)
        return if (result == null) {
            QrCodeResult(null)
        } else {
            QrCodeResult(result)
        }
    }
}
