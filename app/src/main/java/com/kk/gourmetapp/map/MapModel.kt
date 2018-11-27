package com.kk.gourmetapp.map

import android.content.Context
import android.content.SharedPreferences
import com.kk.gourmetapp.util.PreferenceUtil

class MapModel(context: Context): MapContract.Model {

    private var mContext: Context = context

    /**
     * Preferenceから住所を取得する
     */
    override fun loadGurunaviLatitude(): Long {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        return preference.getLong(PreferenceUtil.KEY_GURUNAVI_LATITUDE, 0)
    }

    override fun loadGurunaviLongitude(): Long {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        return preference.getLong(PreferenceUtil.KEY_GURUNAVI_LONGITUDE, 0)
    }

    override fun clearGurunaviAddress() {
    }
}