package com.raiserdev.photogallery.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.raiserdev.photogallery.databinding.FragmentPhotoPageBinding

class PhotoPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoPageBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }
}