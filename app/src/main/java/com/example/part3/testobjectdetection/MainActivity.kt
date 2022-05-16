package com.example.part3.testobjectdetection


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }

    val list = mutableListOf<detectedItem>(detectedItem("신발"),detectedItem("모자"))
    private lateinit var mAdatper : Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 물체가 인식 된 후,인식된 물체가 리스트로 넘어온다고 가정

        // 넘어온 리스트로 리사이클러뷰를 만들어준다.
        // 리사이클러뷰의 갤러리 선택을 누르면, 새로운 액티비티가 열리고 추천 사진 목록을 보여준다 ( 정민 )
        // 새로운 액티비티에서 갤러리 사진 선택을 누르면, 갤러리가 열린다.
        // 갤러리에서 사진을 선택하면 된다.

        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdatper = Adapter(mButtonClickListener = {
            onButtonClickedListener(it)
        })
        recyclerView.adapter = mAdatper

        val list = mutableListOf<detectedItem>().apply {
            add(detectedItem("신발"))
            add(detectedItem("모자"))
        }

        mAdatper.submitList(list)
    }

    private fun onButtonClickedListener(name : String) {
        Toast.makeText(this,"${name}, button clicked!!",Toast.LENGTH_SHORT).show()
    }

}