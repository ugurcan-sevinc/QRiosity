package com.ugrcaan.qriosity

import android.annotation.SuppressLint
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
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ugrcaan.qriosity.data.SavedLinkRepository
import com.ugrcaan.qriosity.data.local.QriosityDatabase
import com.ugrcaan.qriosity.databinding.ActivityMainBinding
import com.ugrcaan.qriosity.model.SavedLink
import com.ugrcaan.qriosity.utils.ImageScanUtil
import com.ugrcaan.qriosity.viewmodel.MainVM
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val viewModel: MainVM by viewModels {
        MainVM.provideFactory(
            SavedLinkRepository(
                QriosityDatabase.getInstance(applicationContext).savedLinkDao()
            )
        )
    }

    private val savedLinkAdapter: SavedLinkAdapter by lazy {
        SavedLinkAdapter(object : SavedLinkAdapter.SavedLinkListener {
            override fun onOpenLink(savedLink: SavedLink) {
                openLinkInWebView(savedLink.link)
            }

            override fun onDeleteLink(savedLink: SavedLink) {
                confirmDeletion(savedLink)
            }
        })
    }

    private var isClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        isClicked = false

        setupRecyclerView()
        observeSavedLinks()

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

        viewBinding.animationView.setOnClickListener {
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

    private fun setupRecyclerView() {
        viewBinding.savedLinkRecyclerview.layoutManager = LinearLayoutManager(this)
        viewBinding.savedLinkRecyclerview.adapter = savedLinkAdapter
    }

    private fun observeSavedLinks() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedLinks.collect { links ->
                    savedLinkAdapter.submitList(links)
                }
            }
        }
    }

    private fun openRickrollOnYouTube() {
        val videoId = "dQw4w9WgXcQ"
        val youtubeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$videoId"))

        try {
            startActivity(youtubeAppIntent)
        } catch (ex: Exception) {
            startActivity(webIntent)
        }
    }

    override fun onBackPressed() {
        if (viewBinding.webView.visibility == View.VISIBLE) {
            viewBinding.webView.visibility = View.GONE
            viewBinding.fabNewQR.visibility = View.VISIBLE
            viewBinding.webView.loadUrl("about:blank")
        } else {
            super.onBackPressed()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                processImageForQrCode(bitmap)
            }
        }
    }

    private fun processImageForQrCode(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
        val result = ImageScanUtil.decodeQrCode(decodedBitmap)
        if (result == null) {
            Toast.makeText(this, "No QR code found in the image", Toast.LENGTH_LONG).show()
        } else {
            showResultDialog(this, result)
        }
    }

    private fun showResultDialog(context: Context, link: String) {
        val formattedLink = ensureScheme(link)

        AlertDialog.Builder(context)
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
            .setNeutralButton("Open in Browser") { dialog, _ ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(formattedLink)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Intent.createChooser(intent, "Open link with..."))
                dialog.dismiss()
            }
            .setPositiveButton("Kaydet") { dialog, _ ->
                viewModel.saveLink(deriveDisplayName(formattedLink), formattedLink)
                openLinkInWebView(formattedLink)
                Toast.makeText(context, R.string.link_saved, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    private fun setVisibility(isClicked: Boolean, fab: ExtendedFloatingActionButton) {
        fab.visibility = if (isClicked) View.VISIBLE else View.GONE
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun openLinkInWebView(url: String) {
        viewBinding.fabNewQR.visibility = View.GONE
        viewBinding.webView.visibility = View.VISIBLE
        viewBinding.webView.settings.javaScriptEnabled = true
        viewBinding.webView.loadUrl(url)
    }

    private fun ensureScheme(link: String): String {
        if (URLUtil.isValidUrl(link)) return link
        val prefixes = listOf("http://", "https://")
        if (prefixes.any { link.startsWith(it, ignoreCase = true) }) {
            return link
        }
        return "http://$link"
    }

    private fun deriveDisplayName(url: String): String {
        return try {
            val parsed = Uri.parse(url)
            parsed.host ?: url
        } catch (error: Exception) {
            url
        }
    }

    private fun confirmDeletion(savedLink: SavedLink) {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(getString(R.string.delete_confirmation, savedLink.name))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                viewModel.deleteLink(savedLink)
                Toast.makeText(this, R.string.link_deleted, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
