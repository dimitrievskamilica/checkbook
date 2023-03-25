package com.example.checkbook

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_transactions.*


class TransactionsActivity : AppCompatActivity() {
    lateinit var app: MyApplication
    var db = FirebaseFirestore.getInstance()
    private var documentId:String=""
    private lateinit var adapter: TransactionsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        app = application as MyApplication

        recyclerView2.layoutManager = LinearLayoutManager(this)

        adapter = TransactionsAdapter(app.transactions, object:TransactionsAdapter.MyOnClick{
            override fun onLongClick(p0: View?, pos: Int) {

                val builder = AlertDialog.Builder(this@TransactionsActivity) //access context from inner class
                //set title for alert dialog
                builder.setTitle("Delete")
                builder.setMessage("Are you sure you want to delete this transaction?")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                    Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()
                    var newBalance=0
                    for(acc in app.transactionAccounts){
                        if(acc.nameOfTransactionAccount==app.transactions[pos].nameOfTransactionAccount){
                            if(app.transactions[pos].type=="withdrawal"){
                                acc.balance+=app.transactions[pos].amount
                                newBalance=acc.balance
                            }else{
                                acc.balance-=app.transactions[pos].amount
                                newBalance=acc.balance
                            }
                            val intent = Intent()
                            intent.putExtra("position", app.transactionAccounts.indexOf(acc))
                            setResult(RESULT_OK,intent)
                        }
                    }
                    val reff = db!!.collection("users").document(app.userID)
                        .collection("transactionAccounts")
                        .whereEqualTo(
                            "nameOfTransactionAccount",
                            app.transactions[pos].nameOfTransactionAccount
                        ).get()
                    reff.addOnSuccessListener { docs ->
                        for (d in docs) {
                            documentId = d.id
                            /*var newBalance=d.data.get("balance").toString().toInt()
                            if(app.transactions[pos].type=="withdrawal"){
                                newBalance-=app.transactions[pos].amount
                            }else{
                                newBalance+=app.transactions[pos].amount
                            }*/
                            val ref2 =db!!.collection("users").document(app.userID)
                                .collection("transactionAccounts").document(documentId)
                                .update(mapOf("balance" to newBalance))
                            val ref = db!!.collection("users").document(app.userID)
                                .collection("transactionAccounts").document(documentId)
                                .collection("transactions")
                                .whereEqualTo("amount", app.transactions[pos].amount)
                                .whereEqualTo("description", app.transactions[pos].description)
                                .whereEqualTo("category", app.transactions[pos].category)
                                .whereEqualTo("type", app.transactions[pos].type)
                                .whereEqualTo("date", app.transactions[pos].date)
                                .get()
                            ref.addOnSuccessListener { docss ->

                                for (dd in docss) {

                                    dd.reference.delete()
                                    Log.i("laalala","tuka sum")
                                }
                                app.transactions.remove(app.transactions[pos])
                                adapter.notifyItemRemoved(pos)
                            }
                        }

                    }
                  }
                builder.setNeutralButton("Cancel") { dialogInterface, which -> //performing cancel action
                        Toast.makeText(
                            applicationContext,
                            "clicked cancel\n operation cancel",
                            Toast.LENGTH_LONG
                        ).show()
                }
                builder.setNegativeButton("No") { dialogInterface, which -> //performing negative action
                        Toast.makeText(applicationContext, "clicked No", Toast.LENGTH_LONG).show()
                }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()


            }
            override fun onClick(p0: View?, pos:Int) {

                val builder =
                    AlertDialog.Builder(this@TransactionsActivity) //access context from inner class
                //set title for alert dialog
                builder.setTitle("Transaction")
                if(app.transactions[pos].type=="withdrawal") {
                    builder.setMessage("Withdrawed ${app.transactions[pos].amount}${app.transactions[pos].currency} from ${app.transactions[pos].nameOfTransactionAccount} on ${app.transactions[pos].date} \n" +
                            "Description: ${app.transactions[pos].description} \n" +
                            "Category: ${app.transactions[pos].category}")
                }else{
                    builder.setMessage("Transfered ${app.transactions[pos].amount}${app.transactions[pos].currency} to ${app.transactions[pos].nameOfTransactionAccount} on ${app.transactions[pos].date} \n" +
                            "Description: ${app.transactions[pos].description} \n" +
                            "Category: ${app.transactions[pos].category}")
                }
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                    builder.setNeutralButton("Close") { dialogInterface, which -> //performing cancel action
                        Toast.makeText(
                            applicationContext,
                            "clicked close\n operation cancel",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()



            }

        })
        recyclerView2.adapter = adapter
    }

    fun backTransactions(view: android.view.View) {
        finish()
    }
}