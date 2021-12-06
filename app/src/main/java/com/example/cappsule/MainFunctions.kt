package com.example.cappsule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import java.io.ByteArrayOutputStream

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