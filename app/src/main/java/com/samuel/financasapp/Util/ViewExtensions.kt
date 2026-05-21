package com.samuel.financasapp.Util

import android.view.View
import android.view.animation.AnimationUtils
import com.samuel.financasapp.R


fun View.setAnimateOnClickListener(onClick: () -> Unit) {
    this.setOnClickListener {
        val anim = AnimationUtils.loadAnimation(context, R.anim.click_bounce)
        this.startAnimation(anim)

        this.postDelayed({
            onClick()
        }, 100)
    }
}