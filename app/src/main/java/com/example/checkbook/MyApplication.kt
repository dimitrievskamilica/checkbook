package com.example.checkbook

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MyApplication : Application() {
    lateinit var auth: FirebaseAuth
    var username =""
    var userID =""
    lateinit var transactionAccounts:MutableList<TransactionAccount>
    lateinit var transactions:MutableList<Transaction>

    override fun onCreate() {
        super.onCreate()
        auth= FirebaseAuth.getInstance()
        transactionAccounts = mutableListOf<TransactionAccount>()
        transactions = mutableListOf<Transaction>()

    }
}