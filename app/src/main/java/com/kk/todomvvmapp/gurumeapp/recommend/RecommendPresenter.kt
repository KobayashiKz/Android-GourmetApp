package com.kk.todomvvmapp.gurumeapp.recommend

import com.kk.todomvvmapp.gurumeapp.data.source.ShopRepository

class RecommendPresenter(mRecomendView: RecommendContract.View, repository: ShopRepository)
    : RecommendContract.UserActionListener {
}