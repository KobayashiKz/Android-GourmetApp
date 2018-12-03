package com.kk.gourmetapp.data.source

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop

interface DataSource {

    // 現在地取得完了コールバック
    interface LocationCallback {
        fun onComplete(bundle: Bundle)
    }

    // ぐるなびAPIからデータ取得完了コールバック
    interface CreateGurunaviShopCallback {
        /**
         * ぐるなびAPIからEntity作成完了時に呼ばれるコールバック
         * @param shops       お店情報リスト
         * @param imageLoader お店の画像用イメージローダー
         */
        fun createGurunaviShop(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?)

        /**
         * ぐるなびAPIからエラーが返ってきた場合のコールバック
         */
        fun onError()
    }

    // ホットペッパーAPIからデータ取得完了コールバック
    interface CreateHotpepperShopCallback {
        /**
         * ホットペッパーAPIからEntity作成完了時に呼ばれるコールバック
         * @param shops       お店情報リスト
         * @param imageLoader お店の画像用イメージローダー
         */
        fun createHotpepperShop(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?)

        /**
         * ホットペッパーAPIからエラーが返ってきた場合のコールバック
         */
        fun onError()
    }

    /**
     * ぐるなびからお店情報を生成する処理
     * @param callback 情報取得後のコールバック
     * @param isCeleb  セレブモード状態
     * @param bundle   現在地
     */
    fun createGurunaviInfo(callback: CreateGurunaviShopCallback, isCeleb: Boolean, bundle: Bundle)

    /**
     * ぐるなびからお店情報を生成する処理
     * @param keyword  検索キーワード
     * @param callback 情報取得後のコールバック
     * @param isCeleb  セレブモード状態
     * @param bundle   現在地
     */
    fun createGurunaviInfo(keyword: String?, callback: CreateGurunaviShopCallback,
                           isCeleb: Boolean, bundle: Bundle)

    /**
     * ホットペッパーからお店情報を生成する処理
     * @param callback 情報取得後のコールバック
     * @param isCeleb  セレブモード状態
     * @param bundle   現在地
     */
    fun createHotpepperInfo(callback: CreateHotpepperShopCallback, isCeleb: Boolean, bundle: Bundle)

    /**
     * ホットペッパーからお店情報を生成する処理
     * @param keyword  検索キーワード
     * @param callback 情報取得後のコールバック
     * @param isCeleb  セレブモード状態
     * @param bundle   現在地
     */
    fun createHotpepperInfo(keyword: String?, callback: CreateHotpepperShopCallback
                            , isCeleb: Boolean, bundle: Bundle)

    // 画像認証後のコールバック
    interface RecognizeCallback {
        /**
         * 画像認証が終わった際に呼ばれるコールバック
         * @param response 画像認証情報
         */
        fun onFinish(response: ClassifiedImages)

        /**
         * 画像認証結果をパースした際に呼ばれるコールバック
         * @param keyword 抽出したキーワード
         */
        fun onParsed(keyword: String?)
    }

    /**
     * 画像認証処理
     * @param uri      画像認証する対象の画像Uri
     * @param callback コールバック
     */
    fun startRecognizeImage(uri: Uri?, callback: RecognizeCallback)

    /**
     * 画像認証データをDBに保存する
     * @param keyword 画像認証から抽出したキーワード
     */
    fun saveRecognizeData(keyword: String?)

    /**
     * 嗜好キーワードを抜き出す
     * @return 嗜好キーワード. nullは嗜好キーワードなし
     */
    fun pickKeyword(): String?

    /**
     * 画像解析直後に使用する検索キーワードの削除
     */
    fun removeRecognizeKeyword()

    /**
     * ぐるなびのクレジット画像の読み込み
     * @return Web画像のRequest
     */
    fun loadGurunaviCredit(): RequestBuilder<Drawable>?

    /**
     * ホットペッパーのクレジット画像の読み込み
     * @return Web画像のRequest
     */
    fun loadHotpepperCredit(): RequestBuilder<Drawable>?

    /**
     * ぐるなびのクレジット画像の遷移先読み込み
     * @return 遷移先Uri
     */
    fun loadGurunaviCreditUri(): Uri?

    /**
     * ホットペッパーのクレジット画像の遷移先読み込み
     * @return 遷移先Uri
     */
    fun loadHotpepperCreditUri(): Uri?

    /**
     * セレブモード状態の取得
     * @return true  : ON
     *         false : OFF
     */
    fun isCelebMode(): Boolean

    /**
     * おすすめショップの更新可否
     * @return true  : 更新必要
     *         false : 更新不必要
     */
    fun shouldUpdate(): Boolean

    /**
     * 通信状態チェック
     * @return true  : ネットワーク接続あり
     *         false : ネットワーク接続なし
     */
    fun isConnectNetwork(): Boolean

    /**
     * 現在地取得
     * @param callback 現在地取得コールバック
     */
    fun getCurrentLocation(callback: DataSource.LocationCallback)

    /**
     * すでに取得済みの現在地取得
     * @return 現在地
     */
    fun getSavedCurrentLocation(): Bundle

    /**
     * 現在地の保存
     * @param bundle 現在地
     */
    fun saveCurrentLocation(bundle: Bundle)

    /**
     * 現在地パーミッションのチェック
     * @return true:  取得済み
     *         false: 未取得
     */
    fun hasLocationPermission(): Boolean

    /**
     * 手動入力キーワードの保存
     * @param keyword 手動入力キーワード
     */
    fun saveManualKeyword(keyword: String)

    /**
     * 手動入力キーワードの削除
     */
    fun clearManualKeyword()
}