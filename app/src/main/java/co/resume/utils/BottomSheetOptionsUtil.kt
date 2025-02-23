package co.resume.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import app.craft.myresume.R
import co.resume.listener.BottomSheetListener
import com.google.android.material.bottomsheet.BottomSheetDialog


object BottomSheetOptionsUtil {
    @SuppressLint("InflateParams")
    fun showBottomSheetDialog(context: Context?, listener: BottomSheetListener?) {
        val bottomSheetDialog = BottomSheetDialog(context!!)

        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_option, null)
        bottomSheetDialog.setContentView(view)

        val editTextView = view.findViewById<TextView>(R.id.edit_txt)
        val deleteTextView = view.findViewById<TextView>(R.id.delete_txt)

        editTextView.setOnClickListener {
            listener?.onEditClicked()
            bottomSheetDialog.dismiss()
        }

        deleteTextView.setOnClickListener { v: View? ->
            listener?.onDeleteClicked()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
}
