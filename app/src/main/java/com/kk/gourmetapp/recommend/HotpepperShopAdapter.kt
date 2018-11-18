package com.kk.gourmetapp.recommend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.HotpepperShop

class HotpepperShopAdapter(shopList: MutableList<HotpepperShop>, imageLoader: ImageLoader?)
    : RecyclerView.Adapter<HotpepperShopAdapter.ShopViewHolder>() {

    // レストランリスト
    private var mShopList: MutableList<HotpepperShop> = shopList
    // 画像のイメージローダー
    private var mImageLoader: ImageLoader? = imageLoader

    private var mContext: Context? = null

    override fun getItemCount(): Int {
        return mShopList.size
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.name.text = mShopList[position].mName

        // 画像URLが存在する場合は取得し存在しない場合はデフォルド画像を表示する
        if (!mShopList[position].mImageUrl.isEmpty()) {
            holder.image.setImageUrl(mShopList[position].mImageUrl, mImageLoader)
        } else {
            holder.image.setDefaultImageResId(R.drawable.default_image)
        }

        // 画像タップ時にはブラウザでWebページを開く
        holder.image.setOnClickListener {
            // 遷移処理はFragmentにかいたほうがよさそう
            val uri: Uri = Uri.parse(mShopList[position].mPageUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            mContext?.startActivity(intent)
        }
        holder.category.text = mShopList[position].mCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        mContext = parent.context
        val item: View = LayoutInflater.from(parent.context).inflate(
            R.layout.shop_item, parent, false
        )
        return ShopViewHolder(item)
    }

    /**
     * ViewHolderクラス
     */
    class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.shop_name)
        val image: NetworkImageView = itemView.findViewById(R.id.shop_image)
        val category: TextView = itemView.findViewById(R.id.shop_category)
    }
}