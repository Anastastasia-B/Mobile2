package com.example.lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.opengl.ETC1.encodeImage
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class InfoFragment : Fragment() {

    lateinit var title: TextView
    lateinit var category: TextView
    lateinit var price: TextView
    lateinit var rating: TextView
    lateinit var description: TextView
    lateinit var web_view: WebView
    lateinit var button: Button
    var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        title = view.findViewById(R.id.txt_title)
        category = view.findViewById(R.id.txt_category)
        price = view.findViewById(R.id.txt_price)
        rating = view.findViewById(R.id.txt_rating)
        description = view.findViewById(R.id.txt_description)
        button = view.findViewById(R.id.btn_add_favorites)

        var product = Gson().fromJson(getArguments()?.getString("product"), Product::class.java)

        web_view = view.findViewById(R.id.web_view)
        web_view.getSettings().setBuiltInZoomControls(true)
        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }
        }
        web_view.loadUrl(product.image)

        var favorites: MutableSet<String> = pref?.getStringSet("favorites", setOf())!!
        if (favorites.contains(product.id.toString()))
        {
            button.text = "Убрать из избранного"
        } else
        {
            button.text = "Добавить в избранное"
        }
        title.text = product.title
        price.text = "Цена: ".plus(product.price.toString())
        category.text = "Категория: ".plus(product.category)
        rating.text = "Рейтинг: ".plus(product.rating.rate.toString()).plus(" (").plus(product.rating.count.toString()).plus(")")
        description.text = "Описание: ".plus(product.description)
        button.setOnClickListener{
            var favorites: MutableSet<String> = pref?.getStringSet("favorites", mutableSetOf())!!
            if (favorites.contains(product.id.toString()))
            {
                favorites?.remove(product.id.toString())
                button.text = "Добавить в избранное"
            }
            else
            {
                favorites?.add(product.id.toString())
                button.text = "Убрать из избранного"
            }
            val editor = pref?.edit()
            editor?.remove("favorites")
            editor?.apply()
            editor?.putStringSet("favorites", favorites)
            editor?.apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }
}