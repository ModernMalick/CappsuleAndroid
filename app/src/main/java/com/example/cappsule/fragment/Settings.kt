package com.example.cappsule.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.cappsule.R
import com.example.cappsule.toaster
import com.google.android.play.core.review.ReviewManagerFactory

class Settings : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var prefWeatherMinLightTemp: Int = 0
    private lateinit var minTempText: EditText
    private lateinit var unit: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        val more = view.findViewById<TextView>(R.id.more)
        more.setOnClickListener{
            val webpage: Uri = Uri.parse("https://modernmalick.com")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            requireActivity().startActivity(intent)
        }
        val rate = view.findViewById<TextView>(R.id.rate)
        rate.setOnClickListener{
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { _ ->
                    }
                } else {
                    // There was some problem, log or handle the error code.
                    toaster(requireContext(), getString(R.string.Unavailable))
                }
            }
        }
        val credits = view.findViewById<TextView>(R.id.ill)
        credits.setOnClickListener{
            val webpage: Uri = Uri.parse("https://storyset.com/")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            requireActivity().startActivity(intent)
        }
        val creditsIcons = view.findViewById<TextView>(R.id.icons)
        creditsIcons.setOnClickListener{
            val webpage: Uri = Uri.parse("https://flaticon.com/")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            requireActivity().startActivity(intent)
        }
        unit = view.findViewById(R.id.settingsUnit)

        if(!sharedPreferences.contains("weather_unit")){
            sharedPreferences.edit().putBoolean("weather_unit", true).apply()
            unit.text = "°C"
        }

        if(!sharedPreferences.contains("min_temp")){
            sharedPreferences.edit().putString("min_temp", "15").apply()
        }

        val prefWeatherUnitMetricStatus = sharedPreferences.getBoolean("weather_unit", true)

        if(!prefWeatherUnitMetricStatus){
            unit.text = "°F"
        } else {
            unit.text = "°C"
        }

        prefWeatherMinLightTemp = sharedPreferences.getString("min_temp", "0")?.toInt() ?: 0

        val switch = view.findViewById<Switch>(R.id.metricSwitch)

        switch.isChecked = prefWeatherUnitMetricStatus

        switch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            sharedPreferences.edit().putBoolean("weather_unit", isChecked).apply()
            if(isChecked){
                unit.text = "°C"
            } else {
                unit.text = "°F"
            }
        }

        minTempText = view.findViewById(R.id.minTemp)

        minTempText.setText(prefWeatherMinLightTemp.toString())

        minTempText.doOnTextChanged { _, _, _, _ ->
            sharedPreferences.edit().putString("min_temp", minTempText.text.toString()).apply()
        }

        return view
    }
}