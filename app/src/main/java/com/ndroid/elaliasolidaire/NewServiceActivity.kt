package com.ndroid.elaliasolidaire

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.new_services_activity.*
import java.text.SimpleDateFormat
import java.util.*


class NewServiceActivity : AppCompatActivity() {

    lateinit var android_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_services_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // save the unique device id to sharedPreferences
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("device_id", "") == "") {
            android_id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val editor =  sharedPreferences.edit()
            editor.putString("device_id", android_id)
            editor.apply()
        } else {
            android_id = sharedPreferences.getString("device_id", "").toString()
        }
        print("android_id $android_id")

        tvServicesList.setMovementMethod(ScrollingMovementMethod())
        var servicesContent = ""
        btnAddService.setOnClickListener {
            if (editService.text.toString() != "") {
                val newService = editService.text.trim()
                servicesContent = if (servicesContent.equals(""))
                    "- $newService"
                else
                    "${servicesContent}\n- $newService"
                tvServicesList.text = servicesContent
                editService.setText("")
                hideKeyboard()
            } else
                Toast.makeText(this, "يرجو أن لا تترك خانة الطلب فارغة", Toast.LENGTH_LONG).show()
        }

        btnSave.setOnClickListener {
            if (servicesContent == "" || editName.text.toString() == "" || editAdresse.text.toString() == "" || editTel.text.toString() == "") {
                Toast.makeText(this, "يرجو منكم ملء كل الخانات", Toast.LENGTH_LONG).show()
            } else {
                // Write a message to the database
                val database = FirebaseDatabase.getInstance().reference.child(android_id).child("services")
                val newService = database.push()
                newService.child("user").setValue(editName.text.toString())
                newService.child("tel").setValue(editTel.text.toString())
                newService.child("adresse").setValue(editAdresse.text.toString())
                newService.child("service").setValue(servicesContent)
                newService.child("etat").setValue(0)

                val date = getCurrentDateTime()
                val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

                newService.child("dateDemande").setValue(dateInString)

                finish()
                startActivity(Intent(this, MyServicesActivity::class.java))
            }
        }

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }
}
