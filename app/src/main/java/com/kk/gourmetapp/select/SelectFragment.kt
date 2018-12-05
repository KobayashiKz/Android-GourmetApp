package com.kk.gourmetapp.select

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.kk.gourmetapp.R
import java.io.InputStream

class SelectFragment : Fragment(), SelectContract.View {

    private var mSelectPresenter: SelectPresenter? = null

    private var mSelectImageView: ImageView? = null

    private var mRecognizeImageUri: Uri? = null

    private var mLoadingDialog: RecognizingDialog? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View? = inflater.inflate(R.layout.fragment_select, container, false)
        mSelectImageView = root?.findViewById(R.id.select_image_view)

        // 画像認証ボタンタップされたら画像解析を開始する
        val recognizeButton: Button? = root?.findViewById(R.id.recognize_button)
        recognizeButton?.setOnClickListener {
            mSelectPresenter?.startRecognizerImage(mRecognizeImageUri)

            // 読み込み中ダイアログを表示
            mLoadingDialog = RecognizingDialog()
            mLoadingDialog?.show(fragmentManager, "tag")
        }

        // キャンセルボタンタップ時には画面を戻す
        val cancelButton: Button? = root?.findViewById(R.id.recognize_cancel_button)
        cancelButton?.setOnClickListener {
            activity?.setResult(Activity.RESULT_CANCELED)
            activity?.finish()
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
            closeCanceled()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun showRecognizeToast(keyword: String?) {
        Toast.makeText(context, keyword, Toast.LENGTH_SHORT).show()
    }

    /**
     * {@inheritDoc}
     */
    override fun dismissProgressDialog() {
        mLoadingDialog?.dismiss()
    }

    /**
     * {@inheritDoc}
     */
    override fun close() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

    /**
     * 選択画面を閉じる
     */
    private fun closeCanceled() {
        activity?.setResult(Activity.RESULT_CANCELED)
        activity?.finish()
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

    /**
     * 読み込み中ダイアログ
     */
    class RecognizingDialog: DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // 透過背景でキャンセル不能なダイアログを生成
            val dialog: Dialog = Dialog(activity, R.style.TransparentDialogTheme)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            return dialog
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.dialog_recognizing, null)
        }
    }
}
