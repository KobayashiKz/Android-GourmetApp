package com.kk.gourmetapp.data.source

import android.net.Uri
import com.android.volley.toolbox.ImageLoader
import com.kk.gourmetapp.data.GurunaviShop

interface DataSource {

    // ぐるなびAPIからデータ取得完了コールバック
    interface CreateGurunaviShopCallback {

        /**
         * ぐるなびAPIからEntity作成完了時に呼ばれるコールバック
         * @param shops お店情報リスト
         * @param imageLoader お店の画像用イメージローダー
         */
        fun createdShop(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?)
    }

    /**
     * ぐるなびからお店情報を生成する処理
     * @param callback 情報取得後のコールバック
     */
    fun createGurunaviInfo(callback: CreateGurunaviShopCallback)

    interface RecognizeCallback {
        fun onFinish()
    }

    /**
     * 画像認証処理
     * @param inputStream 画像認証する対象の画像InputStream
     */
    fun startRecognizeImage(uri: Uri?)

    /**
     * 画像認証データをDBに保存する
     */
    fun saveRecognizeData()
}