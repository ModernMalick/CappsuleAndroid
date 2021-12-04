package com.example.cappsule.onboardingPackage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.example.cappsule.R

class Start : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.myViewPager)

        val proceed = view.findViewById<Button>(R.id.proceed)

        proceed.setOnClickListener {
            viewPager?.currentItem = 3
        }

        return view
    }
}