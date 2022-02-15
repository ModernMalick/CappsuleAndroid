package com.example.cappsule.onboardingPackage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.cappsule.R

lateinit var viewPager: ViewPager2

class Onboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val fragmentList = arrayListOf(
            Welcome(), Explanation(), Start(), PermissionsFragment(), Subscription()
        )

        val adapter = ViewPagerAdapter(fragmentList, this.supportFragmentManager, lifecycle)

        viewPager = findViewById(R.id.myViewPager)

        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
    }
}