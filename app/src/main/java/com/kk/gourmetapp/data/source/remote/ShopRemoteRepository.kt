package com.kk.gourmetapp.data.source.remote

import android.content.Context
import android.net.Uri
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.data.source.DataSource
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class ShopRemoteRepository(context: Context): DataSource {

    private var mContext: Context? = context

    companion object {
        // レストランの取得数
        const val REST_GET_COUNT: Int = 5

        // ぐるなびAPI URL
        const val NAME_GURUNAVI_AUTH_INFO_FILE: String = "gurunavi-auth-info.txt"
        const val URL_GURUNAVI_API: String = "https://api.gnavi.co.jp/RestSearchAPI/v3/"
        const val URL_SEPARATOR: String = "&"

        // ぐるなびJsonパース用キー
        const val KEY_REST: String = "rest"
        const val KEY_REST_NAME: String = "name"
        const val KEY_REST_CATEGORY: String = "category"
        const val KEY_REST_URL_MOBILE: String = "url_mobile"
        const val KEY_REST_URL_MOBILE_SHOP: String = "shop_image1"
        const val KEY_REST_IMAGE_URL: String = "image_url"

        // ホットペッパーAPI URL
        const val NAME_HOTPEPPER_AUTH_INFO_FILE: String = "hotpepper-auth-info.txt"
        const val URL_HOTPEPPER_API: String = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/"

        // ホットペッパーJsonパース用キー
        const val KEY_HOTPEPPER_RESULTS: String = "results"
        const val KEY_HOTPEPPER_SHOP: String = "shop"
        const val KEY_HOTPEPPER_SHOP_NAME: String = "name"
        const val KEY_HOTPEPPER_SHOP_GENRE: String = "genre"
        const val KEY_HOTPEPPER_SHOP_GENRE_NAME: String = "name"
        const val KEY_HOTPEPPER_SHOP_PHOTO: String = "photo"
        const val KEY_HOTPEPPER_SHOP_PHOTO_MOBILE: String = "mobile"
        const val KEY_HOTPEPPER_SHOP_PHOTO_MOBILE_LARGE: String = "l"
        const val KEY_HOTPEPPER_SHOP_URL: String = "urls"
        const val KEY_HOTPEPPER_SHOP_URL_PC: String = "pc"
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(callback: DataSource.CreateGurunaviShopCallback) {
        // do nothing.
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(keyword: String?,
                                    callback: DataSource.CreateGurunaviShopCallback) {
        val apikey: String = getApiKey(NAME_GURUNAVI_AUTH_INFO_FILE)

        val request = JsonObjectRequest(createGurunaviURL(apikey, keyword), null,
            Response.Listener { response ->

                val shopList: MutableList<GurunaviShop> = ArrayList()

                for (i in 0..(REST_GET_COUNT - 1)) {
                    val restJsonObject = response.getJSONArray(KEY_REST).getJSONObject(i)

                    // 各レストラン情報をパースする
                    val name: String = restJsonObject.getString(KEY_REST_NAME)
                    val category: String = restJsonObject.getString(KEY_REST_CATEGORY)
                    val imageUrl: String = restJsonObject.getJSONObject(KEY_REST_IMAGE_URL)
                        .getString(KEY_REST_URL_MOBILE_SHOP)
                    val pageUrl: String = restJsonObject.getString(KEY_REST_URL_MOBILE)
                    val shop = GurunaviShop(name, category, imageUrl, pageUrl)

                    // ぐるなびのショップリストに追加
                    shopList.add(shop)
                }

                // 取得完了コールバックを返す
                callback.createGurunaviShop(shopList, RequestSingleQueue.getImageLoader())
            },
            Response.ErrorListener { _ ->
                // TODO: エラー時の処理
            })
        // リクエストキューを追加
        RequestSingleQueue.addToRequestQueue(request, mContext!!)
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo(callback: DataSource.CreateHotpepperShopCallback) {
        // do nothing.
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo(keyword: String?,
                                     callback: DataSource.CreateHotpepperShopCallback) {
        val apikey: String = getApiKey(NAME_HOTPEPPER_AUTH_INFO_FILE)

        val request = JsonObjectRequest(createHotpepperURL(apikey, keyword), null,
            Response.Listener { response ->
                val shopList: MutableList<HotpepperShop> = ArrayList()

                val resultJsonObject = response.getJSONObject(KEY_HOTPEPPER_RESULTS)

                for (i in 0..(REST_GET_COUNT - 1)) {
                    val restJsonObject = resultJsonObject.getJSONArray(KEY_HOTPEPPER_SHOP).getJSONObject(i)

                    // 各レストラン情報をパースする
                    val name: String = restJsonObject.getString(KEY_HOTPEPPER_SHOP_NAME)
                    val category: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_GENRE)
                        .getString(KEY_HOTPEPPER_SHOP_GENRE_NAME)
                    val imageUrl: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_PHOTO)
                        .getJSONObject(KEY_HOTPEPPER_SHOP_PHOTO_MOBILE)
                        .getString(KEY_HOTPEPPER_SHOP_PHOTO_MOBILE_LARGE)
                    val pageUrl: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_URL)
                        .getString(KEY_HOTPEPPER_SHOP_URL_PC)
                    val shop = HotpepperShop(name, category, imageUrl, pageUrl)

                    shopList.add(shop)
                }

                callback.createHotpepperShop(shopList, RequestSingleQueue.getImageLoader())
            },
            Response.ErrorListener { _ ->
                // TODO: エラー時の処理
            })
        // リクエストキューを追加
        RequestSingleQueue.addToRequestQueue(request, mContext!!)
    }

    /**
     * ぐるなびAPIキーをassetsフォルダのテキストファイルから取得する処理
     * @param fileName 読み込むテキストファイル名
     * @return ApiKey
     */
    private fun getApiKey(fileName: String): String {
        // assetsフォルダのテキストファイルを読み込む
        val inputStream: InputStream? = mContext?.assets?.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream) as Reader?)

        // テキストファイルに記載されているAPIkeyを取得
        return bufferedReader.readLine()
    }

    /**
     * ぐるなびURLの生成処理
     * @param key     APIキー
     * @param keyword 検索キーワード
     * @return 検索URL
     */
    private fun createGurunaviURL(key: String, keyword: String?): String {
        // TODO: 現在地取得処理.ひとまず東京駅の座標で固定検索かける
        val latitude: String = "35.681167"
        val longitude: String = "139.767052"
        val url: String = URL_GURUNAVI_API + "?keyid=" + key + URL_SEPARATOR + "latitude=" + latitude +
                URL_SEPARATOR + "longitude=" + longitude
        return if (keyword != null) {
            url + URL_SEPARATOR + "freeword=" + keyword
        } else {
            url
        }
    }

    /**
     * ホットペッパーURLの生成処理
     * @param key     APIキー
     * @param keyword 検索キーワード
     * @return 検索URL
     */
    private fun createHotpepperURL(key: String, keyword: String?): String {
        // TODO: 現在地取得処理.ひとまず東京駅の座標で固定検索かける
        val latitude: String = "35.681167"
        val longitude: String = "139.767052"
        val url: String = URL_HOTPEPPER_API + "?key=" + key + URL_SEPARATOR + "lat=" + latitude +
                URL_SEPARATOR + "lng=" + longitude + URL_SEPARATOR + "format=json"
        return if (keyword != null) {
            url + URL_SEPARATOR + "keyword=" + keyword
        } else {
            url
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun saveRecognizeData(keyword: String?) {
        // do nothing.
    }

    /**
     * {@inheritDoc}
     */
    override fun startRecognizeImage(uri: Uri?, callback: DataSource.RecognizeCallback) {
        // do nothing.
    }

    /**
     * {@inheritDoc}
     */
    override fun pickKeyword(): String? {
        // do nothing.
        return null
    }

    /**
     * {@inheritDoc}
     */
    override fun removeRecognizeKeyword() {
        // do nothing.
    }
}