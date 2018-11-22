package com.kk.gourmetapp.recommend

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.GurunaviShop

class GurunaviShopAdapter(shopList: MutableList<GurunaviShop>, imageLoader: ImageLoader?)
    : RecyclerView.Adapter<GurunaviShopAdapter.ShopViewHolder>() {

    // レストランリスト
    private var mShopList: MutableList<GurunaviShop> = shopList
    // 画像のイメージローダー
    private var mImageLoader: ImageLoader? = imageLoader

    private var mContext: Context? = null

    override fun getItemCount(): Int {
        return mShopList.size
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.scrollView.isVerticalScrollBarEnabled = false

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

        holder.tel.paintFlags = holder.tel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.tel.text = mShopList[position].mTelNumber
        if (!TextUtils.isEmpty(mShopList[position].mTelNumber)) {
            // 電話番号をタップしたらダイアル画面へ遷移させる
            holder.tel.setOnClickListener {
                val uri: Uri = Uri.parse("tel:" + mShopList[position].mTelNumber)
                val intent = Intent(Intent.ACTION_DIAL, uri)
                mContext?.startActivity(intent)
            }
        }

        holder.openTime.text = mShopList[position].mOpenTime
        holder.budget.text = mShopList[position].mBudget
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        mContext = parent.context
        val item: View = LayoutInflater.from(parent.context).inflate(
            R.layout.gurunavi_shop_item, parent, false)
        return ShopViewHolder(item)
    }

    /**
     * ViewHolderクラス
     */
    class ShopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.gurunavi_shop_name)
        val image: NetworkImageView = itemView.findViewById(R.id.gurunavi_shop_image)
        val category: TextView = itemView.findViewById(R.id.gurunavi_shop_category)
        val tel: TextView = itemView.findViewById(R.id.gurunavi_shop_tel)
        val openTime: TextView = itemView.findViewById(R.id.gurunavi_shop_open_time)
        val budget: TextView = itemView.findViewById(R.id.gurunavi_shop_budget)

        val scrollView: ScrollView = itemView.findViewById(R.id.gurunavi_shop_text_scroll_view)
    }
}