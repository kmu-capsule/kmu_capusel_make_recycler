package com.example.part3.testobjectdetection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage

class DetectActivity : AppCompatActivity() {
    private val SelectBtn: Button by lazy {
        findViewById(R.id.ImageSelect)
    }
    private val UploadBtn: Button by lazy {
        findViewById(R.id.Upload)
    }
    private var ImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)

        SelectBtn.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivityForResult(intent,1000)
        }

        UploadBtn.setOnClickListener {
            ImageUploadToStorage()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            Log.d("Detect","RESULT_OK")
            when(requestCode) {
                1000 -> {
                    ImageUri = data?.getStringExtra("ImagePath")
//                    val imageURI = intent.getStringExtra("ImagePath")
                    Log.d("DetectedActivity","${data?.getStringExtra("ImagePath")}")
                }
            }
    }

    private fun ImageUploadToStorage() {
        // FirebaseStorage 인스턴스 생성
        var fbStorage: FirebaseStorage? = FirebaseStorage.getInstance()
        // FileName 생성
        val fileName = "${System.currentTimeMillis()}.png"
        Log.i("FileName", fileName)

        var storageRef = fbStorage?.reference?.child("object/photo")?.child(fileName)

        ImageUri?.let {
            storageRef?.putFile(it.toUri())?.addOnSuccessListener {
                Toast.makeText(baseContext, "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
            }
        }
    }
}