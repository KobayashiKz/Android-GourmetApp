package com.kk.gourmetapp.recommend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.GurunaviShop

class RecommendFragment : Fragment(), RecommendContract.View {

    var mRecommendPresenter: RecommendPresenter? = null

    var mShopAdapter: ShopAdapter? = null

    var mRecyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // お店情報を表示するRecyclerViewの設定
        val root: View? = inflater.inflate(R.layout.fragment_recommend, container, false)
        mRecyclerView = root?.findViewById(R.id.shop_recycler_view)
        mRecyclerView?.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mRecyclerView?.layoutManager = linearLayoutManager

        // ぐるなびのお店情報を取得開始
        mRecommendPresenter?.createGurunaviInfo()

        return root
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
        mShopAdapter = ShopAdapter(shops, imageLoader)
        mRecyclerView?.adapter = mShopAdapter
    }

    companion object {
        fun newInstance(): RecommendFragment {
            return RecommendFragment()
        }
    }

    /**
     * お店情報を表示するためのアダプタークラス
     */
    class ShopAdapter(shopList: MutableList<GurunaviShop>, imageLoader: ImageLoader?)
        : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

        // レストランリスト
        private var mShopList: MutableList<GurunaviShop> = shopList
        // 画像のイメージローダー
        private var mImageLoader: ImageLoader? = imageLoader

        override fun getItemCount(): Int {
            return mShopList.size
        }

        override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
            holder.name.text = mShopList[position].mName
            if (!mShopList[position].mImageUrl.isEmpty()) {
                holder.image.setImageUrl(mShopList[position].mImageUrl, mImageLoader)
            }
            holder.category.text = mShopList[position].mCategory
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
            val item: View = LayoutInflater.from(parent.context).inflate(R.layout.shop_item, parent, false)
            return ShopViewHolder(item)
        }

        /**
         * ViewHolderクラス
         */
        class ShopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.shop_name)
            val image: NetworkImageView = itemView.findViewById(R.id.shop_image)
            val category: TextView = itemView.findViewById(R.id.shop_category)
        }
    }
}
