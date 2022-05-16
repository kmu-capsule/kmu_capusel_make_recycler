package com.example.part3.testobjectdetection

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private val GALLERY_CODE: Int = 1112
    private val ImageSelect: ImageButton by lazy {
        findViewById(R.id.ImageSelect)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ImageSelect.setOnClickListener {
            openGallery()
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor?.moveToFirst() == true) {
            columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        return cursor?.getString(columnIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                //갤러리에서 가져오기
                GALLERY_CODE -> data?.data?.let { getRealPathFromURI(it) }
                    ?.let { Log.i("image", it) }   // 이미지 경로 로그에 출력
                else -> {}
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // 갤러리 인텐트를 부르고 사진을 선택하면 이전 액티비티로 돌아옴
        startActivityForResult(intent, GALLERY_CODE);
    }
}


