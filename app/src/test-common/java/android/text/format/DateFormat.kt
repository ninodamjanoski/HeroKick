package android.text.format

import java.util.*


/**
 * Created by Nino on 29.08.19
 */
object DateFormat {
    @JvmStatic
    fun format(format: CharSequence, date: Date): CharSequence {
        return date.toString()
    }
}