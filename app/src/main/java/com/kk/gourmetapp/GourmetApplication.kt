package com.kk.gourmetapp

import android.app.Application
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.kk.gourmetapp.util.GoogleAnalyticsUtil

class GourmetApplication: Application() {

    private var sAnalytics: GoogleAnalytics? = null
    private var sTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()

        sAnalytics = GoogleAnalytics.getInstance(this)
        val util = GoogleAnalyticsUtil()
        util.setApplicationContext(applicationContext)
    }

    /**
     * Gets the default [Tracker] for this [Application].
     * @return tracker
     */
    @Synchronized
    fun getDefaultTracker(): Tracker? {
        if (sTracker == null) {
            sTracker = sAnalytics!!.newTracker(R.xml.global_tracker)
        }

        return sTracker
    }
}