package com.offalmeat.weightean

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.offalmeat.weightean.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var b: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = HistoryAdapter(loadHistory())
        b.recycler.layoutManager = LinearLayoutManager(this)
        b.recycler.adapter = adapter

        b.btnClear.setOnClickListener {
            val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
            prefs.edit().remove("history").apply()
            adapter.update(emptyList())
        }
    }

    private fun loadHistory(): List<HistoryRow> {
        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        val raw = prefs.getString("history", "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split("\n".toRegex()).filter { it.isNotBlank() }.mapNotNull { line ->
            val parts = line.split("\t".toRegex())
            if (parts.size < 6) null else {
                HistoryRow(
                    ts = parts[0],
                    code = parts[1],
                    prefix = parts[2],
                    plu = parts[3],
                    weight = parts[4].ifBlank { null },
                    price = parts[5].ifBlank { null }
                )
            }
        }.reversed()
    }
}
