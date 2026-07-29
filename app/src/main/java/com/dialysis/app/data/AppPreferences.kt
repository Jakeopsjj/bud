package com.dialysis.app.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BpRecord(
    val date: String,
    val systolic: Int,
    val diastolic: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class WeightRecord(
    val date: String,
    val weight: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class WaterRecord(
    val date: String,
    val amount: Int,
    val type: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Medication(
    val id: String,
    val name: String,
    val time: String,
    val taken: Boolean = false,
    val badge: String? = null
)

data class PrepItem(
    val id: String,
    val text: String,
    val checked: Boolean = false
)

data class DialysisSchedule(
    val date: LocalDateTime,
    val location: String,
    val bedNumber: String,
    val confirmed: Boolean = true
)

data class EmergencyContact(
    val name: String,
    val role: String,
    val phone: String,
    val avatarGradient: List<Int>
)

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dialysis_app", Context.MODE_PRIVATE)

    // Water intake
    fun getTodayWaterIntake(): Int {
        val today = LocalDate.now().toString()
        return prefs.getInt("water_$today", 0)
    }

    fun addWaterIntake(amount: Int) {
        val today = LocalDate.now().toString()
        val current = prefs.getInt("water_$today", 0)
        prefs.edit().putInt("water_$today", current + amount).apply()
    }

    fun getWaterTarget(): Int = prefs.getInt("water_target", 1500)
    fun setWaterTarget(target: Int) = prefs.edit().putInt("water_target", target).apply()

    // BP records
    fun addBpRecord(systolic: Int, diastolic: Int) {
        val today = LocalDate.now().toString()
        val records = getBpRecords().toMutableList()
        records.add(BpRecord(today, systolic, diastolic))
        if (records.size > 30) records.removeAt(0)
        saveBpRecords(records)
    }

    fun getBpRecords(): List<BpRecord> {
        val json = prefs.getString("bp_records", null) ?: return getDefaultBpRecords()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                BpRecord(
                    obj.getString("date"),
                    obj.getInt("systolic"),
                    obj.getInt("diastolic"),
                    obj.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            getDefaultBpRecords()
        }
    }

    private fun saveBpRecords(records: List<BpRecord>) {
        val arr = JSONArray()
        records.forEach { r ->
            arr.put(JSONObject().apply {
                put("date", r.date)
                put("systolic", r.systolic)
                put("diastolic", r.diastolic)
                put("timestamp", r.timestamp)
            })
        }
        prefs.edit().putString("bp_records", arr.toString()).apply()
    }

    private fun getDefaultBpRecords(): List<BpRecord> {
        val today = LocalDate.now()
        return (-6..0).map { dayOffset ->
            val date = today.plusDays(dayOffset.toLong())
            val dayNames = arrayOf("一", "二", "三", "四", "五", "六", "日")
            BpRecord(
                date = date.toString(),
                systolic = (130..145).random(),
                diastolic = (82..95).random()
            )
        }
    }

    // Weight records
    fun addWeightRecord(weight: Float) {
        val today = LocalDate.now().toString()
        val records = getWeightRecords().toMutableList()
        records.add(WeightRecord(today, weight))
        if (records.size > 30) records.removeAt(0)
        saveWeightRecords(records)
    }

    fun getWeightRecords(): List<WeightRecord> {
        val json = prefs.getString("weight_records", null) ?: return getDefaultWeightRecords()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                WeightRecord(
                    obj.getString("date"),
                    obj.getDouble("weight").toFloat(),
                    obj.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            getDefaultWeightRecords()
        }
    }

    private fun saveWeightRecords(records: List<WeightRecord>) {
        val arr = JSONArray()
        records.forEach { r ->
            arr.put(JSONObject().apply {
                put("date", r.date)
                put("weight", r.weight.toDouble())
                put("timestamp", r.timestamp)
            })
        }
        prefs.edit().putString("weight_records", arr.toString()).apply()
    }

    private fun getDefaultWeightRecords(): List<WeightRecord> {
        val today = LocalDate.now()
        val base = 62.5f
        return (-7..0).map { dayOffset ->
            val date = today.plusDays(dayOffset.toLong())
            WeightRecord(
                date = date.toString(),
                weight = base + ((-3..3).random() / 10f)
            )
        }
    }

    // Latest vitals for home
    fun getLatestBp(): Pair<Int, Int> {
        val records = getBpRecords()
        return if (records.isNotEmpty()) {
            records.last().systolic to records.last().diastolic
        } else 135 to 85
    }

    fun getLatestWeight(): Float {
        val records = getWeightRecords()
        return if (records.isNotEmpty()) records.last().weight else 62.3f
    }

    fun getHeartRate(): Int = prefs.getInt("heart_rate", 72)
    fun setHeartRate(rate: Int) = prefs.edit().putInt("heart_rate", rate).apply()

    // Medications - user defined, persisted
    fun getMedications(): List<Medication> {
        val today = LocalDate.now().toString()
        val taken = prefs.getStringSet("med_taken_$today", emptySet()) ?: emptySet()
        val saved = prefs.getString("medications_list", null) ?: return emptyList()
        return try {
            val arr = JSONArray(saved)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Medication(
                    obj.getString("id"),
                    obj.getString("name"),
                    obj.getString("time"),
                    obj.getString("id") in taken,
                    if (obj.has("badge")) obj.getString("badge") else null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addMedication(name: String, time: String, badge: String? = null): Medication {
        val meds = getMedicationDefs().toMutableList()
        val id = "med_${System.currentTimeMillis()}"
        meds.add(MedicationDef(id, name, time, badge))
        saveMedicationDefs(meds)
        return Medication(id, name, time, false, badge)
    }

    fun deleteMedication(id: String) {
        val meds = getMedicationDefs().filter { it.id != id }
        saveMedicationDefs(meds)
        // Also clear taken status for this med today
        val today = LocalDate.now().toString()
        val key = "med_taken_$today"
        val taken = (prefs.getStringSet(key, mutableSetOf()) ?: mutableSetOf()).toMutableSet()
        taken.remove(id)
        prefs.edit().putStringSet(key, taken).apply()
    }

    private data class MedicationDef(
        val id: String,
        val name: String,
        val time: String,
        val badge: String? = null
    )

    private fun getMedicationDefs(): List<MedicationDef> {
        val saved = prefs.getString("medications_list", null) ?: return emptyList()
        return try {
            val arr = JSONArray(saved)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                MedicationDef(
                    obj.getString("id"),
                    obj.getString("name"),
                    obj.getString("time"),
                    if (obj.has("badge")) obj.getString("badge") else null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveMedicationDefs(meds: List<MedicationDef>) {
        val arr = JSONArray()
        meds.forEach { m ->
            arr.put(JSONObject().apply {
                put("id", m.id)
                put("name", m.name)
                put("time", m.time)
                if (m.badge != null) put("badge", m.badge)
            })
        }
        prefs.edit().putString("medications_list", arr.toString()).apply()
    }

    fun toggleMedication(id: String) {
        val today = LocalDate.now().toString()
        val key = "med_taken_$today"
        val taken = (prefs.getStringSet(key, mutableSetOf()) ?: mutableSetOf()).toMutableSet()
        if (id in taken) taken.remove(id) else taken.add(id)
        prefs.edit().putStringSet(key, taken).apply()
    }

    // Prep checklist
    fun getPrepItems(): List<PrepItem> {
        val today = LocalDate.now().toString()
        val checked = prefs.getStringSet("prep_checked_$today", emptySet()) ?: emptySet()
        return listOf(
            PrepItem("prep1", "身份证件", "prep1" in checked),
            PrepItem("prep2", "降压药（按医嘱）", "prep2" in checked),
            PrepItem("prep3", "止血带", "prep3" in checked),
            PrepItem("prep4", "透析记录本", "prep4" in checked)
        )
    }

    fun togglePrepItem(id: String) {
        val today = LocalDate.now().toString()
        val key = "prep_checked_$today"
        val checked = (prefs.getStringSet(key, mutableSetOf()) ?: mutableSetOf()).toMutableSet()
        if (id in checked) checked.remove(id) else checked.add(id)
        prefs.edit().putStringSet(key, checked).apply()
    }

    // Dialysis schedule - next dialysis
    fun getNextDialysis(): DialysisSchedule {
        val saved = prefs.getString("next_dialysis", null)
        if (saved != null) {
            try {
                val obj = JSONObject(saved)
                return DialysisSchedule(
                    LocalDateTime.parse(obj.getString("date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    obj.getString("location"),
                    obj.getString("bed"),
                    obj.getBoolean("confirmed")
                )
            } catch (e: Exception) { }
        }
        // Default: next Monday/Wednesday/Friday at 8:00
        val now = LocalDateTime.now()
        val nextDay = when (now.dayOfWeek.value) {
            1 -> if (now.hour < 8) now else now.plusDays(2) // Mon
            2 -> now.plusDays(1) // Tue -> Wed
            3 -> if (now.hour < 8) now else now.plusDays(2) // Wed
            4 -> now.plusDays(1) // Thu -> Fri
            5 -> if (now.hour < 8) now else now.plusDays(3) // Fri -> Mon
            6 -> now.plusDays(2) // Sat -> Mon
            7 -> now.plusDays(1) // Sun -> Mon
            else -> now.plusDays(1)
        }.withHour(8).withMinute(0).withSecond(0).withNano(0)
        return DialysisSchedule(nextDay, "大坪医院血液净化中心", "3层15号机位")
    }

    // Dialysis history
    fun getDialysisHistory(): List<Triple<String, String, String>> {
        return listOf(
            Triple("7月25日", "4h · 脱水2.1kg", "干体重62.5"),
            Triple("7月23日", "4h · 脱水2.3kg", "干体重62.5"),
            Triple("7月21日", "4h · 脱水2.0kg", "干体重62.5"),
            Triple("7月18日", "4h · 脱水2.2kg", "干体重62.5")
        )
    }

    // Emergency contacts
    fun getEmergencyContacts(): List<EmergencyContact> {
        return listOf(
            EmergencyContact("李主任", "主治医生", "13800138001", listOf(0x405AC8FA.toInt(), 0x405856D6)),
            EmergencyContact("王小明", "家属 · 儿子", "13800138002", listOf(0x40FF9500, 0x40FF3B30)),
            EmergencyContact("血透中心", "24小时值班", "01012345678", listOf(0x40FFCC00, 0x40FF9500)),
            EmergencyContact("急救中心", "120", "120", listOf(0x40FF3B30, 0x40FF2D55))
        )
    }

    // Dry weight
    fun getDryWeight(): Float = prefs.getFloat("dry_weight", 62.5f)
    fun setDryWeight(weight: Float) = prefs.edit().putFloat("dry_weight", weight).apply()
}
