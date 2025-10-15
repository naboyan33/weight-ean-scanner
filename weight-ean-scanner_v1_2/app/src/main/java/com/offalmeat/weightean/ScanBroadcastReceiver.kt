package com.offalmeat.weightean

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScanBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.getStringExtra("data")
            ?: intent.getStringExtra("barcode_string")
            ?: intent.getStringExtra("scan_data")
            ?: return
        // Отправляем во фронт-активити через локальный бродкаст
        val forward = Intent(MainActivity.ACTION_SCAN).apply {
            putExtra("code", data.trim())
        }
        context.sendBroadcast(forward)
    }
}
