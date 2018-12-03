package com.kk.gourmetapp.recommend

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.data.source.DataRepository
import com.kk.gourmetapp.data.source.DataSource

class RecommendPresenter(recommendView: RecommendContract.View, context: Context)
    : RecommendContract.UserActionListener {

    // フラグメントはコンストラクタで受け取る
    private var mRecommendView: RecommendContract.View = recommendView
    // レポジトリはコンストラクタで受け取る
    private var mDataRepository: DataRepository? = DataRepository(context)

    init {
        // FragmentにPresenterの登録要求
        mRecommendView.setUserActionListener(this)
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(bundle: Bundle) {

        val isCelebMode: Boolean? = mDataRepository?.isCelebMode()

            // Repository側でモデルクラス作成する
            mDataRepository?.createGurunaviInfo(object : DataSource.CreateGurunaviShopCallback {
                override fun createGurunaviShop(
                    shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
                    // ぐるなびのレストラン情報が取得できた場合にはUI更新をかける
                    mRecommendView.showGurunaviShops(shops, imageLoader)
                }

                override fun onError() {
                    // 結果が取得できなかったら通信状況確認ダイアログ表示
                    if (!isConnectNetwork()) {
                        mRecommendView.showNetworkErrorDialog()
                    }
                }
            }, isCelebMode as Boolean , bundle)
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo(bundle: Bundle) {

        val isCelebMode: Boolean? = mDataRepository?.isCelebMode()

        mDataRepository?.createHotpepperInfo(object :DataSource.CreateHotpepperShopCallback {
            override fun createHotpepperShop(
                shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?) {
                // ホットペッパーのレストラン情報が取得できた場合にはUI更新をかける
                mRecommendView.showHotpepperShops(shops, imageLoader)
                // 画像解析で取得保存しておいたキーワードを空文字にしておく
                mDataRepository?.removeRecognizeKeyword()
            }

            override fun onError() {
                // 結果が取得できなかったら通信状況確認ダイアログ表示
                if (!isConnectNetwork()) {
                    mRecommendView.showNetworkErrorDialog()
                }
            }
        }, isCelebMode as Boolean, bundle)
    }

    /**
     * {@inheritDoc}
     */
    override fun loadGurunaviCredit(): RequestBuilder<Drawable>? {
        return mDataRepository?.loadGurunaviCredit()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadHotpepperCredit(): RequestBuilder<Drawable>? {
        return mDataRepository?.loadHotpepperCredit()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadGurunaviCreditUri(): Uri? {
        return mDataRepository?.loadGurunaviCreditUri()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadHotpepperCreditUri(): Uri? {
        return mDataRepository?.loadHotpepperCreditUri()
    }

    /**
     * {@inheritDoc}
     */
    override fun isCelebMode(): Boolean {
        return mDataRepository?.isCelebMode() as Boolean
    }

    /**
     * {@inheritDoc}
     */
    override fun shouldUpdate(): Boolean  {
        return mDataRepository?.shouldUpdate() as Boolean
    }

    /**
     * {@inheritDoc}
     */
    override fun isConnectNetwork(): Boolean {
        return mDataRepository?.isConnectNetwork() as Boolean
    }

    /**
     * {@inheritDoc}
     */
    override fun loadShopInfo() {
        if (mDataRepository!!.hasLocationPermission()) {
            // パーミッションが許可されている場合には現在地を取得する
            mDataRepository?.getCurrentLocation(object : DataSource.LocationCallback {
                override fun onComplete(bundle: Bundle) {
                    // 現在地取得後にショップ情報を作成
                    createGurunaviInfo(bundle)
                    createHotpepperInfo(bundle)

                    // 現在地取得中画面を閉じる
                    mRecommendView.removeLoadingFragment()
                }
            })
        } else {
            // パーミッションが許可されていない場合にはダイアログ表示させる
            mRecommendView.showRequestLocationPermission()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSavedCurrentLocation(): Bundle {
        return mDataRepository!!.getSavedCurrentLocation()
    }

    /**
     * {@inheritDoc}
     */
    override fun saveManualKeyword(keyword: String) {
        mDataRepository?.saveManualKeyword(keyword)
    }

    /**
     * {@inheritDoc}
     */
    override fun researchShopManualKeyword(keyword: String) {
        // キーワードをPreferenceに一時保存
        mDataRepository?.saveManualKeyword(keyword)

        // 画像解析が完了している場合には取得済みの現在地で再検索をかける
        val bundle: Bundle? = mDataRepository!!.getSavedCurrentLocation()
        if (bundle != null) {
            createGurunaviInfo(bundle)
            createHotpepperInfo(bundle)
        }

        // Preferenceに一時保存していたキーワードを削除する
        mDataRepository?.clearManualKeyword()
    }
}