package com.ugrcaan.qriosity.viewmodel.feature

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ugrcaan.qriosity.R

class FeatureUIViewModel: ViewModel() {

    fun setVisibility(isClicked: Boolean, fab: FloatingActionButton, activity: Activity) {
        activity.runOnUiThread {
            if(!isClicked){
                fab.visibility = View.VISIBLE
                fab.visibility = View.VISIBLE
            }else {
                fab.visibility = View.INVISIBLE
                fab.visibility = View.INVISIBLE
            }
        }

    }

    fun setAnimation(
        isClicked: Boolean,
        fab: FloatingActionButton,
        extFabGallery: ExtendedFloatingActionButton,
        extFabCamera: ExtendedFloatingActionButton,
        activity: Activity
    ) {
        val scaleUp: Animation by lazy { AnimationUtils.loadAnimation(activity.applicationContext, R.anim.anim_fab_scaleup) }
        val scaleDown: Animation by lazy { AnimationUtils.loadAnimation(activity.applicationContext, R.anim.anim_fab_scaledown) }
        val fromRight: Animation by lazy { AnimationUtils.loadAnimation(activity.applicationContext, R.anim.anim_extfabs_fromright) }
        val toRight: Animation by lazy { AnimationUtils.loadAnimation(activity.applicationContext, R.anim.anim_extfabs_toright) }

        activity.runOnUiThread {
            if(!isClicked){
                extFabGallery.startAnimation(fromRight)
                extFabCamera.startAnimation(fromRight)
                fab.startAnimation(scaleUp)
            }else {
                extFabGallery.startAnimation(toRight)
                extFabCamera.startAnimation(toRight)
                fab.startAnimation(scaleDown)
            }
        }
    }

    fun setFabSize(
        isClicked: Boolean,
        fab: FloatingActionButton,
        activity: Activity,
        resources: Resources
    ){
        activity.runOnUiThread {
            if(!isClicked){
                fab.customSize = resources.getDimensionPixelSize(R.dimen.fabSmallerSize)
                fab.setMaxImageSize(resources.getDimensionPixelSize(R.dimen.fabIconSmallerSize))
                fab.setImageResource(R.drawable.ic_fab_close)
            }else {
                fab.customSize = resources.getDimensionPixelSize(R.dimen.fabDefaultSize)
                fab.setMaxImageSize(resources.getDimensionPixelSize(R.dimen.fabIconDefaultSize))
                fab.setImageResource(R.drawable.ic_fab_qr)
            }
        }
    }

}