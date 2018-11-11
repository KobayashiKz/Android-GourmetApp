package com.kk.gourmetapp.select

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.kk.gourmetapp.R
import java.io.InputStream

class SelectFragment : Fragment(), SelectContract.View {

    private var mSelectPresenter: SelectPresenter? = null

    private var mSelectImageView: ImageView? = null

    private var mRecognizeImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View? = inflater.inflate(R.layout.fragment_select, container, false)
        mSelectImageView = root?.findViewById(R.id.select_image_view)

        // 画像認証ボタンタップされたら画像解析を開始する
        val recognizeButton: Button? = root?.findViewById(R.id.recognize_button)
        recognizeButton?.setOnClickListener {
            mSelectPresenter?.startRecognizerImage(mRecognizeImageUri)
        }

        // キャンセルボタンタップ時にはInputStreamをcloseする
        val cancelButton: Button? = root?.findViewById(R.id.recognize_cancel_button)
        cancelButton?.setOnClickListener {
            // TODO: キャンセルボタンの処理
        }

        // ギャラリー呼び出し処理
        showGallery()

        return root
    }

    /**
     * {@inheritDoc}
     */
    override fun setUserActionListener(selectPresenter: SelectPresenter) {
        mSelectPresenter = selectPresenter
    }

    /**
     * ギャラリー表示
     */
    private fun showGallery() {
        val intentGallery = Intent(Intent.ACTION_GET_CONTENT)
        intentGallery.type = GALLERY_MIME_TYPE
        startActivityForResult(intentGallery, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            // 選択された画像が取得できたらUI更新する
            mRecognizeImageUri = data?.data
            val inputStream: InputStream = context?.contentResolver!!.openInputStream(data?.data)
            val image: Bitmap = BitmapFactory.decodeStream(inputStream)
            mSelectImageView?.setImageBitmap(image)
        } else {
            // do nothing.
        }
    }

    companion object {
        // リクエストコード
        private const val REQUEST_GALLERY: Int = 0
        // ギャラリーのMIMEタイプ
        private const val GALLERY_MIME_TYPE: String = "image/*"

        fun newInstance(): SelectFragment {
            return SelectFragment()
        }
    }
}
