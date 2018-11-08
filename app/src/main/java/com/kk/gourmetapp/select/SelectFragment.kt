package com.kk.gourmetapp.select

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kk.gourmetapp.R
import java.io.InputStream

class SelectFragment : Fragment(), SelectContract.View {

    private var mSelectPresenter: SelectPresenter? = null

    private var mSelectImageView: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View? = inflater.inflate(R.layout.fragment_select, container, false)
        mSelectImageView = root?.findViewById(R.id.select_image_view)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            // 選択された画像が取得できたらUI更新する
            val inputStream: InputStream = context?.contentResolver!!.openInputStream(data?.data)
            val image: Bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            mSelectImageView?.setImageBitmap(image)
        } else {
            // do nothing.
        }
    }

    /**
     * ギャラリー表示
     */
    private fun showGallery() {
        val intentGallery = Intent(Intent.ACTION_GET_CONTENT)
        intentGallery.type = GALLERY_MIME_TYPE
        startActivityForResult(intentGallery, REQUEST_GALLERY)
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
