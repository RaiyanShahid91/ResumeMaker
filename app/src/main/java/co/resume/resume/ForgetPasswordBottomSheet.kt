package co.resume.resume

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import app.craft.myresume.databinding.LayoutPasswordBinding
import co.resume.utils.FirebaseAuthRepository
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ForgetPasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LayoutPasswordBinding? = null
    private val binding get() = _binding!!
    private var userEmail: String? = null

    companion object {
        private const val ARG_EMAIL = "arg_email"

        fun newInstance(email: String): ForgetPasswordBottomSheet {
            val fragment = ForgetPasswordBottomSheet()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userEmail = arguments?.getString(ARG_EMAIL)
        if (userEmail.isNullOrEmpty()){
            userEmail = ""
            binding.edtEmail.isEnabled = true
        } else{
            binding.edtEmail.setText(userEmail)
            binding.edtEmail.isEnabled = false
        }

        dialog?.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent) // Remove the white background
        }

        binding.promptButtonOk.setOnClickListener {
            val currentPassword = binding.edtCurrentPassword.text.toString().trim()
            val newPassword = binding.edtNewPassword.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            FirebaseAuthRepository().changePasswordWithVerification(
                currentPassword,
                newPassword,
                userEmail.toString(),
            ) { success, message ->
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Password changed successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
