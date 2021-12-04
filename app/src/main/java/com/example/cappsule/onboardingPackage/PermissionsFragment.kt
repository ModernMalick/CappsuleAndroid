package com.example.cappsule.onboardingPackage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.example.cappsule.MainActivity
import com.example.cappsule.R
import com.example.cappsule.toaster

class PermissionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_permissions, container, false)

        activity?.findViewById<ViewPager2>(R.id.myViewPager)

        val start = view.findViewById<Button>(R.id.proceed)
        val buttonGrantCamera = view.findViewById<Button>(R.id.buttonGrantCamera)
        val buttonGrantLocation = view.findViewById<Button>(R.id.buttonGrantLocation)

        buttonGrantCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    7
                )
            }
        }

        buttonGrantLocation.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    10
                )
            }
        }

        start.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_DENIED)
                &&
                (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_DENIED)
            ) {
                onBoardingFinished()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                toaster(requireContext(), requireContext().getString(R.string.NeedsPermissions))
            }
        }
        return view
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("min_temp", "20").apply()
    }
}