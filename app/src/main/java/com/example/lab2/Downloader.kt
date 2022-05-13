package com.example.lab2

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayOutputStream

public class Downloader(var imageView: ImageView, img_id: Int, pref: SharedPreferences?) : AsyncTask<String, Void, Bitmap?>() {
    var img_id = img_id
    var pref = pref
    override fun doInBackground(vararg urls: String): Bitmap? {
        if (pref?.getString("image".plus(img_id.toString()), "") != "")
        {
            var bytes = Base64.decode(pref?.getString("image".plus(img_id.toString()), ""), Base64.DEFAULT)
            var image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return image
        }
        val imageURL = urls[0]
        var image: Bitmap? = null
        try {
            val `in` = java.net.URL(imageURL).openStream()
            image = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            Log.e("Error Message", e.message.toString())
            e.printStackTrace()
        }
        val editor = pref?.edit()
        editor?.putString("image".plus(img_id.toString()), encodeImage(image!!))
        editor?.apply()
        return image
    }

    override fun onPostExecute(result: Bitmap?) {
        imageView.setImageBitmap(result)
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}