package com.kk.gourmetapp.data

/**
 * ぐるなびのレストランのEntityクラス
 */
class GurunaviShop(name: String, category: String, imageUrl: String, pageUrl: String,
                   tel: String, openTime: String, budget: String, latitude: Double, longitude: Double) {

    // レストラン名
    val mName: String = name
    // レストランのカテゴリ
    val mCategory: String = category
    // 画像URL
    val mImageUrl: String = imageUrl
    // モバイル用WebページURL
    val mPageUrl: String = pageUrl
    // 電話番号
    val mTelNumber: String = tel
    // 営業時間
    val mOpenTime: String = openTime
    // 平均予算
    val mBudget: String = budget
    // 緯度
    val mLatitude: Double = latitude
    // 経度
    val mLongitude: Double = longitude
}