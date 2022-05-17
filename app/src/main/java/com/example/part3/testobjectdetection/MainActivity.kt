package com.example.part3.testobjectdetection


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }

    private val gotItemList = mutableListOf<detectedItem>()
    private lateinit var mAdatper : Adapter
    private lateinit var getImageURI: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 데이터를 받아왔다는 전제하, 데이터를 넘겨줄때는 detectedItem에서 itemname을 가져오고 이미지 uri를 null값을 줌
        gotItemList.add(detectedItem("신발",null))
        gotItemList.add(detectedItem("모자",null))
        gotItemList.add(detectedItem("가방",null))
        gotItemList.add(detectedItem("노트북",null))

        // 물체가 인식 된 후,인식된 물체가 리스트로 넘어온다고 가정

        // 넘어온 리스트로 리사이클러뷰를 만들어준다.
        // 리사이클러뷰의 갤러리 선택을 누르면, 새로운 액티비티가 열리고 추천 사진 목록을 보여준다 ( 정민 )
        // 새로운 액티비티에서 갤러리 사진 선택을 누르면, 갤러리가 열린다.
        // 갤러리에서 사진을 선택하면 된다.

//         uri값을 반환받기 위한 콜백함수
        getImageURI = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.getStringExtra(STRING_INTENT_KEY)
                val itemName = result.data?.getStringExtra(STRING_INTENT_ITEM_KEY)
                if (uri == null ) {
                    Log.d(TAG,"uri값을 가져오지 못했습니다")
                    return@registerForActivityResult
                }
                Log.d(TAG,"${uri?.toUri()}")
//                gotItemList.add(detectedItem("Random().toString()", uri.toUri()))
                // find로 아이템을 찾은후, 해당 uri값만 변경 후 리스트 변화를 등록해줌
                gotItemList.find { it.name == itemName }?.let {
                    it.uri = uri.toUri()
                }
                mAdatper.notifyDataSetChanged()
            }
        }

//        getImageURI = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
//
//
//        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdatper = Adapter(mButtonClickListener = {
            onButtonClickedListener(it)
        })
        recyclerView.adapter = mAdatper
        mAdatper.submitList(gotItemList)
    }

    private fun onButtonClickedListener(name : String) {
        // launch를 작동시켜 인탠트 전환하고, onCreate에서 콜백함수로 결과를 받아옴
        Toast.makeText(this,"${name}, button clicked!!",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,RecommendActivity::class.java)
        intent.putExtra(STRING_INTENT_ITEM_KEY,name)
        getImageURI.launch(intent)
//        startActivity(intent)


    }
    companion object {
        const val STRING_INTENT_KEY = "imageURI"
        const val STRING_INTENT_ITEM_KEY = "itemName"
    }

}