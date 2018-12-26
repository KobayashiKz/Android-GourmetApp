package com.kk.gourmetapp.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ibm.watson.developer_cloud.service.exception.NotFoundException
import com.ibm.watson.developer_cloud.service.exception.RequestTooLargeException
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException
import com.ibm.watson.developer_cloud.service.security.IamOptions
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions
import com.kk.gourmetapp.data.source.DataSource
import com.kk.gourmetapp.util.GoogleAnalyticsUtil
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.*
import kotlin.concurrent.thread

class ImageRecognizer(context: Context) {

    private val mContext: Context = context

    /**
     * Watson Visual Recognizerによる画像認証処理
     * @param uri      画像解析対象のUri
     * @param callback 画像解析後に発火するコールバック
     */
    fun recognizeImage(uri: Uri?, callback: DataSource.RecognizeCallback) {
        GoogleAnalyticsUtil.sendActionEvent(mContext.applicationContext,
            GoogleAnalyticsUtil.ActionEventAction.OTHER_START_IMAGE_RECOGNIZE.key)

        thread {
            val apiInfoList: MutableList<String> = getRecoginzeApiKey()
            analize(
                apiInfoList[APIINFO.API_KEY.getIndex()],
                apiInfoList[APIINFO.VERSION.getIndex()],
                uri,
                callback
            )
        }
    }

    /**
     * 画像解析
     * @param apiKey   WatsonAPIキー
     * @param version  Watsonバージョン
     * @param uri      画像解析対象のUri
     * @param callback 画像解析後に発火するコールバック
     * @return 画像解析結果
     */
    private fun analize(apiKey: String, version: String,uri: Uri?,
                        callback: DataSource.RecognizeCallback): Boolean {
        var result: Boolean
        try {
            val inputStream: InputStream = mContext.contentResolver.openInputStream(uri)

            val options: IamOptions = IamOptions.Builder().apiKey(apiKey).build()
            val visualRecognition: VisualRecognition = VisualRecognition(version, options)

            // 分類器の設定.日本語/food分類器を使用
            val classifyOptions: ClassifyOptions = ClassifyOptions.Builder()
                .imagesFile(inputStream)
                .imagesFilename(RECOGNIZE_FILE_NAME)
                .threshold(RECOGNIZE_THRESHOLD)
                .acceptLanguage(ClassifyOptions.AcceptLanguage.JA)
                .classifierIds(RECOGNIZE_TYPE)
                .build()

            // 画像認証をおこなう
            val responce: ClassifiedImages = visualRecognition.classify(classifyOptions).execute()

            Log.d(TAG, "Recognition result: $responce")

            callback.onFinish(responce)

            result = true
        } catch (e: NotFoundException) {
            // TODO: 異常系のハンドリング
            Log.w(TAG, "Failed authentication of Watson Visual Recognizer because 404 error. " + e.message)
            result = false
        } catch (e: RequestTooLargeException) {
            Log.w(TAG, "Failed authentication of Watson Visual Recognizer because 413 error. " + e.message)
            result = false
        } catch (e: ServiceResponseException) {
            Log.w(TAG, "Failed authentication of Watson Visual Recognizer. Status: "
                        + e.statusCode + ": " + e.message)
            result = false
        }
        return result
    }

    /**
     * WatsonのVisual RecognizerのAPIキーを取得
     * @return APIキーリスト
     *         0:APIキー
     *         1:バージョン
     */
    private fun getRecoginzeApiKey(): MutableList<String> {
        // assetsフォルダのテキストファイルを読み込む
        val inputStream: InputStream? = mContext.assets?.open(NAME_AUTH_INFO_FILE)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream) as Reader?)

        val infoList: MutableList<String> = ArrayList()

        // それぞれリストに詰めてreturn
        val apikey: String = bufferedReader.readLine()
        infoList.add(apikey)
        val version: String = bufferedReader.readLine()
        infoList.add(version)

        return infoList
    }

    /**
     * Watson Visual Recognition APIテキストのインデックス情報
     * 実際のテキストファイルはassets配下に保存
     */
    enum class APIINFO{
        API_KEY,
        VERSION;

        fun getIndex(): Int {
            return ordinal
        }
    }

    companion object {
        const val TAG: String = "ImageRecognizer"

        // Watson Visual RecognizerのAPIキーテキストファイル名
        // 1行目：APIキー, 2行目：バージョン
        const val NAME_AUTH_INFO_FILE: String = "watson-recognize-info.txt"
        // 画像認証のファイル名
        const val RECOGNIZE_FILE_NAME: String = "recognizeImage"
        // 画像認証の精度
        const val RECOGNIZE_THRESHOLD: Float = 0.5f
        // 分類器
        val RECOGNIZE_TYPE:List<String> = listOf("food")

        fun newInstance(context: Context): ImageRecognizer {
            return ImageRecognizer(context)
        }
    }
}