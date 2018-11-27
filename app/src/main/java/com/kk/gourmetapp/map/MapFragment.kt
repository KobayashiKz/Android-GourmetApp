package com.kk.gourmetapp.map

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.kk.gourmetapp.R

class MapFragment : Fragment(), MapContract.View, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var mPresenter: MapPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root: View? = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment: SupportMapFragment = childFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    /**
     * {@inheritDoc}
     */
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        val latitude: Long = mPresenter.getGurunaviLatitude()
        val longitude: Long = mPresenter.getGurunaviLongitude()

        val location = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    override fun setPresenter(presenter: MapPresenter) {
        mPresenter = presenter
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}
