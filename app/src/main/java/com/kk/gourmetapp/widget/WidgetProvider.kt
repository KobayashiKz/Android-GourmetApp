package com.kk.gourmetapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.kk.gourmetapp.R
import com.kk.gourmetapp.recommend.RecommendActivity


class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?,
                          appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val views = RemoteViews(context?.packageName, R.layout.widget_layout)

        val imageIntent = Intent(IMAGE_ACTION)
        val pendingIntent: PendingIntent? = PendingIntent.getBroadcast(
            context, WIDGET_REQUEST_CODE, imageIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.widget_image_view, pendingIntent)

        // ウィジェットの更新
        appWidgetManager?.updateAppWidget(appWidgetIds, views)
    }



    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d("kkkk", "receive")

        val action: String? = intent?.action

        Log.d("kkkk", "receive: " + action)

        if (action.equals(IMAGE_ACTION)) {
            // おすすめ画面起動
            val recommendIntent = Intent(context, RecommendActivity::class.java)
            context?.startActivity(recommendIntent)
        }
    }

    companion object {
        // インテントフィルタ
        const val IMAGE_ACTION: String = "com.kk.gourmetapp.widget.IMAGE_CLICKED"
        // リクエストコード
        const val WIDGET_REQUEST_CODE: Int = 0
    }
}