package com.example.lab2

import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONObject

class Product {
    class Rating
    {
        var rate: Float = 0F
        var count: Int = 0
    }
    var id: Int = 0
    var title: String = ""
    var price: Float = 0F
    var description: String = ""
    var category: String = ""
    var image: String = ""
    var rating: Rating = Rating()
}

class Products
{
    var products: List<Product> = emptyList()
}