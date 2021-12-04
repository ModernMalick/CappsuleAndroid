package com.example.cappsule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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