package com.kiru.scrollcounter

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView

class ScrollService : AccessibilityService() {

    private var overlay: TextView? = null
    private var lastScrollTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val e = event ?: return
        val pkg = e.packageName?.toString() ?: return

        when (e.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (pkg in Tracker.APPS) {
                    showOverlay()
                } else if (pkg != "com.android.systemui") {
                    hideOverlay()
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                if (pkg in Tracker.APPS) {
                    val now = System.currentTimeMillis()
                    // Satu swipe memicu banyak event; hanya hitung kalau sudah ada jeda.
                    if (now - lastScrollTime > Tracker.GAP_MS) {
                        Tracker.increment(this, pkg)
                    }
                    lastScrollTime = now
                    showOverlay()
                }
            }
        }
    }

    private fun showOverlay() {
        if (overlay != null) {
            updateOverlay()
            return
        }
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val tv = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 18f
            setPadding(32, 16, 32, 16)
            background = GradientDrawable().apply {
                setColor(0xAA000000.toInt())
                cornerRadius = 48f
            }
        }
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 24
            y = 200
        }
        try {
            wm.addView(tv, lp)
            overlay = tv
            updateOverlay()
        } catch (ex: Exception) {
            overlay = null
        }
    }

    private fun updateOverlay() {
        overlay?.text = "Scroll: ${Tracker.totalToday(this)}"
    }

    private fun hideOverlay() {
        val v = overlay ?: return
        try {
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(v)
        } catch (ex: Exception) {
        }
        overlay = null
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        hideOverlay()
        super.onDestroy()
    }
}
