package com.example.part3.testobjectdetection

import android.net.Uri

data class DetectedItem(
    val name : String,
    var uri : Uri?
){
    constructor() : this("",null)
}
