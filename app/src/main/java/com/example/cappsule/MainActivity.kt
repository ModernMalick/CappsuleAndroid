package com.example.cappsule
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.cappsule.onboardingPackage.Onboarding
import com.example.cappsule.dialog.MoreDialog
import com.example.cappsule.fragment.Home
import com.example.cappsule.fragment.Outfits
import com.example.cappsule.fragment.Settings
import com.example.cappsule.fragment.Wardrobe
import com.google.android.material.navigation.NavigationBarView
import java.util.*


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
        moreButton = findViewById(R.id.moreButton)
        moreButton.setOnClickListener{
            val moreDialog = MoreDialog()
            moreDialog.show(supportFragmentManager, "MOREDIALOG")
        }

        createNotificationChannel()
        val intent = Intent(this, NotificationBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val triggerTime: Long = if (System.currentTimeMillis() > calendar.timeInMillis) {
            calendar.timeInMillis + AlarmManager.INTERVAL_DAY
        } else {
            calendar.timeInMillis
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, 0, pendingIntent)
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
            R.id.menu_outfit -> {
                if(currentFragmentName != "Outfits"){
                    selectedFragment = Outfits()
                    currentFragmentName = "Outfits"
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NOTIFCHANNEL"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("dailyNotifier", name, importance).apply {
                description = name
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPreferences = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }
}