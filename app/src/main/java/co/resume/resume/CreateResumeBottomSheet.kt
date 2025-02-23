package co.resume.resume

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import app.craft.myresume.R
import app.craft.myresume.databinding.PromptCreateResumeBinding
import co.resume.utils.Constants
import co.resume.utils.SnackbarUtil
import com.craft.resumebuilder.tinydb.TinyDB
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class CreateResumeBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PromptCreateResumeBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PromptCreateResumeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent) // Remove the white background
        }

        binding.promptButtonOk.setOnClickListener {
            handleResumeCreation()
        }

        binding.promptButtonCancel.setOnClickListener {
            dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleResumeCreation() {
        val format = SimpleDateFormat(Constants.YEAR_MONTH_DAY_SEC).format(Date())
        val personName = binding.promptPersonName.text.toString().trim()
        val designation = binding.promptDesignation.text.toString().trim()
        val date = getCurrentDate()

        if (personName.isNotEmpty() && designation.isNotEmpty()) {
            val tinyDB = TinyDB(requireContext())
            val listString =
                tinyDB.getListString(Constants.ALL_RESUME)?.toMutableList() ?: mutableListOf()

            listString.add(format)
            tinyDB.putListString(Constants.ALL_RESUME, listString as ArrayList<String>?)
            tinyDB.putString("$format:${Constants.CREATED_RESUME_DATE}", date)
            tinyDB.putString("$format:name", personName)
            tinyDB.putString("$format:descriptionDesignation", designation)
            tinyDB.putString("$format:time", date)
            tinyDB.putInt("$format:color_one", 41)
            tinyDB.putInt("$format:color_two", 39)
            tinyDB.putInt("$format:font_family", 0)
            tinyDB.putString(
                "$format:objective",
                "I am seeking employment with a company where I can grow professionally and personally."
            )
            tinyDB.putString(
                "$format:declaration",
                "I do hereby confirm that the information given above is true to the best of my knowledge."
            )

            dismiss()

            context?.let {
                SnackbarUtil.showSnackbar(it, getString(R.string.resume_created_successfully))
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.enter_mandatory_details),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDateTime.format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
