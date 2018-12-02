package com.kk.gourmetapp.recommend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.kk.gourmetapp.R
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.map.MapActivity
import com.kk.gourmetapp.util.PreferenceUtil

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
        holder.openTime.text = mShopList[position].mOpenTime
        holder.budget.text = mShopList[position].mBudget

        // Mapボタンタップ時にはMapActivityを起動する
        holder.mapButton.setOnClickListener {
            val latitude: Double = mShopList[position].mLatitude
            val longitude: Double = mShopList[position].mLongitude

            // 緯度経度をPreferenceに保存しておく. Double型はそのまま保存できないのでLong型のビット表現で保存.
            val preference: SharedPreferences = mContext!!.getSharedPreferences(
                PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
            preference.edit().putLong(
                PreferenceUtil.KEY_SHOP_LATITUDE,
                java.lang.Double.doubleToRawLongBits(latitude)).apply()
            preference.edit().putLong(
                PreferenceUtil.KEY_SHOP_LONGITUDE,
                java.lang.Double.doubleToRawLongBits(longitude)).apply()

            val intent = Intent(mContext, MapActivity::class.java)
            mContext?.startActivity(intent)
        }

        setFadeAnimation(holder.itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        mContext = parent.context
        val item: View = LayoutInflater.from(parent.context).inflate(
            R.layout.hotpepper_shop_item, parent, false
        )
        return ShopViewHolder(item)
    }

    /**
     * フェードインアニメーションの付加
     * @param view フェードインさせるビュー
     */
    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = ITEM_ANIMATION_DURATION
        view.startAnimation(anim)
    }

    /**
     * ViewHolderクラス
     */
    class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.hotpepper_shop_name)
        val image: NetworkImageView = itemView.findViewById(R.id.hotpepper_shop_image)
        val category: TextView = itemView.findViewById(R.id.hotpepper_shop_category)
        val openTime: TextView = itemView.findViewById(R.id.hotpepper_shop_open_time)
        val budget: TextView = itemView.findViewById(R.id.hotpepper_shop_budget)
        val mapButton: Button = itemView.findViewById(R.id.map_button)

        val scrollView: ScrollView = itemView.findViewById(R.id.hotpepper_shop_text_scroll_view)
    }

    companion object {
        const val ITEM_ANIMATION_DURATION: Long = 800
    }
}