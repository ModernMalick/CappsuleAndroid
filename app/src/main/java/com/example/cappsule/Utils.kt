package com.example.cappsule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.net.URL

fun getBytes(bitmap: Bitmap?): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, stream)
    return stream.toByteArray()
}

fun getImage(image: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(image, 0, image.size)
}

fun toaster(context: Context?, string: String?) {
    val toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
    toast.show()
}

fun getData(city: String, unit: String): String {
    return URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$unit&appid=$apiKey").readText(Charsets.UTF_8)
}

const val apiKey = "d6ec42cf87a93d3d280eb8b3e98d8436"