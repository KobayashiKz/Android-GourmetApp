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

        // TODO ひとまずシドニーを中心としたマップを表示. 目的の住所を表示させる.
        val sydney = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}
