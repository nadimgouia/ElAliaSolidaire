package com.ndroid.elaliasolidaire

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_services.*


class MyServicesActivity : AppCompatActivity() {

    lateinit var android_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_services)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var servicesData = arrayListOf<Service>()
        val servicesAdapter = ServiceAdapter(this)
        servicesAdapter.setData(servicesData)
        servicesList.adapter = servicesAdapter

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("device_id", "") != "") {
            android_id = sharedPreferences.getString("device_id", "").toString()
        }
        print("android_id $android_id")
        val database = FirebaseDatabase.getInstance().reference.child(android_id)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               if(!snapshot.hasChild("services")) {
                   progressBar.visibility = View.GONE
                   tvNoService.visibility = View.VISIBLE
               }
            }
        })

        database.child("services").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) { // Failed to read value

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if (dataSnapshot.children.count() == 0) {
                    progressBar.visibility = View.GONE
                    servicesList.visibility = View.GONE
                    tvNoService.visibility = View.VISIBLE
                } else {
                    val value = dataSnapshot.getValue(Service::class.java)!!

                    progressBar.visibility = View.GONE
                    tvNoService.visibility = View.GONE
                    servicesList.visibility = View.VISIBLE
                    servicesData.add(
                        Service(
                            user = value.user,
                            adresse = value.adresse,
                            service = value.service,
                            tel = value.tel,
                            dateDemande = value.dateDemande,
                            databaseKey = dataSnapshot.key!!
                        )
                    )
                    servicesAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                for(i in 0 until servicesData.size) {
                    if(servicesData[i].databaseKey == dataSnapshot.key)
                        servicesData.removeAt(i)
                }
                servicesAdapter.notifyDataSetChanged()
                if(servicesData.size==0){
                    servicesList.visibility = View.GONE
                    tvNoService.visibility = View.VISIBLE
                }
            }
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
