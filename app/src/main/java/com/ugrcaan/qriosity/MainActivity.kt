package com.ugrcaan.qriosity

import SavedLinkAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ugrcaan.qriosity.databinding.ActivityMainBinding
import com.ugrcaan.qriosity.model.SavedLink
import com.ugrcaan.qriosity.utils.ImageScanUtil
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private var isClicked: Boolean = false

    private val galleryLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { processImageForQrCode(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        isClicked = false

        val savedLinks = listOf(
            SavedLink("Sopung Menu", "https://sopungmenu.com/izmir"),
            SavedLink("Bolemo", "https://bolemo.weebly.com/"),
            // Add more SavedLink objects here
        )

        // Set the layout manager and adapter for the RecyclerView
        viewBinding.savedLinkRecyclerview.layoutManager = LinearLayoutManager(this)
        viewBinding.savedLinkRecyclerview.adapter = SavedLinkAdapter(savedLinks,viewBinding.webView,viewBinding.fabNewQR)

        viewBinding.fabNewQR.setOnClickListener {
            isClicked = !isClicked
            setAnimation(
                isClicked,
                viewBinding.fabNewQR,
                viewBinding.fabGallery,
                viewBinding.fabCamera,
                this
            )
            setFabSize(isClicked, viewBinding.fabNewQR, resources)
            setVisibility(isClicked, viewBinding.fabCamera)
            setVisibility(isClicked, viewBinding.fabGallery)
        }

        viewBinding.animationView.setOnClickListener{
            openRickrollOnYouTube()
        }

        viewBinding.fabCamera.setOnClickListener {
            onCameraButtonClick()
        }

        viewBinding.fabGallery.setOnClickListener {
            onGalleryButtonClick()
        }

        viewBinding.fabNewQR.setOnLongClickListener {
            if (!isClicked) {
                onCameraButtonClick()
            }
            return@setOnLongClickListener true
        }
    }

    private fun openRickrollOnYouTube() {
        val videoId = "dQw4w9WgXcQ" // Rick Astley - Never Gonna Give You Up
        val youtubeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$videoId"))

        try {
            startActivity(youtubeAppIntent)
        } catch (ex: Exception) {
            // If the YouTube app is not installed or an error occurred, open the video in the web browser
            startActivity(webIntent)
        }
    }

    override fun onBackPressed() {
        if (viewBinding.webView.visibility == View.VISIBLE) {
            viewBinding.webView.visibility = View.GONE
            viewBinding.fabNewQR.visibility = View.VISIBLE
        } else {
            // Otherwise, proceed with normal back button behavior
            super.onBackPressed()
        }
    }

    private fun processImageForQrCode(uri: Uri) {
        val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

        if (bitmap == null) {
            Toast.makeText(this, "Unable to read image", Toast.LENGTH_LONG).show()
            return
        }

        // Convert bitmap to byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        // Decode byte array to bitmap with RGB_565 format for QR code scanning
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
        // Start QR code scanning with the decoded bitmap
        val result = ImageScanUtil.decodeQrCode(decodedBitmap)
        if (result == null) {
            Toast.makeText(this, "No QR code found in the image", Toast.LENGTH_LONG).show()
        } else {
            // Handle the QR code result
            showResultDialog(this, result)
        }
    }


    private fun showResultDialog(context: Context, link: String) {
        val domainExtensions = listOf(".com", ".net", ".org", ".edu", ".gov", ".co.uk", ".ca", ".au", ".us", ".info", ".biz", ".io", ".org.uk", ".online", ".xyz")
        var formattedLink = link

        if (domainExtensions.any { formattedLink.contains(it) }) {
            if (!formattedLink.startsWith("http://") && !formattedLink.startsWith("https://")) {
                formattedLink = "http://$formattedLink"
            }
        }


        val alertDialog = AlertDialog.Builder(context)
            .setTitle("QR Code Result")
            .setMessage(formattedLink)
            .setNegativeButton("Copy Link") { dialog, _ ->
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Link", formattedLink)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setPositiveButton("Open in Browser") { dialog, _ ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(formattedLink)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Intent.createChooser(intent, "Open link with..."))
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
        alertDialog.show()
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            showResultDialog(this, result.contents)
        }
    }

    private fun onCameraButtonClick() {
        val options = ScanOptions()
        options.setPrompt("Scan a barcode")
        options.setCameraId(0)
        options.captureActivity = CaptureActivityPortrait::class.java
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }

    private fun onGalleryButtonClick() {
        galleryLauncher.launch("image/*")
    }

    private fun setVisibility(isClicked: Boolean, fab: ExtendedFloatingActionButton) {
        if (isClicked) {
            fab.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE
        } else {
            fab.visibility = View.GONE
            fab.visibility = View.GONE
        }

    }

    private fun setAnimation(
        isClicked: Boolean,
        fab: FloatingActionButton,
        extFabGallery: ExtendedFloatingActionButton,
        extFabCamera: ExtendedFloatingActionButton,
        activity: Activity
    ) {
        val scaleUp: Animation by lazy {
            AnimationUtils.loadAnimation(
                activity.applicationContext,
                R.anim.anim_mainfab_scaleup
            )
        }
        val scaleDown: Animation by lazy {
            AnimationUtils.loadAnimation(
                activity.applicationContext,
                R.anim.anim_mainfab_scaledown
            )
        }
        val rotateToCloseIcon: Animation by lazy {
            AnimationUtils.loadAnimation(
                activity.applicationContext,
                R.anim.anim_mainfab_iconrotate
            )
        }
        val returnToAddIcon: Animation by lazy {
            AnimationUtils.loadAnimation(
                activity.applicationContext,
                R.anim.anim_mainfab_icon_reverse
            )
        }
        val fromRight: Animation by lazy {
            AnimationUtils.loadAnimation(
                activity.applicationContext,
                R.anim.anim_extfabs_fromright
            )
        }
        val toRight: Animation by lazy {
            AnimationUtils.loadAnimation(
                activity.applicationContext,
                R.anim.anim_extfabs_toright
            )
        }


        if (isClicked) {
            extFabGallery.startAnimation(fromRight)
            extFabCamera.startAnimation(fromRight)
            fab.startAnimation(scaleDown)
            fab.startAnimation(rotateToCloseIcon)

        } else {
            extFabGallery.startAnimation(toRight)
            extFabCamera.startAnimation(toRight)
            fab.startAnimation(scaleUp)
            fab.startAnimation(returnToAddIcon)
        }

    }

    private fun setFabSize(
        isClicked: Boolean,
        fab: FloatingActionButton,
        resources: Resources
    ) {
        if (isClicked) {
            fab.customSize = resources.getDimensionPixelSize(R.dimen.fabSmallerSize)
            fab.setMaxImageSize(resources.getDimensionPixelSize(R.dimen.fabIconSmallerSize))
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_close_24))
        } else {
            fab.customSize = resources.getDimensionPixelSize(R.dimen.fabDefaultSize)
            fab.setMaxImageSize(resources.getDimensionPixelSize(R.dimen.fabIconDefaultSize))
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_add_24))
        }
    }
}