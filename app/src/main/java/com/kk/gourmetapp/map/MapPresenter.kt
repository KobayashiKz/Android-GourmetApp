package com.kk.gourmetapp.map

import android.content.Context

class MapPresenter(mapView: MapContract.View, context: Context): MapContract.Presenter {

    private var mView: MapContract.View = mapView
    private var mContext: Context = context
    private var mModel: MapContract.Model = MapModel(context)

    init {
        mView.setPresenter(this)
    }

    override fun getGurunaviLatitude(): Double {
        return mModel.loadGurunaviLatitude()
    }

    override fun getGurunaviLongitude(): Double {
        return mModel.loadGurunaviLongitude()
    }

    override fun clearGurunaviAddressInfo() {
        mModel.clearGurunaviAddress()
    }
}