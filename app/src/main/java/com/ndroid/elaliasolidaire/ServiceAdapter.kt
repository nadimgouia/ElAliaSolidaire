package com.ndroid.elaliasolidaire

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class ServiceAdapter(var context: Context) : BaseAdapter() {

    var servicesList: ArrayList<Service> = ArrayList()
    lateinit var android_id: String

    fun setData(servicesList:ArrayList<Service> ) {
        this.servicesList = servicesList
    }

    init {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("device_id", "") != "") {
            android_id = sharedPreferences.getString("device_id", "").toString()
        }
        print("android_id $android_id")
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {

        var convertView = LayoutInflater.from(parent?.context).inflate(R.layout.service_item, parent, false)
        var tvService : TextView = convertView.findViewById(R.id.tvService)
        var tvDate : TextView = convertView.findViewById(R.id.tvDate)
        var btnDelete: LinearLayout = convertView.findViewById(R.id.btnDelete)

        val selectedService = this.servicesList[position]
        tvService.text = selectedService.service
        tvDate.text = selectedService.dateDemande

        convertView.setOnClickListener {
            val layoutParams = convertView.layoutParams
            if(layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
             else
                layoutParams.height = 75.dp

            convertView.layoutParams = layoutParams
        }

        btnDelete.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(parent?.context)
            //alertDialogBuilder.setTitle("تأكيد")
            alertDialogBuilder.setMessage("قم بتأكيد محو الطلب")
            alertDialogBuilder.setPositiveButton("تأكيد") { dialogInterface: DialogInterface, id: Int ->
                val database = FirebaseDatabase.getInstance().reference.child(android_id).child("services").child(selectedService.databaseKey)
                database.removeValue()
                dialogInterface.dismiss()
            }
            alertDialogBuilder.setNegativeButton("تراجع") { dialogInterface: DialogInterface, id: Int ->
                dialogInterface.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            //Toast.makeText(parent?.context, "delete: ${selectedService.databaseKey}", Toast.LENGTH_LONG).show()
        }

        return convertView
    }

    override fun getItem(position: Int): Any {
        return this.servicesList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return this.servicesList.count()
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

}
