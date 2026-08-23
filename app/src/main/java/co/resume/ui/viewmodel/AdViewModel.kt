package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.resume.ads.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    val adManager: AdManager
) : ViewModel()
