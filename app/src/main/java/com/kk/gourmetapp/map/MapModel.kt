package com.kk.gourmetapp.map

import android.content.Context
import android.content.SharedPreferences
import com.kk.gourmetapp.util.PreferenceUtil

class MapModel(context: Context): MapContract.Model {

    private var mContext: Context = context

    /**
     * Preferenceから住所を取得する
     */
    override fun loadGurunaviLatitude(): Double {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        return java.lang.Double.longBitsToDouble(preference.getLong(
            PreferenceUtil.KEY_GURUNAVI_LATITUDE, 0L))
    }

    override fun loadGurunaviLongitude(): Double {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        return java.lang.Double.longBitsToDouble(preference.getLong(
            PreferenceUtil.KEY_GURUNAVI_LONGITUDE, 0L))
    }

    override fun clearGurunaviAddress() {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        preference.edit().putFloat(PreferenceUtil.KEY_GURUNAVI_LATITUDE, 0f).apply()
        preference.edit().putFloat(PreferenceUtil.KEY_GURUNAVI_LONGITUDE, 0f).apply()
    }
}