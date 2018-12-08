package com.kk.gourmetapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kk.gourmetapp.R
import com.kk.gourmetapp.recommend.RecommendActivity

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?,
                          appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val views = RemoteViews(context?.packageName, R.layout.widget_layout)

        // タップ時にアプリを起動させるようにPendingIntentを設定
        val intent = Intent(context, RecommendActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,
            WIDGET_REQUEST_CODE, intent, 0)
        views.setOnClickPendingIntent(R.id.widget_image_view, pendingIntent)

        // ウィジェットの更新
        appWidgetManager?.updateAppWidget(appWidgetIds, views)
    }

    companion object {
        // リクエストコード
        const val WIDGET_REQUEST_CODE: Int = 0
    }
}