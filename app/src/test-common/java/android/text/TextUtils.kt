package android.text


/**
 * Created by Nino on 29.08.19
 */
object TextUtils {

    @JvmStatic
    fun isEmpty(str: CharSequence?) = str == null || str.isEmpty()
}