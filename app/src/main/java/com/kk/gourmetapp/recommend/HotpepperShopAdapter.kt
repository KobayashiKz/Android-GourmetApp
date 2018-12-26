package com.kk.gourmetapp.recommend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
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
import com.kk.gourmetapp.util.GoogleAnalyticsUtil
import com.kk.gourmetapp.util.PreferenceUtil

class HotpepperShopAdapter(shopList: MutableList<HotpepperShop>, imageLoader: ImageLoader?)
    : RecyclerView.Adapter<HotpepperShopAdapter.ShopViewHolder>() {

    // レストランリスト
    private var mShopList: MutableList<HotpepperShop> = shopList
    // 画像のイメージローダー
    private var mImageLoader: ImageLoader? = imageLoader

    private lateinit var mContext: Context

    override fun getItemCount(): Int {
        return mShopList.size
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.scrollView.isVerticalScrollBarEnabled = false

        // お店情報の設定
        setShopName(holder, position)
        setShopImage(holder, position)
        setShopCategory(holder, position)
        setShopOpenTime(holder, position)
        setShopBudget(holder, position)
        setShopMapButton(holder, position)

        // フェードイン
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
     * ショップ名の設定
     * @param holder   アダプターにセットするView
     * @param position RecyclerViewのポジション
     */
    private fun setShopName(holder: ShopViewHolder, position: Int) {
        if (!TextUtils.isEmpty(mShopList[position].mName)) {
            holder.name.text = mShopList[position].mName
        } else {
            holder.name.text = mContext.getString(R.string.text_empty_info)
        }
    }

    /**
     * お店画像の設定
     * @param holder   アダプターにセットするView
     * @param position RecyclerViewのポジション
     */
    private fun setShopImage(holder: ShopViewHolder, position: Int) {
        // 画像URLが存在する場合は取得し存在しない場合はデフォルト画像を表示する
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
            mContext.startActivity(intent)

            GoogleAnalyticsUtil.sendActionEvent(mContext.applicationContext,
                GoogleAnalyticsUtil.ActionEventAction.CLICK_SHOP_DETAIL.key,
                GoogleAnalyticsUtil.ActionEventCategory.CLICK_HOTPEPPER.key)
        }

    }

    /**
     * カテゴリー情報の設定
     * @param holder   アダプターにセットするView
     * @param position RecyclerViewのポジション
     */
    private fun setShopCategory(holder: ShopViewHolder, position: Int) {
        if (!TextUtils.isEmpty(mShopList[position].mCategory)) {
            holder.category.text = mShopList[position].mCategory
        } else {
            holder.category.text = mContext.getString(R.string.text_empty_info)
        }
    }

    /**
     * 営業時間の設定
     * @param holder   アダプターにセットするView
     * @param position RecyclerViewのポジション
     */
    private fun setShopOpenTime(holder: ShopViewHolder, position: Int) {
        if (!TextUtils.isEmpty(mShopList[position].mOpenTime)) {
            holder.openTime.text = mShopList[position].mOpenTime
        } else {
            holder.openTime.text = mContext.getString(R.string.text_empty_info)
        }
    }

    /**
     * 平均予算の設定
     * @param holder   アダプターにセットするView
     * @param position RecyclerViewのポジション
     */
    private fun setShopBudget(holder: ShopViewHolder, position: Int) {
        if (!TextUtils.isEmpty(mShopList[position].mBudget)) {
            holder.budget.text = mShopList[position].mBudget
        } else {
            holder.budget.text = mContext.getString(R.string.text_empty_info)
        }
    }

    /**
     * マップボタンの設定
     * @param holder   アダプターにセットするView
     * @param position RecyclerViewのポジション
     */
    private fun setShopMapButton(holder: ShopViewHolder, position: Int) {
        holder.mapButton.setOnClickListener {
            val latitude: Double = mShopList[position].mLatitude
            val longitude: Double = mShopList[position].mLongitude

            // 緯度経度をPreferenceに保存しておく. Double型はそのまま保存できないのでLong型のビット表現で保存.
            val preference: SharedPreferences = mContext.getSharedPreferences(
                PreferenceUtil.KEY_PREFERENCE_MAP, Context.MODE_PRIVATE)
            preference.edit().putLong(
                PreferenceUtil.KEY_SHOP_LATITUDE,
                java.lang.Double.doubleToRawLongBits(latitude)).apply()
            preference.edit().putLong(
                PreferenceUtil.KEY_SHOP_LONGITUDE,
                java.lang.Double.doubleToRawLongBits(longitude)).apply()

            // Mapボタンタップ時にはMapActivityを起動する
            val intent = Intent(mContext, MapActivity::class.java)
            mContext.startActivity(intent)

            GoogleAnalyticsUtil.sendActionEvent(mContext.applicationContext,
                GoogleAnalyticsUtil.ActionEventAction.CLICK_SHOP_MAP.key,
                GoogleAnalyticsUtil.ActionEventCategory.CLICK_HOTPEPPER.key)
        }
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