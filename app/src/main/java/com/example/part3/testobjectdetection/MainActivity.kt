package com.example.part3.testobjectdetection

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var ImageURI: Uri     // 이미지 uri
    private var ImageAbsPath: String? = ""    // uri로부터 얻어온 이미지 절대 경로
    private val GALLERY_CODE: Int = 1112

    private val ImageSelect: ImageButton by lazy {
        findViewById(R.id.ImageSelect)
    }
    private val ImageUpload: Button by lazy {
        findViewById(R.id.ImageUpload)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ImageSelect.setOnClickListener {
            openGallery()
        }

        ImageUpload.setOnClickListener {
            ImageUploadToStorage()
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
            ImageURI = data?.data.toString().toUri()
            when (requestCode) {
                GALLERY_CODE ->
                    ImageAbsPath = data?.data?.let { getRealPathFromURI(it) }
//                GALLERY_CODE -> data?.data?.let { getRealPathFromURI(it)?.let { Log.i("imagePath", it) } }
                else -> {}
            }
            // 이미지 Uri 로그에 출력
            Log.i("ImageUri", ImageURI.toString())
            // 선택된 이미지 화면에 띄워줌
            ImageSelect.setImageURI(ImageURI)
            Toast.makeText(baseContext, "사진 선택 완료", Toast.LENGTH_SHORT).show()
            // 이미지 절대 경로 로그에 출력
            Log.i("ImagePath", ImageAbsPath.toString())
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // 갤러리 인텐트를 부르고 사진을 선택하면 이전 액티비티로 돌아옴
        startActivityForResult(intent, GALLERY_CODE);
    }

    private fun ImageUploadToStorage() {
        // FirebaseStorage 인스턴스 생성
        var fbStorage: FirebaseStorage? = FirebaseStorage.getInstance()
        // FileName 생성
        val fileName = "${System.currentTimeMillis()}.png"
        Log.i("FileName", fileName)

        var storageRef = fbStorage?.reference?.child("object/photo")?.child(fileName)

        storageRef?.putFile(ImageURI!!)?.addOnSuccessListener {
            Toast.makeText(baseContext, "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
        }
    }
}


