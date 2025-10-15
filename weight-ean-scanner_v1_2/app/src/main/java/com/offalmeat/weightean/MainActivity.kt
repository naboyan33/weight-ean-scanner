package com.offalmeat.weightean

import android.content.*
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.offalmeat.weightean.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object { const val ACTION_SCAN = "com.offalmeat.weightean.SCAN" }

    private lateinit var b: ActivityMainBinding
    private val uiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val code = intent?.getStringExtra("code") ?: return
            handleScanned(code)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Buttons
        b.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        b.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Keyboard-mode fallback
        b.input.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
                val s = v.text?.toString()?.trim().orEmpty()
                if (s.isNotEmpty()) handleScanned(s)
                v.text = null
                true
            } else false
        }

        showCurrentConfig()
    }

    private fun showCurrentConfig() {
        val cfg = readCfg()
        val text = "Префиксы: ${cfg.allowedPrefixes.joinToString()}  |  Режим: ${cfg.mode}  |  scale=${cfg.scale}"
        b.tvCfg.text = text
    }

    data class Cfg(val allowedPrefixes: Set<String>, val mode: ValueMode, val scale: Int)

    private fun readCfg(): Cfg {
        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        val prefixesCsv = prefs.getString("prefixes", "20,21,22,23,24,25,26,27,28,29")!!
        val modeStr = prefs.getString("valueMode", "WEIGHT")!!
        val scale = prefs.getInt("scale", 1000)
        val set = prefixesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val mode = if (modeStr == "PRICE") ValueMode.PRICE else ValueMode.WEIGHT
        return Cfg(set, mode, scale)
    }

    private fun handleScanned(code: String) {
        try {
            val cfg = readCfg()
            val parsed = BarcodeParser.parse(code, cfg.allowedPrefixes, cfg.mode, cfg.scale)
            val text = buildString {
                appendLine("ШК: ${parsed.raw}")
                appendLine("Префикс: ${parsed.prefix}")
                appendLine("PLU: ${parsed.plu}")
                parsed.weightKg?.let { appendLine("Вес: $it кг") }
                parsed.price?.let { appendLine("Цена: $it") }
            }
            b.result.text = text
            showCurrentConfig()
            saveHistory(parsed)
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveHistory(p: ParsedBarcode) {
        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val weightStr = p.weightKg?.toString() ?: ""
        val priceStr = p.price?.toString() ?: ""
        val line = listOf(now, p.raw, p.prefix, p.plu, weightStr, priceStr).joinToString("\t")
        val raw = prefs.getString("history", "") ?: ""
        val lines = (raw.split("\n".toRegex()).filter { it.isNotBlank() } + line).takeLast(100)
        prefs.edit().putString("history", lines.joinToString("\n")).apply()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(uiReceiver, IntentFilter(ACTION_SCAN))
        showCurrentConfig()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(uiReceiver)
    }
}
