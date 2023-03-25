package com.example.checkbook

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() , View.OnClickListener {
    lateinit var app: MyApplication
    private lateinit var adapter: AccountsAdapter
    var db = FirebaseFirestore.getInstance()
    var getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val pos = data.getIntExtra("position", -1)
                    if(pos!=-1) {
                        adapter.notifyItemChanged(pos)
                    }

                }
            }
        }
    var getContent2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val pos = data.getIntExtra("position", -1)
                    if(pos!=-1) {
                        adapter.notifyItemInserted(pos)
                    }

                }
            }
        }
    var getContent3 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val pos = data.getIntExtra("position", -1)
                    if(pos!=-1) {
                        adapter.notifyItemChanged(pos)
                    }

                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        app = application as MyApplication
        helloUser.text= app.username
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AccountsAdapter(app.transactionAccounts, object:AccountsAdapter.MyOnClick{
            override fun onLongClick(p0: View?, pos: Int) {

                val builder =
                    AlertDialog.Builder(this@HomeActivity) //access context from inner class
                //set title for alert dialog
                builder.setTitle("Delete")
                builder.setMessage("Transaction Account ${app.transactionAccounts[pos].nameOfTransactionAccount}")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                    Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()

                    var ref= db!!.collection("users").document(app.userID).collection("transactionAccounts")
                        .whereEqualTo("nameOfTransactionAccount", app.transactionAccounts[pos].nameOfTransactionAccount)
                        .whereEqualTo("bankName", app.transactionAccounts[pos].bankName)
                        .whereEqualTo("balance", app.transactionAccounts[pos].balance)
                        .whereEqualTo("currency", app.transactionAccounts[pos].currency).get()
                    ref.addOnSuccessListener { docs->

                        for (d in docs) {

                            var ref = db!!.collection("users").document(app.userID)
                                .collection("transactionAccounts").document(d.id)
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

                                }

                            }
                            d.reference.delete()

                        }
                        val toRemove=HashSet<Transaction>()
                        for(transaction in app.transactions){
                            if(transaction.nameOfTransactionAccount==app.transactionAccounts[pos].nameOfTransactionAccount){
                                toRemove.add(transaction)
                            }

                        }
                        app.transactions.removeAll(toRemove)
                        app.transactionAccounts.remove(app.transactionAccounts[pos])
                        adapter.notifyItemRemoved(pos)
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

                var addIntent= Intent(getApplicationContext(), AddTransactionAccount::class.java)
                addIntent.putExtra("position", pos.toString().toInt())

                setResult(RESULT_OK, addIntent)
                getContent.launch(addIntent)

            }

        })
        recyclerView.adapter = adapter
        SignOut.setOnClickListener(this )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.add_account -> {
                var addIntent= Intent(getApplicationContext(), AddTransactionAccount::class.java)
                // addIntent.putExtra("position", pos.toString().toInt())

                setResult(RESULT_OK, addIntent)
                getContent2.launch(addIntent)
            }
            R.id.transactions -> {
                var addIntent= Intent(getApplicationContext(), TransactionsActivity::class.java)
                // addIntent.putExtra("position", pos.toString().toInt())

                setResult(RESULT_OK, addIntent)
                getContent.launch(addIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        app.auth.signOut()
        app.username=""
        app.userID=""
        app.transactions.clear()
        app.transactionAccounts.clear()
        Toast.makeText(this@HomeActivity, "Signed out", Toast.LENGTH_SHORT).show()
        finish()
    }
}