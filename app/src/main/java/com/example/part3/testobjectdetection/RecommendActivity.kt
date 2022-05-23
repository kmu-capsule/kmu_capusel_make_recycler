package com.example.part3.testobjectdetection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RecommendActivity : AppCompatActivity() {
    private val TAG = "RecommendActivity"
    private lateinit var selectedImageURI: Uri
    private lateinit var itemName: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        itemName = intent.getStringExtra(MainActivity.STRING_INTENT_ITEM_KEY).toString()

        val itemText = findViewById<TextView>(R.id.itemNameTextView)
        itemText.text = itemName

        initGoGalleryButton()
        initCancelButton()
        initSubmitURIButton()
    }

    private fun initSubmitURIButton() {
        val submitImageDataURIButton = findViewById<Button>(R.id.submitImageDataURIButton)
        submitImageDataURIButton.setOnClickListener {
            Toast.makeText(this, " 메인화면으로 이동합니다", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java).apply {
                Log.d(TAG, "${selectedImageURI}")
//                putExtra(MainActivity.STRING_INTENT_KEY, selectedImageURI)
                data = selectedImageURI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                putExtra(MainActivity.STRING_INTENT_ITEM_FROM_RECOMMEND_KEY, itemName)
            }
            setResult(Activity.RESULT_OK, intent)
            if (!isFinishing) finish()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initGoGalleryButton() {
        val goGallery = findViewById<Button>(R.id.goGalleryButton)
        goGallery.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    // READ_EXTERNAL_STORAGE의 권한이 PERMISSION_GRANTED와 같다면..
                    //TODO 권한이 잘 부여되었을 때상황, 갤러리에서 사진을 선택하는 코드 구현
                    getImageFromGallery()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 권한을 명시적으로 거부한 경우 true
                    // 처음보거나, 다시묻지 않음을 선택한 경우 false
                    popRePermissonUpDialog()
                }
                else -> {
                    // 처음봤을때 띄워줌
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun popRePermissonUpDialog() {
        AlertDialog.Builder(this)
            .setTitle("갤러리 접근 권한 요청")
            .setMessage("사진 선택을 위해서는 갤러리 접근 권한이 필요합니다")
            .setPositiveButton("확인", { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            })
            .setNegativeButton("취소", { _, _ -> })
            .show()
    }

    private fun getImageFromGallery() {
        Toast.makeText(this, " 갤러리로 이동합니다!!", Toast.LENGTH_SHORT).show()
//        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }


    private fun initCancelButton() {
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show()
            return
        }

        when (requestCode) {
            2000 -> {
                if (data?.data == null) {
                    Toast.makeText(this, "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()

                    return
                }

                // 받아온 uri값에 대한 권한 유지
                val takeFlags = intent.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                selectedImageURI = data?.data!!
                Log.d(TAG,"taskFlags!!")
                this.contentResolver.takePersistableUriPermission(selectedImageURI, takeFlags)
                val testImageView = findViewById<ImageView>(R.id.testImageView)
                testImageView.setImageURI(selectedImageURI)


            }
            else -> {
                Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show()
            }
        }

    }

}