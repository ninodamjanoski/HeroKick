package com.endumedia.herokick.utils

import android.content.SharedPreferences


/**
 * Created by Nino on 06.10.19
 */
class MockedSharedPrefs : SharedPreferences {

    private val editor = MockEditor()

    private var page: Int? = null

    override fun contains(key: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return page ?: defValue
    }

    override fun getAll(): MutableMap<String, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun edit(): SharedPreferences.Editor {
        return editor
    }

    override fun getLong(key: String?, defValue: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getString(key: String?, defValue: String?): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class MockEditor : SharedPreferences.Editor {
        override fun clear(): SharedPreferences.Editor {
            page = null
            return this
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            page = value
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun commit(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun apply() {
        }

        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}