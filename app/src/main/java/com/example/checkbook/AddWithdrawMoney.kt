package com.example.checkbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_transaction_account.*
import kotlinx.android.synthetic.main.activity_add_transaction_account.btn_add
import kotlinx.android.synthetic.main.activity_add_transaction_account.textView
import kotlinx.android.synthetic.main.activity_add_withdraw_money.*

class AddWithdrawMoney : AppCompatActivity() {
    lateinit var app: MyApplication
    var db = FirebaseFirestore.getInstance()
    private var value : Int = -1
    private var pos : Int = -1
    private var documentId:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_withdraw_money)
        app = application as MyApplication
        if(intent.hasExtra("value")) {
            value =intent.getIntExtra("value",-1)
            if(value==2) {

                textView.text="Money withdrawal"
                btn_add.text = "Withdraw"

            }

        }
    }

    fun backTransactionAccount(view: android.view.View) {
        finish()
    }
    fun addWithdraw(view: android.view.View) {
        if(intent.hasExtra("value") && intent.hasExtra("position")) {
            value =intent.getIntExtra("value",-1)
            pos =intent.getIntExtra("position",-1)

            if(pos!=-1) {
                var ref= db!!.collection("users").document(app.userID).collection("transactionAccounts")
                    .whereEqualTo("nameOfTransactionAccount", app.transactionAccounts[pos].nameOfTransactionAccount).get()
                ref.addOnSuccessListener { docs ->
                    for (d in docs) {
                        documentId = d.id
                        if (value == 2) {

                            val intent = Intent()
                            var category=""

                            val reg_entry: MutableMap<String, Any> =
                                HashMap()

                            reg_entry["description"] = editTextTextMultiLine.text.toString()
                            reg_entry["amount"] = amount.text.toString().toInt()
                            reg_entry["type"] = "withdrawal"
                            reg_entry["date"]=editTextDate.text.toString()
                            if(food.isChecked) {
                                reg_entry["category"] = "food"
                                category="food"
                            }else if(house.isChecked){
                                reg_entry["category"] = "house"
                                category="house"
                            }else if(car.isChecked){
                                reg_entry["category"] = "car"
                                category="car"
                            }else{
                                reg_entry["category"] = "sport"
                                category="sport"
                            }

                            app.transactions.add(
                                Transaction(amount.text.toString().toInt(),
                                    editTextTextMultiLine.text.toString(),
                                    category,
                                    "withdrawal",
                                    app.transactionAccounts[pos].nameOfTransactionAccount,
                                    editTextDate.text.toString(),
                                    app.transactionAccounts[pos].currency))

                            db!!.collection("users").document(app.userID).collection("transactionAccounts")
                                .document(documentId) .collection("transactions").add(reg_entry)
                            app.transactionAccounts[pos].balance -= amount.text.toString().toInt()

                            db!!.collection("users").document(app.userID).collection("transactionAccounts").document(documentId)
                                .update(mapOf("balance" to app.transactionAccounts[pos].balance))
                            intent.putExtra("position", pos.toString().toInt())
                            setResult(RESULT_OK,intent)
                            finish()
                        }else if(value==1){

                            val intent = Intent()
                            var category=""

                            val reg_entry: MutableMap<String, Any> =
                                HashMap()
                            // reg_entry["nameOfTransactionAccount"] = app.transactionAccounts[pos].nameOfTransactionAccount
                            reg_entry["description"] = editTextTextMultiLine.text.toString()
                            reg_entry["amount"] = amount.text.toString().toInt()
                            reg_entry["type"] = "transfer"
                            reg_entry["date"]=editTextDate.text.toString()
                           // reg_entry["currency"]=
                            if(food.isChecked) {
                                reg_entry["category"] = "food"
                                category="food"
                            }else if(house.isChecked){
                                reg_entry["category"] = "house"
                                category="house"
                            }else if(car.isChecked){
                                reg_entry["category"] = "car"
                                category="car"
                            }else{
                                reg_entry["category"] = "sport"
                                category="sport"
                            }
                            app.transactions.add(
                                Transaction(amount.text.toString().toInt(),
                                    editTextTextMultiLine.text.toString(),
                                    category,
                                    "transfer",
                                    app.transactionAccounts[pos].nameOfTransactionAccount,
                                    editTextDate.text.toString(),
                                    app.transactionAccounts[pos].currency))

                            db!!.collection("users").document(app.userID).collection("transactionAccounts")
                                .document(documentId) .collection("transactions").add(reg_entry)
                            app.transactionAccounts[pos].balance += amount.text.toString().toInt()
                            val amount_entry: MutableMap<String, Any> =
                                HashMap()
                            amount_entry["balance"] = app.transactionAccounts[pos].balance
                            db!!.collection("users").document(app.userID).collection("transactionAccounts").document(documentId)
                                .update(mapOf("balance" to app.transactionAccounts[pos].balance))

                            intent.putExtra("position", pos.toString().toInt())
                            setResult(RESULT_OK,intent)
                            finish()
                        }
                    }
                }

            }
        }
    }
}