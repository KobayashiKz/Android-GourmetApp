package com.kk.gourmetapp.recommend

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.map.MapFragment
import com.kk.gourmetapp.select.SelectActivity
import com.kk.gourmetapp.util.ActivityUtil
import pub.devrel.easypermissions.EasyPermissions

class RecommendFragment : Fragment(), RecommendContract.View {

    private var mRecommendPresenter: RecommendPresenter? = null

    private var mGurunaviRecyclerView: RecyclerView? = null
    private var mHotpepperRecyclerView: RecyclerView? = null

    private var mCelebBackground: LottieAnimationView? = null

    private var mLoadingFragment: LocationLoadingFragment = LocationLoadingFragment.newInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root: View? = inflater.inflate(R.layout.fragment_recommend, container, false)

        // fabアイコンの設定
        val fab: FloatingActionButton? = root?.findViewById(R.id.fab_add_image)
        fab?.setOnClickListener {
            // タップされたら画像選択画面を起動する
            showRecognizeScreen()
        }

        // セレブモードの背景設定
        mCelebBackground = root?.findViewById(R.id.celeb_background)
        mRecommendPresenter?.updateCelebMode()

        // ぐるなびのお店情報を表示するRecyclerViewの設定
        setupGurunaviRecyclerView(root)
        // ホットペッパーのお店情報を表示するRecyclerViewの設定
        setupHotpepperRecyclerView(root)

        // ぐるなびクレジット画像
        setupGurunaviCredit(root)
        // ホットペッパークレジット画像
        setupHotpepperCredit(root)

        // 現在地取得中Fragmentの生成
        showLocationLoadingFragment()

        // ショップ情報の読み込み
        mRecommendPresenter?.loadShopInfo()

        return root
    }

    /**
     * ぐるなびお店情報表示RecyclerViewの設定
     * @param root ルートビュー
     */
    private fun setupGurunaviRecyclerView(root: View?) {
        mGurunaviRecyclerView = root?.findViewById(R.id.gurunavi_shop_recycler_view)
        mGurunaviRecyclerView?.setHasFixedSize(true)
        val gurunaviLinearLayoutManager = LinearLayoutManager(context)
        gurunaviLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mGurunaviRecyclerView?.layoutManager = gurunaviLinearLayoutManager
    }

    /**
     * ホットペッパーお店情報表示RecyclerViewの設定
     * @param root ルートビュー
     */
    private fun setupHotpepperRecyclerView(root: View?) {
        mHotpepperRecyclerView = root?.findViewById(R.id.hotpepper_shop_recycler_view)
        mHotpepperRecyclerView?.setHasFixedSize(true)
        val hotpepperLinearLayoutManager = LinearLayoutManager(context)
        hotpepperLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mHotpepperRecyclerView?.layoutManager = hotpepperLinearLayoutManager
    }

    /**
     * ぐるなびクレジット画像の設定
     * @param root ルートビュー
     */
    private fun setupGurunaviCredit(root: View?) {
        val gurunaviCredit: ImageView? = root?.findViewById(R.id.gurunavi_credit)
        val gurunaviCreditBuilder: RequestBuilder<Drawable>?
                = mRecommendPresenter?.loadGurunaviCredit()
        gurunaviCreditBuilder?.into(gurunaviCredit)
        gurunaviCredit?.setOnClickListener {
            mRecommendPresenter?.updateGurunaviCreditTransition()
        }
    }

    /**
     * ホットペッパークレジット画像の設定
     * @param root ルートビュー
     */
    private fun setupHotpepperCredit(root: View?) {
        val hotpepperCredit: ImageView? = root?.findViewById(R.id.hotpepper_credit)
        val hotpepperCreditBuilder: RequestBuilder<Drawable>?
                = mRecommendPresenter?.loadHotpepperCredit()
        hotpepperCreditBuilder?.into(hotpepperCredit)
        hotpepperCredit?.setOnClickListener {
            mRecommendPresenter?.updateHotpepperCreditTransition()
        }
    }

    /**
     * 画像認証画面の表示
     */
    override fun showRecognizeScreen() {
        val intent = Intent(context, SelectActivity::class.java)
        activity?.startActivityForResult(intent, ActivityUtil.REQUEST_CODE_RECOGNIZE)
    }

    /**
     * Presenterの登録
     * @param recommendPresenter 登録するPresenter
     */
    override fun setPresenter(recommendPresenter: RecommendPresenter) {
        mRecommendPresenter = recommendPresenter
    }

    /**
     * ぐるなびのレストラン情報を表示
     * @param shops       レストランリスト
     * @param imageLoader レストラン画像
     */
    override fun showGurunaviShops(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
        // 表示するお店情報をもとにアダプターを生成してRecyclerViewにセット
        val gurunaviShopAdapter: GurunaviShopAdapter? = GurunaviShopAdapter(shops, imageLoader)
        mGurunaviRecyclerView?.adapter = gurunaviShopAdapter
    }

    /**
     * ホットペッパーのレストラン情報を表示
     * @param shops       レストランリスト
     * @param imageLoader レストラン画像
     */
    override fun showHotpepperShops(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?) {
        // 表示するお店情報をもとにアダプターを生成してRecyclerViewにセット
        val hotpepperShopAdapter: HotpepperShopAdapter? = HotpepperShopAdapter(shops, imageLoader)
        mHotpepperRecyclerView?.adapter = hotpepperShopAdapter
    }

    /**
     * ネットワーク未接続確認ダイアログの表示
     */
    override fun showNetworkErrorDialog() {
        val errorDialog = NetworkErrorDialog()
        errorDialog.show(fragmentManager, "tag")
    }

    /**
     * ロケーションパーミッションダイアログの表示
     */
    override fun showRequestLocationPermission() {
        EasyPermissions.requestPermissions(this,
            getString(R.string.location_request_permission_message),
            MapFragment.REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * 現在地読み込み中Fragmentの表示
     */
    private fun showLocationLoadingFragment() {
        ActivityUtil.addFragmentToActivity(activity!!.supportFragmentManager, mLoadingFragment,
            R.id.content_frame)
    }

    /**
     * 現在地読み込み中Fragmentの削除
     */
    override fun closeLocationLoadingFragment() {
        mLoadingFragment.removeLocationLoadingFragment()
    }

    /**
     * APIのクレジット画像遷移先の表示
     */
    override fun showApiCreditTransition(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    /**
     * セレブモード背景の表示
     */
    override fun showCelebBackground() {
        mCelebBackground?.visibility = View.VISIBLE
    }

    /**
     * セレブモード背景の非表示
     */
    override fun removeCelebBackground() {
        mCelebBackground?.visibility = View.GONE
    }

    companion object {
        fun newInstance(): RecommendFragment {
            return RecommendFragment()
        }
    }

    /**
     * ネットワーク未接続確認ダイアログの表示
     */
    class NetworkErrorDialog: DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val builder = AlertDialog.Builder(activity)
            builder.setMessage(getString(R.string.network_dialog_message))
                .setPositiveButton(R.string.network_dialog_positive_button) { _, _ ->
                    showWifiSetting()
                }

            val dialog = builder.create()
            // キャンセルさせないダイアログ
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            return dialog
        }

        /**
         * Wifi設定画面表示
         */
        private fun showWifiSetting() {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
    }
}
