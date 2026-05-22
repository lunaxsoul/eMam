//package com.example.emam
//
//import android.content.Context
//import org.json.JSONArray
//import org.json.JSONObject
//
///**
// * Simple SharedPreferences-based store for nutrition data
// * populated by ScanActivity and read by MainActivity.
// *
// * Keys stored:
// *  - calories_consumed   : Int
// *  - ai_note             : String
// *  - nutrients_json      : JSON array of {name, pct}
// *  - lila_values_json    : JSON array of floats
// *  - lila_labels_json    : JSON array of strings
// *  - hb_values_json      : JSON array of floats
// *  - hb_labels_json      : JSON array of strings
// */
//object NutritionStore {
//
//    private const val PREFS = "nutrition_store"
//
//    // ── Write (called by ScanActivity after AI response) ─────────────────────
//
//    fun saveAiNote(ctx: Context, note: String) {
//        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//            .edit().putString("ai_note", note).apply()
//    }
//
//    fun addCalories(ctx: Context, kcal: Int) {
//        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//        val current = prefs.getInt("calories_consumed", 0)
//        prefs.edit().putInt("calories_consumed", current + kcal).apply()
//    }
//
//    fun saveNutrients(ctx: Context, nutrients: List<Pair<String, Int>>) {
//        val arr = JSONArray()
//        nutrients.forEach { (name, pct) ->
//            arr.put(JSONObject().put("name", name).put("pct", pct))
//        }
//        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//            .edit().putString("nutrients_json", arr.toString()).apply()
//    }
//
//    // ── LILA & Hemoglobin (entered manually or from health device) ────────────
//
//    fun addLilaReading(ctx: Context, month: String, value: Float) {
//        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//        val labels = getJsonFloatList(prefs.getString("lila_labels_json", "[]")!!, isFloat = false)
//            .map { it.toString() }.toMutableList()
//        val values = getJsonFloatList(prefs.getString("lila_values_json", "[]")!!).toMutableList()
//        labels.add(month); values.add(value)
//        prefs.edit()
//            .putString("lila_labels_json", JSONArray(labels).toString())
//            .putString("lila_values_json", JSONArray(values).toString())
//            .apply()
//    }
//
//    fun addHbReading(ctx: Context, month: String, value: Float) {
//        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//        val labels = getJsonFloatList(prefs.getString("hb_labels_json", "[]")!!, isFloat = false)
//            .map { it.toString() }.toMutableList()
//        val values = getJsonFloatList(prefs.getString("hb_values_json", "[]")!!).toMutableList()
//        labels.add(month); values.add(value)
//        prefs.edit()
//            .putString("hb_labels_json", JSONArray(labels).toString())
//            .putString("hb_values_json", JSONArray(values).toString())
//            .apply()
//    }
//
//    // ── Read (called by MainActivity) ─────────────────────────────────────────
//
//    fun getAiNote(ctx: Context): String =
//        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//            .getString("ai_note", "Scan makanan untuk mendapatkan catatan nutrisi") ?: ""
//
//    fun getCaloriesConsumed(ctx: Context): Int =
//        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//            .getInt("calories_consumed", 0)
//
//    fun getNutrients(ctx: Context): List<Pair<String, Int>> {
//        val json = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//            .getString("nutrients_json", "[]") ?: "[]"
//        val arr = JSONArray(json)
//        val result = mutableListOf<Pair<String, Int>>()
//        for (i in 0 until arr.length()) {
//            val obj = arr.getJSONObject(i)
//            result.add(Pair(obj.getString("name"), obj.getInt("pct")))
//        }
//        return result
//    }
//
//    fun getLilaData(ctx: Context): Pair<List<String>, List<Float>> {
//        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//        // Default demo data if nothing saved yet
//        val defaultLabels = listOf("Jan","Feb","Mar","Apr","Mei","Jun")
//        val defaultValues = listOf(23.8f, 24.0f, 24.1f, 24.3f, 24.5f, 24.6f)
//        val labelsJson = prefs.getString("lila_labels_json", null)
//        val valuesJson = prefs.getString("lila_values_json", null)
//        if (labelsJson == null) return Pair(defaultLabels, defaultValues)
//        val labels = (0 until JSONArray(labelsJson).length()).map { JSONArray(labelsJson).getString(it) }
//        val values = getJsonFloatList(valuesJson ?: "[]")
//        return Pair(labels, values)
//    }
//
//    fun getHbData(ctx: Context): Pair<List<String>, List<Float>> {
//        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//        val defaultLabels = listOf("Jan","Feb","Mar","Apr","Mei","Jun")
//        val defaultValues = listOf(11.2f, 11.0f, 10.8f, 11.1f, 11.4f, 11.6f)
//        val labelsJson = prefs.getString("hb_labels_json", null)
//        val valuesJson = prefs.getString("hb_values_json", null)
//        if (labelsJson == null) return Pair(defaultLabels, defaultValues)
//        val labels = (0 until JSONArray(labelsJson).length()).map { JSONArray(labelsJson).getString(it) }
//        val values = getJsonFloatList(valuesJson ?: "[]")
//        return Pair(labels, values)
//    }
//
//    fun resetDaily(ctx: Context) {
//        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//            .edit()
//            .putInt("calories_consumed", 0)
//            .putString("ai_note", "")
//            .putString("nutrients_json", "[]")
//            .apply()
//    }
//
//    // ── Private helper ────────────────────────────────────────────────────────
//    private fun getJsonFloatList(json: String, isFloat: Boolean = true): List<Float> {
//        val arr = JSONArray(json)
//        return (0 until arr.length()).map { arr.getDouble(it).toFloat() }
//    }
//}