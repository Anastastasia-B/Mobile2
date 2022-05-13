package com.example.lab2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class ProfileFragment : Fragment() {
    lateinit var nameField: EditText
    lateinit var surnameField: EditText
    lateinit var patronymicField: EditText
    lateinit var emailField: EditText
    lateinit var image_view: ImageView
    lateinit var btn_about: Button
    lateinit var btn_favorites: Button
    lateinit var btn_map: Button
    lateinit var swt_dark: Switch


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_about = view.findViewById(R.id.btn_about)
        btn_about.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("О разработчике")
                .setMessage("Лабораторная работа No 2\nАвтор: Березнёва А.С.\nГруппа: 951005")
                .setPositiveButton(
                    "понятно",
                    DialogInterface.OnClickListener { dialog, id -> // Закрываем окно
                        dialog.cancel()
                    })
            builder.create().show()
        }
        btn_favorites = view.findViewById(R.id.btn_favorites)
        btn_favorites.setOnClickListener{
            val navController = activity?.findNavController(R.id.frg_main)
            var bundle = Bundle()
            bundle.putBoolean("favorites", true)
            navController?.navigate(R.id.homeFragment, bundle)
        }

        image_view = view.findViewById(R.id.image_view)
        image_view.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED)
                {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else
                {
                    pickImageFromGallery()
                }
            }
            else
            {
                pickImageFromGallery()
            }
        }


        nameField = view.findViewById(R.id.name)
        surnameField = view.findViewById(R.id.surname)
        patronymicField = view.findViewById(R.id.patronymic)
        emailField = view.findViewById(R.id.email)

        var pref = context?.getSharedPreferences("data", Context.MODE_PRIVATE)

        var uri = pref?.getString("pic", "")
        if (uri != "")
        image_view.setImageURI(uri?.toUri())

        var name = pref?.getString("name", "")
        nameField?.setText(name)
        nameField?.setOnFocusChangeListener() { view, b ->
            val editor = pref?.edit()
            editor?.putString("name", nameField.text.toString())
            editor?.apply()
        }

        var surname = pref?.getString("surname", "")
        surnameField?.setText(surname)
        surnameField?.setOnFocusChangeListener() { view, b ->
            val editor = pref?.edit()
            editor?.putString("surname", surnameField.text.toString())
            editor?.apply()
        }

        var patronymic = pref?.getString("patronymic", "")
        patronymicField?.setText(patronymic)
        patronymicField?.setOnFocusChangeListener() { view, b ->
            val editor = pref?.edit()
            editor?.putString("patronymic", patronymicField.text.toString())
            editor?.apply()
        }

        var email = pref?.getString("email", "")
        emailField?.setText(email)
        emailField?.setOnFocusChangeListener() { view, b ->
            val editor = pref?.edit()
            editor?.putString("email", emailField.text.toString())
            editor?.apply()
        }

        btn_map = view.findViewById(R.id.btn_map)
        btn_map.setOnClickListener{
            val mapsIntent = Intent(activity, MapsActivity::class.java)
            startActivity(mapsIntent)
        }

        swt_dark = view.findViewById(R.id.swt_dark)
        swt_dark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun pickImageFromGallery()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PIC_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        when(requestCode)
        {
            PERMISSION_CODE ->
            {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    pickImageFromGallery()
                }
                else
                {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PIC_CODE)
        {
            var pref = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = pref?.edit()
            editor?.putString("pic", data?.data.toString())
            editor?.apply()
            image_view.setImageURI(data?.data)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        private val IMAGE_PIC_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
}