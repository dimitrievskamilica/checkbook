package com.example.checkbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_transaction_account.*

class AddTransactionAccount : AppCompatActivity() {
    lateinit var app: MyApplication
    var db = FirebaseFirestore.getInstance()
    private var pos : Int = -1
    private var documentId:String=""
    var getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val pos = data.getIntExtra("position", -1)
                    if(pos!=-1) {
                        balance.setText(app.transactionAccounts[pos].balance.toString())
                        val intent = Intent()
                        intent.putExtra("position", pos.toString().toInt())
                        setResult(RESULT_OK,intent)
                    }

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction_account)
        app = application as MyApplication
        if(intent.hasExtra("position")) {
            pos =intent.getIntExtra("position",-1)
            if(pos!=-1) {

                textView.text="Update Transaction Account"
                btn_add.text = "Update"
                transaction_account_name.setText(app.transactionAccounts[pos].nameOfTransactionAccount)
                bank_name.setText(app.transactionAccounts[pos].bankName)
                balance.setText(app.transactionAccounts[pos].balance.toString())
                if(app.transactionAccounts[pos].currency=="MKD"){
                    MKD.isChecked=true
                }else{
                    euro.isChecked=true
                }

                btn_add.setTextKeepState("Update")
                var ref= db!!.collection("users").document(app.userID).collection("transactionAccounts")
                    .whereEqualTo("nameOfTransactionAccount", app.transactionAccounts[pos].nameOfTransactionAccount)
                    .whereEqualTo("bankName", app.transactionAccounts[pos].bankName)
                    .whereEqualTo("balance", app.transactionAccounts[pos].balance)
                    .whereEqualTo("currency", app.transactionAccounts[pos].currency).get()
                ref.addOnSuccessListener { docs ->

                    for (d in docs) {
                        documentId = d.id

                    }
                }
            }

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(pos!=-1) {
            getMenuInflater().inflate(R.menu.account_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var addIntent= Intent(getApplicationContext(), AddWithdrawMoney::class.java)
        addIntent.putExtra("position", pos)

        when (item.itemId){
             R.id.add_money -> addIntent.putExtra("value", 1)
            R.id.money_withdrawal -> addIntent.putExtra("value", 2)
        }

        setResult(RESULT_OK, addIntent)
        getContent.launch(addIntent)

        return super.onOptionsItemSelected(item)
    }
    fun addTransactionAccount(view: android.view.View) {
        var radioName="";
        if(euro.isChecked){
            radioName="euro";
        }else{
            radioName="MKD"
        }

        if(pos==-1){

            val intent = Intent()
            app.transactionAccounts.add(TransactionAccount(
                transaction_account_name.text.toString(),
                bank_name.text.toString(),
                balance.text.toString().toInt(),
                radioName
                )
            )
            val reg_entry: MutableMap<String, Any> =
                HashMap()
            reg_entry["nameOfTransactionAccount"] = transaction_account_name.text.toString()
            reg_entry["bankName"] = bank_name.text.toString()
            reg_entry["balance"] = balance.text.toString().toInt()
            reg_entry["currency"] = radioName
            db!!.collection("users").document(app.userID).collection("transactionAccounts").add(reg_entry)
            intent.putExtra("position", app.transactionAccounts.size-1)
            setResult(RESULT_OK,intent)
            finish()

        }else{

            val intent = Intent()
            app.transactionAccounts[pos].nameOfTransactionAccount=transaction_account_name.text.toString()
            app.transactionAccounts[pos].bankName=bank_name.text.toString()
            app.transactionAccounts[pos].balance= balance.text.toString().toInt()
            app.transactionAccounts[pos].currency=radioName
            val reg_entry: MutableMap<String, Any> =
                HashMap()
            reg_entry["nameOfTransactionAccount"] = transaction_account_name.text.toString()
            reg_entry["bankName"] = bank_name.text.toString()
            reg_entry["balance"] = balance.text.toString().toInt()
            reg_entry["currency"] = radioName
            db!!.collection("users").document(app.userID).collection("transactionAccounts").document(documentId)
                .set(reg_entry)
            intent.putExtra("position", pos.toString().toInt())
            setResult(RESULT_OK,intent)
            finish()
        }
    }
    fun backTransactionAccount(view: android.view.View) {
        finish();
    }
}