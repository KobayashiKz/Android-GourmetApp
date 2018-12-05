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
    : RecommendContract.Presenter {

    // フラグメントはコンストラクタで受け取る
    private var mRecommendView: RecommendContract.View = recommendView
    // レポジトリはコンストラクタで受け取る
    private var mDataRepository: DataRepository = DataRepository(context)

    init {
        // FragmentにPresenterの登録要求
        mRecommendView.setPresenter(this)
    }

    /**
     * ぐるなびのお店情報生成
     * @param bundle 現在地
     */
    override fun createGurunaviInfo(bundle: Bundle) {

        val isCelebMode: Boolean? = mDataRepository.isCelebMode()

            // Repository側でモデルクラス作成する
            mDataRepository.createGurunaviInfo(object : DataSource.CreateGurunaviShopCallback {
                override fun createGurunaviShop(
                    shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
                    // ぐるなびのレストラン情報が取得できた場合にはUI更新をかける
                    mRecommendView.showGurunaviShops(shops, imageLoader)
                }

                override fun onError() {
                    // 結果が取得できなかったら通信状況確認ダイアログ表示
                    if (!mDataRepository.isConnectNetwork()) {
                        mRecommendView.showNetworkErrorDialog()
                    }
                }
            }, isCelebMode as Boolean , bundle)
    }

    /**
     * ホットペッパーのお店情報生成
     * @param bundle 現在地
     */
    override fun createHotpepperInfo(bundle: Bundle) {

        val isCelebMode: Boolean? = mDataRepository.isCelebMode()

        mDataRepository.createHotpepperInfo(object :DataSource.CreateHotpepperShopCallback {
            override fun createHotpepperShop(
                shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?) {
                // ホットペッパーのレストラン情報が取得できた場合にはUI更新をかける
                mRecommendView.showHotpepperShops(shops, imageLoader)
                // 画像解析で取得保存しておいたキーワードを空文字にしておく
                mDataRepository.removeRecognizeKeyword()
            }

            override fun onError() {
                // 結果が取得できなかったら通信状況確認ダイアログ表示
                if (!mDataRepository.isConnectNetwork()) {
                    mRecommendView.showNetworkErrorDialog()
                }
            }
        }, isCelebMode as Boolean, bundle)
    }

    /**
     * ぐるなびのクレジット画像の読み込み
     * @return Web画像のRequest
     */
    override fun loadGurunaviCredit(): RequestBuilder<Drawable>? {
        return mDataRepository.loadGurunaviCredit()
    }

    /**
     * ホットペッパーのクレジット画像の読み込み
     * @return Web画像のRequest
     */
    override fun loadHotpepperCredit(): RequestBuilder<Drawable>? {
        return mDataRepository.loadHotpepperCredit()
    }

    /**
     * ぐるなびのクレジット画像の遷移先読み込み
     */
    override fun updateGurunaviCreditTransition() {
        val uri: Uri =  mDataRepository.loadGurunaviCreditUri()
        mRecommendView.showApiCreditTransition(uri)
    }

    /**
     * ホットペッパーのクレジット画像の遷移先読み込み
     */
    override fun updateHotpepperCreditTransition(){
        val uri: Uri = mDataRepository.loadHotpepperCreditUri()
        mRecommendView.showApiCreditTransition(uri)
    }

    /**
     * セレブモードの更新
     */
    override fun updateCelebMode() {
        if (mDataRepository.isCelebMode()) {
            mRecommendView.showCelebBackground()
        } else {
            mRecommendView.removeCelebBackground()
        }
    }

    /**
     * おすすめショップの更新可否
     * @return true  : 更新必要
     *         false : 更新不必要
     */
    override fun shouldUpdate(): Boolean  {
        return mDataRepository.shouldUpdate()
    }

    /**
     * ショップ情報の読み込み処理
     */
    override fun loadShopInfo() {
        if (mDataRepository.hasLocationPermission()) {
            // パーミッションが許可されている場合には現在地を取得する
            mDataRepository.getCurrentLocation(object : DataSource.LocationCallback {
                override fun onComplete(bundle: Bundle) {
                    // 現在地取得後にショップ情報を作成
                    createGurunaviInfo(bundle)
                    createHotpepperInfo(bundle)

                    // 現在地取得中画面を閉じる
                    mRecommendView.closeLocationLoadingFragment()
                }
            })
        } else {
            // パーミッションが許可されていない場合にはダイアログ表示させる
            mRecommendView.showRequestLocationPermission()
        }
    }

    /**
     * すでに取得済みの現在地取得
     * @return 現在地
     */
    override fun getSavedCurrentLocation(): Bundle {
        return mDataRepository.getSavedCurrentLocation()
    }

    /**
     * 手動入力キーワードによる再検索処理
     * @param keyword 手動入力キーワード
     */
    override fun researchShopManualKeyword(keyword: String) {
        // キーワードをPreferenceに一時保存
        mDataRepository.saveManualKeyword(keyword)

        // 画像解析が完了している場合には取得済みの現在地で再検索をかける
        val bundle: Bundle? = mDataRepository.getSavedCurrentLocation()
        if (bundle != null) {
            createGurunaviInfo(bundle)
            createHotpepperInfo(bundle)
        }

        // Preferenceに一時保存していたキーワードを削除する
        mDataRepository.clearManualKeyword()
    }
}