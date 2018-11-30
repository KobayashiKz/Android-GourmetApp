package com.kk.gourmetapp.recommend

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop

interface RecommendContract {

    /**
     * Fragment側へ呼び出すUI関連の処理
     */
    interface View {

        /**
         * Presenterの登録
         * @param recommendPresenter 登録するPresenter
         */
        fun setUserActionListener(recommendPresenter: RecommendPresenter)

        /**
         * ぐるなびのレストラン情報を表示
         * @param shops       レストランリスト
         * @param imageLoader レストラン画像
         */
        fun showGurunaviShops(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?)

        /**
         * ホットペッパーのレストラン情報を表示
         * @param shops       レストランリスト
         * @param imageLoader レストラン画像
         */
        fun showHotpepperShops(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?)

        /**
         * 通信エラーダイアログ表示
         */
        fun showNetworkErrorDialog()

        fun showRequestLocationPermission()
    }

    /**
     * Repository側へ呼び出すデータ関連の処理
     */
    interface UserActionListener {

        /**
         * ぐるなびのお店情報生成
         * @param bundle 現在地
         */
        fun createGurunaviInfo(bundle: Bundle)

        /**
         * ホットペッパーのお店情報生成
         * @param bundle 現在地
         */
        fun createHotpepperInfo(bundle: Bundle)

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

        fun loadShopInfo()
    }
}