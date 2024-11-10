package com.elmansidik.dicodingevent.utils

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

object UiHandler {

    fun showLoading(isLoading: Boolean, progressBar: ProgressBar) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun handleError(
        isError: Boolean,
        message: String?,
        errorTextView: TextView,
        refreshButton: MaterialButton,
        recyclerView: RecyclerView,
        onRetry: () -> Unit
    ) {
        if (isError) {
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = message ?: "Terjadi kesalahan"
            refreshButton.visibility = View.VISIBLE
            refreshButton.setOnClickListener {
                onRetry()
            }
            recyclerView.visibility = View.VISIBLE
        } else {
            errorTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }
}