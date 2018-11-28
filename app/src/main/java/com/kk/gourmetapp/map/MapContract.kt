package com.kk.gourmetapp.map

interface MapContract {

    interface View {
        fun setPresenter(presenter: MapPresenter)
    }

    interface Presenter {
        fun getGurunaviLatitude(): Double

        fun getGurunaviLongitude(): Double

        fun clearGurunaviAddressInfo()
    }

    interface Model {
        fun loadGurunaviLatitude(): Double

        fun loadGurunaviLongitude(): Double

        fun clearGurunaviAddress()
    }
}