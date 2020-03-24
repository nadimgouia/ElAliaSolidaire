package com.ndroid.elaliasolidaire

class Service() {

    var user:String = ""
    var adresse:String = ""
    var tel:String = ""
    var service:String = ""
    var dateDemande:String = ""
    var databaseKey:String = ""

    constructor(user: String, adresse: String, tel: String, service: String, dateDemande: String, databaseKey: String) : this() {
        this.user = user
        this.adresse = adresse
        this.tel = tel
        this.service = service
        this.dateDemande = dateDemande
        this.databaseKey = databaseKey
    }

    constructor(user: String, adresse: String, tel: String, service: String) : this() {
        this.user = user
        this.adresse = adresse
        this.tel = tel
        this.service = service
    }

}
