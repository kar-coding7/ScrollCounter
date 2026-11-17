package com.kiru.scrollcounter

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Tracker {
    // package name -> nama tampil
    val APPS = mapOf(
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok",
        "com.instagram.android" to "Instagram",
        "com.google.android.youtube" to "YouTube"
    )

    // Jeda minimal antar "swipe" (ms). Kalau hitungan terlalu banyak/sedikit, ubah angka ini.
    const val GAP_MS = 350L

    private fun today(): String =
        SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())

    private fun prefs(c: Context) =
        c.getSharedPreferences("scroll", Context.MODE_PRIVATE)

    fun appNames(): Set<String> = APPS.values.toSet()

    fun increment(c: Context, pkg: String) {
        val name = APPS[pkg] ?: return
        val key = "${today()}_$name"
        val p = prefs(c)
        p.edit().putInt(key, p.getInt(key, 0) + 1).apply()
    }

    fun countFor(c: Context, name: String): Int =
        prefs(c).getInt("${today()}_$name", 0)

    fun totalToday(c: Context): Int =
        appNames().sumOf { countFor(c, it) }

    fun resetToday(c: Context) {
        val e = prefs(c).edit()
        appNames().forEach { e.remove("${today()}_$it") }
        e.apply()
    }
}
