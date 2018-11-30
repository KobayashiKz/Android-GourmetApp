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
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.bumptech.glide.RequestBuilder
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.kk.gourmetapp.data.ImageRecognizer
import com.kk.gourmetapp.data.source.remote.ShopRemoteRepository
import com.kk.gourmetapp.util.DatabaseHelper
import com.kk.gourmetapp.util.PreferenceUtil
import pub.devrel.easypermissions.EasyPermissions
import java.util.*



class DataRepository(context: Context): DataSource {

    private val mContext: Context = context

    private var mShopRemoteRepository: ShopRemoteRepository = ShopRemoteRepository(context)

    private var mDbHelper: DatabaseHelper? = DatabaseHelper(mContext)

    companion object {
        const val KEY_BUNDLE_LATITUDE: String = "key_bundle_latitude"
        const val KEY_BUNDLE_LONGITUDE: String = "key_bundle_longitude"
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(callback: DataSource.CreateGurunaviShopCallback,
                                    isCeleb: Boolean, bundle: Bundle) {
        val keyword: String? = pickKeyword()
        //リモート側でAPIをたたいて情報生成する
        mShopRemoteRepository.createGurunaviInfo(keyword, callback, isCeleb, bundle)
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(keyword: String?,
                                    callback: DataSource.CreateGurunaviShopCallback,
                                    isCeleb: Boolean, bundle: Bundle) {
        // do nothing.
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo(callback: DataSource.CreateHotpepperShopCallback,
                                     isCeleb: Boolean, bundle: Bundle) {
        val keyword: String? = pickKeyword()
        mShopRemoteRepository.createHotpepperInfo(keyword, callback, isCeleb, bundle)
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo(keyword: String?,
                                     callback: DataSource.CreateHotpepperShopCallback,
                                     isCeleb: Boolean, bundle: Bundle) {
        // do nothing.
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    override fun saveRecognizeData(keyword: String?) {
        // SQLiteにキーワードを登録する. TODO: Priority計算処理
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    override fun removeRecognizeKeyword() {
        // Preferenceに保存されている場合には、キーワードを抜き出して空文字をセットしておく
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)
        preference.edit().putString(PreferenceUtil.KEY_KEYWORD, "").apply()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadGurunaviCredit(): RequestBuilder<Drawable>? {
        return mShopRemoteRepository.loadGurunaviCredit()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadHotpepperCredit(): RequestBuilder<Drawable>? {
        return mShopRemoteRepository.loadHotpepperCredit()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadGurunaviCreditUri(): Uri? {
        return mShopRemoteRepository.loadGurunaviCreditUri()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadHotpepperCreditUri(): Uri? {
        return mShopRemoteRepository.loadHotpepperCreditUri()
    }

    /**
     * {@inheritDoc}
     */
    override fun isCelebMode(): Boolean {
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_SETTING, Context.MODE_PRIVATE)
        return preference.getBoolean(PreferenceUtil.KEY_PREFERENCE_CELEB_MODE, false)
    }

    /**
     * {@inheritDoc}
     */
    override fun shouldUpdate(): Boolean {
        // 画像解析ワードがPreferenceに保存されているかで判断
        val preference: SharedPreferences = mContext.getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_KEYWORD, Context.MODE_PRIVATE)

        val keyword: String? = preference.getString(PreferenceUtil.KEY_KEYWORD, "")

        return !TextUtils.isEmpty(keyword)
    }

    /**
     * {@inheritDoc}
     */
    override fun isConnectNetwork(): Boolean {
        return mShopRemoteRepository.isConnectNetwork() as Boolean
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(callback: DataSource.LocationCallback) {
        // インスタンス生成
        val locationManager: LocationManager
                = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 詳細設定
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_HIGH
        criteria.isSpeedRequired = false
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
        criteria.verticalAccuracy = Criteria.ACCURACY_HIGH
        val bestProvider: String = locationManager.getBestProvider(criteria, true)
        val bundle: Bundle = Bundle()

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            300, 3f, object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
//                    bundle.putDouble(KEY_BUNDLE_LATITUDE, location.latitude)
//                    bundle.putDouble(KEY_BUNDLE_LONGITUDE, location.longitude)
//                    callback.onComplete(bundle)
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

        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        bundle.putDouble(KEY_BUNDLE_LATITUDE, location.latitude)
        bundle.putDouble(KEY_BUNDLE_LONGITUDE, location.longitude)
        callback.onComplete(bundle)

    }

    /**
     * 現在地パーミッションのチェック
     * @return true:  取得済み
     *         false: 未取得
     */
    override fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
