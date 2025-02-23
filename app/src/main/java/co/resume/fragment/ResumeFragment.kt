package co.resume.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.craft.myresume.R
import app.craft.myresume.ads.AdsCommon
import app.craft.myresume.databinding.FragmentResumeBinding
import co.resume.adapter.ResumeAdapter
import co.resume.utils.Constants
import co.resume.utils.Constants.Companion.ALL_RESUME
import co.resume.utils.Constants.Companion.SELECTED_RESUME_ID
import co.resume.utils.SnackbarUtil
import com.craft.resumebuilder.resume.ResumeActivity
import com.craft.resumebuilder.tinydb.TinyDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResumeFragment : Fragment() {

    private var binding: FragmentResumeBinding? = null
    private lateinit var allResumes: ArrayList<String>
    private lateinit var tinyDB: TinyDB
    private var resumeAdapter: ResumeAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResumeBinding.inflate(inflater, container, false)
        tinyDB = TinyDB(requireActivity())

        tinyDB.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        updateResume()
        setupRecyclerView()

        return binding?.root
    }

    private fun showShimmer(){
        binding?.nestedScrollView?.visibility = View.GONE
        binding?.shimmerMain?.shimmerLayout?.visibility = View.VISIBLE
        binding?.shimmerMain?.shimmerLayout?.startShimmer()

        requireActivity().window.decorView.postDelayed({
            binding?.shimmerMain?.shimmerLayout?.stopShimmer()
            binding?.shimmerMain?.shimmerLayout?.visibility = View.GONE
        }, 1000)
    }

    private fun setupRecyclerView() {
        binding?.resumeRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        resumeAdapter = ResumeAdapter(
            arrayListOf(),
            tinyDB,
            onViewClick = { openResume(it) },
            onDeleteClick = { deleteResume(it) }
        )
        binding?.resumeRecyclerView?.adapter = resumeAdapter
    }

    private fun updateResume() {
        binding?.layoutNoResume?.visibility = View.GONE
        allResumes = tinyDB.getListString(ALL_RESUME)

        lifecycleScope.launch(Dispatchers.Main) {
            if (allResumes.isNotEmpty()) {
                showShimmer()
                delay(1000)
                binding?.apply {
                    binding?.layoutNoResume?.visibility = View.GONE
                    nestedScrollView.visibility = View.VISIBLE
                }
                resumeAdapter?.updateData(allResumes)
            } else {
                binding?.apply {
                    nestedScrollView.visibility = View.GONE
                    binding?.shimmerMain?.shimmerLayout?.visibility = View.GONE
                    binding?.layoutNoResume?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun openResume(resumeId: String) {
        val intent = Intent(requireActivity(), ResumeActivity::class.java).apply {
            putExtra(Constants.RESUME_ID, resumeId)
        }
        tinyDB.putString(SELECTED_RESUME_ID, resumeId)
        AdsCommon.InterstitialAd(requireActivity(), intent)
    }

    private fun deleteResume(resumeId: String) {
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.prompt_confirm_delete, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.show()

        dialogView.findViewById<Button>(R.id.prompt_button_yes).setOnClickListener {
            val updatedResumes = tinyDB.getListString(ALL_RESUME).apply {
                remove(resumeId)
            }
            tinyDB.putListString(ALL_RESUME, updatedResumes)
            updateResume()
            context?.let { it1 ->
                SnackbarUtil.showSnackbar(
                    it1,
                    getString(R.string.resume_deleted_successfully)
                )
            }

            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.prompt_button_no).setOnClickListener {
            dialog.dismiss()
        }
    }

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            updateResume()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        tinyDB.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        binding = null
    }

    override fun onResume() {
        super.onResume()
        updateResume()
    }
}