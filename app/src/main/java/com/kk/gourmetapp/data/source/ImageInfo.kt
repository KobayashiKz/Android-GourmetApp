package com.kk.gourmetapp.data.source

class ImageInfo(name: String, score: Float, typeHierarchy: String?) {

    // 画像解析判定をおこなった名前
    private val mName: String = name
    // 画像解析判定の類似スコア
    private val mScore: Float = score
    // カテゴリ
    private val mTypeHierarchy: String? = typeHierarchy

    fun getName(): String {
        return mName
    }

    fun getScore(): Float {
        return mScore
    }

    fun getTypeHierarchy(): String? {
        return mTypeHierarchy
    }
}