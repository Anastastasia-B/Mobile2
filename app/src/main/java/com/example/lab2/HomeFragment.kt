package com.example.lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import java.net.URL

class HomeFragment : Fragment() {

    private var products: ArrayList<Product> = ArrayList<Product>()
    var pref: SharedPreferences? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (products.size == 0)
        GlobalScope.launch {
            products = getProducts()
            launch(Dispatchers.Main)
            {
                addRltLayouts(view)
            }
        }
        else
        {
            addRltLayouts(view)
        }
        pref = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
    }

    fun addRltLayouts(view: View)
    {
        var name = pref?.getString("search_name", "")
        var from = pref?.getInt("search_from", 0)
        var to = pref?.getInt("search_to", 5000)

        var lnr_products = view.findViewById<LinearLayout>(R.id.lnr_products)
        var j = 0
        var favorites = getArguments()?.getBoolean("favorites")
        var button = view.findViewById<Button>(R.id.btn_all_products)
        if (favorites == true)
        {
            button.isVisible = true
            button.setOnClickListener{
                val navController = activity?.findNavController(R.id.frg_main)
                navController?.navigate(R.id.homeFragment)
            }
        }
        else
        {
            button.isVisible = false
        }
        for (i in 0..products.size - 1)
        if (((favorites == null) && ((products[i].price >= from!!) && (products[i].price <= to!!) && (products[i].title.contains(name!!, ignoreCase = true)))) ||
                ((favorites == true) && (pref?.getStringSet("favorites", setOf())?.contains(products[i].id.toString())!!)))
        {
            var rlt_product = getRlt(products[i])
            j++
            if (j % 2 == 0)
                rlt_product?.setBackgroundColor(Color.LTGRAY)
            if (rlt_product != null)
            lnr_products.addView(rlt_product)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setRetainInstance(true);
    }

    suspend fun getProducts(): ArrayList<Product>
    {
        var result:String? = ""
        var request = GlobalScope.launch {
            result = get("https://fakestoreapi.com/products")
        }
        request.join()

        if (result == "error") {
            Looper.prepare()
            val text = "Нет доступа в интернет"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(activity?.applicationContext, text, duration)
            toast.show()
            Looper.loop()
            return ArrayList()
        }

        var gson = Gson()
        val array = object : TypeToken<ArrayList<Product>>() {}.type
        return gson.fromJson(result, array)
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun get(url: String) : String
    {
        var result: String = ""
        var url = URL(url)
        var request = GlobalScope.launch {
            try {
                result = url.readText()
            }
            catch(e: Exception)
            {
            }
        }
        request.join()
        if (result == "")
            return "error"
        return result
    }

    fun getRlt(product: Product): RelativeLayout?
    {
        try {

            var rlt = RelativeLayout(context)
            rlt.layoutParams =
                RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 210)

            var img = ImageView(context)
            Downloader(img, product.id, pref).execute(product.image)
            img.layoutParams = ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT)
            img.setPadding(0, 30, 0, 30)

            var txt = TextView(context)
            txt.text = getVisibleTitle(product.title)
            txt.setTextSize(15F)
            txt.width = 900
            txt.setPadding(400, 30, 0, 30)

            var price = TextView(context)
            price.text = product.price.toString()
            price.setTextSize(25F)
            price.setTextColor(Color.rgb(0, 128, 0))
            price.setPadding(910, 50, 0, 30)

            rlt.addView(img)
            rlt.addView(txt)
            rlt.addView(price)
            //lnr.layoutParams = ViewGroup.LayoutParams(lnr.width, lnr.height)

            rlt.setOnClickListener {
                val navController = activity?.findNavController(R.id.frg_main)
                var bundle = Bundle()
                bundle.putString("product", Gson().toJson(product))
                navController?.navigate(R.id.infoFragment, bundle)
            }
            return rlt
        }
        catch(e: Exception)
        {
            return null
        }
    }

    fun getVisibleTitle(title: String): String
    {
        val maxLength = 30
        if (title.length < maxLength)
            return title
        var strBuilder: StringBuilder = StringBuilder()
        var shortTitle = title.subSequence(0, maxLength - 3)
        strBuilder.append(shortTitle).append("...")
        shortTitle = strBuilder.toString()
        return shortTitle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}