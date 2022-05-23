package com.example.part3.testobjectdetection

import android.R.attr.data
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {


    private val TAG = "MainActivity"

    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }

    private val storage by lazy {
        Firebase.storage
    }

    private lateinit var db: DatabaseReference
//    private val db by lazy {
//        Firebase.database.reference
//    }

    private val gotItemList = mutableListOf<DetectedItem>()
    private lateinit var mAdatper: Adapter
    private lateinit var getImageURI: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.database.reference

        // 데이터를 받아왔다는 전제하, 데이터를 넘겨줄때는 detectedItem에서 itemname을 가져오고 이미지 uri를 null값을 줌
        gotItemList.add(DetectedItem("신발", null))
        gotItemList.add(DetectedItem("모자", null))
        gotItemList.add(DetectedItem("가방", null))
//        gotItemList.add(DetectedItem("노트북",null))

        // 물체가 인식 된 후,인식된 물체가 리스트로 넘어온다고 가정
        // 넘어온 리스트로 리사이클러뷰를 만들어준다.
        // 리사이클러뷰의 갤러리 선택을 누르면, 새로운 액티비티가 열리고 추천 사진 목록을 보여준다 ( 정민 )
        // 새로운 액티비티에서 갤러리 사진 선택을 누르면, 갤러리가 열린다.
        // 갤러리에서 사진을 선택하면 된다.

        initActivityResultRegister()
        initRecyclerView()
        initSubmitButton()

    }

    private fun initSubmitButton() {
        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            Log.d(TAG, "on Clicked initSubmitButton !! ")
            gotItemList.forEach { item ->
                if (item.uri == null) {
                    Log.d(TAG, "image src is null !")
                } else {
                    Log.d(TAG, "forEach RUN item.uri : ${item.uri} ")
                    // uri 즉 이미지가 등록된 사진들만 파이어베이스에 저장시켜야함
//                    dummyUploader()
                    uploadPhotoToFirebase(item.uri!!,
                        mSuccessHandler = { url ->
                            saveObjectSetToDatabase(FB_DB_USER_AUTH_KEY,
                                FB_DB_CAPSULE_KEY,
                                item.name,
                                url)
                        }, mErrorHandler = {
                            Toast.makeText(this, "사진 저장에 실패 했습니다", Toast.LENGTH_SHORT).show()
                        })
                }
            }
        }
    }

    private fun dummyUploader() {
        val fileName = "${System.currentTimeMillis()}.png"
//        storage.reference.child("object/photo").child(fileName)
//            .putFile()
//            .addOnCompleteListener{ mIt ->
//                if ( mIt.isSuccessful ) {
//
//                }else {
//                    // 파일 업로드 실패 시
//                    mErrorHandler()
//                }
//            }
    }

    private fun uploadPhotoToFirebase(
        mUri: Uri,
        mSuccessHandler: (String) -> Unit,
        mErrorHandler: () -> Unit,
    ) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("object/photo").child(fileName)
            .putFile(mUri)
            .addOnCompleteListener { mIt ->
                if (mIt.isSuccessful) {
                    Log.d(TAG, "isSuccessful !")
                    // 파일 업로드 성공 시
                    storage.reference.child("object/photo").child(fileName).downloadUrl
                        .addOnSuccessListener { url ->
                            Log.d(TAG, "url  : ${url}")
                            mSuccessHandler(url.toString())
                        }
                } else {
                    // 파일 업로드 실패 시
                    mErrorHandler()
                }
            }
    }

    private fun saveObjectSetToDatabase(
        dummyFbDbUserAuthKey: String,
        dummyFbDbCapsuleKey: String,
        name: String,
        url: String,
    ) {
        val userDB = db.child("Users").child(FB_DB_USER_AUTH_KEY).child(FB_DB_CAPSULE_KEY)
        val uploadModel = UploadModel(name, url)
        userDB.push().setValue(uploadModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "캡슐 저장에 성공했습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "캡슐 저장에 실패 했습니다", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "리얼타임 데이터베이스에 저장 실패")
                }
            }

    }


    @SuppressLint("WrongConstant")
    private fun initActivityResultRegister() {
        //         uri값을 반환받기 위한 콜백함수
        getImageURI =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

//                val uri = result.data?.getStringExtra(STRING_INTENT_KEY)
//                    val uri = result.data?.getParcelableExtra<Uri>(STRING_INTENT_KEY)
                    val uri = result.data?.data
//                    val takeFlags = intent.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    Log.d(TAG, "intent result.data = ${result.data?.data}")
                    val itemName =
                        result.data?.getStringExtra(STRING_INTENT_ITEM_FROM_RECOMMEND_KEY)

                    if (uri == null) {
                        Log.d(TAG, "uri값을 가져오지 못했습니다")
                        return@registerForActivityResult
                    }
//                    this.contentResolver.takePersistableUriPermission(uri, takeFlags)

                    Log.d(TAG, "${uri}")
//                this.contentResolver.takePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION)
//                contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                getContentResolver().takePersistablePermission(uri., Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                gotItemList.add(detectedItem("Random().toString()", uri.toUri()))
                    // find로 아이템을 찾은후, 해당 uri값만 변경 후 리스트 변화를 등록해줌
                    gotItemList.find { it.name == itemName }?.let {
                        it.uri = uri
                        Log.d(TAG, "update url done")
                    }
                    mAdatper.notifyDataSetChanged()
                }
            }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdatper = Adapter(mButtonClickListener = {
            onButtonClickedListener(it)
        })
        recyclerView.adapter = mAdatper
        mAdatper.submitList(gotItemList)
    }

    private fun onButtonClickedListener(name: String) {
        // launch를 작동시켜 인탠트 전환하고, onCreate에서 콜백함수로 결과를 받아옴
        Toast.makeText(this, "${name}, button clicked!!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RecommendActivity::class.java)
        intent.putExtra(STRING_INTENT_ITEM_KEY, name)
//        getContent.launch(intent)
        getImageURI.launch(intent)
//        startActivity(intent)

//        val temp = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        startActivity(temp)


    }

    companion object {
        const val STRING_INTENT_KEY = "imageURI"
        const val STRING_INTENT_ITEM_KEY = "itemName"
        const val STRING_INTENT_ITEM_FROM_RECOMMEND_KEY = "itemfromRecommend"

        // 임시 데이터 (일회성)
        const val FB_DB_USER_AUTH_KEY = "asdfifeiofjn1233"
        const val FB_DB_CAPSULE_KEY = "capsulekey123vasdfg"
    }

}