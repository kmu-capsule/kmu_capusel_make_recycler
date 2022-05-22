package com.example.part3.testobjectdetection

import android.net.Uri

data class UploadModel(
    val name : String,
    val url : String?
){
    constructor() : this("",null)
}
