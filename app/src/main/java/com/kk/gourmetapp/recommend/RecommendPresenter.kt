package com.kk.gourmetapp.recommend

import com.kk.gourmetapp.data.source.ShopRepository

class RecommendPresenter(mRecomendView: RecommendContract.View, repository: ShopRepository)
    : RecommendContract.UserActionListener {
}