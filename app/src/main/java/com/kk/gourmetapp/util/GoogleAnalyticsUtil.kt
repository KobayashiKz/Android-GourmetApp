package com.kk.gourmetapp.util

import android.content.Context
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.kk.gourmetapp.GourmetApplication


class GoogleAnalyticsUtil {

    /**
     * スクリーンイベントのキー
     */
    enum class ScreenEvent(val key: String) {
        SHOW_SPLASH_SCREEN("【show】スプラッシュ画面"),
        SHOW_RECOMMEND_SCREEN("【show】おすすめ画面"),
        SHOW_IMAGE_RECOGNIZE_SCREEN("【show】画像解析画面"),
        SHOW_MAP_SCREEN("【show】マップ画面"),
        SHOW_SETTING_SCREEN("【show】設定画面");
    }

    companion object {
        /**
         * スクリーンイベント送信
         * @param context    アプリケーションコンテキスト
         * @param screenName スクリーン名
         */
        fun sendScreenEvent(context: Context, screenName: String) {
            val application = context as GourmetApplication
            val tracker: Tracker? = application.getDefaultTracker()

            tracker?.setScreenName(screenName)
            tracker?.send(HitBuilders.ScreenViewBuilder().build())
        }
    }
}