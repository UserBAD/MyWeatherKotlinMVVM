package com.example.myweatherkotlinmvvm.di

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    text: String,
    actionText: String,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    action: (View) -> Unit,
) {
    Snackbar.make(this, text, length).setAction(actionText, action).show()
}