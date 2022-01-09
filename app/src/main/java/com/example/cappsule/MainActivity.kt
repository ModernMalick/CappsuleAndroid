package com.example.cappsule
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cappsule.fragment.Home
import com.example.cappsule.fragment.Settings
import com.example.cappsule.fragment.Wardrobe
import com.example.cappsule.onboardingPackage.Onboarding
import com.google.android.material.navigation.NavigationBarView


class MainActivity : AppCompatActivity() {

    private var selectedFragment: Fragment = Home()
    private var currentFragmentName: String = "Home"
    private lateinit var moreButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!onBoardingFinished()){
            startActivity(Intent(applicationContext, Onboarding::class.java))
            finish()
        }

        val bottomNavigationView = findViewById<NavigationBarView>(R.id.myBottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem -> onNavigationItemSelected(item) }
        bottomNavigationView.menu.getItem(0).isChecked = true
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, selectedFragment).commit()
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                if(currentFragmentName != "Home"){
                    selectedFragment = Home()
                    currentFragmentName = "Home"
                } else {
                    toaster(this, resources.getString(R.string.AlreadyOnPage))
                }
            }
            R.id.wardrobe -> {
                if(currentFragmentName != "Wardrobe"){
                    selectedFragment = Wardrobe()
                    currentFragmentName = "Wardrobe"
                } else {
                    toaster(this, resources.getString(R.string.AlreadyOnPage))
                }
            }
            R.id.menu_settings -> {
                if(currentFragmentName != "Settings"){
                    selectedFragment = Settings()
                    currentFragmentName = "Settings"
                } else {
                    toaster(this, resources.getString(R.string.AlreadyOnPage))
                }
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, selectedFragment).commit()
        return true
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPreferences = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }
}