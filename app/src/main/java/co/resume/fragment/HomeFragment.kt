package co.resume.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.craft.myresume.R
import app.craft.myresume.ads.AdsCommon
import app.craft.myresume.databinding.FragmentHomeBinding
import app.craft.myresume.databinding.LayoutTipsBinding
import co.resume.resume.CreateResumeBottomSheet
import co.resume.utils.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())

        binding.tipLayout.setOnClickListener { tipsBottom() }
        loadBannerAds()
        setUpCard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun tipsBottom() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)

        val binding = LayoutTipsBinding.inflate(bottomSheetDialog.layoutInflater)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        binding.okThanksBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun loadBannerAds() {
        AdsCommon.RegulerBigNative(
            requireContext(),
            binding.AdmobNativeFrame,
            binding.nativeAdContainer,
            binding.maxNativeAdLayout
        )
    }

    private fun setUpCard() {
        binding.createResume.setOnClickListener {
            val bottomSheet = CreateResumeBottomSheet()
            activity?.let { bottomSheet.show(it.supportFragmentManager, bottomSheet.tag) }
        }

        binding.myResume.setOnClickListener {
            findNavController().navigate(R.id.resumeFragment)
            val bottomNav =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.resumeFragment
        }
    }
}