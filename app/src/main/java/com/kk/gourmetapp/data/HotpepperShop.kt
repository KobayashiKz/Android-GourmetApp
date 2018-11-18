package com.kk.gourmetapp.data

class HotpepperShop(name: String, category: String, imageUrl: String, pageUrl: String,
                    openTime: String, budget: String) {

    // レストラン名
    val mName: String = name
    // レストランのカテゴリ
    val mCategory: String = category
    // 画像URL
    val mImageUrl: String = imageUrl
    // モバイル用WebページURL
    val mPageUrl: String = pageUrl
    // 営業時間
    val mOpenTime: String = openTime
    // 平均予算
    val mBudget: String = budget

}