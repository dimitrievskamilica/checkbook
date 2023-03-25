package com.example.checkbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class AccountsAdapter(private val data: MutableList<TransactionAccount>, private val onClickObject:AccountsAdapter.MyOnClick) :
    RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {
    interface MyOnClick {
        fun onLongClick(p0: View?, position:Int)
        fun onClick(p0: View?, position:Int)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val TransactionAccountName: TextView = itemView.findViewById(R.id.textViewName)
        val BankName: TextView = itemView.findViewById(R.id.textViewBankName)
        val Balance: TextView = itemView.findViewById(R.id.textViewBalance)

        val line: CardView = itemView.findViewById(R.id.accountCvLine)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_transacion_account, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = data[position]
        //Picasso.get().load("https://iconarchive.com/show/business-economic-icons-by-inipagi/store-icon.html").placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(holder.imageView);
        // sets the text to the textview from our itemHolder class
        //Timber.d("MM onBindViewHolder ${data.size}")
        holder.TransactionAccountName.text = ItemsViewModel.nameOfTransactionAccount
        holder.BankName.text = ItemsViewModel.bankName
        if(ItemsViewModel.currency=="euro") {
            holder.Balance.text = ItemsViewModel.balance.toString() + "€"
        }else{
            holder.Balance.text = ItemsViewModel.balance.toString() + "MKD"
        }


        holder.line.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                println("Here code comes Click on ${holder.adapterPosition}")

                onClickObject.onLongClick(v, holder.adapterPosition)//delegacija klica na lasten objekt-sledi razlaga

                return true
            }
        });

        holder.line.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {

                onClickObject.onClick(p0,holder.adapterPosition) //Action from Activity
            }
        })

    }
}