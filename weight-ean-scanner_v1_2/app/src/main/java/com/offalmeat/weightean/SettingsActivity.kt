package com.offalmeat.weightean

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.offalmeat.weightean.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var b: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)

        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)

        b.etPrefixes.setText(prefs.getString("prefixes", "20,21,22,23,24,25,26,27,28,29"))
        b.etScale.setText(prefs.getInt("scale", 1000).toString())
        val mode = prefs.getString("valueMode", "WEIGHT")
        if (mode == "PRICE") b.rbPrice.isChecked = true else b.rbWeight.isChecked = true

        b.btnSave.setOnClickListener {
            prefs.edit()
                .putString("prefixes", b.etPrefixes.text.toString().trim())
                .putInt("scale", b.etScale.text.toString().toIntOrNull() ?: 1000)
                .putString("valueMode", if (b.rbPrice.isChecked) "PRICE" else "WEIGHT")
                .apply()
            finish()
        }
        b.btnCancel.setOnClickListener { finish() }
    }
}
