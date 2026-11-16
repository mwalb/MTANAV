package tz.mtanav.util

import tz.mtanav.model.JobDto

enum class UrgencyLevel {
    URGENT,
    NORMAL,
    EXPIRED
}

data class UrgencyTag(
    val label: String,
    val level: UrgencyLevel
)

object FormattingUtils {

    /**
     * Formats a number with comma separators (e.g. 350000 -> "350,000").
     */
    fun formatAmount(amount: Double?): String {
        if (amount == null) return "0"
        val isNegative = amount < 0
        val absAmount = kotlin.math.abs(amount)
        val longPart = absAmount.toLong()
        val doubleStr = absAmount.toString()
        val decimalPart = if (doubleStr.contains(".") && !doubleStr.endsWith(".0")) {
            "." + doubleStr.substringAfter(".")
        } else ""

        val str = longPart.toString()
        val sb = StringBuilder()
        var count = 0
        for (i in str.length - 1 downTo 0) {
            sb.append(str[i])
            count++
            if (count % 3 == 0 && i != 0) {
                sb.append(',')
            }
        }
        val formattedInt = sb.reverse().toString()
        return (if (isNegative) "-" else "") + formattedInt + decimalPart
    }

    /**
     * Formats currency string with TSh prefix and comma separators (e.g. "TSh 350,000").
     */
    fun formatTsh(amount: Double?): String {
        if (amount == null) return "TSh 0"
        return "TSh ${formatAmount(amount)}"
    }

    /**
     * Formats job compensation display cleanly.
     */
    fun formatJobCompensation(job: JobDto): String {
        val freq = (job.payFrequency ?: "month").lowercase()
        return when {
            job.payAmountMin != null && job.payAmountMax != null ->
                "TSh ${formatAmount(job.payAmountMin)} - ${formatAmount(job.payAmountMax)} / ${freq.uppercase()}"
            job.budgetAmount > 0 ->
                "TSh ${formatAmount(job.budgetAmount)} (${(job.payFrequency ?: "fixed").uppercase()})"
            else -> "Not specified"
        }
    }

    private data class ParsedDate(val year: Int, val month: Int, val day: Int)

    private fun parseIsoDate(dateStr: String?): ParsedDate? {
        if (dateStr.isNullOrBlank()) return null
        val cleanStr = dateStr.trim()
        val regex = Regex("""^(\d{4})-(\d{2})-(\d{2})""")
        val match = regex.find(cleanStr) ?: return null
        val (y, m, d) = match.destructured
        return ParsedDate(y.toInt(), m.toInt(), d.toInt())
    }

    private val MONTH_NAMES = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    /**
     * Converts raw ISO timestamp/date string (e.g., 2026-12-04T15:50:05.057796Z or 2026-12-04)
     * to human-readable date format (e.g., "Dec 4, 2026").
     */
    fun formatDateReadable(dateStr: String?, fallback: String = "Open until filled"): String {
        val parsed = parseIsoDate(dateStr) ?: return dateStr ?: fallback
        if (parsed.month !in 1..12) return dateStr ?: fallback
        val monthName = MONTH_NAMES[parsed.month - 1]
        return "$monthName ${parsed.day}, ${parsed.year}"
    }

    /**
     * Calculates civil epoch days from year, month, day.
     */
    private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val era = (if (y >= 0) y else y - 399) / 400
        val yoe = y - era * 400
        val doy = (153 * (m - 3) + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097L + doe - 719468L
    }

    /**
     * Computes an urgency tag for deadlines or offer expiry dates.
     * Uses current date in system/app (e.g., 2026-11-16 or relative to year 2026).
     */
    fun getUrgencyTag(dateStr: String?, isDeadline: Boolean = true): UrgencyTag? {
        val parsed = parseIsoDate(dateStr) ?: return null
        val targetDays = daysFromCivil(parsed.year, parsed.month, parsed.day)

        // Base current date for calculations (2026-11-16)
        val currentDays = daysFromCivil(2026, 11, 16)
        val diffDays = targetDays - currentDays

        val verb = if (isDeadline) "Closes" else "Ends"
        return when {
            diffDays < 0 -> UrgencyTag("Expired", UrgencyLevel.EXPIRED)
            diffDays == 0L -> UrgencyTag("$verb today", UrgencyLevel.URGENT)
            diffDays == 1L -> UrgencyTag("$verb tomorrow", UrgencyLevel.URGENT)
            diffDays in 2..3 -> UrgencyTag("$verb in $diffDays days", UrgencyLevel.URGENT)
            diffDays in 4..60 -> UrgencyTag("$verb in $diffDays days", UrgencyLevel.NORMAL)
            else -> UrgencyTag(formatDateReadable(dateStr), UrgencyLevel.NORMAL)
        }
    }
}
