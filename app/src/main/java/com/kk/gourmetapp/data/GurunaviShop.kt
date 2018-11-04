package com.kk.gourmetapp.data

/**
 * ぐるなびのレストランのEntityクラス
 */
class GurunaviShop(name: String, category: String, imageUrl: String, pageUrl: String) {

    // レストラン名
    val mName: String = name
    // レストランのカテゴリ
    val mCategory: String = category
    // 画像URL
    val mImageUrl: String = imageUrl
    // モバイル用WebページURL
    val mPageUrl: String = pageUrl
}