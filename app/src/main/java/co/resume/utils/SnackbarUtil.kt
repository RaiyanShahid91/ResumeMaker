package co.resume.utils

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import app.craft.myresume.R

object SnackbarUtil {

    fun showSnackbar(context: Context, message: String) {
        val rootView: View = (context as? Activity)?.findViewById(android.R.id.content) ?: return

        val snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG)

        val customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar_layout, null)

        val imageView = customView.findViewById<ImageView>(R.id.snackbar_image)
        imageView.setImageResource(R.drawable.ic_info)  // Replace with your image resource

        val textView = customView.findViewById<TextView>(R.id.snackbar_message)
        textView.text = message

        val dismissButton = customView.findViewById<ImageView>(R.id.snackbar_dismiss)
        dismissButton.setOnClickListener {
            snackbar.dismiss()
        }

        val snackbarView = snackbar.view as ViewGroup
        snackbarView.removeAllViews()
        snackbarView.addView(customView)

        val contextResources = context.resources
        val height = contextResources.getDimensionPixelSize(R.dimen.dp_60)
        val layoutParams = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.height = height

        if (rootView is CoordinatorLayout) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.topMargin = 100
            params.bottomMargin = 100
            snackbar.view.layoutParams = params
        } else {
            val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = 100
            params.bottomMargin = 100
            snackbar.view.layoutParams = params
        }

        snackbar.view.setBackgroundColor(context.getColor(android.R.color.transparent))

        snackbar.show()
    }
}
