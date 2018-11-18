package com.kk.gourmetapp.recommend

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.toolbox.ImageLoader
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.select.SelectActivity

class RecommendFragment : Fragment(), RecommendContract.View {

    private var mRecommendPresenter: RecommendPresenter? = null

    private var mGurunaviRecyclerView: RecyclerView? = null

    private var mHotpepperRecyclerView: RecyclerView? = null

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

    companion object {
        fun newInstance(): RecommendFragment {
            return RecommendFragment()
        }
    }
}
