package com.kk.gourmetapp.recommend

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.select.SelectActivity

class RecommendFragment : Fragment(), RecommendContract.View {

    private var mRecommendPresenter: RecommendPresenter? = null

    private var mGurunaviRecyclerView: RecyclerView? = null
    private var mHotpepperRecyclerView: RecyclerView? = null

    private var mGurunaviCredit: ImageView? = null
    private var mHotpepperCredit: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root: View? = inflater.inflate(R.layout.fragment_recommend, container, false)

        // fabアイコンの設定
        val fab: FloatingActionButton? = root?.findViewById(R.id.fab_add_image)
        fab?.setOnClickListener {
            // タップされたら画像選択画面を起動する
            val intent: Intent = Intent(context, SelectActivity::class.java)
            startActivity(intent)
        }

        // ぐるなびのお店情報を表示するRecyclerViewの設定
        mGurunaviRecyclerView = root?.findViewById(R.id.gurunavi_shop_recycler_view)
        mGurunaviRecyclerView?.setHasFixedSize(true)
        val gurunaviLinearLayoutManager = LinearLayoutManager(context)
        gurunaviLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mGurunaviRecyclerView?.layoutManager = gurunaviLinearLayoutManager

        // ホットペッパーのお店情報を表示するRecyclerViewの設定
        mHotpepperRecyclerView = root?.findViewById(R.id.hotpepper_shop_recycler_view)
        mHotpepperRecyclerView?.setHasFixedSize(true)
        val hotpepperLinearLayoutManager = LinearLayoutManager(context)
        hotpepperLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mHotpepperRecyclerView?.layoutManager = hotpepperLinearLayoutManager

        // クレジット画像
        mGurunaviCredit = root?.findViewById(R.id.gurunavi_credit)
        mGurunaviCredit?.setOnClickListener {
            showGurunaviCreditInfo()
        }
        mHotpepperCredit = root?.findViewById(R.id.hotpepper_credit)
        mHotpepperCredit?.setOnClickListener {
            showHotpepperCreditInfo()
        }

        val gurunaviCreditBuilder: RequestBuilder<Drawable>? = mRecommendPresenter?.loadGurunaviCredit()
        gurunaviCreditBuilder?.into(mGurunaviCredit)
        val hotpepperCreditBuilder: RequestBuilder<Drawable>? = mRecommendPresenter?.loadHotpepperCredit()
        hotpepperCreditBuilder?.into(mHotpepperCredit)

        return root
    }

    override fun onResume() {
        super.onResume()
        // ぐるなびとホットペッパーのお店情報を取得開始
        mRecommendPresenter?.createGurunaviInfo()
        mRecommendPresenter?.createHotpepperInfo()
    }

    /**
     * {@inheritDoc}
     */
    override fun setUserActionListener(recommendPresenter: RecommendPresenter) {
        mRecommendPresenter = recommendPresenter
    }

    /**
     * {@inheritDoc}
     */
    override fun showGurunaviShops(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
        // 表示するお店情報をもとにアダプターを生成してRecyclerViewにセット
        val gurunaviShopAdapter: GurunaviShopAdapter? = GurunaviShopAdapter(shops, imageLoader)
        mGurunaviRecyclerView?.adapter = gurunaviShopAdapter
    }

    /**
     * {@inheritDoc}
     */
    override fun showHotpepperShops(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?) {
        // 表示するお店情報をもとにアダプターを生成してRecyclerViewにセット
        val hotpepperShopAdapter: HotpepperShopAdapter? = HotpepperShopAdapter(shops, imageLoader)
        mHotpepperRecyclerView?.adapter = hotpepperShopAdapter
    }

    /**
     * ぐるなびのクレジット画像遷移先の表示
     */
    private fun showGurunaviCreditInfo() {
        val uri: Uri? = mRecommendPresenter?.loadGurunaviCreditUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    /**
     * ホットペッパーのクレジット画像遷移先の表示
     */
    private fun showHotpepperCreditInfo() {
        val uri: Uri? = mRecommendPresenter?.loadHotpepperCreditUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    companion object {
        fun newInstance(): RecommendFragment {
            return RecommendFragment()
        }
    }
}
