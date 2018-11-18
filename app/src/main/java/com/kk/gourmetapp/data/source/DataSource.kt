package com.kk.gourmetapp.data.source

import android.net.Uri
import com.android.volley.toolbox.ImageLoader
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop

interface DataSource {

    // ぐるなびAPIからデータ取得完了コールバック
    interface CreateGurunaviShopCallback {
        /**
         * ぐるなびAPIからEntity作成完了時に呼ばれるコールバック
         * @param shops       お店情報リスト
         * @param imageLoader お店の画像用イメージローダー
         */
        fun createGurunaviShop(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?)
    }

    // ホットペッパーAPIからデータ取得完了コールバック
    interface CreateHotpepperShopCallback {
        /**
         * ホットペッパーAPIからEntity作成完了時に呼ばれるコールバック
         * @param shops       お店情報リスト
         * @param imageLoader お店の画像用イメージローダー
         */
        fun createHotpepperShop(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?)
    }

    /**
     * ぐるなびからお店情報を生成する処理
     * @param callback 情報取得後のコールバック
     */
    fun createGurunaviInfo(callback: CreateGurunaviShopCallback)

    /**
     * ぐるなびからお店情報を生成する処理
     * @param keyword  検索キーワード
     * @param callback 情報取得後のコールバック
     */
    fun createGurunaviInfo(keyword: String?, callback: CreateGurunaviShopCallback)

    /**
     * ホットペッパーからお店情報を生成する処理
     * @param callback 情報取得後のコールバック
     */
    fun createHotpepperInfo(callback: CreateHotpepperShopCallback)

    /**
     * ホットペッパーからお店情報を生成する処理
     * @param keyword  検索キーワード
     * @param callback 情報取得後のコールバック
     */
    fun createHotpepperInfo(keyword: String?, callback: CreateHotpepperShopCallback)

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
}