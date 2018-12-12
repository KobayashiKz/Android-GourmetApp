package com.kk.gourmetapp.data.source

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.Drawable
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.data.ImageRecognizer
import com.kk.gourmetapp.data.RequestSingleQueue
import com.kk.gourmetapp.util.DatabaseHelper
import com.kk.gourmetapp.util.PreferenceUtil
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.*

class DataRepository(context: Context): DataSource {

    private val mContext: Context = context

    private var mDbHelper: DatabaseHelper? = DatabaseHelper(mContext)

    // 現在地取得済みかどうかを判定するフラグ
    private var mLocationKnownFlg: Boolean = false

    companion object {
        const val KEY_BUNDLE_LATITUDE: String = "key_bundle_latitude"
        const val KEY_BUNDLE_LONGITUDE: String = "key_bundle_longitude"

        // レストランの取得数
        const val REST_GET_COUNT: Int = 20

        // ぐるなびAPI URL
        const val NAME_GURUNAVI_AUTH_INFO_FILE: String = "gurunavi-auth-info.txt"
        const val URL_GURUNAVI_API: String = "https://api.gnavi.co.jp/RestSearchAPI/v3/"
        const val URL_SEPARATOR: String = "&"

        // ぐるなびクレジット画像URL
        const val URL_GURUNAVI_CREDIT: String
                = "https://api.gnavi.co.jp/api/img/credit/api_90_35.gif"
        const val URL_GURUNAVI_CREDIT_TRANSITION: String
                = "https://api.gnavi.co.jp/api/scope/"

        // ぐるなびJsonパース用キー
        const val KEY_REST: String = "rest"
        const val KEY_REST_NAME: String = "name"
        const val KEY_REST_CATEGORY: String = "category"
        const val KEY_REST_URL_MOBILE: String = "url_mobile"
        const val KEY_REST_URL_MOBILE_SHOP: String = "shop_image1"
        const val KEY_REST_IMAGE_URL: String = "image_url"
        const val KEY_REST_TEL: String = "tel"
        const val KEY_REST_OPEN_TIME: String = "opentime"
        const val KEY_REST_BUDGET: String = "budget"
        const val KEY_REST_LATITUDE: String = "latitude"
        const val KEY_REST_LONGITUDE: String = "longitude"

        // ホットペッパーAPI URL
        const val NAME_HOTPEPPER_AUTH_INFO_FILE: String = "hotpepper-auth-info.txt"
        const val URL_HOTPEPPER_API: String
                = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/"

        // ホットペッパークレジット画像URL
        const val URL_HOTPEPPER_CREDIT: String
                = "http://webservice.recruit.co.jp/banner/hotpepper-m.gif"
        const val URL_HOTPEPPER_CREDIT_TRANSITION: String
                = "http://webservice.recruit.co.jp/"

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
        const val KEY_HOTPEPPER_SHOP_OPEN: String = "open"
        const val KEY_HOTPEPPER_SHOP_BUDGET: String = "budget"
        const val KEY_HOTPEPPER_SHOP_BUDGET_AVERAGE: String = "average"
        const val KEY_HOTPEPPER_SHOP_LATITUDE: String = "lat"
        const val KEY_HOTPEPPER_SHOP_LONGITUDE: String = "lng"

        // セレブモード検索ワード
        const val TEXT_CELEB_SEATCH_WORD: String = "フレンチ"
    }

    /**
     * ぐるなびからお店情報を生成する処理
     * @param keyword  検索キーワード
     * @param callback 情報取得後のコールバック
     * @param isCeleb  セレブモード状態
     * @param bundle   現在地
     */
    override fun createGurunaviInfo(keyword: String?, callback: DataSource.CreateGurunaviShopCallback,
                                    isCeleb: Boolean, bundle: Bundle) {

        val apikey: String = getApiKey(NAME_GURUNAVI_AUTH_INFO_FILE)

        val request = JsonObjectRequest(createGurunaviURL(apikey, keyword, isCeleb, bundle), null,
            Response.Listener { response ->

                val shopList: MutableList<GurunaviShop> = ArrayList()

                for (i in 0..(REST_GET_COUNT - 1)) {
                    if (response.getJSONArray(KEY_REST).length()  > i) {
                        val restJsonObject = response.getJSONArray(KEY_REST).getJSONObject(i)

                        // 各レストラン情報をパースする
                        val name: String = restJsonObject.getString(KEY_REST_NAME)
                        val category: String = restJsonObject.getString(KEY_REST_CATEGORY)
                        val imageUrl: String = restJsonObject.getJSONObject(KEY_REST_IMAGE_URL)
                            .getString(KEY_REST_URL_MOBILE_SHOP)
                        val pageUrl: String = restJsonObject.getString(KEY_REST_URL_MOBILE)
                        val tel: String = restJsonObject.getString(KEY_REST_TEL)
                        val openTime: String = restJsonObject.getString(KEY_REST_OPEN_TIME)
                        val budget: String = restJsonObject.getString(KEY_REST_BUDGET)
                        val latitude: Double = restJsonObject.getDouble(KEY_REST_LATITUDE)
                        val longitude: Double = restJsonObject.getDouble(KEY_REST_LONGITUDE)
                        val shop = GurunaviShop(name, category, imageUrl, pageUrl, tel,
                            openTime, budget, latitude, longitude)

                        // ぐるなびのショップリストに追加
                        shopList.add(shop)
                    }

                    // 取得完了コールバックを返す
                    callback.createGurunaviShop(shopList, RequestSingleQueue.getImageLoader())
                }
            },
            Response.ErrorListener { _ ->
                callback.onError()
            })
        // リクエストキューを追加
        RequestSingleQueue.addToRequestQueue(request, mContext)
    }

    /**
     * ぐるなびURLの生成処理
     * @param key     APIキー
     * @param keyword 検索キーワード
     * @param bundle  現在地
     * @return 検索URL
     */
    private fun createGurunaviURL(key: String, keyword: String?, isCeleb: Boolean, bundle: Bundle): String {
        // 現在地の緯度経度を取得
        var latitude: Double = bundle.getDouble(DataRepository.KEY_BUNDLE_LATITUDE)
        var longitude: Double = bundle.getDouble(DataRepository.KEY_BUNDLE_LONGITUDE)
        // TODO: 東京駅の座標で検索.
        latitude = 35.681167
        longitude = 139.767052
        val url: String = URL_GURUNAVI_API + "?keyid=" + key + URL_SEPARATOR + "latitude=" +
                latitude.toString() + URL_SEPARATOR + "longitude=" + longitude.toString()
        return when {
            isCeleb -> url + URL_SEPARATOR + "freeword=" + TEXT_CELEB_SEATCH_WORD
            keyword != null -> url + URL_SEPARATOR + "freeword=" + keyword
            else -> url
        }
    }

    /**
     * APIキーをassetsフォルダのテキストファイルから取得
     * @param fileName 読み込むテキストファイル名
     * @return ApiKey
     */
    private fun getApiKey(fileName: String): String {
        // assetsフォルダのテキストファイルを読み込む
        val inputStream: InputStream? = mContext.assets?.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream) as Reader?)

        // テキストファイルに記載されているAPIkeyを取得
        return bufferedReader.readLine()
    }

    /**
     * ホットペッパーからお店情報を生成する処理
     * @param keyword  検索キーワード
     * @param callback 情報取得後のコールバック
     * @param isCeleb  セレブモード状態
     * @param bundle   現在地
     */
    override fun createHotpepperInfo(keyword: String?, callback: DataSource.CreateHotpepperShopCallback,
                                     isCeleb: Boolean, bundle: Bundle) {
        val apikey: String = getApiKey(NAME_HOTPEPPER_AUTH_INFO_FILE)

        val request = JsonObjectRequest(createHotpepperURL(apikey, keyword, isCeleb, bundle), null,
            Response.Listener { response ->
                val shopList: MutableList<HotpepperShop> = ArrayList()

                val resultJsonObject = response.getJSONObject(KEY_HOTPEPPER_RESULTS)

                for (i in 0..(REST_GET_COUNT - 1)) {
                    if (resultJsonObject.getJSONArray(KEY_HOTPEPPER_SHOP).length() > i) {
                        val restJsonObject =
                            resultJsonObject.getJSONArray(KEY_HOTPEPPER_SHOP).getJSONObject(i)

                        // 各レストラン情報をパースする
                        val name: String = restJsonObject.getString(KEY_HOTPEPPER_SHOP_NAME)
                        val category: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_GENRE)
                            .getString(KEY_HOTPEPPER_SHOP_GENRE_NAME)
                        val imageUrl: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_PHOTO)
                            .getJSONObject(KEY_HOTPEPPER_SHOP_PHOTO_MOBILE)
                            .getString(KEY_HOTPEPPER_SHOP_PHOTO_MOBILE_LARGE)
                        val pageUrl: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_URL)
                            .getString(KEY_HOTPEPPER_SHOP_URL_PC)
                        val openTime: String = restJsonObject.getString(KEY_HOTPEPPER_SHOP_OPEN)
                        val budget: String = restJsonObject.getJSONObject(KEY_HOTPEPPER_SHOP_BUDGET)
                            .getString(KEY_HOTPEPPER_SHOP_BUDGET_AVERAGE)
                        val latitude: Double = restJsonObject.getDouble(KEY_HOTPEPPER_SHOP_LATITUDE)
                        val longitude: Double = restJsonObject.getDouble(KEY_HOTPEPPER_SHOP_LONGITUDE)

                        val shop = HotpepperShop(name, category, imageUrl, pageUrl, openTime, budget,
                            latitude, longitude)

                        shopList.add(shop)
                    }
                    callback.createHotpepperShop(shopList, RequestSingleQueue.getImageLoader())
                }
            },
            Response.ErrorListener { _ ->
                callback.onError()
            })
        // リクエストキューを追加
        RequestSingleQueue.addToRequestQueue(request, mContext!!)
    }

    /**
     * ホットペッパーURLの生成処理
     * @param key     APIキー
     * @param keyword 検索キーワード
     * @param bundle  現在地
     * @return 検索URL
     */
    private fun createHotpepperURL(key: String, keyword: String?, isCeleb: Boolean,
                                   bundle: Bundle): String {
        // 現在地の緯度経度を取得
        var latitude: Double = bundle.getDouble(DataRepository.KEY_BUNDLE_LATITUDE)
        var longitude: Double = bundle.getDouble(DataRepository.KEY_BUNDLE_LONGITUDE)
        // TODO: 東京駅の座標で検索.
        latitude = 35.681167
        longitude = 139.767052
        val url: String = URL_HOTPEPPER_API + "?key=" + key + URL_SEPARATOR + "lat=" +
                latitude.toString() + URL_SEPARATOR + "lng=" + longitude.toString() +
                URL_SEPARATOR + "format=json"
        return when {
            isCeleb -> url + URL_SEPARATOR + "keyword=" + TEXT_CELEB_SEATCH_WORD
            keyword != null -> url + URL_SEPARATOR + "keyword=" + keyword
            else -> url
        }
    }

    /**
     * 画像認証処理
     * @param uri      画像認証する対象の画像Uri
     * @param callback コールバック
     */
    override fun startRecognizeImage(uri: Uri?, callback: DataSource.RecognizeCallback) {
        val imageRecognizer: ImageRecognizer = ImageRecognizer.newInstance(mContext)
        imageRecognizer.recognizeImage(uri, object : DataSource.RecognizeCallback {

            override fun onFinish(response: ClassifiedImages) {
                // 画像解析したデータをパースしてデータリストを生成
                val resultList: MutableList<ClassResult> = response.images[0].classifiers[0].classes
                val imageInfoList: MutableList<ImageInfo>? = ArrayList()
                for (result: ClassResult in resultList) {
                    val imageInfo = ImageInfo(result.className, result.score, result.typeHierarchy)
                    imageInfoList?.add(imageInfo)
                }

                // 使用するキーワードを抽出する
                val keyword: String? = pickWord(imageInfoList)

                // キーワードが抽出できた場合だけコールバックを返す
                if (keyword != null) {
                    callback.onParsed(keyword)
                }
            }

            override fun onParsed(keyword: String?) {
                // キーワードをDBに保存
                saveRecognizeData(keyword)
            }
        })
    }

    /**
     * 画像解析結果から使用するキーワードを抜き出す
     * @param imageInfoList 画像解析結果リスト
     * @return キーワード
     */
    private fun pickWord(imageInfoList: MutableList<ImageInfo>?): String? {
        if (imageInfoList == null) return null

        var keyword: String? = null
        val score: Float = 0f
        // typeHierarchyをもっていないスコアのもっとも高いキーワードとして抽出する
        for (imageInfo: ImageInfo in imageInfoList) {
            if (imageInfo.getTypeHierarchy() == null) {
                if (score < imageInfo.getScore()) {
                    keyword = imageInfo.getName()
                }
            }
        }
        return keyword
    }

    /**
     * 画像認証データをDBに保存する
     * @param keyword 画像認証から抽出したキーワード
     */
    override fun saveRecognizeData(keyword: String?) {
        // SQLiteにキーワードを登録する.
        val writer: SQLiteDatabase? = mDbHelper?.writableDatabase

        val values = ContentValues()
        values.put(DatabaseHelper.KeywordTable.COLUMN_NAME_KEYWORD.value, keyword)
        values.put(DatabaseHelper.KeywordTable.COLUMN_NAME_PRIORITY.value, 1)
        writer?.insert(DatabaseHelper.KeywordTable.TABLE_NAME.value, null, values)

        // Preferenceにも保存しておく
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)
        preference.edit().putString(PreferenceUtil.KEY_KEYWORD, keyword).apply()
    }

    /**
     * 嗜好キーワードを抜き出す
     * @return 嗜好キーワード. nullは嗜好キーワードなし
     */
    override fun pickKeyword(): String? {

        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)

        var keyword: String? = preference.getString(PreferenceUtil.KEY_KEYWORD, "")

        if (TextUtils.isEmpty(keyword)) {
            // Preferenceにキーワードが保存されていない場合にはDBからランダム番目のキーワードを取得する
            val reader: SQLiteDatabase? = mDbHelper?.readableDatabase

            // キーワード保存件数の取得
            val recordCount: Long? = mDbHelper?.getRecordCount(reader,
                DatabaseHelper.KeywordTable.TABLE_NAME.value)
            if (recordCount != 0L) {
                // ランダム値を生成
                val randomInt: Int = Random().nextInt(recordCount!!.toInt())

                val projection: Array<String> = arrayOf(
                    DatabaseHelper.KeywordTable.COLUMN_NAME_KEYWORD.value)
                // ランダム番目のキーワードを取得する
                val cursor: Cursor? = reader?.query(
                    DatabaseHelper.KeywordTable.TABLE_NAME.value,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "$randomInt,1")

                while (cursor!!.moveToNext()) {
                    keyword = cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseHelper.KeywordTable.COLUMN_NAME_KEYWORD.value))
                }
                cursor.close()
            } else {
                // キーワードが保存されていない場合にはnullを返す
                keyword = null
            }
        }
        return keyword
    }

    /**
     * 画像解析直後に使用する検索キーワードの削除
     */
    override fun removeRecognizeKeyword() {
        // Preferenceに保存されている場合には、キーワードを抜き出して空文字をセットしておく
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)
        preference.edit().putString(PreferenceUtil.KEY_KEYWORD, "").apply()
    }

    /**
     * ぐるなびのクレジット画像の読み込み
     * @return Web画像のRequest
     */
    override fun loadGurunaviCredit(): RequestBuilder<Drawable>? {
        return Glide.with(mContext).load(URL_GURUNAVI_CREDIT)
    }

    /**
     * ホットペッパーのクレジット画像の読み込み
     * @return Web画像のRequest
     */
    override fun loadHotpepperCredit(): RequestBuilder<Drawable>? {
        return Glide.with(mContext).load(URL_HOTPEPPER_CREDIT)
    }

    /**
     * ぐるなびのクレジット画像の遷移先読み込み
     * @return 遷移先Uri
     */
    override fun loadGurunaviCreditUri(): Uri {
        return Uri.parse(URL_GURUNAVI_CREDIT_TRANSITION)
    }

    /**
     * ホットペッパーのクレジット画像の遷移先読み込み
     * @return 遷移先Uri
     */
    override fun loadHotpepperCreditUri(): Uri {
        return Uri.parse(URL_HOTPEPPER_CREDIT_TRANSITION)
    }

    /**
     * セレブモード状態の取得
     * @return true  : ON
     *         false : OFF
     */
    override fun isCelebMode(): Boolean {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_SETTING, Context.MODE_PRIVATE)
        return preference.getBoolean(PreferenceUtil.KEY_PREFERENCE_CELEB_MODE, false)
    }

    /**
     * おすすめショップの更新可否
     * @return true  : 更新必要
     *         false : 更新不必要
     */
    override fun shouldUpdate(): Boolean {
        // 画像解析ワードがPreferenceに保存されているかで判断
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)

        val keyword: String? = preference.getString(PreferenceUtil.KEY_KEYWORD, "")

        return !TextUtils.isEmpty(keyword)
    }

    /**
     * 通信状態チェック
     * @return true  : ネットワーク接続あり
     *         false : ネットワーク接続なし
     */
    override fun isConnectNetwork(): Boolean {
        val connMgr = mContext?.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    /**
     * 現在地取得
     * @param callback 現在地取得コールバック
     */
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(callback: DataSource.LocationCallback) {
        // インスタンス生成
        val locationManager: LocationManager
                = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 詳細設定
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_LOW
        criteria.powerRequirement = Criteria.POWER_HIGH
        criteria.isSpeedRequired = false
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.horizontalAccuracy = Criteria.ACCURACY_LOW
        criteria.verticalAccuracy = Criteria.ACCURACY_LOW
        val bundle = Bundle()

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            0, 0f, object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null && !mLocationKnownFlg) {
                    Toast.makeText(mContext, location.latitude.toString() , Toast.LENGTH_SHORT).show()
                    bundle.putDouble(KEY_BUNDLE_LATITUDE, location.latitude)
                    bundle.putDouble(KEY_BUNDLE_LONGITUDE, location.longitude)

                    // 現在地を保存
                    saveCurrentLocation(bundle)

                    callback.onComplete(bundle)

                    // 現在地取得終了
                    locationManager.removeUpdates(this)
                    mLocationKnownFlg = true
                }
            }

            override fun onProviderDisabled(provider: String?) {
                // do nothing.
            }

            override fun onProviderEnabled(provider: String?) {
                // do nothing.
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // do nothing.
            }
        })
    }

    /**
     * 現在地パーミッションのチェック
     * @return true:  取得済み
     *         false: 未取得
     */
    override fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * すでに取得済みの現在地取得
     * @return 現在地
     */
    override fun getSavedCurrentLocation(): Bundle {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_CURRENT_LOCATION, Context.MODE_PRIVATE)
        val latitude: Double = java.lang.Double.longBitsToDouble(preference.getLong(
            PreferenceUtil.KEY_CURRENT_LATITUDE, 0L))
        val longitude: Double = java.lang.Double.longBitsToDouble(preference.getLong(
            PreferenceUtil.KEY_CURRENT_LONGITUDE, 0L))

        val bundle = Bundle()
        bundle.putDouble(KEY_BUNDLE_LATITUDE, latitude)
        bundle.putDouble(KEY_BUNDLE_LONGITUDE, longitude)
        return bundle
    }

    /**
     * 現在地の保存
     * @param bundle 現在地
     */
    override fun saveCurrentLocation(bundle: Bundle) {
        var latitude: Double = bundle.getDouble(DataRepository.KEY_BUNDLE_LATITUDE)
        var longitude: Double = bundle.getDouble(DataRepository.KEY_BUNDLE_LONGITUDE)
        // TODO: 東京駅の座標で検索.
        latitude = 35.681167
        longitude = 139.767052

        // Preferenceに保存
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_CURRENT_LOCATION, Context.MODE_PRIVATE)
        preference.edit().putLong(
            PreferenceUtil.KEY_CURRENT_LATITUDE,
            java.lang.Double.doubleToRawLongBits(latitude)).apply()
        preference.edit().putLong(
            PreferenceUtil.KEY_CURRENT_LONGITUDE,
            java.lang.Double.doubleToRawLongBits(longitude)).apply()
    }

    /**
     * 手動入力キーワードの保存
     * @param keyword 手動入力キーワード
     */
    override fun saveManualKeyword(keyword: String) {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)
        preference.edit().putString(PreferenceUtil.KEY_KEYWORD, keyword).apply()
    }

    /**
     * 手動入力キーワードの削除
     */
    override fun clearManualKeyword() {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)
        preference.edit().putString(PreferenceUtil.KEY_KEYWORD, "").apply()
    }
}
