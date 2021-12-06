package com.example.cappsule.onboardingPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.cappsule.R

class Welcome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.myViewPager)

        val next = view.findViewById<Button>(R.id.next)

        next.setOnClickListener {
            viewPager?.currentItem = 1
        }

        return view
    }
}