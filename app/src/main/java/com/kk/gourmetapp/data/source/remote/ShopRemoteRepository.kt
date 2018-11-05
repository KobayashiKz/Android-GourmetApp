package com.kk.gourmetapp.data.source.remote

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.source.ShopDataSource
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class ShopRemoteRepository(context: Context): ShopDataSource {

    private var mImageLoader: ImageLoader? = null
    private var mContext: Context? = context

    private var mGurunaviShopList: MutableList<GurunaviShop> = ArrayList()

    companion object {
        // レストランの取得数
        const val REST_GET_COUNT: Int = 5

        // ぐるなびAPI URL
        const val NAME_AUTH_INFO_FILE: String = "authinfo.txt"
        const val URL_GURUNAVI_API: String = "https://api.gnavi.co.jp/RestSearchAPI/v3/"
        const val URL_GURUNAVI_SEPARATOR: String = "&"

        // ぐるなびJsonパース用キー
        const val KEY_REST: String = "rest"
        const val KEY_REST_NAME: String = "name"
        const val KEY_REST_CATEGORY: String = "category"
        const val KEY_REST_URL_MOBILE: String = "url_mobile"
        const val KEY_REST_URL_MOBILE_SHOP: String = "shop_image1"
        const val KEY_REST_IMAGE_URL: String = "image_url"
    }

    init {
        mImageLoader = RequestSingleQueue.getImageLoader()
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(callback: ShopDataSource.CreateGurunaviShopCallback) {
        val apikey: String = getGurunaviApiKey()

        val request: JsonObjectRequest = JsonObjectRequest(createURL(apikey), null,
            Response.Listener { response ->

                for (i in 0..(REST_GET_COUNT - 1)) {
                    val restJsonObject = response.getJSONArray(KEY_REST).getJSONObject(i)

                    // 各レストラン情報をパースする
                    val name: String = restJsonObject.getString(KEY_REST_NAME)
                    val category: String = restJsonObject.getString(KEY_REST_CATEGORY)
                    val imageUrl: String = restJsonObject.getJSONObject(KEY_REST_IMAGE_URL)
                        .getString(KEY_REST_URL_MOBILE_SHOP)
                    val pageUrl: String = restJsonObject.getString(KEY_REST_URL_MOBILE)
                    val gurunaviShop = GurunaviShop(name, category, imageUrl, pageUrl)

                    // ぐるなびのショップリストに追加
                    mGurunaviShopList.add(gurunaviShop)
                }

                // 取得完了コールバックを返す
                callback.createdShop(mGurunaviShopList, mImageLoader)
            },
            Response.ErrorListener { error ->
                // TODO: エラー時の処理
            })
        // リクエストキューを追加
        RequestSingleQueue.addToRequestQueue(request, mContext!!)
    }

    /**
     * ぐるなびAPIキーをassetsフォルダのテキストファイルから取得する処理
     */
    private fun getGurunaviApiKey(): String {
        // assetsフォルダのテキストファイルを読み込む
        val inputStream: InputStream? = mContext?.assets?.open(NAME_AUTH_INFO_FILE)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream) as Reader?)

        // テキストファイルに記載されているAPIkeyを取得
        return bufferedReader.readLine()
    }

    /**
     * ぐるなびURLの生成処理
     * @param key ぐるなびAPIのAPIキー
     */
    private fun createURL(key: String): String {
        // TODO: 現在地取得処理.ひとまず東京駅の座標で固定検索かける
        val latitude: String = "35.681167"
        val longitude: String = "139.767052"
        return URL_GURUNAVI_API + "?keyid=" + key + URL_GURUNAVI_SEPARATOR + "latitude=" + latitude +
                URL_GURUNAVI_SEPARATOR + "longitude=" + longitude
    }
}