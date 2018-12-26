package com.kk.gourmetapp.util

import android.content.Context
import android.text.TextUtils
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

    /**
     * アクションイベント カテゴリー名
     */
    enum class ActionEventCategory(val key: String) {
        CLICK_GURUNAVI("【click】ぐるなび"),
        CLICK_HOTPEPPER("【click】ホットペッパー"),
        CLICK_DRAWER("【click】ドロワー"),
        OTHER_GURUNAVI("【other】ぐるなび"),
        OTHER_HOTPEPPER("【other】ホットペッパー"),
        OTHER_IMAGE_RECOGNIZE("【other】画像解析")
    }

    /**
     * アクションイベント アクション名
     */
    enum class ActionEventAction(val key: String) {
        CLICK_SHOP_DETAIL("【click】ショップ詳細"),
        CLICK_SHOP_TEL_NUMBER("【click】電話番号"),
        CLICK_SHOP_MAP("【click】マップ"),
        CLICK_SHOP_CREDIT("【click】クレジット"),
        CLICK_SEARCH_BUTTON("【click】検索ボタン"),
        CLICK_SETTINGS("【click】Settings"),
        OTHER_SEARCH("【other】検索"),
        OTHER_START_IMAGE_RECOGNIZE("【other】画像解析開始"),
        OTHER_END_IMAGE_RECOGNIZE("【other】画像解析完了")
    }

    /**
     * アクションイベント ラベル名
     */
    enum class ActionEventLabel(val key: String) {
        CLICK_SHOP_DETAIL("【click】ショップ詳細"),
        CLICK_SHOP_TEL_NUMBER("【click】電話番号"),
        CLICK_SHOP_MAP("【click】マップ"),
        CLICK_SHOP_CREDIT("【click】クレジット"),
        CLICK_SEARCH_BUTTON("【click】検索ボタン"),
        CLICK_SETTINGS("【click】Settings"),
        OTHER_SEARCH("【other】検索"),
        OTHER_START_IMAGE_RECOGNIZE("【other】画像解析開始"),
        OTHER_END_IMAGE_RECOGNIZE("【other】画像解析完了")
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

        /**
         * アクションイベント送信
         * @param context    アプリケーションコンテキスト
         * @param actionName アクション名
         */
        fun sendActionEvent(context: Context, actionName: String, categoryName: String? = null,
                            dimension: String? = null) {
            val application: GourmetApplication = context as GourmetApplication
            val tracker: Tracker? = application.getDefaultTracker()

            val builder: HitBuilders.EventBuilder = HitBuilders.EventBuilder()

            var labelName = ""
            var category: String? = categoryName
            var customDimension: String? = ""

            when (actionName) {
                ActionEventAction.CLICK_SHOP_DETAIL.key -> {
                    labelName = ActionEventLabel.CLICK_SHOP_DETAIL.key
                }
                ActionEventAction.CLICK_SHOP_TEL_NUMBER.key -> {
                    category = ActionEventCategory.CLICK_GURUNAVI.key
                    labelName = ActionEventLabel.CLICK_SHOP_TEL_NUMBER.key
                }
                ActionEventAction.CLICK_SHOP_MAP.key -> {
                    labelName = ActionEventAction.CLICK_SHOP_MAP.key
                }
                ActionEventAction.CLICK_SHOP_CREDIT.key -> {
                    labelName = ActionEventAction.CLICK_SHOP_CREDIT.key
                }
                ActionEventAction.CLICK_SEARCH_BUTTON.key -> {
                    category = ActionEventCategory.CLICK_DRAWER.key
                    labelName = ActionEventAction.CLICK_SEARCH_BUTTON.key
                }
                ActionEventAction.CLICK_SETTINGS.key -> {
                    category = ActionEventCategory.CLICK_DRAWER.key
                    labelName = ActionEventLabel.CLICK_SETTINGS.key
                }
                ActionEventAction.OTHER_SEARCH.key -> {
                    labelName = ActionEventLabel.OTHER_SEARCH.key
                    if (!TextUtils.isEmpty(dimension)) {
                        builder.setCustomDimension(1, dimension)
                    }
                }
                ActionEventAction.OTHER_START_IMAGE_RECOGNIZE.key -> {
                    category = ActionEventCategory.OTHER_IMAGE_RECOGNIZE.key
                    labelName = ActionEventLabel.OTHER_START_IMAGE_RECOGNIZE.key
                }
                ActionEventAction.OTHER_END_IMAGE_RECOGNIZE.key -> {
                    category = ActionEventCategory.OTHER_IMAGE_RECOGNIZE.key
                    labelName = ActionEventLabel.OTHER_END_IMAGE_RECOGNIZE.key
                    if (!TextUtils.isEmpty(dimension)) {
                        builder.setCustomDimension(2, dimension)
                    }
                }
            }

            if (category != null && !TextUtils.isEmpty(labelName)) {
                tracker?.send(builder.setCategory(category)
                        .setAction(actionName)
                        .setLabel(labelName)
                        .build())
            }
        }
    }
}