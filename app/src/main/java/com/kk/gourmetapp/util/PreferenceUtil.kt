package com.kk.gourmetapp.util

class PreferenceUtil {

    companion object {
        // Preferenceで使用するKEY
        // 画像解析キーワード保存用
        const val KEY_PREFERENCE_KEYWORD: String = "key_preerence_keyword"
        const val KEY_KEYWORD: String = "key_keyword"

        // セレブモード保存用
        const val KEY_PREFERENCE_SETTING: String = "key_preference_setting"
        const val KEY_PREFERENCE_CELEB_MODE: String = "key_preference_celeb_mode"

        // 現在地保存用
        const val KEY_PREFERENCE_CURRENT_LOCATION: String = "key_preference_current_location"
        const val KEY_CURRENT_LATITUDE = "key_current_latitude"
        const val KEY_CURRENT_LONGITUDE = "key_current_longitude"

        // マップ住所保存用
        const val KEY_PREFERENCE_MAP: String = "key_preference_map"
        const val KEY_SHOP_LATITUDE = "key_shop_latitude"
        const val KEY_SHOP_LONGITUDE = "key_shop_longitude"
    }
}