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

    fun createGurunaviInfo(keyword: String?, callback: CreateGurunaviShopCallback,
                           isCeleb: Boolean, bundle: Bundle)

    fun createHotpepperInfo(keyword: String?, callback: CreateHotpepperShopCallback,
                            isCeleb: Boolean, bundle: Bundle)

    fun startRecognizeImage(uri: Uri?, callback: RecognizeCallback)

    fun saveRecognizeData(keyword: String?)

    fun pickKeyword(): String?

    fun removeRecognizeKeyword()

    fun loadGurunaviCredit(): RequestBuilder<Drawable>?

    fun loadHotpepperCredit(): RequestBuilder<Drawable>?

    fun loadGurunaviCreditUri(): Uri?

    fun loadHotpepperCreditUri(): Uri?

    fun isCelebMode(): Boolean

    fun shouldUpdate(): Boolean

    fun isConnectNetwork(): Boolean

    fun getCurrentLocation(callback: DataSource.LocationCallback)

    fun getSavedCurrentLocation(): Bundle

    fun saveCurrentLocation(bundle: Bundle)

    fun hasLocationPermission(): Boolean

    fun saveManualKeyword(keyword: String)

    fun clearManualKeyword()

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
}