package com.example.lab2

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class ExploreFragment : Fragment() {

    lateinit var btn_find: Button
    lateinit var edt_search_name: EditText
    lateinit var edt_search_from: EditText
    lateinit var edt_search_to: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_find = view.findViewById(R.id.btn_find)
        edt_search_name = view.findViewById(R.id.edt_search_name)
        edt_search_from = view.findViewById(R.id.edt_search_from)
        edt_search_to = view.findViewById(R.id.edt_search_to)
        var pref = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
        var name = pref?.getString("search_name", "")
        var from = pref?.getInt("search_from", 0)
        var to = pref?.getInt("search_to", 5000)
        edt_search_name.setText(name)
        edt_search_from.setText(from.toString())
        edt_search_to.setText(to.toString())

        btn_find.setOnClickListener {
            var pref = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = pref?.edit()
            editor?.putString("search_name", edt_search_name.text.toString())
            if (edt_search_from.text.toString().length > 0)
            editor?.putInt("search_from", edt_search_from.text.toString().toInt())
            else
                editor?.putInt("search_from", 0)
            if (edt_search_to.text.toString().length > 0)
            editor?.putInt("search_to", edt_search_to.text.toString().toInt())
            else
                editor?.putInt("search_to", 5000)

            editor?.apply()
            val navController = activity?.findNavController(R.id.frg_main)
            navController?.navigate(R.id.homeFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    companion object {

    }

}