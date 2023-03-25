package com.example.checkbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TransactionsAdapter (private val data: MutableList<Transaction>, private val onClickObject:TransactionsAdapter.MyOnClick) :
    RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    interface MyOnClick {
        fun onLongClick(p0: View?, position:Int)
        fun onClick(p0: View?, position:Int)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val TransactionAccountName: TextView = itemView.findViewById(R.id.textViewName)
        val Type: TextView = itemView.findViewById(R.id.textViewType)
        val Amount: TextView = itemView.findViewById(R.id.textViewAmount)
        val TransactionDate: TextView = itemView.findViewById(R.id.textViewDate)
        val line: CardView = itemView.findViewById(R.id.transactionCvLine)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = data[position]
        if(ItemsViewModel.currency=="euro") {
            holder.Amount.text = ItemsViewModel.amount.toString() + "€"
        }else{
            holder.Amount.text = ItemsViewModel.amount.toString() + "MKD"
        }
        //holder.Amount.text = ItemsViewModel.amount.toString()
        holder.Type.text = ItemsViewModel.type
        holder.TransactionAccountName.text = ItemsViewModel.nameOfTransactionAccount
        holder.TransactionDate.text=ItemsViewModel.date


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