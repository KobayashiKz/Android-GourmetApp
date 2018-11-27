package com.kk.gourmetapp.map

interface MapContract {

    interface View {
        fun setPresenter(presenter: MapPresenter)
    }

    interface Presenter {
        fun getGurunaviLatitude(): Long

        fun getGurunaviLongitude(): Long

        fun clearGurunaviAddressInfo()
    }

    interface Model {
        fun loadGurunaviLatitude(): Long

        fun loadGurunaviLongitude(): Long

        fun clearGurunaviAddress()
    }
}