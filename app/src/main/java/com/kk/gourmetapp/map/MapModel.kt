package com.kk.gourmetapp.map

import android.content.Context
import android.content.SharedPreferences
import com.kk.gourmetapp.util.PreferenceUtil

class MapModel(context: Context): MapContract.Model {

    private var mContext: Context = context

    /**
     * ぐるなびショップの緯度取得
     * @return 緯度
     */
    override fun loadShopLatitude(): Double {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        return java.lang.Double.longBitsToDouble(preference.getLong(
            PreferenceUtil.KEY_SHOP_LATITUDE, 0L))
    }

    /**
     * ぐるなびショップの経度取得
     * @return 経度
     */
    override fun loadShopLongitude(): Double {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        return java.lang.Double.longBitsToDouble(preference.getLong(
            PreferenceUtil.KEY_SHOP_LONGITUDE, 0L))
    }

    /**
     * ぐるなびショップの位置情報を削除
     */
    override fun clearShopAddress() {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
        preference.edit().putFloat(PreferenceUtil.KEY_SHOP_LATITUDE, 0f).apply()
        preference.edit().putFloat(PreferenceUtil.KEY_SHOP_LONGITUDE, 0f).apply()
    }
}