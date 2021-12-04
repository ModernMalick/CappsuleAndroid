package com.example.cappsule.fragment

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.cappsule.R


class Settings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val myEditTextPreference : EditTextPreference? = findPreference("min_temp")
        myEditTextPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        myEditTextPreference?.onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(
                preference: Preference?, newValue: Any
            ): Boolean {
                if (newValue.toString().trim { it <= ' ' } == "") {
                    Toast.makeText(
                        activity, resources.getString(R.string.Empty),
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
                return true
            }
        }
    }
}