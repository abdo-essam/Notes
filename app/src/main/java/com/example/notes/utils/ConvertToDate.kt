import java.text.SimpleDateFormat
import java.util.*

open class ConvertToDate {

    fun convertTimeStampToDate(timeStamp: Long): String {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = timeStamp

        val dateFormat = SimpleDateFormat("MMMM d h:mm a", Locale.ENGLISH)
        return dateFormat.format(cal.time)
    }

}