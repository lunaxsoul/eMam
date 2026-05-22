//package com.example.emam
//
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.time.temporal.ChronoUnit
//import java.util.Locale
//
///**
// * Helper untuk perhitungan kehamilan:
// *
// * 1. HPL (Hari Perkiraan Lahir) – Naegele's Rule
// *    HPL = HPHT + 280 hari (40 minggu)
// *    atau: bulan +9, hari +7, tahun ±1
// *
// * 2. Usia kehamilan dalam minggu dari HPHT
// *
// * 3. Trimester
// *    - T1: 1–12 minggu
// *    - T2: 13–26 minggu
// *    - T3: 27–40 minggu
// *
// * 4. Status LILA (Kemenkes RI 2015)
// *    - Normal : LILA ≥ 23.5 cm
// *    - KEK    : LILA < 23.5 cm
// *
// * 5. Status Hemoglobin (WHO / Kemenkes)
// *    - Normal : Hb ≥ 11 gr/dl
// *    - Anemia : Hb < 11 gr/dl
// */
//object PregnancyHelper {
//
//    private val OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id"))
//
//    // ── HPL calculation (Naegele's Rule) ─────────────────────────────────────
//    /**
//     * @param hpht Hari Pertama Haid Terakhir
//     * @return HPL as formatted Indonesian date string
//     */
//    fun calculateHpl(hpht: LocalDate): String {
//        val hpl = hpht.plusDays(280)
//        return hpl.format(OUTPUT_FORMATTER)
//    }
//
//    fun calculateHplDate(hpht: LocalDate): LocalDate = hpht.plusDays(280)
//
//    // ── Gestational age in weeks ───────────────────────────────────────────────
//    fun gestationalWeeks(hpht: LocalDate, today: LocalDate = LocalDate.now()): Int {
//        return ChronoUnit.WEEKS.between(hpht, today).toInt().coerceIn(0, 42)
//    }
//
//    // ── Trimester ──────────────────────────────────────────────────────────────
//    enum class Trimester { T1, T2, T3, POSTTERM }
//
//    fun getTrimester(weeks: Int): Trimester = when {
//        weeks <= 12 -> Trimester.T1
//        weeks <= 26 -> Trimester.T2
//        weeks <= 40 -> Trimester.T3
//        else         -> Trimester.POSTTERM
//    }
//
//    fun getTrimesterLabel(weeks: Int): String = when (getTrimester(weeks)) {
//        Trimester.T1       -> "Trimester 1 (Minggu 1–12)"
//        Trimester.T2       -> "Trimester 2 (Minggu 13–26)"
//        Trimester.T3       -> "Trimester 3 (Minggu 27–40)"
//        Trimester.POSTTERM -> "Lewat HPL"
//    }
//
//    // ── Progress percentage (for progress bar) ────────────────────────────────
//    fun pregnancyProgress(weeks: Int): Int = ((weeks.toFloat() / 40f) * 100).toInt().coerceIn(0, 100)
//
//    // ── LILA status ───────────────────────────────────────────────────────────
//    /** @param lila in cm */
//    fun lilaStatus(lila: Float): String = if (lila >= 23.5f) "Normal" else "Berisiko KEK"
//    fun lilaIsNormal(lila: Float): Boolean = lila >= 23.5f
//
//    // ── Hemoglobin status ─────────────────────────────────────────────────────
//    /** @param hb in gr/dl */
//    fun hbStatus(hb: Float): String = when {
//        hb >= 11.0f -> "Normal"
//        hb >= 8.0f  -> "Anemia Ringan-Sedang"
//        else         -> "Anemia Berat"
//    }
//    fun hbIsNormal(hb: Float): Boolean = hb >= 11.0f
//
//    // ── LILA trend delta ──────────────────────────────────────────────────────
//    fun delta(values: List<Float>): Float {
//        if (values.size < 2) return 0f
//        return values.last() - values[values.size - 2]
//    }
//}