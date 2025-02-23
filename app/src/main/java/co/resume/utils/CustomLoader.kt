package co.resume.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import app.craft.myresume.R

class CustomLoader(private val activity: Activity) {

    private var loaderLayout: RelativeLayout? = null
    private var overlayView: View? = null

    fun showLoader() {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        if (overlayView == null) {
            overlayView = View(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                isClickable = true
                isFocusable = true
            }
            rootView.addView(overlayView)
        }

        // Create loader layout if not already created
        if (loaderLayout == null) {
            loaderLayout = LayoutInflater.from(activity).inflate(R.layout.custom_loader, null) as RelativeLayout
            rootView.addView(loaderLayout)
        }

        // Make both loader and overlay visible
        overlayView?.visibility = View.VISIBLE
        loaderLayout?.visibility = View.VISIBLE
    }

    fun hideLoader() {
        // Hide loader and overlay
        overlayView?.visibility = View.GONE
        loaderLayout?.visibility = View.GONE
    }
}
