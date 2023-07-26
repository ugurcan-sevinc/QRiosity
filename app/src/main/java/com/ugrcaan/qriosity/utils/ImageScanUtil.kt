package com.ugrcaan.qriosity.utils

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

class ImageScanUtil {
    companion object {
        fun decodeQrCode(bitmap: Bitmap): String? {
            val intArray = IntArray(bitmap.width * bitmap.height)
            // Copy pixel data from the Bitmap to the intArray
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            // Convert the intArray to a BinaryBitmap
            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            // Decode the QR code from the BinaryBitmap
            val reader = MultiFormatReader()
            try {
                val result = reader.decode(binaryBitmap)
                return result.text
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}