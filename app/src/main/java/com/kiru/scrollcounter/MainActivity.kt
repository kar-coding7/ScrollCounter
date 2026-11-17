package com.kiru.scrollcounter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var stats: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (24 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }
        stats = TextView(this).apply { textSize = 20f }

        val btnAcc = Button(this).apply {
            text = "Aktifkan Layanan Aksesibilitas"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        val btnReset = Button(this).apply {
            text = "Reset hitungan hari ini"
            setOnClickListener {
                Tracker.resetToday(this@MainActivity)
                refresh()
            }
        }

        root.addView(stats)
        root.addView(btnAcc)
        root.addView(btnReset)
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val lines = Tracker.appNames().joinToString("\n") {
            "$it: ${Tracker.countFor(this, it)}"
        }
        stats.text = "Scroll hari ini\n\n$lines\n\nTotal: ${Tracker.totalToday(this)}\n"
    }
}
